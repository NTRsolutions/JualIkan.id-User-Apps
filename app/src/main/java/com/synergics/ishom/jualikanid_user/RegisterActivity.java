package com.synergics.ishom.jualikanid_user;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.synergics.ishom.jualikanid_user.Model.Retrofit.ApiClient;
import com.synergics.ishom.jualikanid_user.Model.Retrofit.ApiInterface;
import com.synergics.ishom.jualikanid_user.Model.Retrofit.Object.ResponseKota;
import com.synergics.ishom.jualikanid_user.Model.Retrofit.Object.ResponseRegister;
import com.synergics.ishom.jualikanid_user.View.Spinner.SpinAdapter;
import com.synergics.ishom.jualikanid_user.View.Spinner.SpinnerItem;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    //spinner
    private Spinner spinner;
    private SpinAdapter spinAdapter;
    private ArrayList<SpinnerItem> spinnerItems = new ArrayList<>();

    //editText
    private EditText edtUsername, edtEmail, edtPhone, edtPassword, edtAlamat;
    private Button btnLogin, btnRegister;

    private int kota;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edtUsername = findViewById(R.id.edtUsername);
        edtEmail = findViewById(R.id.edtEmail);
        edtPhone = findViewById(R.id.edtTelp);
        edtPassword = findViewById(R.id.edtPassword);
        edtAlamat = findViewById(R.id.edtAlamat);

        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        setSpinner();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkRegister();
            }
        });

    }

    private void setSpinner() {

        //get data spinner
        spinnerData();

        spinner = (Spinner) findViewById(R.id.spinner);
        spinAdapter= new SpinAdapter(getApplicationContext(), spinnerItems);
        spinner.setAdapter(spinAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                kota = spinnerItems.get(i).getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void spinnerData() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading....");
        progressDialog.show();

        ApiInterface apiInterface = ApiClient.jualikanService().create(ApiInterface.class);
        Call call = apiInterface.kota();
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {

                if (response.isSuccessful()){
                    ResponseKota res = (ResponseKota) response.body();
                    List<ResponseKota.Data> kotas = res.data;
                    for (ResponseKota.Data kota : kotas){
                        spinnerItems.add(new SpinnerItem(Integer.parseInt(kota.kota_id), kota.kota_name));
                    }
                }
                spinAdapter.notifyDataSetChanged();
                progressDialog.hide();
            }

            @Override
            public void onFailure(Call call, Throwable t) {

            }
        });
    }

    private void checkRegister() {
        String username, email, phone, password, alamat;

        username = edtUsername.getText().toString().trim();
        email = edtEmail.getText().toString().trim();
        password = edtPassword.getText().toString().trim();
        phone = edtPhone.getText().toString().trim();
        alamat = edtAlamat.getText().toString().trim();

        if (!username.isEmpty() && !email.isEmpty() && !phone.isEmpty() && !password.isEmpty() && kota != 0 && !alamat.isEmpty()){
            postRegister(username, email, phone, password, String.valueOf(kota), alamat);
        }else {
            Toast.makeText(getApplicationContext(), "Pastikan form terisi semua", Toast.LENGTH_SHORT).show();
        }
    }

    private void postRegister(String username, String email, String phone, String password, String kota, String alamat) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading....");
        progressDialog.show();

        ApiInterface apiInterface = ApiClient.jualikanService().create(ApiInterface.class);

        RequestBody reUsername = RequestBody.create(MediaType.parse("text/plain"), username);
        RequestBody reKota = RequestBody.create(MediaType.parse("text/plain"), kota);
        RequestBody reEmail = RequestBody.create(MediaType.parse("text/plain"), email);
        RequestBody rePhone = RequestBody.create(MediaType.parse("text/plain"), phone);
        RequestBody rePassword = RequestBody.create(MediaType.parse("text/plain"), password);
        RequestBody reAlamat = RequestBody.create(MediaType.parse("text/plain"), alamat);


        Call call = apiInterface.register(reUsername, rePhone, reEmail, rePassword, reKota, reAlamat);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {

                if (response.isSuccessful()){
                    ResponseRegister res = (ResponseRegister) response.body();
                    Toast.makeText(getApplicationContext(), res.message , Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    Toast.makeText(getApplicationContext(), "Register failed!", Toast.LENGTH_SHORT).show();
                }
                progressDialog.hide();
            }

            @Override
            public void onFailure(Call call, Throwable t) {

            }
        });
    }
}
