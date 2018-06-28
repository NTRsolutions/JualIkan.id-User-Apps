package com.synergics.ishom.jualikanid_user;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.synergics.ishom.jualikanid_user.Controller.AppConfig;
import com.synergics.ishom.jualikanid_user.Controller.SQLiteHandler;
import com.synergics.ishom.jualikanid_user.Model.Retrofit.ApiClient;
import com.synergics.ishom.jualikanid_user.Model.Retrofit.ApiInterface;
import com.synergics.ishom.jualikanid_user.Model.Retrofit.Object.ResponseMessage;
import com.synergics.ishom.jualikanid_user.Model.Retrofit.Object.ResponseReview;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReivewActivity extends AppCompatActivity {

    private RatingBar rating;
    private ImageView fishImage;
    private TextView fishName, fishPrice, statusReview;
    private EditText edtFeedBack;
    private Button btnSubmit;

    private int ratingValue = 0;
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reivew);

        bundle = getIntent().getExtras();

        rating = (RatingBar) findViewById(R.id.ratingReview);
        fishImage = findViewById(R.id.fishImage);
        fishName = findViewById(R.id.fishName);
        fishPrice = findViewById(R.id.fishPrice);
        statusReview = findViewById(R.id.ratingText);
        edtFeedBack = findViewById(R.id.edtFeedBack);
        btnSubmit = findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String feedBack = edtFeedBack.getText().toString().trim();
                if (feedBack.isEmpty() && ratingValue == 0){
                    Toast.makeText(ReivewActivity.this, "Pastikan feedback terisi", Toast.LENGTH_SHORT).show();
                }else {
                    postSubmit(feedBack, ratingValue);
                }
            }
        });

        rating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                switch ((int) ratingBar.getRating()){
                    case 1:
                        ratingValue = 1;
                        statusReview.setText("Very bad");
                        break;
                    case 2:
                        ratingValue = 2;
                        statusReview.setText("Need some improvement");
                        break;
                    case 3:
                        ratingValue = 3;
                        statusReview.setText("Good");
                        break;
                    case 4:
                        ratingValue = 4;
                        statusReview.setText("Great");
                        break;
                    case 5:
                        ratingValue = 5;
                        statusReview.setText("Awesome. I love it");
                        break;
                    default:
                        statusReview.setText("");
                }
            }
        });

        setToolbar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getDetailReview();
    }

    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView toolbarTitle = findViewById(R.id.toolbarTittle);
        toolbarTitle.setText(bundle.getString("fish_name"));
    }

    private void getDetailReview() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading....");
        progressDialog.show();

        ApiInterface apiInterface = ApiClient.jualikanService().create(ApiInterface.class);

        RequestBody reUserId = RequestBody.create(MediaType.parse("text/plain"), new SQLiteHandler(getApplicationContext()).getUser().user_id);
        RequestBody reKoperasiId = RequestBody.create(MediaType.parse("text/plain"), bundle.getString("koperasi_id"));
        RequestBody reFishId = RequestBody.create(MediaType.parse("text/plain"), bundle.getString("fish_id"));
//        RequestBody reFeedBack = RequestBody.create(MediaType.parse("text/plain"), user_level);
//        RequestBody reValue = RequestBody.create(MediaType.parse("text/plain"), user_level);

        Call call = apiInterface.getReview(reUserId, reFishId, reKoperasiId);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {

                if (response.isSuccessful()){
                    ResponseReview res= (ResponseReview) response.body();
                    Picasso.with(getApplicationContext()).load(AppConfig.url_image + res.fish.fish_image).into(fishImage);
                    fishName.setText(res.fish.fish_name);
                    fishPrice.setText("Rp. " + money(Integer.parseInt(res.fish.fish_price)));
                    if (res.review != null){
                        rating.setRating(Float.parseFloat(res.review.review_jumalh));
                        edtFeedBack.setText(res.review.review_text);
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

    private void postSubmit(String feedBack, int ratingValue) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading....");
        progressDialog.show();

        ApiInterface apiInterface = ApiClient.jualikanService().create(ApiInterface.class);

        RequestBody reUserId = RequestBody.create(MediaType.parse("text/plain"), new SQLiteHandler(getApplicationContext()).getUser().user_id);
        RequestBody reKoperasiId = RequestBody.create(MediaType.parse("text/plain"), bundle.getString("koperasi_id"));
        RequestBody reFishId = RequestBody.create(MediaType.parse("text/plain"), bundle.getString("fish_id"));
        RequestBody reFeedBack = RequestBody.create(MediaType.parse("text/plain"), feedBack);
        RequestBody reValue = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(ratingValue));

        Call call = apiInterface.postReview(reUserId, reFishId, reKoperasiId, reFeedBack, reValue);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {

                if (response.isSuccessful()){
                    ResponseMessage res= (ResponseMessage) response.body();
                    if (res.status){
                        Toast.makeText(ReivewActivity.this, res.message, Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(ReivewActivity.this, res.message, Toast.LENGTH_SHORT).show();
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


