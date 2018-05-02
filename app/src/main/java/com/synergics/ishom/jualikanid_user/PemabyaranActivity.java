package com.synergics.ishom.jualikanid_user;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonArray;
import com.synergics.ishom.jualikanid_user.Controller.GMapsTrack.GMapsAdress;
import com.synergics.ishom.jualikanid_user.Controller.GMapsTrack.GMapsDirectionResponse;
import com.synergics.ishom.jualikanid_user.Controller.GMapsTrack.GMapsTracking;
import com.synergics.ishom.jualikanid_user.Controller.SQLiteHandler;
import com.synergics.ishom.jualikanid_user.Controller.Setting;
import com.synergics.ishom.jualikanid_user.Model.Retrofit.ApiClient;
import com.synergics.ishom.jualikanid_user.Model.Retrofit.ApiInterface;
import com.synergics.ishom.jualikanid_user.Model.Retrofit.Object.ResponseMidtransSnap;
import com.synergics.ishom.jualikanid_user.Model.Retrofit.Object.ResponsePembayaran;
import com.synergics.ishom.jualikanid_user.Model.Retrofit.Object.ResponseRegister;
import com.synergics.ishom.jualikanid_user.View.Object.CustomerDetail;
import com.synergics.ishom.jualikanid_user.View.Object.DelivTime;
import com.synergics.ishom.jualikanid_user.View.Object.DetailTransaksi;
import com.synergics.ishom.jualikanid_user.View.Object.MidtransPayment;

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
    private Double latKoperasi = 0d, lngKoperasi = 0d, distance = 0d;
    private int biayaPerKm, biayaTotalKeranjang, biayaPengiriman;
    private LatLng origin, destination;
    private CheckBox radioCash, radioTrasfer, radioSaldo;

    //data yang akan di post
    private String postIdKeranjang = "", postIdUser = "", postAlamat = "", postLatAlamat = "", postLngAlamat = "", postPaymentType = "",
            postIdKoperasi = "", postWaktuPengiriman = "", postJarakPengiriman = "", postBiayaPengiriman = "", postBeratOrder = "",
            postTotalPembayaran = "", postIdWaktuPengiriman = "", postOrderStatus = "", postUrlPayment = "";

    private int totalBiaya = 0;
    private int beratKeranjang = 0;

    private Setting setting;
    private GMapsTracking gMapsTracking;
    private JsonArray keranjang;

    private Bundle bundle;
    private Button btnPemesanan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pemabyaran);

        keranjang = new JsonArray();
        bundle = getIntent().getExtras();

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

        btnPemesanan = findViewById(R.id.btnPemesanan);

        serRecycleview();
        setRadioButtonChecked();

        btnUbahAlamat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), UbahAlamatActivity.class);
                startActivity(intent);
            }
        });

        btnPemesanan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPostPemesanan();
            }
        });
    }

    private void checkPostPemesanan() {

        Toast.makeText(this, postIdKeranjang + "|" + postIdUser + "|" + postAlamat + "|" + postLatAlamat + "|" + postPaymentType + "|"
                + postIdKoperasi + "|" + postWaktuPengiriman + "|" + postJarakPengiriman + "|" + postBiayaPengiriman + "|" + postBeratOrder + "|" + postTotalPembayaran
                + "|" + postIdWaktuPengiriman + "|" + postOrderStatus, Toast.LENGTH_SHORT).show();

        if (postPaymentType.isEmpty()){
            Toast.makeText(this, "Pastikan anda telah memilih metode pembayaran", Toast.LENGTH_SHORT).show();
            return;
        }
        if (postIdWaktuPengiriman.isEmpty()){
            Toast.makeText(this, "Pastikan anda telah memilih waktu pengiriman", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!postIdKeranjang.isEmpty() && !postIdUser.isEmpty() && !postAlamat.isEmpty() && !postLatAlamat.isEmpty() && !postLngAlamat.isEmpty() && !postPaymentType.isEmpty() &&
                !postIdKoperasi.isEmpty() && !postWaktuPengiriman.isEmpty() && !postJarakPengiriman.isEmpty() && !postBiayaPengiriman.isEmpty() && !postBeratOrder.isEmpty() &&
                !postTotalPembayaran.isEmpty() && !postIdWaktuPengiriman.isEmpty() && !postOrderStatus.isEmpty()){
            if (postOrderStatus == "0"){
                fetchMidtransUrl();
            }else {
                postToDatabase();
            }
        }else {
            Toast.makeText(this, "Pastikan data yang anda masukan lengkap", Toast.LENGTH_SHORT).show();
        }
    }

    private void postToDatabase() {

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Loading...");
        dialog.show();

        RequestBody rePostKeranjang = RequestBody.create(MediaType.parse("text/plain"), postIdKeranjang);
        RequestBody rePostIdUser = RequestBody.create(MediaType.parse("text/plain"), postIdUser);
        RequestBody rePostAlamat = RequestBody.create(MediaType.parse("text/plain"), postAlamat);
        RequestBody rePostLatAlamat = RequestBody.create(MediaType.parse("text/plain"), postLatAlamat);
        RequestBody rePostLngAlamat = RequestBody.create(MediaType.parse("text/plain"), postLngAlamat);
        RequestBody rePostPayment = RequestBody.create(MediaType.parse("text/plain"), postPaymentType);
        RequestBody rePostIdKoperasi = RequestBody.create(MediaType.parse("text/plain"), postIdKoperasi);
        RequestBody rePostWaktuPengiriman = RequestBody.create(MediaType.parse("text/plain"), postWaktuPengiriman);
        RequestBody rePostJarakPengiriman = RequestBody.create(MediaType.parse("text/plain"), postJarakPengiriman);
        RequestBody rePostPaymentUrl = RequestBody.create(MediaType.parse("text/plain"), postUrlPayment);
        RequestBody rePostBiayaPengiriman = RequestBody.create(MediaType.parse("text/plain"), postBiayaPengiriman);
        RequestBody rePostBeratOrder = RequestBody.create(MediaType.parse("text/plain"), postBeratOrder);
        RequestBody rePostTotalPembayaran = RequestBody.create(MediaType.parse("text/plain"), postTotalPembayaran);
        RequestBody rePostWaktuIdpengiriman = RequestBody.create(MediaType.parse("text/plain"), postIdWaktuPengiriman);
        RequestBody rePostOrderstatus = RequestBody.create(MediaType.parse("text/plain"), postOrderStatus);

        ApiInterface apiInterface = ApiClient.jualikanService().create(ApiInterface.class);
        Call call = apiInterface.postOrder(rePostKeranjang, rePostIdUser, rePostAlamat, rePostLatAlamat, rePostLngAlamat, rePostPayment, rePostIdKoperasi, rePostWaktuPengiriman, rePostJarakPengiriman, rePostPaymentUrl, rePostBiayaPengiriman, rePostBeratOrder, rePostTotalPembayaran, rePostWaktuIdpengiriman, rePostOrderstatus);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                Toast.makeText(PemabyaranActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                if (response.isSuccessful()){
                    ResponseRegister res = (ResponseRegister) response.body();
                    if (res.status){
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                    Toast.makeText(PemabyaranActivity.this, res.message , Toast.LENGTH_SHORT).show();

                }else {
                    Toast.makeText(getApplicationContext(), "Gagal konerksi ke server!", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call call, Throwable t) {

            }
        });

    }

    private void fetchMidtransUrl() {
        SQLiteHandler db = new SQLiteHandler(getApplicationContext());
        String username = db.getUser().user_full_name;
        String email = db.getUser().user_email;
        String phone = db.getUser().user_phone;

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Loading...");
        dialog.show();

        MidtransPayment body = new MidtransPayment(new DetailTransaksi("JI-" + postTotalPembayaran, Integer.parseInt(postTotalPembayaran)), new CustomerDetail(username, email, phone));

        ApiInterface apiInterface = ApiClient.midtrans().create(ApiInterface.class);
        Call call = apiInterface.snapMidtrans(body);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                Toast.makeText(PemabyaranActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                if (response.isSuccessful()){
                    ResponseMidtransSnap body = (ResponseMidtransSnap) response.body();
                    postUrlPayment = body.redirect_url;
                }else {
                    Toast.makeText(getApplicationContext(), "Gagal konerksi ke server!", Toast.LENGTH_SHORT).show();
                }
                postToDatabase();
            }
            @Override
            public void onFailure(Call call, Throwable t) {

            }
        });

    }

    private void setRadioButtonChecked() {

        radioCash.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                radioCash.setChecked(b);
                if (b){
                    radioTrasfer.setChecked(false);
                    radioSaldo.setChecked(false);
                    postPaymentType = payment1;
                    postOrderStatus = "1";
                }else {
                    postPaymentType = "";
                    postOrderStatus = "";
                }
            }
        });

        radioTrasfer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                radioTrasfer.setChecked(b);
                if (b){
                    radioCash.setChecked(false);
                    radioSaldo.setChecked(false);
                    postPaymentType = payment2;
                    postOrderStatus = "0";
                }else {
                    postPaymentType = "" ;
                    postOrderStatus = "";
                }
            }
        });

        radioSaldo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                radioSaldo.setChecked(b);
                if (b){
                    radioCash.setChecked(false);
                    radioTrasfer.setChecked(false);
                    postPaymentType = payment3;
                    postOrderStatus = "1";
                }else {
                    postPaymentType = "" ;
                    postOrderStatus = "";
                }
            }
        });

        if (!radioSaldo.isEnabled()){
            Toast.makeText(this, "Mohon maaf saldo anda tidak mencukupi", Toast.LENGTH_SHORT).show();
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
                        injector.text(R.id.nama, data.name + " (" + data.qty + "Kg)")
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
                .register(R.layout.layout_pembayaran_payment_time, new SlimInjector<DelivTime>() {
                    @Override
                    public void onInject(final DelivTime data, final IViewInjector injector) {
                        injector.text(R.id.nama, data.getNama())
                                .text(R.id.harga, data.getCaption())
                                .with(R.id.item, new IViewInjector.Action() {
                                @Override
                                public void action(View view) {
                                    final CheckBox radio = view.findViewById(R.id.radio);
                                    data.setCheckBox(radio);
                                    if (data.getStatus() == 0){
                                        radio.setEnabled(false);
                                        radio.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                Toast.makeText(PemabyaranActivity.this, data.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                    radio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                        @Override
                                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                                            if (data.getStatus() == 1){
                                                radio.setChecked(b);
                                                if (b){
                                                    checkedTime();
                                                    data.setCheck(1);
                                                    postIdWaktuPengiriman = String.valueOf(data.getId());
                                                }else {
                                                    data.setCheck(0);
                                                    postIdWaktuPengiriman = "";
                                                }
                                            }else {
                                                Toast.makeText(PemabyaranActivity.this, data.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                        });
                    }
                })
                .attachTo(recyclerView2);

        getItemFromServer();
    }

    private void checkedTime(){
        for (Object data : items2){
            DelivTime deliveryTime = (DelivTime) data;
            if (deliveryTime.getCheck() == 1){
                deliveryTime.setCheck(0);
                deliveryTime.getCheckBox().setChecked(false);
            }
        }
    }

    private void countAndDisplay(){
        SQLiteHandler db = new SQLiteHandler(getApplicationContext());
        int saldo = Integer.parseInt(db.getUser().user_saldo);
        txtSaldo.setText("Rp. " + money(saldo));

        biayaPengiriman = (int) (distance/1000 * biayaPerKm);
        postBiayaPengiriman = String.valueOf(biayaPengiriman);
        totalDelivery.setText("Rp. " + money(biayaPengiriman));
        totalPembayaran.setText("Rp. " + money(biayaPengiriman + biayaTotalKeranjang));
        totalBiaya = biayaPengiriman + biayaTotalKeranjang;
        postTotalPembayaran = String.valueOf(totalBiaya);

        if (saldo == 0 || saldo < totalBiaya){
            radioSaldo.setEnabled(false);
        }
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
                            postJarakPengiriman = String.valueOf(distance);
                            postWaktuPengiriman = String.valueOf(legs.duration.durationValue);
                        }

                    }
                }
                dialog.hide();
                countAndDisplay();
                searchAdreess(String.valueOf(destination.latitude), String.valueOf(destination.longitude));
            }

            @Override
            public void onFailure(Call call, Throwable t) {

            }
        });
    }

    private void searchAdreess(String lat, String lng) {

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Loading...");
        dialog.show();

        String originText = lat+ "," + lng;

        ApiInterface apiInterface = ApiClient.mapsApi().create(ApiInterface.class);
        Call call = apiInterface.getAddress(originText, true);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful()){
                    GMapsAdress directionResponse = (GMapsAdress) response.body();
                    List<GMapsAdress.Route> routes = directionResponse.results;
                    if (routes.size() == 0){
                        postAlamat = "Alamat tidak ditemukan";
                    }else {
                        postAlamat = routes.get(0).formatted_address;
                    }
                }
                dialog.hide();
                txtAlamat.setText(postAlamat);
            }
            @Override
            public void onFailure(Call call, Throwable t) {

            }
        });
    }

    private void getItemFromServer() {

        SQLiteHandler db = new SQLiteHandler(getApplicationContext());
        String id = db.getUser().user_id;
        postIdUser = id;

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading....");
        progressDialog.show();

        ApiInterface apiInterface = ApiClient.jualikanService().create(ApiInterface.class);

        RequestBody reId = RequestBody.create(MediaType.parse("text/plain"), id);

        Call call = apiInterface.payment_detail(reId);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {

                if (response.isSuccessful()){
                    ResponsePembayaran res = (ResponsePembayaran) response.body();

                    if (res.status){

                        if (!res.statusOrder){
                            Toast.makeText(PemabyaranActivity.this, res.messageOrder, Toast.LENGTH_SHORT).show();
                            finish();
                        }else {

                            biayaPerKm = res.delviery_cost_pkm;
                            biayaTotalKeranjang = res.cart.total;
                            totalKeranjang.setText("Rp. " + money(res.cart.total));
                            totalKeranjang2.setText("Rp. " + money(res.cart.total));
                            latKoperasi = Double.valueOf(res.cart.koperasi.lat);
                            lngKoperasi = Double.valueOf(res.cart.koperasi.lng);
                            postIdKoperasi = res.cart.koperasi.id;

                            for (ResponsePembayaran.Items item : res.cart.items){
                                keranjang.add(item.id);
                                beratKeranjang =  beratKeranjang + item.qty;
                                items.add(item);
                            }

                            for (ResponsePembayaran.DeliveryTime time : res.deliveryTimes){
                                items2.add(new DelivTime(Integer.parseInt(time.delivery_time_id), time.delivery_time_name, time.delivery_time_start + " - " + time.delivery_time_end, time.message, time.status));
                            }

                            payment1 = res.paymentTypes.get(0).payment_type_id;
                            payment2 = res.paymentTypes.get(1).payment_type_id;
                            payment3 = res.paymentTypes.get(2).payment_type_id;

                        }

                    }else {
                        Toast.makeText(PemabyaranActivity.this, res.message, Toast.LENGTH_SHORT).show();
                    }

                }else {
                    Toast.makeText(getApplicationContext(), "Gagal konerksi ke server!", Toast.LENGTH_SHORT).show();
                }

                postBeratOrder = String.valueOf(beratKeranjang);
                postIdKeranjang = keranjang.toString();
                progressDialog.hide();
                slimAdapter.updateData(items);
                slimAdapter.notifyDataSetChanged();

                slimAdapter2.updateData(items2);
                slimAdapter2.notifyDataSetChanged();

                if (bundle == null){
                    destination = new LatLng(setting.getLatitude(), setting.getLongitude());
                    postLatAlamat = String.valueOf(setting.getLatitude());
                    postLngAlamat = String.valueOf(setting.getLongitude());
                }else {
                    destination = new LatLng(bundle.getDouble("lat"), bundle.getDouble("lng"));
                    postLatAlamat = String.valueOf(bundle.getDouble("lat"));
                    postLngAlamat = String.valueOf(bundle.getDouble("lng"));
                }

                if (latKoperasi != 0){
                    origin = new LatLng(latKoperasi, lngKoperasi);
                    Log.i("Destination : ", destination.toString());
                    countDistance(origin, destination, "driving");
                }
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
