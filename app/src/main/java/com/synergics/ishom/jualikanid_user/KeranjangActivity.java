package com.synergics.ishom.jualikanid_user;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.synergics.ishom.jualikanid_user.Controller.AppConfig;
import com.synergics.ishom.jualikanid_user.Controller.SQLiteHandler;
import com.synergics.ishom.jualikanid_user.Model.Retrofit.ApiClient;
import com.synergics.ishom.jualikanid_user.Model.Retrofit.ApiInterface;
import com.synergics.ishom.jualikanid_user.Model.Retrofit.Object.ResponseKeranjang;

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

public class KeranjangActivity extends AppCompatActivity {

    private TextView toolbarTitle;
    private Button btnPembayaran;

    //recycleview
    private SlimAdapter slimAdapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager manager;
    private ArrayList<Object> items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keranjang);

        btnPembayaran = findViewById(R.id.btnPembayaran);

        setToolbar();

        setRecycleview();

        btnPembayaran.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (items.size() == 0){
                    Toast.makeText(KeranjangActivity.this, "Keranjang dalam kedaan kosong!", Toast.LENGTH_SHORT).show();
                }else {
                    Intent intent = new Intent(getApplicationContext(), PemabyaranActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    private void setRecycleview() {
        recyclerView = findViewById(R.id.recycle);
        manager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        slimAdapter = SlimAdapter.create()
                .register(R.layout.layout_cart_item, new SlimInjector<ResponseKeranjang.Keranjang>() {
                    @Override
                    public void onInject(final ResponseKeranjang.Keranjang data, IViewInjector injector) {
                        injector.with(R.id.item, new IViewInjector.Action() {
                            @Override
                            public void action(View view) {
                                TextView nama = view.findViewById(R.id.nama);
                                TextView harga = view.findViewById(R.id.harga);
                                TextView total = view.findViewById(R.id.berat);

                                Button btnMin = view.findViewById(R.id.btnMin);
                                Button btnPlus = view.findViewById(R.id.btnPlus);

                                ImageView image = view.findViewById(R.id.image);

                                nama.setText(data.name);
                                harga.setText("Rp. " + money(data.price));
                                total.setText(money(data.qty) + " Kg");

                                Picasso.with(view.getContext())
                                        .load(AppConfig.url + data.image)
                                        .into(image);

                                btnMin.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        getItemFromServer(data.id,data.qty-1);
                                    }
                                });

                                btnPlus.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        getItemFromServer(data.id,data.qty+1);
                                    }
                                });
                            }
                        });
                    }
                })
                .attachTo(recyclerView);

        //get Menu From Server
        getItemFromServer("0",0);
    }

    private void getItemFromServer(String id_keranjang, int total) {
        SQLiteHandler db = new SQLiteHandler(getApplicationContext());
        String id = db.getUser().user_id;

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading....");
        progressDialog.show();

        ApiInterface apiInterface = ApiClient.jualikanService().create(ApiInterface.class);

        RequestBody reId = RequestBody.create(MediaType.parse("text/plain"), id);
        RequestBody reIdKeranjang = RequestBody.create(MediaType.parse("text/plain"), id_keranjang);
        RequestBody reTotal = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(total));

        Call call = apiInterface.keranjang(reId, reIdKeranjang, reTotal);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {

                if (response.isSuccessful()){
                    ResponseKeranjang res = (ResponseKeranjang) response.body();

                    if (res.status){
                        if (res.data.size() == 0){
                            TextView text = (TextView) findViewById(R.id.txtNotFound);
                            text.setVisibility(TextView.VISIBLE);
                        }else {
                            items.clear();
                            for (ResponseKeranjang.Keranjang keranjang : res.data){
                                items.add(keranjang);
                            }
                            btnPembayaran.setText("Pembayaran (Rp. " + money(res.total_price) + " )");
                        }
                    }else {
                        Toast.makeText(KeranjangActivity.this, res.message , Toast.LENGTH_SHORT).show();
                    }

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
        toolbarTitle.setText("Keranjang");
    }

    private String money(int d) {
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);;
        formatter .applyPattern("#,###");
        return formatter.format(d);
    }
}
