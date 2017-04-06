package com.ocam.activityList.recycler;

import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ocam.R;
import com.ocam.model.Activity;

import java.util.List;

/**
 * Clase adaptador para el recyclerView en la lista de actividades
 */
public class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.ActivitiesViewHolder>
        implements View.OnClickListener
{

    private View.OnClickListener listener;
    private List<Activity> data;

    public ActivityAdapter(List<Activity> data) {
        this.data = data;
    }

    @Override
    public ActivitiesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_item, parent, false);

        itemView.setOnClickListener(this);
        ActivitiesViewHolder avh = new ActivitiesViewHolder(itemView);
        return avh;
    }

    @Override
    public void onBindViewHolder(ActivitiesViewHolder holder, int position) {
        Activity item = data.get(position);
        holder.bindActivity(item);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public void onClick(View v) {
        if (this.listener != null) {
            this.listener.onClick(v);
        }
    }

    public void setOnClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    public static class ActivitiesViewHolder
            extends RecyclerView.ViewHolder {

        private TextView txDescripcion;
        private TextView txDetalle;
        private TextView txFecha;
        private TextView txEstado;

        public ActivitiesViewHolder(View itemView) {
            super(itemView);

            txDescripcion = (TextView)itemView.findViewById(R.id.lbDescripcion);
            txDetalle = (TextView)itemView.findViewById(R.id.lbDetalle);
            txFecha = (TextView)itemView.findViewById(R.id.lbFecha);
            txEstado = (TextView)itemView.findViewById(R.id.lbEstado);
        }

        public void bindActivity(Activity activity) {
            txDescripcion.setText(activity.getShortDescription());
            txDetalle.setText(activity.getLabel());
            txFecha.setText(com.ocam.util.DateUtils.formatDate(activity.getStartDate()));
            txEstado.setText(activity.getFormattedStatus());
        }
    }

    public List<Activity> getData() {
        return data;
    }

    public void setData(List<Activity> data) {
        this.data = data;
    }
}
