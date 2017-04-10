package com.ocam.activity.monitorization;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.ocam.R;
import com.ocam.model.Activity;
import com.ocam.model.Hiker;
import com.ocam.model.Report;
import com.ocam.util.DateUtils;

import org.w3c.dom.Text;

import java.util.Date;
import java.util.List;

/**
 * Clase adaptador para el recyclerView en la monitorización
 */
public class HikerAdapter extends RecyclerView.Adapter<HikerAdapter.HikersViewHolder>
{

    private List<Report> data;
    private static HikerClickListener listener;

    public HikerAdapter(List<Report> data, HikerClickListener list) {
        this.data = data;
        listener = list;
    }

    @Override
    public HikersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.hiker_item, parent, false);

        HikersViewHolder avh = new HikersViewHolder(itemView);
        return avh;
    }

    @Override
    public void onBindViewHolder(HikersViewHolder holder, int position) {
        Report item = data.get(position);
        holder.bindActivity(item);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class HikersViewHolder
            extends RecyclerView.ViewHolder {

        private CheckBox hikerLogin;
        private TextView dateTextView;

        public HikersViewHolder(View itemView) {
            super(itemView);

            hikerLogin = (CheckBox)itemView.findViewById(R.id.chHiker);
            dateTextView = (TextView)itemView.findViewById(R.id.txReporte);
        }

        public void bindActivity(final Report report) {
            hikerLogin.setText(report.getHiker().getLogin());
            hikerLogin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                    listener.onClick(report);
                }
            });
            dateTextView.setText(DateUtils.formatDate(new Date(report.getDate()), "HH:mm"));
        }
    }

    public List<Report> getData() {
        return data;
    }

    public void setData(List<Report> data) {
        this.data = data;
    }
}
