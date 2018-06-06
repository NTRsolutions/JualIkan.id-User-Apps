package com.synergics.ishom.jualikanid_user;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.synergics.ishom.jualikanid_user.Controller.AppConfig;
import com.synergics.ishom.jualikanid_user.Controller.SQLiteHandler;
import com.synergics.ishom.jualikanid_user.Controller.Setting;
import com.synergics.ishom.jualikanid_user.Model.Retrofit.ApiClient;
import com.synergics.ishom.jualikanid_user.Model.Retrofit.ApiInterface;
import com.synergics.ishom.jualikanid_user.Model.Retrofit.Object.ResponseTrackingOrder;
import com.synergics.ishom.jualikanid_user.Model.TrackMaps.Direction;

import net.idik.lib.slimadapter.SlimAdapter;
import net.idik.lib.slimadapter.SlimInjector;
import net.idik.lib.slimadapter.viewinjector.IViewInjector;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrackingDeliveryActivity extends AppCompatActivity implements OnMapReadyCallback{

    private TextView txtTotalKeranjang1, txtAlamat, txtTotalKeranjang2,
            txtBiayaPengiriman, txtTotalBiaya, toolbarTitle;

    private TextView txtUserName, txtUserPhone;
    private ImageView imgUser;
    private LinearLayout btnMessage, btnCall;

    private String call, message;

    private Bundle bundle;

    private GoogleMap mMap;
    private Setting setting;
    private ArrayList<LatLng> points = new ArrayList<>();
    private LatLngBounds.Builder bounds;
    private PolylineOptions lineOptions;

    private Marker markerDriver;

    //recycleview
    private SlimAdapter slimAdapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager manager;
    private ArrayList<Object> items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking_delivery);

        bounds = new LatLngBounds.Builder();

        setting = new Setting(this);
        setting.forceEnableGPSAcess();
        setting.getLocation();
        bundle = getIntent().getExtras();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        setToolbar();
    }

    private void setStatusColor(int status, TextView textView){
        if (status == 0){
            textView.setTextColor(getApplicationContext().getResources().getColor(R.color.orange));
        }else if (status == 5){
            textView.setTextColor(getApplicationContext().getResources().getColor(R.color.red));
        }else if (status == 3){
            textView.setTextColor(getApplicationContext().getResources().getColor(R.color.green));
        }else {
            textView.setTextColor(getApplicationContext().getResources().getColor(R.color.blueFont));
        }
    }

    private void getDataFromServer() {
        String id = bundle.getString("order_id");

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading....");
        progressDialog.show();

        ApiInterface apiInterface = ApiClient.jualikanService().create(ApiInterface.class);

        RequestBody reId = RequestBody.create(MediaType.parse("text/plain"), id);

        Call call = apiInterface.trackingOrder(reId);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {

                if (response.isSuccessful()){
                    ResponseTrackingOrder orderDetail = (ResponseTrackingOrder) response.body();
                    ResponseTrackingOrder.OrderDetail order = orderDetail.data;

                    //mengatur bagian keranjang
                    for (ResponseTrackingOrder.CartItem data : order.cart.items){
                        items.add(data);
                    }
                    txtTotalKeranjang1.setText("Rp. " + money(order.cart.total));

                    //mengatur alamat pengiriman
                    txtAlamat.setText(order.orderLocation.address);

                    //mengatur total semuanya
                    txtTotalBiaya.setText("Rp. " + money(order.payment.total));
                    txtTotalKeranjang2.setText("Rp. " + money(order.payment.cart));
                    txtBiayaPengiriman.setText("Rp. " + money(order.payment.delivery));

                    displayDataDriver(orderDetail.data.driver);
                    displayRoute(orderDetail.data.orderLocation, orderDetail.data.koperasi, orderDetail.data.driver);

                }else {
                    Toast.makeText(getApplicationContext(), "Gagal konerksi ke server!", Toast.LENGTH_SHORT).show();
                }
                progressDialog.hide();
                slimAdapter.updateData(items);
                slimAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call call, Throwable t) {

            }
        });

    }

    private void displayRoute(ResponseTrackingOrder.OrderLocation orderLocation, ResponseTrackingOrder.Koperasi koperasi, ResponseTrackingOrder.Driver driver) {
        //mengambar marker order
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(Double.parseDouble(koperasi.lat), Double.parseDouble(koperasi.lng)))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_company)));
        bounds.include(new LatLng(Double.parseDouble(koperasi.lat), Double.parseDouble(koperasi.lng)));
        //mengambar marker pesanan
        drawMarkerOrder(orderLocation);

        String origin = koperasi.lat + "," + koperasi.lng;
        String destionation = orderLocation.lat + "," + orderLocation.lng;
        trackingDirection(origin, destionation, "", "DRIVING");

        trackDriver(driver);
    }

    private void trackingDirection(String origin, String destionation, String waypoint, String mode) {

        final ProgressDialog pDialog = new ProgressDialog(this);
        // Showing progress dialog before Amaking http request
        pDialog.setMessage("Loading...");
        pDialog.show();

        ApiInterface apiInterface = ApiClient.mapsApi().create(ApiInterface.class);
        Call call = apiInterface.getDirection(origin, destionation, false, mode);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {

                //mengambil data tracking
                if (response.isSuccessful()) {
                    Direction direction = (Direction) response.body();
                    List<Direction.Route> routes = direction.routes;
                    for (Direction.Route route : routes) {

                        List<Direction.Legs> legs = route.legs;

                        for (Direction.Legs leg : legs) {

                            //mengambil steps
                            List<Direction.Steps> steps = leg.steps;

                            for (Direction.Steps step : steps) {

                                List poly = decodePoly(step.polyLine.points);

                                lineOptions = new PolylineOptions();

                                for (int l = 0; l < poly.size(); l++) {
                                    points.add(new LatLng(((LatLng) poly.get(l)).latitude, ((LatLng) poly.get(l)).longitude));
                                    bounds.include(points.get(l));
                                }

                                lineOptions.addAll(points);
                                lineOptions.width(8);
                                lineOptions.color(Color.BLUE);
                                lineOptions.geodesic(true);

                            }
                        }
                    }
                }

                //selesai mengambil data tracking
                mMap.addPolyline(lineOptions);
                LatLngBounds latLngBounds = bounds.build();
                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 100));

                pDialog.hide();
            }

            @Override
            public void onFailure(Call call, Throwable t) {

            }
        });
    }

    private List decodePoly(String encoded) {

        List poly = new ArrayList();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }

    private void trackDriver(ResponseTrackingOrder.Driver driver) {
        DatabaseReference firebaseDb = FirebaseDatabase.getInstance().getReference();
        firebaseDb.child("Tracking").child(driver.device_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Location location = new Location("Driver Location");
                location.setLatitude((Double) dataSnapshot.child("latitude").getValue());
                location.setLongitude((Double) dataSnapshot.child("longitude").getValue());
                location.setBearing(Float.parseFloat(dataSnapshot.child("bearing").getValue().toString()));
                if (markerDriver == null){
                    markerDriver = mMap.addMarker(new MarkerOptions()
                            .flat(true)
                            .icon(getDriverMarkerBitmap())
                            .anchor(0.5f, 0.5f)
                            .position(new LatLng(location.getLatitude(), location.getLongitude()))
                    );
                }else {
                    animateMarker(markerDriver, location);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private BitmapDescriptor getDriverMarkerBitmap() {
        View iconView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.item_marker_driver, null);
        iconView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        iconView.layout(0, 0, iconView.getMeasuredWidth(), iconView.getMeasuredHeight());
        iconView.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(iconView.getMeasuredWidth(), iconView.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = iconView.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        iconView.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void animateMarker(final Marker marker, final Location location) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final LatLng startLatLng = marker.getPosition();
        final double startRotation = marker.getRotation();
        final long duration = 500;

        final LinearInterpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed/duration);

                double lat = t * location.getLatitude() + (1-t) * startLatLng.latitude;
                double lng = t * location.getLongitude() + (1-t) * startLatLng.longitude;

                float rotation = (float) (t * location.getBearing() + (1 - t) * startRotation);

                marker.setPosition(new LatLng(lat, lng));
                marker.setRotation(rotation);

                if ( t < 1.0){
                    handler.postDelayed(this, 16);
                }
            }
        });

    }

    private Marker drawMarkerOrder(ResponseTrackingOrder.OrderLocation data) {
        SQLiteHandler db = new SQLiteHandler(getApplicationContext());

        View iconView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.item_marker, null);

        ImageView imageLokasi = iconView.findViewById(R.id.image);
        Picasso.with(iconView.getContext())
                .load(AppConfig.url + db.getUser().user_image)
                .resize(100, 100)
                .centerCrop()
                .into(imageLokasi);

        iconView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        iconView.layout(0, 0, iconView.getMeasuredWidth(), iconView.getMeasuredHeight());
        iconView.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(iconView.getMeasuredWidth(), iconView.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = iconView.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        iconView.draw(canvas);

        LatLng position = new LatLng(Double.parseDouble(data.lat), Double.parseDouble(data.lng));

        bounds.include(position);

        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(position)
                .icon(BitmapDescriptorFactory.fromBitmap(bitmap)));

        return marker;
    }

    private String statusChecker(int status){
        if (status == 0){
            return "Belum di Bayar";
        }else if (status == 1){
            return "Sedang di Konfirmasi";
        }else if (status == 5){
            return "Expired";
        }else if (status == 3){
            return "Finished";
        }else if (status == 2){
            return "Sedang di Korim";
        }
        else {
            return "";
        }
    }

    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbarTitle = findViewById(R.id.toolbarTittle);
        toolbarTitle.setText("Order JD-" + bundle.getString("order_id"));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private String money(int d) {
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);;
        formatter .applyPattern("#,###");
        return formatter.format(d);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        setting.forceEnableGPSAcess();
        setting.getLocation();

        insialisasiWidget();
    }

    private void insialisasiWidget() {

        txtTotalKeranjang1 = findViewById(R.id.totalKeranjang);
        txtTotalKeranjang2 = findViewById(R.id.totalKeranjang2);
        txtAlamat = findViewById(R.id.txtAlamat);
        txtBiayaPengiriman = findViewById(R.id.totalDelivery);
        txtTotalBiaya = findViewById(R.id.totalPembayaran);



        //inisialisasi recyclwview
        recyclerView = findViewById(R.id.recycle);
        manager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);

        slimAdapter = SlimAdapter.create()
                .register(R.layout.layout_pembayaran_item, new SlimInjector<ResponseTrackingOrder.CartItem>() {
                    @Override
                    public void onInject(final ResponseTrackingOrder.CartItem data, IViewInjector injector) {
                        injector.text(R.id.nama, data.name + " (" + data.qty + "Kg)")
                                .text(R.id.harga, "Rp. " + money(data.price));
                    }
                })
                .attachTo(recyclerView);

        getDataFromServer();
    }

    private void displayDataDriver(final ResponseTrackingOrder.Driver driver){
        txtUserName = findViewById(R.id.userName);
        txtUserPhone = findViewById(R.id.userPhone);

        imgUser = findViewById(R.id.userImage);

        txtUserName.setText(driver.name);
        txtUserPhone.setText(driver.phone);

        Picasso.with(getApplicationContext())
                .load(AppConfig.url + driver.image)
                .into(imgUser);

        btnMessage = findViewById(R.id.btnMessage);
        btnCall = findViewById(R.id.btnCall);

        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + driver.phone));
                startActivity(intent);
            }
        });

        btnMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.fromParts("sms", driver.phone, null));
                startActivity(intent);
            }
        });
    }

}
