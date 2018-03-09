package com.synergics.ishom.jualikanid_user.View.Spinner;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.synergics.ishom.jualikanid_user.R;

import java.util.ArrayList;

/**
 * Created by asmarasusanto on 8/4/17.
 */

public class SpinAdapter extends BaseAdapter {
    Context context;
    ArrayList<SpinnerItem> data;
    LayoutInflater inflter;

    public SpinAdapter(Context applicationContext, ArrayList<SpinnerItem> countryNames) {
        this.context = applicationContext;
        this.data = countryNames;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.item_spin, null);
        TextView names = view.findViewById(R.id.txtItem);
        names.setTextColor(Color.parseColor("#343434"));
        names.setText(data.get(i).getTitle());
        return view;
    }
}
