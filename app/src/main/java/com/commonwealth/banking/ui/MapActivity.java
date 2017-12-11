package com.commonwealth.banking.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.commonwealth.banking.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static final String LATITUDE = "lat";
    public static final String LONGITUDE = "lng";
    private double mLat;
    private double mLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mLat = getIntent().getDoubleExtra(LATITUDE, 0);
        mLng = getIntent().getDoubleExtra(LONGITUDE, 0);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        final LatLng latLng = new LatLng(mLat, mLng);
        final BitmapDescriptor bdIcon = BitmapDescriptorFactory.fromResource(R.drawable.marker_atm_commbank);
        map.addMarker(new MarkerOptions().position(latLng).icon(bdIcon).title("Marker"));
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
    }
}
