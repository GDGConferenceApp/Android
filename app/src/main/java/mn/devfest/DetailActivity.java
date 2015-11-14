package mn.devfest;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import mn.devfest.base.BaseActivity;

/**
 * Created by chris.black on 6/11/15.
 */
public class DetailActivity extends BaseActivity {

    private static final String EXTRA_IMAGE = "DetailActivity:image";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ImageView image = (ImageView) findViewById(R.id.image);
        ViewCompat.setTransitionName(image, EXTRA_IMAGE);
        image.setImageResource(getIntent().getIntExtra(EXTRA_IMAGE, R.drawable.ic_android_white_48dp));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                // Reverse the animation back to the previous view.
                if(Build.VERSION.SDK_INT >= 21) {
                    finishAfterTransition();
                    return true;
                }else {
                    finish();
                    return true;
                }
        }
        return super.onOptionsItemSelected(item);
    }

    public static void launch(AppCompatActivity activity, View transitionView) {
        ActivityOptionsCompat options =
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                        activity, transitionView, EXTRA_IMAGE);
        Intent intent = new Intent(activity, DetailActivity.class);
        intent.putExtra(EXTRA_IMAGE, R.drawable.ic_android_white_48dp);

        ActivityCompat.startActivity(activity, intent, options.toBundle());
    }
}
