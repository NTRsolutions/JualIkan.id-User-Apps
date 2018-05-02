package com.synergics.ishom.jualikanid_user;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.synergics.ishom.jualikanid_user.Controller.Setting;

public class UbahAlamatActivity extends AppCompatActivity implements OnMapReadyCallback {

    private Toolbar toolbar;
    private GoogleMap mMap;
    private Button btnPickLocation;

    private Setting setting;
    private Double lat;
    private Double lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubah_alamat);

        btnPickLocation = (Button) findViewById(R.id.btnPickLocation);

        setting = new Setting(this);
        setting.forceEnableGPSAcess();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        setToolbar();

        btnPickLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickLocation();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView toolbarTitle = findViewById(R.id.toolbarTittle);
        toolbarTitle.setText("Ubah Alamat");
    }


    private void pickLocation() {
        btnPickLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;

                LatLng newPosition = mMap.getCameraPosition().target;
                lat = newPosition.latitude;
                lng = newPosition.longitude;

                intent = new Intent(UbahAlamatActivity.this, PemabyaranActivity.class);
                intent.putExtra("lat", lat);
                intent.putExtra("lng", lng);
                startActivity(intent);
                finish();
            }
        });
    }

    private void postLokasikeDatabase(Double lat, Double lng) {
        //simpan lokasi ke database lalu intent ke user profile
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("position", 2);
        startActivity(intent);
        finish();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng latLng = new LatLng(setting.getLatitude(), setting.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15.0f));

        mMap.getUiSettings().setZoomControlsEnabled(true);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);

        LatLng newPosition = mMap.getCameraPosition().target;
        lat = newPosition.latitude;
        lng = newPosition.longitude;
    }
}
