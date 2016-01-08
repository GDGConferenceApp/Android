package mn.devfest.map;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import mn.devfest.R;

/**
 * Fragment that displays an event map
 *
 * @author bherbst
 */
public class MapFragment extends Fragment implements OnMapReadyCallback, View.OnClickListener {
    //TODO extract constants to an easy to update file
    private static final double CONFERENCE_CENTER_SOUTHWEST_CORNER_LAT = 44.973986;
    private static final double CONFERENCE_CENTER_SOUTHWEST_CORNER_LONG = -93.278120;
    private static final double CONFERENCE_CENTER_NORTHEAST_CORNER_LAT = 44.974714;
    private static final double CONFERENCE_CENTER_NORTHEAST_CORNER_LONG = -93.276806;
    private static final float CONFERENCE_CENTER_ZOOM_LEVEL = 18.5f;
    private static final int[] FLOOR_OVERLAY_ID_ARRAY = {R.drawable.schultze_level_one, R.drawable.schultze_level_two, R.drawable.schultze_level_three};


    @Bind(R.id.map_view)
    MapView mMapView;
    @Bind(R.id.floor_selector_layout)
    LinearLayout mFloorSelectorLayout;
    @Bind(R.id.map_recenter)
    ImageView mMapRecenterView;


    GoogleMap mMap;
    ArrayList<GroundOverlay> mFloorOverlayArray = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);
        for (int i = 0; i < FLOOR_OVERLAY_ID_ARRAY.length; i++) {
            Button button = new Button(getActivity());
            int floorNumber = i + 1;
            button.setText(String.format(getResources().getString(R.string.floor_selection_button_text), floorNumber));
            button.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            button.setTag(i);
            button.setOnClickListener(this);
            mFloorSelectorLayout.addView(button);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMapView != null) {
            mMapView.onDestroy();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //Move the camera to focus on the conference center
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(getConferenceCenterBounds().getCenter(), CONFERENCE_CENTER_ZOOM_LEVEL));
        //Add ground overlays
        for (int i = 0; i < FLOOR_OVERLAY_ID_ARRAY.length; i++) {
            GroundOverlayOptions floorOverlayOptions = new GroundOverlayOptions()
                    .image(BitmapDescriptorFactory.fromResource(FLOOR_OVERLAY_ID_ARRAY[i]))
                    .zIndex(i)
                    .visible((i == 0)) //Only the lowest floor is visible by default
                    .positionFromBounds(getConferenceCenterBounds());
            mFloorOverlayArray.add(mMap.addGroundOverlay(floorOverlayOptions));
        }
    }

    /**
     * Provides the Lat-Long bounds of the conference center
     *
     * @return The Lat-Long bounds of the conference center
     */
    private LatLngBounds getConferenceCenterBounds() {
        return new LatLngBounds(
                new LatLng(CONFERENCE_CENTER_SOUTHWEST_CORNER_LAT, CONFERENCE_CENTER_SOUTHWEST_CORNER_LONG),
                new LatLng(CONFERENCE_CENTER_NORTHEAST_CORNER_LAT, CONFERENCE_CENTER_NORTHEAST_CORNER_LONG));
    }

    /**
     * Resets the maps camera to it's original location
     */
    @OnClick(R.id.map_recenter)
    protected void onRecenterClicked() {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(getConferenceCenterBounds().getCenter(), CONFERENCE_CENTER_ZOOM_LEVEL));
    }

    @Override
    public void onClick(View v) {
        //Only address views in the floor selector layout
        if (v.getParent() == mFloorSelectorLayout) {
            for (int i = 0; i < FLOOR_OVERLAY_ID_ARRAY.length; i++) {
                GroundOverlay groundOverlay = mFloorOverlayArray.get(i);
                groundOverlay.setVisible((Integer)i == v.getTag());
            }
        }
    }
}
