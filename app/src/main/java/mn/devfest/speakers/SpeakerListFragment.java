package mn.devfest.speakers;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import mn.devfest.R;
import mn.devfest.api.model.Speaker;
import mn.devfest.view.decoration.DividerItemDecoration;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Fragment that displays the list of speakers
 *
 * @author bherbst
 * @author pfuentes
 */
public class SpeakerListFragment extends Fragment {

    @Bind(R.id.speaker_list_recyclerview)
    RecyclerView mSpeakerRecyclerView;

    @Bind(R.id.loading_progress)
    ProgressBar mLoadingView;

    private SpeakerListAdapter mAdapter;

    private List<Speaker> mSpeakerData = new ArrayList<>();
    private Subscription mDataUpdateSubscription;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_speaker_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        getActivity().setTitle(getResources().getString(R.string.speaker_title));

        mAdapter = new SpeakerListAdapter();
        mAdapter.setSpeakers(mSpeakerData);
        mSpeakerRecyclerView.setAdapter(mAdapter);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mSpeakerRecyclerView.setLayoutManager(mLinearLayoutManager);
        mSpeakerRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mDataUpdateSubscription != null) {
            mDataUpdateSubscription.unsubscribe();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        ButterKnife.unbind(this);
        //TODO cleanup resources
    }

    /**
     * Notify this fragment that we have a new list of speakers for this conference
     *
     * @param speakers the new speakers
     */
    public void setSpeakers(List<Speaker> speakers) {
        mDataUpdateSubscription = Observable.from(speakers)
                .toSortedList((speaker1, speaker2) -> {
                    return speaker1.getName().compareTo(speaker2.getName());
                })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::updateDisplayedSpeakers);
    }

    /**
     * Update the list of which speakers are displayed
     * @param speakers The speakers to display
     */
    private void updateDisplayedSpeakers(List<Speaker> speakers) {
        mAdapter.setSpeakers(speakers);
        mAdapter.notifyDataSetChanged();
    }
}
