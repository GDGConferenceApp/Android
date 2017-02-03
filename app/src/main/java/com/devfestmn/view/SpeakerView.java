package com.devfestmn.view;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.devfestmn.R;
import com.devfestmn.api.model.Speaker;
import com.devfestmn.speakers.SpeakerImageTransformation;
import timber.log.Timber;

/**
 * Custom view that displays information about a speaker
 *
 * @author pfuentes
 */
public class SpeakerView extends LinearLayout {

    @Bind(R.id.speaker_view_image)
    ImageView mProfileImageview;
    @Bind(R.id.speaker_view_name)
    TextView mNameTextview;
    @Bind(R.id.speaker_view_company)
    TextView mCompanyTextview;
    @Bind(R.id.speaker_view_bio)
    TextView mBioTextview;
    @Bind(R.id.twitter_button)
    ImageView mTwitterButton;
    @Bind(R.id.website_button)
    ImageView mWebsiteButton;

    Context mContext;
    Speaker mSpeaker;

    public SpeakerView(Context context) {
        super(context);
        init(context);
    }

    public SpeakerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SpeakerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.view_speaker, this);
        ButterKnife.bind(this, view);
    }

    /**
     * Bind to a speaker
     * @param speaker The speaker
     * @param useThumbail True to use a thumbail image for the speaker, false to load the full size image
     */
    public void setSpeaker(Speaker speaker, boolean useThumbail) {
        mSpeaker = speaker;
        updateText();

        if (useThumbail) {
            loadThumbnail();
        } else {
            loadFullSizeImage(true);
        }
    }

    /**
     * Updates the imageview and textviews using the currently set Speaker object.
     * TODO maybe replace this method with data binding
     */
    private void updateText() {
        mNameTextview.setText(mSpeaker.getName());
        mBioTextview.setText(Html.fromHtml(mSpeaker.getBio()));
        if (mSpeaker.getCompany() == null || mSpeaker.getCompany().isEmpty()) {
            mCompanyTextview.setVisibility(GONE);
        } else {
            mCompanyTextview.setText(mSpeaker.getCompany());
        }
        if (mSpeaker.getTwitter() == null || mSpeaker.getTwitter().isEmpty()) {
            mTwitterButton.setVisibility(GONE);
        }
        if (mSpeaker.getWebsite() == null || mSpeaker.getWebsite().isEmpty()) {
            mWebsiteButton.setVisibility(GONE);
        }
    }

    /**
     * Load a thumbnail sized image for the speaker.
     *
     * This is useful when animating in via a transition, where the thumbnail is likely already loaded from
     * the speaker list
     */
    public void loadThumbnail() {
        Timber.d("speaker image URL = " + mSpeaker.getImageUrl());
        Picasso.with(mContext)
                .load(mSpeaker.getImageUrl())
                .noFade()
                .transform(new SpeakerImageTransformation())
                .into(mProfileImageview);
    }

    /**
     * Load the full size speaker image
     *
     * @param fadeAndPlaceholder True to fade in and use a placeholder
     */
    public void loadFullSizeImage(boolean fadeAndPlaceholder) {
        RequestCreator request = Picasso.with(mContext)
                .load(mSpeaker.getImageUrl())
                .transform(new SpeakerImageTransformation());

        if (fadeAndPlaceholder) {
            request = request.placeholder(R.drawable.ic_account_circle_white_48dp);
        } else {
            request = request.noPlaceholder().noFade();
        }

        request.into(mProfileImageview);
    }

    @OnClick(R.id.website_button)
    void onWebsiteClicked() {
        Uri website = Uri.parse(mSpeaker.getWebsite());

        if (website.getScheme() == null) {
            // Some people (*coughdanlewcough*) didn't include a scheme, and our backend isn't
            // cleaning these URLs
            website = website.buildUpon().scheme("http").build();
        }

        Intent webIntent = new Intent(Intent.ACTION_VIEW);
        webIntent.setData(website);
        mContext.startActivity(webIntent);
    }

    @OnClick(R.id.twitter_button)
    void onTwitterClicked() {
        Uri twitterUri = Uri.parse("https://twitter.com/#!/" + mSpeaker.getTwitter());

        Intent webIntent = new Intent(Intent.ACTION_VIEW);
        webIntent.setData(twitterUri);
        mContext.startActivity(webIntent);
    }

}
