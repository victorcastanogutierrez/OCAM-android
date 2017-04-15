package com.ocam.activityList.recycler;

import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.ocam.R;
import com.ocam.model.Activity;

import java.util.List;

import static android.R.attr.data;

/**
 * Clase adaptador para el recyclerView en la lista de actividades
 */
public class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.ActivitiesViewHolder>
        implements View.OnClickListener
{
    private final SortedList<Activity>  mySortedList = new SortedList<>(Activity.class, new SortedList.Callback<Activity>() {

        @Override
        public void onInserted(int position, int count) {
            notifyItemRangeInserted(position, count);
        }

        @Override
        public void onRemoved(int position, int count) {
            notifyItemRangeRemoved(position, count);
        }

        @Override
        public void onMoved(int fromPosition, int toPosition) {
            notifyItemMoved(fromPosition, toPosition);
        }

        @Override
        public int compare(Activity o1, Activity o2) {
            return o1.getStartDate().compareTo(o2.getStartDate());
        }

        @Override
        public void onChanged(int position, int count) {
            notifyItemRangeChanged(position, count);
        }

        @Override
        public boolean areContentsTheSame(Activity oldItem, Activity newItem) {
            return false;
        }

        @Override
        public boolean areItemsTheSame(Activity item1, Activity item2) {
            return false;
        }
    });

    private View.OnClickListener listener;

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
        Activity item = mySortedList.get(position);
        holder.bindActivity(item);
    }

    @Override
    public int getItemCount() {
        return mySortedList.size();
    }

    @Override
    public void onClick(View v) {
        if (this.listener != null) {
            this.listener.onClick(v);
        }
    }

    public void add(Activity activity) {
        mySortedList.add(activity);
    }

    public void add(List<Activity> activitys) {
        mySortedList.addAll(activitys);
    }

    public void replaceAll(List<Activity> activitys) {
        mySortedList.beginBatchedUpdates();
        mySortedList.clear();
        mySortedList.addAll(activitys);
        mySortedList.endBatchedUpdates();
    }

    public void setOnClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    public SortedList<Activity> getData() {
        return mySortedList;
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
}
