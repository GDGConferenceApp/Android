package mn.devfest.speakers;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.transition.Transition;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.Bind;
import butterknife.ButterKnife;
import mn.devfest.R;
import mn.devfest.api.model.Speaker;
import mn.devfest.view.SpeakerView;
import timber.log.Timber;

import static mn.devfest.api.DevFestDataSource.DEVFEST_2017_KEY;
import static mn.devfest.api.DevFestDataSource.SPEAKERS_CHILD_KEY;

/**
 * Fragment that displays details for a particular session
 *
 * @author bherbst
 */
public class SpeakerDetailsFragment extends Fragment {
    private static final String ARG_SPEAKER_ID = "speakerId";

    @Bind(R.id.speaker)
    SpeakerView mSpeakerView;

    private Speaker mSpeaker;
    private DatabaseReference mFirebaseDatabaseReference;

    public static SpeakerDetailsFragment newInstance(String speakerId) {
        Bundle args = new Bundle();
        args.putString(ARG_SPEAKER_ID, speakerId);

        SpeakerDetailsFragment frag = new SpeakerDetailsFragment();
        frag.setArguments(args);

        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_speaker_details, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        Bundle args = getArguments();
        if (args != null && args.containsKey(ARG_SPEAKER_ID)) {
            String speakerId = args.getString(ARG_SPEAKER_ID);
            mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
            mFirebaseDatabaseReference.child(DEVFEST_2017_KEY).child(SPEAKERS_CHILD_KEY)
                    .child(speakerId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Timber.d(dataSnapshot.toString());
                    mSpeaker = dataSnapshot.getValue(Speaker.class);
                    transitionSpeaker();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Failed to read value
                    Log.w(this.getClass().getSimpleName(), "Failed to read speaker value.", databaseError.toException());
                }
            });
        } else {
            throw new IllegalStateException("SpeakerDetailsFragment requires a speaker ID passed via newInstance()");
        }
    }

    private void transitionSpeaker() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && addTransitionListener()) {
            // If we are transitioning in, use the already loaded thumbnail
            mSpeakerView.setSpeaker(mSpeaker, true);
        } else {
            // Otherwise do the normal Picasso loading of the full size image
            mSpeakerView.setSpeaker(mSpeaker, false);
        }

        getActivity().setTitle(getResources().getString(R.string.speaker_title));

        // Now the content exists, so we can start the transition
        getActivity().supportStartPostponedEnterTransition();
    }

    /**
     * Try and add a {@link Transition.TransitionListener} to the entering shared element
     * {@link Transition}. We do this so that we can load the full-size image after the transition
     * has completed.
     *
     * Borrowed from https://github.com/googlesamples/android-ActivitySceneTransitionBasic/blob/master/Application/src/main/java/com/example/android/activityscenetransitionbasic/DetailActivity.java
     *
     * @return true if we were successful in adding a listener to the enter transition
     */
    private boolean addTransitionListener() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            // Can't transition below lollipop
            return false;
        }

        final Transition transition = getActivity().getWindow().getSharedElementEnterTransition();

        if (transition != null) {
            // There is an entering shared element transition so add a listener to it
            transition.addListener(new Transition.TransitionListener() {
                @Override
                public void onTransitionEnd(Transition transition) {
                    // As the transition has ended, we can now load the full-size image
                    mSpeakerView.loadFullSizeImage(false);

                    // Make sure we remove ourselves as a listener
                    transition.removeListener(this);
                }

                @Override
                public void onTransitionStart(Transition transition) {
                    // No-op
                }

                @Override
                public void onTransitionCancel(Transition transition) {
                    // Make sure we remove ourselves as a listener
                    transition.removeListener(this);
                }

                @Override
                public void onTransitionPause(Transition transition) {
                    // No-op
                }

                @Override
                public void onTransitionResume(Transition transition) {
                    // No-op
                }
            });
            return true;
        }

        // If we reach here then we have not added a listener
        return false;
    }
}
