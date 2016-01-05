package mn.devfest.map;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import butterknife.Bind;
import butterknife.ButterKnife;
import mn.devfest.R;

/**
 * Fragment that displays an event map
 *
 * @author bherbst
 */
public class MapFragment extends Fragment implements OnMapReadyCallback {
    //TODO extract constants to an easy to update file
    private static final double CONFERENCE_CENTER_SOUTHWEST_CORNER_LAT = 44.973986;
    private static final double CONFERENCE_CENTER_SOUTHWEST_CORNER_LONG = -93.278120;
    private static final double CONFERENCE_CENTER_NORTHEAST_CORNER_LAT = 44.974714;
    private static final double CONFERENCE_CENTER_NORTHEAST_CORNER_LONG = -93.276806;
    private static final float CONFERENCE_CENTER_ZOOM_LEVEL = 18;

    @Bind(R.id.map_view)
    MapView mMapView;
    //TODO add a re-center button
    //TODO add a widget to select floor

    GoogleMap mMap;

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
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mMapView != null){
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
        LatLngBounds CONFERENCE_CENTER =
                new LatLngBounds(
                        new LatLng(CONFERENCE_CENTER_SOUTHWEST_CORNER_LAT,CONFERENCE_CENTER_SOUTHWEST_CORNER_LONG),
                        new LatLng(CONFERENCE_CENTER_NORTHEAST_CORNER_LAT,CONFERENCE_CENTER_NORTHEAST_CORNER_LONG));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(CONFERENCE_CENTER.getCenter(), CONFERENCE_CENTER_ZOOM_LEVEL));
        //TODO set overlays
    }
}
