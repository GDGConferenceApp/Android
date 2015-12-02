package mn.devfest.speakers;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Used for displaying the list of speakers at the event
 *
 * @author pfuentes
 */
public class SpeakerListAdapter extends RecyclerView.Adapter<SpeakerListAdapter.SpeakerViewHolder> {

    ArrayList<Speaker> mSpeakers;

    @Override
    public SpeakerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //TODO implement
        return null;
    }

    @Override
    public void onBindViewHolder(SpeakerViewHolder holder, int position) {
        //TODO implement

    }

    @Override
    public int getItemCount() {
        return mSpeakers == null ? 0 : mSpeakers.size();
    }

    public ArrayList<Speaker> getSpeakers() {
        return mSpeakers;
    }

    public void setSpeakers(ArrayList<Speaker> speakers) {
        mSpeakers = speakers;
    }

    public class SpeakerViewHolder extends RecyclerView.ViewHolder{
        //TODO implement
        public SpeakerViewHolder(View itemView) {
            super(itemView);
        }
    }
}
