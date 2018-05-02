package com.synergics.ishom.jualikanid_user;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.synergics.ishom.jualikanid_user.Controller.SQLiteHandler;
import com.synergics.ishom.jualikanid_user.Model.Retrofit.ApiClient;
import com.synergics.ishom.jualikanid_user.Model.Retrofit.ApiInterface;
import com.synergics.ishom.jualikanid_user.Model.Retrofit.Object.ResponseOrderProcessed;

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

/**
 * Created by asmarasusanto on 3/16/18.
 */

public class FragmentOrderSelesai2 extends Fragment {

    private View viewFragment;

    //recycleview
    private SlimAdapter slimAdapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager manager;
    private ArrayList<Object> items = new ArrayList<>();

    public FragmentOrderSelesai2() {}

    public static FragmentOrderSelesai2 newInstance() {
        FragmentOrderSelesai2 fragment = new FragmentOrderSelesai2();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
        viewFragment = inflater.inflate(R.layout.fragment_order_selesai2, container, false);

        inisialRecycle();

        return viewFragment;
    }

    private void inisialRecycle() {
        //inisialisasi recyclwview
        recyclerView = viewFragment.findViewById(R.id.recycle);

        //setting layout manager
        manager = new LinearLayoutManager(viewFragment.getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);

        //setting slimAdapter
        slimAdapter = SlimAdapter.create()
                .register(R.layout.layout_list_order_proses, new SlimInjector<ResponseOrderProcessed.OrderProcessed>() {
                    @Override
                    public void onInject(final ResponseOrderProcessed.OrderProcessed data, IViewInjector injector) {
                        injector.text(R.id.idOrder, "Order " + data.orderId)
                                .text(R.id.alamatOrder, data.orderAdress)
                                .text(R.id.statusOrder, statusChecker(Integer.parseInt(data.orderStatus)))
                                .text(R.id.hargaOrder, "Rp. " + money(data.orderTotal))
                                .text(R.id.tanggalorder, dateConvert(data.orderDate))
                                .with(R.id.item, new IViewInjector.Action() {
                                    @Override
                                    public void action(View view) {

                                        TextView status = view.findViewById(R.id.statusOrder);
                                        setStatusColor(Integer.parseInt(data.orderStatus), status);

                                        view.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                Intent intent = new Intent(viewFragment.getContext(), DetailOrderActivity.class);
                                                intent.putExtra("orderId", data.orderIdNumber);
                                                intent.putExtra("orderName", "Order " + data.orderId);
                                                startActivity(intent);
                                            }
                                        });   
                                    }
                                });

                    }
                })
                .attachTo(recyclerView);
        
        getDataFromDatabase();
        
    }

    private void getDataFromDatabase() {

        SQLiteHandler db = new SQLiteHandler(viewFragment.getContext());
        String id_user = db.getUser().user_id;

        final ProgressDialog progressDialog = new ProgressDialog(viewFragment.getContext());
        progressDialog.setMessage("Loading....");
        progressDialog.show();

        ApiInterface apiInterface = ApiClient.jualikanService().create(ApiInterface.class);

        RequestBody reIdUser = RequestBody.create(MediaType.parse("text/plain"), id_user);

        Call call = apiInterface.orderFinished(reIdUser);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {

                if (response.isSuccessful()){
                    ResponseOrderProcessed res= (ResponseOrderProcessed) response.body();

                    if (res.status){

                        if (res.data.size() == 0){
                            TextView text = viewFragment.findViewById(R.id.txtNotFound);
                            text.setVisibility(TextView.VISIBLE);
                        }else {
                            TextView text = viewFragment.findViewById(R.id.txtNotFound);
                            text.setVisibility(TextView.INVISIBLE);
                        }

                        items.clear();
                        for (ResponseOrderProcessed.OrderProcessed item : res.data){
                            items.add(item);
                        }

                    }else {
                        Toast.makeText(viewFragment.getContext(), res.message, Toast.LENGTH_SHORT).show();
                        TextView text = viewFragment.findViewById(R.id.txtNotFound);
                        text.setVisibility(TextView.VISIBLE);
                    }

                }else {
                    Toast.makeText(viewFragment.getContext(), "Failed to connect server", Toast.LENGTH_SHORT).show();
                    TextView text = viewFragment.findViewById(R.id.txtNotFound);
                    text.setVisibility(TextView.VISIBLE);
                }

                progressDialog.hide();

                slimAdapter.updateData(items);
                recyclerView.setAdapter(slimAdapter);
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
            return "Sedang di Korim";
        }
        else {
            return "";
        }
    }

    private void setStatusColor(int status, TextView textView){
        if (status == 0){
            textView.setTextColor(viewFragment.getContext().getResources().getColor(R.color.orange));
        }else if (status == 5){
            textView.setTextColor(viewFragment.getContext().getResources().getColor(R.color.red));
        }else if (status == 3){
            textView.setTextColor(viewFragment.getContext().getResources().getColor(R.color.green));
        }else {
            textView.setTextColor(viewFragment.getContext().getResources().getColor(R.color.blueFont));
        }
    }

    private String dateConvert(String date){
        String[] dates = date.split("\\s+");
        String[] tanggals = dates[0].split("-");
        tanggals[1] = monthConvert(Integer.parseInt(tanggals[1]));
        return tanggals[2] + " " + tanggals[1] + " " + tanggals[0];
    }

    private String monthConvert (int month){
        if (month == 1){
            return "January";
        }
        if (month == 2){
            return "Febuary";
        }
        if (month == 3){
            return "March";
        }
        if (month == 4){
            return "April";
        }
        if (month == 5){
            return "May";
        }
        if (month == 6){
            return "June";
        }
        if (month == 7){
            return "July";
        }
        if (month == 8){
            return "August";
        }
        if (month == 9){
            return "September";
        }
        if (month == 10){
            return "October";
        }
        if (month == 11){
            return "November";
        }
        if (month == 12){
            return "December";
        }
        return "";
    }
}
