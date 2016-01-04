package mn.devfest.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import mn.devfest.R;

/**
 * Encapsulates feeddback fields in a single View
 * Displays a field label, a rating bar, and a description of what ratings mean for this field.
 *
 * @author bherbst
 */
public class NumberFeedbackField extends LinearLayout {

    @Bind(R.id.field_label) TextView mLabel;
    @Bind(R.id.rating_0_label) TextView mRating0Label;
    @Bind(R.id.rating_5_label) TextView mRating5Label;
    @Bind(R.id.rating_bar) NumberRatingBar mRatingBar;

    public NumberFeedbackField(Context context) {
        this(context, null);
    }

    public NumberFeedbackField(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NumberFeedbackField(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setOrientation(LinearLayout.VERTICAL);

        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.number_feedback_field, this);

        ButterKnife.bind(this);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.NumberFeedbackField, 0, 0);
        try {
            String fieldLabelText = a.getString(R.styleable.NumberFeedbackField_fieldLabel);
            String rating0LabelText = a.getString(R.styleable.NumberFeedbackField_rating0Label);
            String rating5LabelText = a.getString(R.styleable.NumberFeedbackField_rating5Label);

            mLabel.setText(fieldLabelText);
            mRating0Label.setText(rating0LabelText);
            mRating5Label.setText(rating5LabelText);
        } finally {
            a.recycle();
        }
    }

    public int getRating() {
        return mRatingBar.getProgress();
    }
}
