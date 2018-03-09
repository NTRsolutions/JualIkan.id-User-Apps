package com.synergics.ishom.jualikanid_user;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Picasso;
import com.synergics.ishom.jualikanid_user.Controller.AppConfig;
import com.synergics.ishom.jualikanid_user.Controller.SQLiteHandler;
import com.synergics.ishom.jualikanid_user.Controller.SessionManager;
import com.synergics.ishom.jualikanid_user.Controller.Setting;
import com.synergics.ishom.jualikanid_user.Model.Retrofit.ApiClient;
import com.synergics.ishom.jualikanid_user.Model.Retrofit.ApiInterface;
import com.synergics.ishom.jualikanid_user.Model.Retrofit.Object.ResponseHome;
import com.synergics.ishom.jualikanid_user.Model.Retrofit.Object.ResponseLogin;
import com.synergics.ishom.jualikanid_user.View.ImageSlide.AdapterImageViewPager;
import com.synergics.ishom.jualikanid_user.View.ImageSlide.ItemImageViewPager;
import com.synergics.ishom.jualikanid_user.View.Object.ItemMenu;
import com.viewpagerindicator.CirclePageIndicator;

import net.idik.lib.slimadapter.SlimAdapter;
import net.idik.lib.slimadapter.SlimInjector;
import net.idik.lib.slimadapter.viewinjector.IViewInjector;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //recycleview
    private SlimAdapter slimAdapter;
    private RecyclerView recyclerView;
    private GridLayoutManager grid;
    private ArrayList<Object> menus = new ArrayList<>();

    //viewpager
    private ViewPager pager;
    private AdapterImageViewPager imgAdapter;
    private CirclePageIndicator indicator;
    private int currentPage;
    private Timer timer;
    private ArrayList<ItemImageViewPager> images = new ArrayList<>();

    //sidebar
    private TextView txtUsername, txtSaldo;
    private ImageView imgUser;
    private View view;

    //db and session
    private SQLiteHandler db;
    private SessionManager sessionManager;

    private NavigationView navigationView;
    private Setting setting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setting = new Setting(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        db = new SQLiteHandler(getApplicationContext());
        sessionManager = new SessionManager(getApplicationContext());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        view = navigationView.getHeaderView(0);

        //setting content header navigation drawer
        navHeaderContent();

        //setting tampilan menu
        setMenu();
    }

    private void setMenu() {

        LatLng myLoc = getMyLocation();

        //setting imageAdapter
        imgAdapter = new AdapterImageViewPager(getApplicationContext(), images);

        //inisialisasi recyclwview
        recyclerView = findViewById(R.id.recycle);

        //setting grid layout
        grid = new GridLayoutManager(getApplicationContext(), 2);
        grid.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return slimAdapter.getItem(position) instanceof ItemMenu ? 1 : 2;
            }
        });
        recyclerView.setLayoutManager(grid);

        //setting slimAdapter
        slimAdapter = SlimAdapter.create()
                //recycle in view pager
                .register(R.layout.layout_image_view_pager, new SlimInjector<ItemImageViewPager>() {

                    @Override
                    public void onInject(final ItemImageViewPager data, IViewInjector injector) {
                        if (pager == null){
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
                                }
                            });
                        }
                        imgAdapter.notifyDataSetChanged();
                    }
                })
                .register(R.layout.layout_menu_fish, new SlimInjector<ItemMenu>() {
                    @Override
                    public void onInject(final ItemMenu data, IViewInjector injector) {
                        injector.with(R.id.item, new IViewInjector.Action() {
                                    @Override
                                    public void action(View view) {
                                        LinearLayout itemClick = view.findViewById(R.id.itemClick);

                                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                                        if (data.getSize() == 1){
                                            if (data.getId() == 1){
                                                layoutParams.setMargins(0, 0, intToDp(0.5f), 0);
                                                layoutParams1.setMargins(intToDp(24), 0, 0, 0);
                                            }else if (data.getId() == 2){
                                                layoutParams.setMargins(intToDp(0.5f), 0, 0, 0);
                                                layoutParams1.setMargins(0, 0, intToDp(24), 0);
                                            }
                                        }else {
                                            if (data.getId() == 1){
                                                layoutParams.setMargins(0, 0, intToDp(0.5f), intToDp(0.5f));
                                                layoutParams1.setMargins(intToDp(24), 0, 0, 0);
                                            }else if (data.getId() == 2){
                                                layoutParams.setMargins(intToDp(0.5f), 0, 0, intToDp(0.5f));
                                                layoutParams1.setMargins(0, 0, intToDp(24), 0);
                                            }else if (data.getId() == 3){
                                                layoutParams.setMargins(0, intToDp(0.5f), intToDp(0.5f), 0);
                                                layoutParams1.setMargins(intToDp(24), 0, 0, 0);
                                            }else if (data.getId() == 4){
                                                layoutParams.setMargins(intToDp(0.5f), intToDp(0.5f), 0, 0);
                                                layoutParams1.setMargins(0, 0, intToDp(24), 0);
                                            }
                                        }

                                        itemClick.setLayoutParams(layoutParams);
                                        view.setLayoutParams(layoutParams1);

                                        TextView title = view.findViewById(R.id.title);
                                        TextView caption = view.findViewById(R.id.caption);

                                        ImageView image = view.findViewById(R.id.image);

                                        if (data.getFish() != null){
                                            title.setText(data.getFish().fish_name);
                                            caption.setText("Rp. " + money(Integer.parseInt(data.getFish().fish_price)));

                                            Picasso.with(view.getContext())
                                                    .load(AppConfig.url + data.getFish().fish_image)
                                                    .into(image);

                                            itemClick.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    Intent intent = new Intent(getApplicationContext(), FishDetailActivity.class);
                                                    intent.putExtra("fish_id", data.getFish().fish_id);
                                                    intent.putExtra("fish_name", data.getFish().fish_name);
                                                    startActivity(intent);
                                                }
                                            });
                                        }else {
                                            title.setText("");
                                            caption.setText("");
                                            Picasso.with(view.getContext())
                                                    .load("asdsad")
                                                    .into(image);
                                        }


                                    }
                                });

                    }
                })
                .register(R.layout.layout_menu_cat, new SlimInjector<ResponseHome.Data.FishCat>() {
                    @Override
                    public void onInject(final ResponseHome.Data.FishCat data, IViewInjector injector) {
                        injector.with(R.id.item, new IViewInjector.Action() {
                            @Override
                            public void action(View view) {
                                TextView title = view.findViewById(R.id.title);
                                TextView btn = view.findViewById(R.id.btn);

                                title.setText(data.fish_category_name);

                                btn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(getApplicationContext(), FishCategoryActivity.class);
                                        intent.putExtra("cat_id", data.fish_category_id);
                                        intent.putExtra("cat_name", data.fish_category_name);
                                        startActivity(intent);
                                    }
                                });
                            }
                        });

                    }
                })
                .register(R.layout.layout_menu_search, new SlimInjector<String>() {
                    @Override
                    public void onInject(final String data, IViewInjector injector) {
                        injector.with(R.id.item, new IViewInjector.Action() {
                            @Override
                            public void action(View view) {
                                view.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(getApplicationContext(), FishCategoryActivity.class);
                                        intent.putExtra("cat_id", "0");
                                        intent.putExtra("cat_name", "Daftar Ikan");
                                        startActivity(intent);
                                    }
                                });
                            }
                        });

                    }
                })
                .register(R.layout.layout_menu_space, new SlimInjector<Integer>() {
                    @Override
                    public void onInject(final Integer data, IViewInjector injector) {}
                })
                .attachTo(recyclerView);

        //get Menu From Server
        getMenuFromServer(myLoc);
    }

    private void getMenuFromServer(LatLng myLoc) {

        menus.add(new ItemImageViewPager("Ini","Buat","Image Pager"));
        menus.add("Buat Search");

        String lat = String.valueOf(myLoc.latitude);
        String lng = String.valueOf(myLoc.longitude);

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading....");
        progressDialog.show();

        ApiInterface apiInterface = ApiClient.jualikanService().create(ApiInterface.class);

        RequestBody reLat = RequestBody.create(MediaType.parse("text/plain"), lat);
        RequestBody reLng = RequestBody.create(MediaType.parse("text/plain"), lng);

        Call call = apiInterface.menu(reLat, reLng);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {

                if (response.isSuccessful()){
                    ResponseHome res = (ResponseHome) response.body();

                    if (res.status){

                        ResponseHome.Data data = res.data;
                        List<ResponseHome.Data.Promo> promos = data.promos;
                        List<ResponseHome.Data.FishCat> fishCats = data.fishCats;

                        for (ResponseHome.Data.Promo promo : promos){
                            images.add(new ItemImageViewPager(promo.promo_id, promo.promo_name, AppConfig.url+promo.promo_image));
                        }

                        for (ResponseHome.Data.FishCat fishCat : fishCats){
                            menus.add(fishCat);
                            ArrayList<ResponseHome.Data.FishCat.Fish> fishes = fishCat.fishes;

                            int i = 1;
                            int size = fishes.size();
                            for (ResponseHome.Data.FishCat.Fish fish : fishes){
                                menus.add(new ItemMenu(i, size, fish));
                                i++;
                            }
                            if (size == 1 || size == 3){
                                menus.add(new ItemMenu(i, size , null));
                            }
                        }

                    }else {
                        Toast.makeText(getApplicationContext(), "Gagal Ambil Menu !", Toast.LENGTH_SHORT).show();
                    }

                }else {
                    Toast.makeText(getApplicationContext(), "Register failed!", Toast.LENGTH_SHORT).show();
                }
                menus.add(1);

                progressDialog.hide();
                imgAdapter.notifyDataSetChanged();

                slimAdapter.updateData(menus);
                slimAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call call, Throwable t) {

            }
        });

    }

    private LatLng getMyLocation() {
        //get From setting
        setting.forceEnableGPSAcess();

        //display in logcat
        LatLng myloc = new LatLng(setting.getLatitude(), setting.getLongitude());
        Log.i("My Location : ", "Lat : " + myloc.latitude);
        Log.i("My Location : ", "Lng : " + myloc.longitude);

        //return
        return myloc;
    }

    private void navHeaderContent() {
        txtUsername = view.findViewById(R.id.userName);
        txtSaldo = view.findViewById(R.id.userSaldo);
        imgUser = view.findViewById(R.id.userImage);

        ResponseLogin.Data user = db.getUser();

        txtUsername.setText(user.user_full_name);
        txtSaldo.setText("Rp. " + money(Integer.parseInt(user.user_saldo)));

        Picasso.with(view.getContext())
                .load(AppConfig.url +user.user_image)
                .into(imgUser);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.keranjang) {
            Intent intent = new Intent(getApplicationContext(), KeranjangActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.btn_home) {

        } else if (id == R.id.btn_ikan_laut) {
            Intent intent = new Intent(getApplicationContext(), FishCategoryActivity.class);
            intent.putExtra("cat_id", "1");
            intent.putExtra("cat_name", "Ikan Laut");
            startActivity(intent);
        } else if (id == R.id.btn_ikan_hias) {
            Intent intent = new Intent(getApplicationContext(), FishCategoryActivity.class);
            intent.putExtra("cat_id", "4");
            intent.putExtra("cat_name", "Ikan Hias");
            startActivity(intent);
        } else if (id == R.id.btn_ikan_tambak) {
            Intent intent = new Intent(getApplicationContext(), FishCategoryActivity.class);
            intent.putExtra("cat_id", "2");
            intent.putExtra("cat_name", "Ikan Tambak");
            startActivity(intent);
        } else if (id == R.id.btn_ikan_budidaya) {
            Intent intent = new Intent(getApplicationContext(), FishCategoryActivity.class);
            intent.putExtra("cat_id", "3");
            intent.putExtra("cat_name", "Ikan Budidaya");
            startActivity(intent);
        } else if (id == R.id.btn_riwayat_order) {

        } else if (id == R.id.btn_bantuan) {
            Intent intent = new Intent(getApplicationContext(), BantuanActivity.class);
            startActivity(intent);
        } else if (id == R.id.btn_logout) {
            Toast.makeText(getApplicationContext(), "Anda sedang logout!", Toast.LENGTH_SHORT).show();
            sessionManager.setLogin(false);
            db.hapusUser();
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private String money(int d) {
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);;
        formatter .applyPattern("#,###");
        return formatter.format(d);
    }

    private int intToDp(float dpValue){
        float d = getApplicationContext().getResources().getDisplayMetrics().density;
        int margin = (int)(dpValue * d); // margin in pixels
        return margin;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
