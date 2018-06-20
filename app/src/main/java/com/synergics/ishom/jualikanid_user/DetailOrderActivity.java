package com.synergics.ishom.jualikanid_user;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.synergics.ishom.jualikanid_user.Model.Retrofit.ApiClient;
import com.synergics.ishom.jualikanid_user.Model.Retrofit.ApiInterface;
import com.synergics.ishom.jualikanid_user.Model.Retrofit.Object.ResponseOrderDetail;

import net.idik.lib.slimadapter.SlimAdapter;
import net.idik.lib.slimadapter.SlimInjector;
import net.idik.lib.slimadapter.viewinjector.IViewInjector;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class DetailOrderActivity extends AppCompatActivity {

    private TextView txtStatusOrder, txtTotalKeranjang1, txtAlamat, txtTotalKeranjang2,
            txtBiayaPengiriman, txtTotalBiaya, txtOrderPaymentLink;

    private LinearLayout orderPayment;

    private String paymentLink;

    private Bundle bundle;

    //recycleview
    private SlimAdapter slimAdapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager manager;
    private ArrayList<Object> items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_order);

        bundle = getIntent().getExtras();

        setToolbar();

        insialisasiWidget();
    }

    private void insialisasiWidget() {

        txtStatusOrder = findViewById(R.id.statusOrder);
        txtTotalKeranjang1 = findViewById(R.id.totalKeranjang);
        txtTotalKeranjang2 = findViewById(R.id.totalKeranjang2);
        txtAlamat = findViewById(R.id.txtAlamat);
        txtBiayaPengiriman = findViewById(R.id.totalDelivery);
        txtTotalBiaya = findViewById(R.id.totalPembayaran);

        txtOrderPaymentLink = findViewById(R.id.oderPaymentLink);
        orderPayment = findViewById(R.id.orderPayment);

        //inisialisasi recyclwview
        recyclerView = findViewById(R.id.recycle);
        manager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);

        slimAdapter = SlimAdapter.create()
                .register(R.layout.layout_pembayaran_item, new SlimInjector<ResponseOrderDetail.CartItem>() {
                    @Override
                    public void onInject(final ResponseOrderDetail.CartItem data, IViewInjector injector) {
                        injector.text(R.id.nama, data.name + " (" + data.qty + "Kg)")
                                .text(R.id.harga, "Rp. " + money(data.price));
                    }
                })
                .attachTo(recyclerView);

        txtOrderPaymentLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), WebViewActivity.class);
                intent.putExtra("web_name", "Payment");
                intent.putExtra("web_url", paymentLink);
                startActivity(intent);
            }
        });

        getDataFromServer();
    }



    private void getDataFromServer() {
        String id = bundle.getString("orderId");

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading....");
        progressDialog.show();

        ApiInterface apiInterface = ApiClient.jualikanService().create(ApiInterface.class);

        RequestBody reId = RequestBody.create(MediaType.parse("text/plain"), id);

        Call call = apiInterface.processed_order_detail(reId);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {

                if (response.isSuccessful()){
                    ResponseOrderDetail orderDetail = (ResponseOrderDetail) response.body();
                    ResponseOrderDetail.OrderDetail order = orderDetail.data;

                    //mengatur bagian status
                    txtStatusOrder.setText(statusChecker(order.status));
                    setStatusColor(order.status, txtStatusOrder);

                    //mengatur ada payemnt apa tidak ?
                    if (order.status == 0){
                        orderPayment.setVisibility(View.VISIBLE);
                        paymentLink = order.payment.url;
                    }

                    //mengatur bagian keranjang
                    for (ResponseOrderDetail.CartItem data : order.cart.items){
                        items.add(data);
                    }
                    txtTotalKeranjang1.setText("Rp. " + money(order.cart.total));

                    //mengatur alamat pengiriman
                    txtAlamat.setText(order.orderLocation.address);

                    //mengatur total semuanya
                    txtTotalBiaya.setText("Rp. " + money(order.payment.total));
                    txtTotalKeranjang2.setText("Rp. " + money(order.payment.cart));
                    txtBiayaPengiriman.setText("Rp. " + money(order.payment.delivery));


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
            return "Sedang di Kirim";
        }
        else {
            return "";
        }
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

    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView toolbarTitle = findViewById(R.id.toolbarTittle);
        toolbarTitle.setText(bundle.getString("orderName"));
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
}
