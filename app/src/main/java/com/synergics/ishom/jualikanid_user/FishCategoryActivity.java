package com.synergics.ishom.jualikanid_user;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.squareup.picasso.Picasso;
import com.synergics.ishom.jualikanid_user.Controller.AppConfig;
import com.synergics.ishom.jualikanid_user.Model.Retrofit.ApiClient;
import com.synergics.ishom.jualikanid_user.Model.Retrofit.ApiInterface;
import com.synergics.ishom.jualikanid_user.Model.Retrofit.Object.ResponseHome;
import com.synergics.ishom.jualikanid_user.Model.Retrofit.Object.ResponseKategori;

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

public class FishCategoryActivity extends AppCompatActivity {

    //recycleview
    private SlimAdapter slimAdapter;
    private RecyclerView recyclerView;
    private GridLayoutManager grid;
    private ArrayList<Object> items = new ArrayList<>();

    //bundle
    Bundle bundle;
    private MaterialSearchView searchView;
    TextView toolbarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fish_category);

        bundle = getIntent().getExtras();
        
        setToolbar();

        //setting tampilan menu
        setContent();
    }

    private void setToolbar() {
        String title = bundle.getString("cat_name");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbarTitle = findViewById(R.id.toolbarTittle);
        toolbarTitle.setText(title);

        searchView = (MaterialSearchView) findViewById(R.id.searchView);

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                toolbarTitle.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onSearchViewClosed() {

                TextView text = (TextView) findViewById(R.id.txtNotFound);
                text.setVisibility(TextView.INVISIBLE);

                toolbarTitle.setVisibility(View.VISIBLE);

                slimAdapter.updateData(items);
                slimAdapter.notifyDataSetChanged();
                recyclerView.setAdapter(slimAdapter);
            }
        });

        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (newText != null && !newText.isEmpty()){
                    ArrayList<Object> dataNew = new ArrayList<>();
                    getSearch(newText, dataNew);

//                    for (PersonilNew data : data1){
//                        if (data.getNama().toLowerCase().contains(newText.toLowerCase())){
//                            dataNew.add(data);
//                        }
//
//                    }
//
//                    dataAdapter = new PersonilNewAdapter(PolisiActivity.this, dataNew);
//                    dataAdapter.notifyDataSetChanged();
//                    recyclerView.setAdapter(dataAdapter);
                }else {

                    TextView text = (TextView) findViewById(R.id.txtNotFound);
                    text.setVisibility(TextView.INVISIBLE);

                    slimAdapter.updateData(items);
                    slimAdapter.notifyDataSetChanged();
                    recyclerView.setAdapter(slimAdapter);
                }
                return true;
            }
        });
    }

    private void getSearch(String newText, final ArrayList<Object> dataNew) {
        //get data from bundle intent
        String fish_id = bundle.getString("cat_id");

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading....");
        progressDialog.show();

        ApiInterface apiInterface = ApiClient.jualikanService().create(ApiInterface.class);

        RequestBody reCat = RequestBody.create(MediaType.parse("text/plain"), fish_id);
        RequestBody reSearch = RequestBody.create(MediaType.parse("text/plain"), newText);

        Call call = apiInterface.serach_kategori(reCat, reSearch);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {

                if (response.isSuccessful()){
                    ResponseKategori res= (ResponseKategori) response.body();

                    if (res.status){

                        List<ResponseHome.Data.FishCat.Fish> fishes = res.data;

                        if (fishes.size() == 0){
                            TextView text = (TextView) findViewById(R.id.txtNotFound);
                            text.setVisibility(TextView.VISIBLE);
                        }else {
                            TextView text = (TextView) findViewById(R.id.txtNotFound);
                            text.setVisibility(TextView.INVISIBLE);
                        }

                        for (ResponseHome.Data.FishCat.Fish fish : fishes){
                            dataNew.add(fish);
                        }

                    }else {
                        Toast.makeText(getApplicationContext(), "Gagal Ambil Fish Kategori !", Toast.LENGTH_SHORT).show();
                        TextView text = (TextView) findViewById(R.id.txtNotFound);
                        text.setVisibility(TextView.VISIBLE);
                    }

                }else {
                    Toast.makeText(getApplicationContext(), "Register failed!", Toast.LENGTH_SHORT).show();
                    TextView text = (TextView) findViewById(R.id.txtNotFound);
                    text.setVisibility(TextView.VISIBLE);
                }

                progressDialog.hide();

                slimAdapter.updateData(dataNew);
                recyclerView.setAdapter(slimAdapter);
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

    private void setContent() {
        //inisialisasi recyclwview
        recyclerView = findViewById(R.id.recycle);

        //setting grid layout
        grid = new GridLayoutManager(getApplicationContext(), 2);
        grid.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return slimAdapter.getItem(position) instanceof ResponseHome.Data.FishCat.Fish ? 1 : 2;
            }
        });
        recyclerView.setLayoutManager(grid);

        //setting slimAdapter
        slimAdapter = SlimAdapter.create()
                //recycle in view pager
                .register(R.layout.layout_fish_category, new SlimInjector<ResponseHome.Data.FishCat.Fish>() {
                    @Override
                    public void onInject(final ResponseHome.Data.FishCat.Fish data, IViewInjector injector) {
                        injector.with(R.id.item, new IViewInjector.Action() {
                            @Override
                            public void action(View view) {
                                TextView title = view.findViewById(R.id.title);
                                TextView harga = view.findViewById(R.id.caption);
                                TextView ukuran = view.findViewById(R.id.ukuran);
                                TextView kondisi = view.findViewById(R.id.kondisi);
                                TextView penjual = view.findViewById(R.id.penjual);

                                ImageView image = view.findViewById(R.id.image);

                                LinearLayout bgRating = view.findViewById(R.id.bgRating);
                                RatingBar rating = view.findViewById(R.id.rating);
                                TextView ratingText = view.findViewById(R.id.ratingText);

                                title.setText(data.fish_name);
                                ukuran.setText(data.fish_size_id);
                                kondisi.setText(data.fish_condition_id);
                                harga.setText("Rp. " + money(Integer.parseInt(data.fish_price)));
                                penjual.setText(data.fish_distribution_location);

                                if (data.fish_total_rating != 0){
                                    bgRating.setVisibility(View.VISIBLE);
                                    rating.setRating(data.fish_rating);
                                    ratingText.setText(data.fish_total_rating + " Reviews");
                                }

                                Picasso.with(view.getContext())
                                        .load(AppConfig.url+data.fish_image)
                                        .into(image);

                                view.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(getApplicationContext(), FishDetailActivity.class);
                                        intent.putExtra("fish_id", data.fish_id);
                                        intent.putExtra("fish_name", data.fish_name);
                                        startActivity(intent);
                                    }
                                });

                            }
                        });

                    }
                })
                .attachTo(recyclerView);

        //get Menu From Server
        getItemFromServer();
    }

    private void getItemFromServer() {
        //get data from bundle intent
        String fish_id = bundle.getString("cat_id");

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading....");
        progressDialog.show();

        ApiInterface apiInterface = ApiClient.jualikanService().create(ApiInterface.class);

        RequestBody reCat = RequestBody.create(MediaType.parse("text/plain"), fish_id);

        Call call = apiInterface.kategori(reCat);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {

                if (response.isSuccessful()){
                    ResponseKategori res= (ResponseKategori) response.body();

                    if (res.status){

                        List<ResponseHome.Data.FishCat.Fish> fishes = res.data;

                        for (ResponseHome.Data.FishCat.Fish fish : fishes){
                            items.add(fish);
                        }

                    }else {
                        Toast.makeText(getApplicationContext(), "Gagal Ambil Fish Kategori !", Toast.LENGTH_SHORT).show();
                    }

                }else {
                    Toast.makeText(getApplicationContext(), "Register failed!", Toast.LENGTH_SHORT).show();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.fish_category, menu);
        MenuItem item = menu.findItem(R.id.search);
        searchView.setMenuItem(item);
        return true;
    }


    private String money(int d) {
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);;
        formatter .applyPattern("#,###");
        return formatter.format(d);
    }
}
