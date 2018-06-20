package com.synergics.ishom.jualikanid_user;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.synergics.ishom.jualikanid_user.Controller.AppConfig;
import com.synergics.ishom.jualikanid_user.Controller.SQLiteHandler;
import com.synergics.ishom.jualikanid_user.Controller.Setting;
import com.synergics.ishom.jualikanid_user.Model.Retrofit.ApiClient;
import com.synergics.ishom.jualikanid_user.Model.Retrofit.ApiInterface;
import com.synergics.ishom.jualikanid_user.Model.Retrofit.Object.ResponseFish;
import com.synergics.ishom.jualikanid_user.Model.Retrofit.Object.ResponseHome;
import com.synergics.ishom.jualikanid_user.Model.Retrofit.Object.ResponseMessage;
import com.synergics.ishom.jualikanid_user.View.ImageSlide.AdapterReviewPager;
import com.synergics.ishom.jualikanid_user.View.ImageSlide.AdapterRoundedImageViewPager;
import com.synergics.ishom.jualikanid_user.View.ImageSlide.ItemImageViewPager;
import com.viewpagerindicator.CirclePageIndicator;

import net.idik.lib.slimadapter.SlimAdapter;
import net.idik.lib.slimadapter.SlimInjector;
import net.idik.lib.slimadapter.viewinjector.IViewInjector;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FishDetailActivity extends AppCompatActivity {

    //recycleview
    private SlimAdapter slimAdapter;
    private RecyclerView recyclerView;
    private GridLayoutManager grid;
    private LinearLayoutManager manager;
    private ArrayList<Object> items = new ArrayList<>();

    //viewpager
    private ViewPager pager;
    private AdapterRoundedImageViewPager imgAdapter;
    private CirclePageIndicator indicator;
    private int currentPage;
    private Timer timer;
    private ArrayList<ItemImageViewPager> images = new ArrayList<>();

    //viewpager
    private ViewPager reviewPager;
    private AdapterReviewPager reviewPagerAdapter;
    private CirclePageIndicator reviewIndicator;
    private int reviewCurrentPage;
    private Timer reviewTimer;
    private ArrayList<ResponseFish.Review> reviews = new ArrayList<>();

    private String idIkan, idKoperasi;

    private Setting setting;

    //toolbar
    private Bundle bundle;
    TextView toolbarTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fish_detail);

        bundle = getIntent().getExtras();

        setting = new Setting(this);
        setting.forceEnableGPSAcess();
        setting.getLocation();

        //setting and insialisasi toolbar
        setToolbar();

        //setting content
        setContent();
    }


    private void setToolbar() {
        String title = bundle.getString("fish_name");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbarTitle = findViewById(R.id.toolbarTittle);
        toolbarTitle.setText(title);
    }


    @Override
    protected void onResume() {
        super.onResume();
        setting.forceEnableGPSAcess();
        setting.getLocation();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void setContent() {
        //inisialisasi recyclwview
        recyclerView = findViewById(R.id.recycle);

        //setting imageAdapter
        imgAdapter = new AdapterRoundedImageViewPager(getApplicationContext(), images);
        reviewPagerAdapter = new AdapterReviewPager(getApplicationContext(), reviews);

        //setting grid layout
        grid = new GridLayoutManager(getApplicationContext(), 2);
        manager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        grid.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return slimAdapter.getItem(position) instanceof ResponseHome.Data.FishCat.Fish ? 1 : 1;
            }
        });
        recyclerView.setLayoutManager(manager);

        //setting slimAdapter
        slimAdapter = SlimAdapter.create()
                //recycle in view pager
                .register(R.layout.layout_fish_information, new SlimInjector<ResponseFish.Fish>() {
                    @Override
                    public void onInject(final ResponseFish.Fish data, IViewInjector injector) {
                        if (pager == null && reviewPager == null){
                            injector.with(R.id.layoutPager, new IViewInjector.Action() {
                                @Override
                                public void action(View view) {
                                    pager = (ViewPager) view.findViewById(R.id.imgPager);
                                    pager.setAdapter(imgAdapter);
                                    Log.i("status : ", pager.toString());
                                    if (pager != null){
                                        indicator = (CirclePageIndicator) view.findViewById(R.id.indicator);
                                        indicator.setViewPager(pager);
                                        final Handler handler = new Handler();
                                        final Runnable Update = new Runnable() {
                                            public void run() {
                                                if (currentPage == images.size()) {
                                                    currentPage = 0;
                                                }
                                                pager.setCurrentItem(currentPage++, true);
                                            }
                                        };
                                        timer = new Timer(); // This will create a new Thread
                                        timer .schedule(new TimerTask() { // task to be scheduled

                                            @Override
                                            public void run() {
                                                handler.post(Update);
                                            }
                                        }, 500, 2500);
                                    }

                                    Button btn = view.findViewById(R.id.btn);

                                    TextView title = view.findViewById(R.id.nama);
                                    TextView harga = view.findViewById(R.id.harga);
                                    TextView ukuran = view.findViewById(R.id.ukuran);
                                    TextView kondisi = view.findViewById(R.id.kondisi);
                                    TextView kategori  = view.findViewById(R.id.kategori);
                                    TextView ketersediaan = view.findViewById(R.id.ketersediaan);
                                    TextView caption = view.findViewById(R.id.caption);
                                    TextView namaKoperasi = view.findViewById(R.id.namaKoperasi);
                                    final TextView statusKoperasi = view.findViewById(R.id.statusKoperasi);
                                    ImageView imageKoperasi = view.findViewById(R.id.imageKoperasi);
                                    TextView moreFish = view.findViewById(R.id.moreFish);

                                    moreFish.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Intent intent = new Intent(getApplicationContext(), FishKoperasiActivity.class);
                                            intent.putExtra("koperasi_id", data.fish_koperasi.id);
                                            intent.putExtra("koperasi_name", data.fish_koperasi.name);
                                            startActivity(intent);
                                        }
                                    });

                                    LinearLayout bgReview = view.findViewById(R.id.bgReview);
                                    LinearLayout bgRating = view.findViewById(R.id.bgRating);

                                    namaKoperasi.setText(data.fish_koperasi.name);
                                    statusKoperasi.setText(data.fish_koperasi.status);

                                    Picasso.with(view.getContext()).load(AppConfig.url + data.fish_koperasi.image).into(imageKoperasi);

                                    if (data.fish_total_rating != 0){
                                        bgReview.setVisibility(View.VISIBLE);
                                        TextView totalReview = view.findViewById(R.id.ratingText);
                                        RatingBar rating = view.findViewById(R.id.rating);
                                        rating.setRating(data.fish_rating);
                                        totalReview.setText(data.fish_total_rating + " Reviews");
                                    }

                                    if (data.fish_review.size() != 0){
                                        bgRating.setVisibility(View.VISIBLE);
                                    }

                                    title.setText(data.fish_name);
                                    ukuran.setText(data.fish_size_id);
                                    kondisi.setText(data.fish_condition_id);
                                    kategori.setText(data.fish_category_id);
                                    ketersediaan.setText(data.fish_stock + " Kg");
                                    caption.setText(data.fish_description);
                                    harga.setText("Rp. " + money(Integer.parseInt(data.fish_price)));

                                    reviewPager = (ViewPager) view.findViewById(R.id.reviewPager);
                                    reviewPager.setAdapter(reviewPagerAdapter);
                                    if (reviewPager != null){
                                        reviewIndicator = (CirclePageIndicator) view.findViewById(R.id.reviewIndicator);
                                        reviewIndicator.setViewPager(reviewPager);
                                        final Handler handler = new Handler();
                                        final Runnable Update = new Runnable() {
                                            public void run() {
                                                if (reviewCurrentPage == reviews.size()) {
                                                    reviewCurrentPage = 0;
                                                }
                                                reviewPager.setCurrentItem(reviewCurrentPage++, true);
                                            }
                                        };
                                        reviewTimer = new Timer(); // This will create a new Thread
                                        reviewTimer .schedule(new TimerTask() { // task to be scheduled

                                            @Override
                                            public void run() {
                                                handler.post(Update);
                                            }
                                        }, 1000, 2500);
                                    }

                                    btn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            postKeranjang(data.fish_id, data.fish_koperasi.id);
                                        }
                                    });
                                }
                            });
                        }
                        imgAdapter.notifyDataSetChanged();
                    }
                })
                .attachTo(recyclerView);

        //get Menu From Server
        getItemFromServer();
    }

    private void postKeranjang(String fish_id, String fish_koperasi_id) {
        SQLiteHandler db = new SQLiteHandler(getApplicationContext());
        String id_user = db.getUser().user_id;

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading....");
        progressDialog.show();

        ApiInterface apiInterface = ApiClient.jualikanService().create(ApiInterface.class);

        RequestBody reFish = RequestBody.create(MediaType.parse("text/plain"), fish_id);
        RequestBody reUser = RequestBody.create(MediaType.parse("text/plain"), id_user);
        RequestBody reLat = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(setting.getLatitude()));
        RequestBody reLng = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(setting.getLongitude()));
        RequestBody reKoperasi = RequestBody.create(MediaType.parse("text/plain"), fish_koperasi_id);

        Call call = apiInterface.addkeranjang(reUser, reFish, reLat, reLng, reKoperasi);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {

                if (response.isSuccessful()){
                    ResponseMessage res = (ResponseMessage) response.body();
                    if (res.status){
                        Intent intent = new Intent(getApplicationContext(), KeranjangActivity.class);
                        startActivity(intent);
                        finish();
                    }else {
                        
                    }
                    Toast.makeText(FishDetailActivity.this, res.message, Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getApplicationContext(), "Gagal terhubung ke server", Toast.LENGTH_SHORT).show();
                }

                progressDialog.hide();
                imgAdapter.notifyDataSetChanged();
                reviewPagerAdapter.notifyDataSetChanged();

                slimAdapter.updateData(items);
                slimAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call call, Throwable t) {

            }
        });

    }

    private void getItemFromServer() {
        //get data from bundle intent
        String fish_id = bundle.getString("fish_id");

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading....");
        progressDialog.show();

        ApiInterface apiInterface = ApiClient.jualikanService().create(ApiInterface.class);

        RequestBody reCat = RequestBody.create(MediaType.parse("text/plain"), fish_id);

        Call call = apiInterface.fish(reCat);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {

                if (response.isSuccessful()){
                    ResponseFish res= (ResponseFish) response.body();

                    if (res.status){

                        ResponseFish.Fish fish = res.data;
                        items.add(fish);
                        images.add(new ItemImageViewPager(fish.fish_id, fish.fish_name, AppConfig.url + fish.fish_image));
                        if (fish.fish_review.size() != 0){
                            for (ResponseFish.Review review : fish.fish_review){
                                reviews.add(review);
                            }
                        }

                    }else {
                        Toast.makeText(getApplicationContext(), "Gagal ambil data ikan !", Toast.LENGTH_SHORT).show();
                    }

                }else {
                    Toast.makeText(getApplicationContext(), "Gagal terhubung ke server!", Toast.LENGTH_SHORT).show();
                }

                progressDialog.hide();
                imgAdapter.notifyDataSetChanged();
                reviewPagerAdapter.notifyDataSetChanged();

                slimAdapter.updateData(items);
                slimAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call call, Throwable t) {

            }
        });
    }

    private String money(int d) {
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);;
        formatter .applyPattern("#,###");
        return formatter.format(d);
    }

}
