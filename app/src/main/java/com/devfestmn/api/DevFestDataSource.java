package com.devfestmn.api;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.devfestmn.api.model.Conference;
import com.devfestmn.api.model.Feedback;
import com.devfestmn.api.model.Session;
import com.devfestmn.api.model.Speaker;
import com.devfestmn.persistence.UserDetailsRepository;
import com.devfestmn.persistence.UserScheduleRepository;
import timber.log.Timber;

/**
 * This is the source of session, schedule, and speaker information. This acts as a general
 * contractor that can coordinate between various subcontractor classes including but not limited to
 * local and remote data sources.
 * <p>
 * Created by chris.black on 12/5/15.
 *
 * @author bherbst
 * @author pfuentes
 */
public class DevFestDataSource {
    private static final int CHANGES_ALLOWED_BEFORE_LOGIN_PROMPT = 2;

    private static final String DEVFEST_2017_KEY = "devfest2017";
    private static final String SESSIONS_CHILD_KEY = "schedule";
    private static final String SPEAKERS_CHILD_KEY = "speakers";
    private static final String AGENDAS_KEY = "agendas";
    private static final String VALUE_KEY = "value";
    private static final String FEEDBACK_CHILD_KEY = "feedback";

    private static DevFestDataSource mOurInstance;

    private UserScheduleRepository mScheduleRepository;
    private UserDetailsRepository mUserDetailsRepository;
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseAuth mFirebaseAuth;
    private int timesSinceLoginPrompted = 0;

    private Conference mConference = new Conference();
    //TODO move to an array of listeners?
    private DataSourceListener mDataSourceListener;
    private UserScheduleListener mUserScheduleListener;
    private LoginPromptListener mLoginPromptListener;
    private ValueEventListener mFirebaseUserScheduleListener;

    public static DevFestDataSource getInstance(Context context) {
        if (mOurInstance == null) {
            mOurInstance = new DevFestDataSource(context);
        }
        return mOurInstance;
    }

    public DevFestDataSource(Context context) {
        mScheduleRepository = new UserScheduleRepository(context);
        mUserDetailsRepository = new UserDetailsRepository(context);

        //TODO move all firebase access into a separate class and de-duplicate code
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        //Get sessions
        mFirebaseDatabaseReference.child(DEVFEST_2017_KEY)
                .child(SESSIONS_CHILD_KEY).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Clear the old schedule data out
                HashMap map = new HashMap<String, Session>();
                //Add each new session into the schedule
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Timber.d("Session snapshot is: %s", snapshot.toString());
                    Session session = snapshot.getValue(Session.class);
                    session.setId(snapshot.getKey());
                    map.put(session.getId(), session);
                }
                mConference.setSchedule(map);
                if (mDataSourceListener != null) {
                    mDataSourceListener.onSessionsUpdate(new ArrayList<>(map.values()));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // TODO handle failing to read value
                Timber.e(databaseError.toException(), "Failed to read sessions value.");
            }
        });
        //Get speakers
        mFirebaseDatabaseReference.child(DEVFEST_2017_KEY).child(SPEAKERS_CHILD_KEY)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Clear the old speaker data out
                        HashMap map = new HashMap<String, Session>();
                        //Add each new speaker into the schedule
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Timber.d("Speaker snapshot is: %s", snapshot.toString());
                            Speaker speaker = snapshot.getValue(Speaker.class);
                            speaker.setId(snapshot.getKey());
                            map.put(speaker.getId(), speaker);
                        }
                        mConference.setSpeakers(map);
                        if (mDataSourceListener != null) {
                            mDataSourceListener.onSpeakersUpdate(new ArrayList<>(map.values()));
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // TODO handle failing to read value
                        Timber.e(databaseError.toException(), "Failed to read speakers value.");
                    }
                });
    }

    @NonNull
    public List<Session> getSessions() {
        if (mConference == null) {
            return new ArrayList<>();
        }
        if (mConference.getSchedule() == null) {
            return new ArrayList<>();
        }

        return new ArrayList<>(mConference.getSchedule().values());
    }

    @Nullable
    public Session getSessionById(String id) {
        if (mConference == null) {
            return null;
        }

        return mConference.getSchedule().get(id);
    }

    @NonNull
    public List<Speaker> getSpeakers() {
        if (mConference == null) {
            return new ArrayList<>();
        }
        if (mConference.getSpeakers() == null) {
            return new ArrayList<>();
        }

        return new ArrayList<>(mConference.getSpeakers().values());
    }

    @Nullable
    public Speaker getSpeakerById(String id) {
        if (mConference == null) {
            return null;
        }

        return mConference.getSpeakers().get(id);
    }

    @NonNull
    public List<Session> getUserSchedule() {
        // Find sessions with an ID matching the user's saved session IDs
        List<Session> sessions = getSessions();
        List<Session> userSessions = new ArrayList<>();

        if (sessions.size() == 0) {
            return sessions;
        }


        if (mScheduleRepository != null) {
            // We use a loop that goes backwards so we can remove items as we iterate over the list without
            // running into a concurrent modification issue or altering the indices of items
            for (int i = sessions.size() - 1; i >= 0; i--) {
                Session session = sessions.get(i);
                if (mScheduleRepository.getScheduleIds().contains(session.getId())) {
                    userSessions.add(session);
                }
            }
        }
        return userSessions;
    }

    /**
     * Adds the session with the given ID to the user's schedule
     *
     * @param sessionId ID of the session to be added
     */
    public void addToUserSchedule(String sessionId) {
        mScheduleRepository.addSession(sessionId);
        mDataSourceListener.onUserScheduleUpdate(getUserSchedule());
        attemptAddingSessionToFirebase(sessionId);
    }

    private void attemptAddingSessionToFirebase(String sessionId) {
        //We can't sync to Firebase if we aren't logged in
        if (!isSignedIn()) {
            checkIfShouldPromptUser();
            return;
        }

        //Add the session to the user's schedule in Firebase
        mFirebaseDatabaseReference.child(DEVFEST_2017_KEY).child(AGENDAS_KEY)
                .child(mFirebaseAuth.getCurrentUser().getUid()).child(sessionId).child(VALUE_KEY).setValue(true);
    }

    /**
     * Removes the session with the given ID from the user's schedule
     *
     * @param sessionId ID of the session to be removed
     */
    public void removeFromUserSchedule(String sessionId) {
        mScheduleRepository.removeSession(sessionId);
        mDataSourceListener.onUserScheduleUpdate(getUserSchedule());
        attemptRemovingSessionFromFirebase(sessionId);
    }

    private void attemptRemovingSessionFromFirebase(String sessionId) {
        //We can't sync to Firebase if we aren't logged in
        if (!isSignedIn()) {
            checkIfShouldPromptUser();
            return;
        }

        //Add the session to the user's schedule in Firebase
        mFirebaseDatabaseReference.child(DEVFEST_2017_KEY).child(AGENDAS_KEY)
                .child(mFirebaseAuth.getCurrentUser().getUid()).child(sessionId).removeValue();
    }

    /**
     * Prompts the user if they've tried to modify their schedule enough times without syncing
     */
    private void checkIfShouldPromptUser() {
        if (timesSinceLoginPrompted < CHANGES_ALLOWED_BEFORE_LOGIN_PROMPT) {
            timesSinceLoginPrompted++;
        } else {
            if (mLoginPromptListener != null) {
                mLoginPromptListener.promptUserToLogin();
            }
            timesSinceLoginPrompted = 0;
        }
    }

    public boolean isSignedIn() {
        return mFirebaseAuth.getCurrentUser() != null;
    }

    /**
     * Checks if a given session is in the user's schedule
     *
     * @param sessionId ID of the session to check for inclusion in the list
     * @return true if the session is in the user's schedule; otherwise false
     */
    public boolean isInUserSchedule(String sessionId) {
        return mScheduleRepository.isInSchedule(sessionId);
    }

    public void setDataSourceListener(DataSourceListener listener) {
        mDataSourceListener = listener;
    }

    public void setUserScheduleListener(UserScheduleListener listener) {
        mUserScheduleListener = listener;
    }

    public void setSessionFeedback(String sessionId, Feedback feedback) {
        if (!isSignedIn()) {
            return;
        }

        mFirebaseDatabaseReference.child(DEVFEST_2017_KEY)
                .child(FEEDBACK_CHILD_KEY)
                .child(mFirebaseAuth.getCurrentUser().getUid())
                .child(sessionId)
                .setValue(feedback);
    }

    public void addSessionFeedbackValueListener(ValueEventListener listener, String sessionId) {
        if (!isSignedIn()) {
            return;
        }

        mFirebaseDatabaseReference.child(DEVFEST_2017_KEY)
                .child(FEEDBACK_CHILD_KEY)
                .child(mFirebaseAuth.getCurrentUser().getUid())
                .child(sessionId)
                .addValueEventListener(listener);
    }

    public void removeSessionFeedbackValueListener(ValueEventListener listener, String sessionId) {
        if (!isSignedIn()) {
            return;
        }

        mFirebaseDatabaseReference.child(DEVFEST_2017_KEY)
                .child(FEEDBACK_CHILD_KEY)
                .child(mFirebaseAuth.getCurrentUser().getUid())
                .child(sessionId)
                .removeEventListener(listener);
    }

    //TODO de-duplicate diff methods
    public DiffUtil.DiffResult calculateSessionDiff(final List<Session> oldList, List<Session> newList) {
        return DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return oldList.size();
            }

            @Override
            public int getNewListSize() {
                return newList.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                return oldList.get(oldItemPosition).getId().equals(newList.get(newItemPosition).getId());
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
            }
        });
    }

    public DiffUtil.DiffResult calculateScheduleDiff(final List<Session> sessions, List<Session> oldSchedule, List<Session> newSchedule) {
        return DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return sessions.size();
            }

            @Override
            public int getNewListSize() {
                return sessions.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                return oldItemPosition == newItemPosition;
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                Session session = sessions.get(oldItemPosition);
                return oldSchedule.contains(session) == newSchedule.contains(session);
            }
        });
    }

    public DiffUtil.DiffResult calculateSpeakerDiff(final List<Speaker> oldList, List<Speaker> newList) {
        return DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return oldList.size();
            }

            @Override
            public int getNewListSize() {
                return newList.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
            }
        });
    }

    public void setGoogleAccount(GoogleSignInAccount googleAccount) {
        //If we are removing the Google account, stop listening
        if (googleAccount == null) {
            if (mFirebaseUserScheduleListener != null && isSignedIn()) {
                mFirebaseDatabaseReference.child(DEVFEST_2017_KEY).child(AGENDAS_KEY)
                        .child(mFirebaseAuth.getCurrentUser().getUid())
                        .removeEventListener(mFirebaseUserScheduleListener);
            }
            return;
        }

        if (googleAccount.getId() == null) {
            throw new IllegalArgumentException("#setGoogleAccount() called without ID. googleAccount = " + googleAccount.toString());
        }

        //Store the user details and authenticate with firebase
        storeUserDetails(googleAccount);
        storeAuthInFirebase(googleAccount);
    }

    public void storeUserDetails(GoogleSignInAccount googleAccount) {
        mUserDetailsRepository.setUserEmail(googleAccount.getEmail());
        mUserDetailsRepository.setUserName(googleAccount.getDisplayName());
        mUserDetailsRepository.setPhotoUri(googleAccount.getPhotoUrl());
    }

    public void clearUserDetails() {
        mUserDetailsRepository.clearUserDetails();
    }

    public UserDetailsRepository getUserDetailsRepository() {
        return mUserDetailsRepository;
    }

    private void storeAuthInFirebase(GoogleSignInAccount account) {
        AuthCredential authCredential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(authCredential)
                .addOnFailureListener(e -> Timber.d(e, "FirebaseAuth login failed"))
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Timber.d("FirebaseAuth login successfully completed");
                        //Be sure that any session we have locally is added to the user's schedule
                        storeLocalAgendaToFirebase();
                        addUserScheduleListener();
                    } else {
                        Timber.d("FirebaseAuth login failed");
                    }
                });
    }

    private void storeLocalAgendaToFirebase() {
        for (String sessionId : mScheduleRepository.getScheduleIds()) {
            attemptAddingSessionToFirebase(sessionId);
        }
    }

    private void addUserScheduleListener() {
        mFirebaseUserScheduleListener = mFirebaseDatabaseReference.child(DEVFEST_2017_KEY).child(AGENDAS_KEY)
                .child(mFirebaseAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Gather all of the session IDs from the user's schedule
                        ArrayList<String> scheduleIds = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Timber.d("User schedule snapshot is: %s", snapshot.toString());
                            String id = snapshot.getKey();
                            scheduleIds.add(id);
                        }
                        //Update the schedule IDs and send the new user schedule to the listener
                        mScheduleRepository.setScheduleIdStringSet(scheduleIds);
                        if (mUserScheduleListener != null) {
                            mUserScheduleListener.onScheduleUpdate(getUserSchedule());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Timber.e(databaseError.toException(), "Failed to read user agenda value.");
                    }
                });
    }

    public void setLoginPromptListener(LoginPromptListener listener) {
        mLoginPromptListener = listener;
    }

    /**
     * Listener that will be updated when the user's schedule is updated
     */
    public interface UserScheduleListener {
        void onScheduleUpdate(List<Session> schedule);
    }

    /**
     * Listener that can remind the user to login
     */
    public interface LoginPromptListener {
        void promptUserToLogin();
    }

    //TODO break this into separate listeners

    /**
     * Listener for updates from the data source
     */
    public interface DataSourceListener {
        //These methods are for updating the listener
        void onSessionsUpdate(List<Session> sessions);

        void onSpeakersUpdate(List<Speaker> speakers);

        void onUserScheduleUpdate(List<Session> userSchedule);
    }
}
