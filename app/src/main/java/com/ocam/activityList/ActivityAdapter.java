package com.ocam.activityList;

import android.support.v7.widget.RecyclerView;
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

    private List<Activity> data;
    private View.OnClickListener listener;

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
        holder.bindTitular(item);
    }

    public void setOnClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public void onClick(View v) {
        Log.d("TODO", "Click");
       /* if(listener != null) {
            listener.onClick(v);
        }*/
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

        public void bindTitular(Activity activity) {
            txDescripcion.setText(activity.getShortDescription());
            txDetalle.setText(activity.getLongDescription());
            txFecha.setText(activity.getStartDate().toString());
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
