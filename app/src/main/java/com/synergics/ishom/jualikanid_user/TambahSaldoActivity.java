package com.synergics.ishom.jualikanid_user;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.synergics.ishom.jualikanid_user.Controller.SQLiteHandler;
import com.synergics.ishom.jualikanid_user.Model.Retrofit.ApiClient;
import com.synergics.ishom.jualikanid_user.Model.Retrofit.ApiInterface;
import com.synergics.ishom.jualikanid_user.Model.Retrofit.Object.ResponseMessage;
import com.synergics.ishom.jualikanid_user.Model.Retrofit.Object.ResponseMidtransSnap;
import com.synergics.ishom.jualikanid_user.View.Object.CustomerDetail;
import com.synergics.ishom.jualikanid_user.View.Object.DetailTransaksi;
import com.synergics.ishom.jualikanid_user.View.Object.MidtransPayment;

import java.util.Random;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TambahSaldoActivity extends AppCompatActivity {

    private Button btnSubmit;
    private EditText edtJumlahSaldo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_saldo);

        btnSubmit = findViewById(R.id.btnSubmit);
        edtJumlahSaldo = findViewById(R.id.edtJumlahSaldo);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String jumlahSaldo = edtJumlahSaldo.getText().toString().trim();
                if (jumlahSaldo.isEmpty()){
                    Toast.makeText(TambahSaldoActivity.this, "Harap masukan jumlah saldo", Toast.LENGTH_SHORT).show();
                }else {
                    postSaldo(jumlahSaldo);
                }
            }
        });

        setToolbar();
    }

    private void postSaldo(final String jumlahSaldo) {
        SQLiteHandler db = new SQLiteHandler(getApplicationContext());
        String username = db.getUser().user_full_name;
        String email = db.getUser().user_email;
        String phone = db.getUser().user_phone;

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Loading...");
        dialog.show();

        Random rand = new Random();
        int  n = rand.nextInt(100) + 1;

        MidtransPayment body = new MidtransPayment(new DetailTransaksi("TOPUP-" + jumlahSaldo+ n, Integer.parseInt(jumlahSaldo)), new CustomerDetail(username, email, phone));

        ApiInterface apiInterface = ApiClient.midtrans().create(ApiInterface.class);
        Call call = apiInterface.snapMidtrans("Basic U0ItTWlkLXNlcnZlci0wekxTWXlrSEJKbFFNenRiOExSanB0VXg6", "application/json","application/json",body);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful()){
                    ResponseMidtransSnap body = (ResponseMidtransSnap) response.body();
                    doingTopUp(body.redirect_url,jumlahSaldo);
                }else {
                    Toast.makeText(getApplicationContext(), "Gagal konerksi ke server!", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(TambahSaldoActivity.this, "Check your intenet connection !", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void doingTopUp(final String redirect_url, String jumlahSaldo) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading....");
        progressDialog.show();

        ApiInterface apiInterface = ApiClient.jualikanService().create(ApiInterface.class);

        RequestBody reUserId = RequestBody.create(MediaType.parse("text/plain"), new SQLiteHandler(getApplicationContext()).getUser().user_id);
        RequestBody reSaldo = RequestBody.create(MediaType.parse("text/plain"), jumlahSaldo);

        Call call = apiInterface.tambah_saldo(reUserId, reSaldo);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {

                if (response.isSuccessful()){
                    ResponseMessage res= (ResponseMessage) response.body();
                    if (res.status){
                        Intent intent = new Intent(getApplicationContext(), WebViewActivity.class);
                        intent.putExtra("web_name", "Payment");
                        intent.putExtra("web_url", redirect_url);
                        startActivity(intent);
                        finish();
                    }else {
                        Toast.makeText(getApplicationContext(), res.message, Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(getApplicationContext(), "Failed to fetch into server!", Toast.LENGTH_SHORT).show();
                }
                progressDialog.hide();
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Please check your internet connection!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView toolbarTitle = findViewById(R.id.toolbarTittle);
        toolbarTitle.setText("Tambah Saldo");
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
