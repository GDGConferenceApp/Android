package mn.devfest.api;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mn.devfest.api.model.Conference;
import mn.devfest.api.model.Session;
import mn.devfest.api.model.Speaker;
import mn.devfest.persistence.UserScheduleRepository;
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

    public static final String DEVFEST_2017_KEY = "devfest2017";
    public static final String SESSIONS_CHILD_KEY = "schedule";
    public static final String SPEAKERS_CHILD_KEY = "speakers";

    private static DevFestDataSource mOurInstance;

    private UserScheduleRepository mScheduleRepository;
    private DatabaseReference mFirebaseDatabaseReference;

    private Conference mConference = new Conference();
    //TODO move to an array of listeners?
    private DataSourceListener mDataSourceListener;

    public static DevFestDataSource getInstance() {
        if (mOurInstance == null) {
            mOurInstance = new DevFestDataSource();
        }
        return mOurInstance;
    }

    public DevFestDataSource() {
        //TODO move all firebase access into a separate class and de-duplicate code
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
    }

    /**
     * Removes the session with the given ID from the user's schedule
     *
     * @param sessionId ID of the session to be removed
     */
    public void removeFromUserSchedule(String sessionId) {
        mScheduleRepository.removeSession(sessionId);
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

    private void onConferenceUpdated() {
        //Notify listener
        mDataSourceListener.onSessionsUpdate(getSessions());
        mDataSourceListener.onSpeakersUpdate(getSpeakers());
        mDataSourceListener.onUserScheduleUpdate(getUserSchedule());
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
