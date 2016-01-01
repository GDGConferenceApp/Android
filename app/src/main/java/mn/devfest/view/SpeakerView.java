package mn.devfest.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import mn.devfest.R;
import mn.devfest.api.model.Speaker;

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
    @Bind(R.id.twitter_heading)
    TextView mTwitterHeading;
    @Bind(R.id.speaker_twitter)
    TextView mTwitterTextview;
    @Bind(R.id.website_heading)
    TextView mWebsiteHeading;
    @Bind(R.id.speaker_website)
    TextView mWebsiteTextview;

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

    public void setSpeaker(Speaker speaker) {
        mSpeaker = speaker;
        updateTextAndImage();
    }

    /**
     * Updates the imageview and textviews using the currently set Speaker object.
     * TODO maybe replace this method with data binding
     */
    private void updateTextAndImage() {
        Picasso.with(mContext)
                .load(mSpeaker.getImage())
                .placeholder(R.drawable.ic_account_circle_white_48dp)
                .into(mProfileImageview);
        mNameTextview.setText(mSpeaker.getName());
        mBioTextview.setText(mSpeaker.getBio());
        if (mSpeaker.getCompany().isEmpty()) {
            mCompanyTextview.setVisibility(GONE);
        } else {
            mCompanyTextview.setText(mSpeaker.getCompany());
        }
        if (mSpeaker.getTwitter().isEmpty()) {
            mTwitterHeading.setVisibility(GONE);
            mTwitterTextview.setVisibility(GONE);
        } else {
            mTwitterTextview.setText(mSpeaker.getTwitter());
        }
        if (mSpeaker.getWebsite().isEmpty()) {
            mWebsiteHeading.setVisibility(GONE);
            mWebsiteTextview.setVisibility(GONE);
        } else {
            mWebsiteTextview.setText(mSpeaker.getWebsite());
        }
    }

}
