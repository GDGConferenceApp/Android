package com.devfestmn.speakers;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
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
import com.devfestmn.R;
import com.devfestmn.api.model.Speaker;

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

                // Create the shared element map


                // For simplicity's sake, just assume our context is an AppCompatActivity. This should
                // always be true. At a later point, we should clean this up to not make this assumption
                AppCompatActivity activity = (AppCompatActivity) context;
                Bundle transitionBundle = ActivityOptionsCompat
                        .makeSceneTransitionAnimation(activity,
                                Pair.create(speakerImage, "speakerImage"),
                                Pair.create(mNameTextView, "speakerName"),
                                Pair.create(mCompanyTextView, "speakerCompany"))
                        .toBundle();

                context.startActivity(speakerDetails, transitionBundle);
            });
        }

        public void bind(Speaker speaker) {
            this.speaker = speaker;

            // Set up transition names for content transitions
            ViewCompat.setTransitionName(speakerImage, String.format("speakerImage_%s", speaker.getId()));
            ViewCompat.setTransitionName(mNameTextView, String.format("speakerName_%s", speaker.getId()));
            ViewCompat.setTransitionName(mCompanyTextView, String.format("speakerCompany_%s", speaker.getId()));

            mNameTextView.setText(speaker.getName());

            //Update company
            if (speaker.getCompany() == null || speaker.getCompany().isEmpty()) {
                mCompanyTextView.setVisibility(View.GONE);
            } else {
                mCompanyTextView.setText(speaker.getCompany());
            }

            int pictureSizePx = speakerImage.getResources().getDimensionPixelSize(R.dimen.speaker_list_image_size);

            //Load the image if there is one
            if (speaker.getImageUrl() != null && !speaker.getImageUrl().isEmpty()) {
                Picasso.with(speakerImage.getContext())
                        .load(speaker.getImageUrl())
                        .transform(new SpeakerImageTransformation())
                        .placeholder(R.drawable.ic_account_circle_white_48dp)
                        .into(speakerImage);
            } else {
                Drawable placeholder = speakerImage.getResources()
                        .getDrawable(R.drawable.ic_account_circle_white_48dp, null);
                speakerImage.setImageDrawable(placeholder);
            }
        }
    }
}
