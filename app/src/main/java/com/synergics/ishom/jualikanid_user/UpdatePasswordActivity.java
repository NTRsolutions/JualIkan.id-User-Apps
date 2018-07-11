package com.synergics.ishom.jualikanid_user;

import android.app.ProgressDialog;
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

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdatePasswordActivity extends AppCompatActivity {

    private EditText edtOldPassword, edtNewPassword;
    private Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);

        edtOldPassword = findViewById(R.id.edtOldPassword);
        edtNewPassword = findViewById(R.id.edtNewPassword);

        btnSubmit = findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String oldPassord = edtOldPassword.getText().toString().trim();
                String newPassord = edtNewPassword.getText().toString().trim();

                if (oldPassord.isEmpty() && newPassord.isEmpty()){
                    Toast.makeText(UpdatePasswordActivity.this, "Pastikan form tidak kosong !", Toast.LENGTH_SHORT).show();
                }else {
                    doingUpdate(oldPassord, newPassord);
                }
            }
        });

        setToolbar();
    }

    private void doingUpdate(String oldPassord, String newPassord) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading....");
        progressDialog.show();

        ApiInterface apiInterface = ApiClient.jualikanService().create(ApiInterface.class);

        RequestBody reUserId = RequestBody.create(MediaType.parse("text/plain"), new SQLiteHandler(getApplicationContext()).getUser().user_id);
        RequestBody reOld = RequestBody.create(MediaType.parse("text/plain"), oldPassord);
        RequestBody reNew = RequestBody.create(MediaType.parse("text/plain"), newPassord);

        Call call = apiInterface.update_profile(reUserId, reOld, reNew);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {

                if (response.isSuccessful()){
                    ResponseMessage res= (ResponseMessage) response.body();
                    if (res.status){
                        Toast.makeText(UpdatePasswordActivity.this, res.message, Toast.LENGTH_SHORT).show();
                        finish();
                    }else {
                        Toast.makeText(UpdatePasswordActivity.this, res.message, Toast.LENGTH_SHORT).show();
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
        toolbarTitle.setText("Ubah Password");
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
