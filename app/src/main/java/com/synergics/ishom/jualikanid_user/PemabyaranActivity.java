package com.synergics.ishom.jualikanid_user;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.synergics.ishom.jualikanid_user.Controller.GMapsTrack.GMapsDirectionResponse;
import com.synergics.ishom.jualikanid_user.Controller.GMapsTrack.GMapsTracking;
import com.synergics.ishom.jualikanid_user.Controller.SQLiteHandler;
import com.synergics.ishom.jualikanid_user.Controller.Setting;
import com.synergics.ishom.jualikanid_user.Model.Retrofit.ApiClient;
import com.synergics.ishom.jualikanid_user.Model.Retrofit.ApiInterface;
import com.synergics.ishom.jualikanid_user.Model.Retrofit.Object.ResponseKeranjang;
import com.synergics.ishom.jualikanid_user.Model.Retrofit.Object.ResponsePembayaran;

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

public class PemabyaranActivity extends AppCompatActivity {

    private TextView toolbarTitle;

    //recycleview
    private SlimAdapter slimAdapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager manager;
    private ArrayList<Object> items = new ArrayList<>();

    //recycleview
    private SlimAdapter slimAdapter2;
    private RecyclerView recyclerView2;
    private LinearLayoutManager manager2;
    private ArrayList<Object> items2 = new ArrayList<>();

    //data variabel
    private TextView totalKeranjang, btnUbahAlamat, txtAlamat, totalKeranjang2, totalDelivery, totalPembayaran, txtSaldo;
    private String namaKoperasi, payment1, payment2, payment3;
    private Double latKoperasi = 0d, lngKoperasi = 0d, distance;
    private int biayaPerKm, biayaTotalKeranjang, biayaPengiriman;
    private LatLng origin, destination;
    private RadioButton radioCash, radioTrasfer, radioSaldo;

    //data yang akan di post
    private String postIdKeranjang, postIdUser, postAlamat, postLatAlamat, postLngAlamat, postPaymentType,
            postIdKoperasi, postWaktuPengiriman, postJarakPengiriman, postBiayaPengiriman, postBeratOrder,
            postTotalPembayaran, postIdWaktuPengiriman, postOrderStatus;

    private Setting setting;
    private GMapsTracking gMapsTracking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pemabyaran);

        setting = new Setting(this);
        gMapsTracking = new GMapsTracking(PemabyaranActivity.this);
        setting.forceEnableGPSAcess();
        setToolbar();

        totalKeranjang = findViewById(R.id.totalKeranjang);
        totalKeranjang2 = findViewById(R.id.totalKeranjang2);
        txtAlamat = findViewById(R.id.txtAlamat);
        txtSaldo = findViewById(R.id.saldo);
        totalDelivery = findViewById(R.id.totalDelivery);
        totalPembayaran = findViewById(R.id.totalPembayaran);
        btnUbahAlamat = findViewById(R.id.btnUbahAlamat);

        radioCash = findViewById(R.id.radioCash);
        radioTrasfer = findViewById(R.id.radioTransfer);
        radioSaldo = findViewById(R.id.radioSaldo);

        serRecycleview();

        setRadioButton();

        setRadioButtonChecked();
    }

    private void setRadioButtonChecked() {
        radioCash.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                radioCash.setChecked(b);
                setRadioButton();
            }
        });

        radioSaldo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                radioSaldo.setChecked(b);
                setRadioButton();
            }
        });

        radioTrasfer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                radioTrasfer.setChecked(b);
                setRadioButton();
            }
        });
    }

    private void setRadioButton() {
        if (radioCash.isChecked()){
            radioTrasfer.setChecked(false);
            radioSaldo.setChecked(false);
        }else if (radioTrasfer.isChecked()){
            radioCash.setChecked(false);
            radioSaldo.setChecked(false);
        }else if (radioSaldo.isChecked()){
            radioCash.setChecked(false);
            radioTrasfer.setChecked(false);
        }
    }

    private void serRecycleview() {

        /////////////////////////////////////// IKAN KERANJANG ///////////////////////////////////////

        //inisialisasi recyclwview
        recyclerView = findViewById(R.id.recycle);
        manager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);

        slimAdapter = SlimAdapter.create()
                .register(R.layout.layout_pembayaran_item, new SlimInjector<ResponsePembayaran.Items>() {
                    @Override
                    public void onInject(final ResponsePembayaran.Items data, IViewInjector injector) {
                        injector.text(R.id.nama, data.name)
                                .text(R.id.harga, "Rp. " + money(data.price));
                    }
                })
                .attachTo(recyclerView);

        ////////////////////////////////////// WAKTU PENGIRIMAN //////////////////////////////////////

        //inisialisasi recyclwview
        recyclerView2 = findViewById(R.id.recycleWaktu);
        manager2 = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView2.setLayoutManager(manager2);

        slimAdapter2 = SlimAdapter.create()
                .register(R.layout.layout_cart_item, new SlimInjector<ResponseKeranjang.Keranjang>() {
                    @Override
                    public void onInject(final ResponseKeranjang.Keranjang data, IViewInjector injector) {
                        injector.with(R.id.item, new IViewInjector.Action() {
                            @Override
                            public void action(View view) {

                            }
                        });
                    }
                })
                .attachTo(recyclerView2);

        getItemFromServer();
    }

    private void countDistance(){
        biayaPengiriman = (int) (distance/1000 * biayaPerKm);
        totalDelivery.setText("Rp. " + money(biayaPengiriman));
        totalPembayaran.setText("Rp. " + money(biayaPengiriman + biayaTotalKeranjang));
    }

    private void countDistance(LatLng origin, LatLng destionation, String mode){

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Loading...");
        dialog.show();

        String originText = origin.latitude + "," + origin.longitude;
        String destinationText = destionation.latitude + "," + destionation.longitude;

        ApiInterface apiInterface = ApiClient.mapsApi().create(ApiInterface.class);
        Call call = apiInterface.getDirection(originText, destinationText, true, mode);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful()){

                    GMapsDirectionResponse directionResponse = (GMapsDirectionResponse) response.body();
                    List<GMapsDirectionResponse.Route> routes = directionResponse.routes;

                    for (GMapsDirectionResponse.Route route : routes){

                        List<GMapsDirectionResponse.Legs> legses = route.legs;

                        for (GMapsDirectionResponse.Legs legs : legses){

                            distance = legs.distance.distanceValue;
                            Log.i("Distance " , distance + "");

                        }

                    }
                }
                dialog.hide();
                countDistance();
            }

            @Override
            public void onFailure(Call call, Throwable t) {

            }
        });
    }

    private void getItemFromServer() {

        SQLiteHandler db = new SQLiteHandler(getApplicationContext());
        String id = db.getUser().user_id;

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading....");
        progressDialog.show();

        ApiInterface apiInterface = ApiClient.jualikanService().create(ApiInterface.class);

        RequestBody reId = RequestBody.create(MediaType.parse("text/plain"), id);

        Call call = apiInterface.payment_detail(reId);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                Toast.makeText(PemabyaranActivity.this, response.message(), Toast.LENGTH_SHORT).show();

                if (response.isSuccessful()){
                    ResponsePembayaran res = (ResponsePembayaran) response.body();

                    if (res.status){

                        biayaPerKm = res.delviery_cost_pkm;
                        biayaTotalKeranjang = res.cart.total;
                        totalKeranjang.setText("Rp. " + money(res.cart.total));
                        totalKeranjang2.setText("Rp. " + money(res.cart.total));
                        latKoperasi = Double.valueOf(res.cart.koperasi.lat);
                        lngKoperasi = Double.valueOf(res.cart.koperasi.lng);

                        for (ResponsePembayaran.Items item : res.cart.items){
                            items.add(item);
                        }

                        for (ResponsePembayaran.DeliveryTime time : res.deliveryTimes){
                            items2.add(time);
                        }

                        payment1 = res.paymentTypes.get(0).payment_type_id;
                        payment2 = res.paymentTypes.get(1).payment_type_id;
                        payment3 = res.paymentTypes.get(2).payment_type_id;

                    }else {
                        Toast.makeText(PemabyaranActivity.this, res.message, Toast.LENGTH_SHORT).show();
                    }

                }else {
                    Toast.makeText(getApplicationContext(), "Gagal konerksi ke server!", Toast.LENGTH_SHORT).show();
                }

                progressDialog.hide();
                slimAdapter.updateData(items);
                slimAdapter.notifyDataSetChanged();

                destination = new LatLng(setting.getLatitude(), setting.getLongitude());
                origin = new LatLng(latKoperasi, lngKoperasi);
                countDistance(origin, destination, "driving");
            }

            @Override
            public void onFailure(Call call, Throwable t) {

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

        toolbarTitle = findViewById(R.id.toolbarTittle);
        toolbarTitle.setText("Detail Pembayaran");
    }

    private String money(int d) {
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);;
        formatter .applyPattern("#,###");
        return formatter.format(d);
    }
}
