package mn.devfest.speakers;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import mn.devfest.R;
import mn.devfest.api.ProfilePictureApi;
import mn.devfest.api.model.Speaker;

/**
 * Used for displaying the list of speakers at the event
 *
 * @author pfuentes
 */
public class SpeakerListAdapter extends RecyclerView.Adapter<SpeakerListAdapter.SpeakerViewHolder> {

    List<Speaker> mSpeakers;

    @Override
    public SpeakerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_speaker, parent, false);
        return new SpeakerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SpeakerViewHolder holder, int position) {
        Speaker speaker = mSpeakers.get(position);
        if (speaker == null) {
            return;
        }
        holder.bind(speaker);
    }

    @Override
    public int getItemCount() {
        return mSpeakers == null ? 0 : mSpeakers.size();
    }

    public List<Speaker> getSpeakers() {
        return mSpeakers;
    }

    public void setSpeakers(List<Speaker> speakers) {
        mSpeakers = speakers;
    }

    public class SpeakerViewHolder extends RecyclerView.ViewHolder {
        private Speaker speaker;

        @Bind(R.id.speaker_image)
        ImageView speakerImage;

        @Bind(R.id.speaker_row_name)
        TextView mNameTextView;

        @Bind(R.id.speaker_row_company)
        TextView mCompanyTextView;

        public SpeakerViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(view -> {
                Context context = mNameTextView.getContext();
                Intent speakerDetails = new Intent(context, SpeakerDetailsActivity.class);
                speakerDetails.putExtra(SpeakerDetailsActivity.EXTRA_SPEAKER_ID, speaker.getId());
                context.startActivity(speakerDetails);
            });
        }

        public void bind(Speaker speaker) {
            this.speaker = speaker;

            mNameTextView.setText(speaker.getName());

            //Update company
            if (speaker.getCompany() == null || speaker.getCompany().isEmpty()) {
                mCompanyTextView.setVisibility(View.GONE);
            } else {
                mCompanyTextView.setText(speaker.getCompany());
            }

            int pictureSizePx = speakerImage.getResources().getDimensionPixelSize(R.dimen.speaker_list_image_size);
            String pictureUrl = ProfilePictureApi.getImageUrl(speaker, pictureSizePx);

            Picasso.with(speakerImage.getContext())
                    .load(pictureUrl)
                    .transform(new SpeakerImageTransformation())
                    .placeholder(R.drawable.ic_account_circle_white_48dp)
                    .into(speakerImage);
        }
    }
}
