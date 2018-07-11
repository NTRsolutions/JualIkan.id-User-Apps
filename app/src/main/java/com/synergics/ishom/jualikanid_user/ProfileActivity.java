package com.synergics.ishom.jualikanid_user;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.synergics.ishom.jualikanid_user.Controller.AppConfig;
import com.synergics.ishom.jualikanid_user.Controller.SQLiteHandler;
import com.synergics.ishom.jualikanid_user.Controller.SessionManager;
import com.synergics.ishom.jualikanid_user.Model.Retrofit.ApiClient;
import com.synergics.ishom.jualikanid_user.Model.Retrofit.ApiInterface;
import com.synergics.ishom.jualikanid_user.Model.Retrofit.Object.ResponseProfile;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    private LinearLayout btnSaldo, btnUbahProfile;
    private ImageView userImage;
    private TextView userName;
    private TextView userPhone;
    private TextView userEmail, userSaldo;
    private Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        btnSaldo = findViewById(R.id.bgSaldo);
        btnUbahProfile = findViewById(R.id.bgUbahProfile);
        userImage = findViewById(R.id.userImage);
        userName = findViewById(R.id.userName);
        userPhone = findViewById(R.id.userPhone);
        userEmail = findViewById(R.id.userEmail);
        btnLogout = findViewById(R.id.btnLogout);
        userSaldo = findViewById(R.id.userSaldo);

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SessionManager(getApplicationContext()).setLogin(false);
                new SQLiteHandler(getApplicationContext()).hapusUser();
                Intent intent = new Intent( getApplicationContext(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        btnSaldo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RiwayatSaldoActivity.class);
                startActivity(intent);
            }
        });

        btnUbahProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), UpdatePasswordActivity.class);
                startActivity(intent);
            }
        });

        setToolbar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getProfile();
    }

    private void getProfile() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading....");
        progressDialog.show();

        ApiInterface apiInterface = ApiClient.jualikanService().create(ApiInterface.class);

        RequestBody reUserId = RequestBody.create(MediaType.parse("text/plain"), new SQLiteHandler(getApplicationContext()).getUser().user_id);

        Call call = apiInterface.profile(reUserId);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {

                if (response.isSuccessful()){
                    ResponseProfile res= (ResponseProfile) response.body();
                    if (res.status){
                        Picasso.with(getApplicationContext()).load(AppConfig.url + res.profile.user_image).into(userImage);
                        userName.setText(res.profile.user_full_name);
                        userEmail.setText(res.profile.user_email);
                        userPhone.setText(res.profile.user_phone);
                        userSaldo.setText("Rp. " + money(Integer.parseInt(res.profile.user_saldo)));
                    }else {
                        Toast.makeText(ProfileActivity.this, "Gagal mengambil profile", Toast.LENGTH_SHORT).show();
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
        toolbarTitle.setText("Profile");
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
