package mn.devfest.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * Custom view that displays information about a speaker
 *
 * @author pfuentes
 */
public class SpeakerView extends LinearLayout {

//    "name": "John Doe",
//            "id": "jdoe123",
//            "twitter": "jdoe",
//            "website": "www.jdoe.com",
//            "bio": "John Doe is a web developer",
//            "image": "www.jdoe.com/profile.jpg"


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
        //TODO implement
    }

}
