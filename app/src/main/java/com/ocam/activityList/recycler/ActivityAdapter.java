package com.ocam.activityList.recycler;

import android.graphics.Color;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.ocam.R;
import com.ocam.model.Activity;
import com.ocam.model.types.ActivityStatus;

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
    private static OnDetailClickListener onDetailClick;

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

    public static void setOnDetailClick(OnDetailClickListener onDetail) {
        onDetailClick = onDetail;
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
        private TextView txFecha;
        private TextView txEstado;
        private Button btDetalle;

        public ActivitiesViewHolder(View itemView) {
            super(itemView);

            txDescripcion = (TextView)itemView.findViewById(R.id.lbDescripcion);
            txFecha = (TextView)itemView.findViewById(R.id.lbFecha);
            txEstado = (TextView)itemView.findViewById(R.id.lbEstado);
            btDetalle = (Button)itemView.findViewById(R.id.btEnlaceDetalle);
        }

        public void bindActivity(final Activity activity) {
            txDescripcion.setText(activity.getShortDescription());
            txFecha.setText(com.ocam.util.DateUtils.formatDate(activity.getStartDate()));
            txEstado.setText(activity.getFormattedStatus());
            txEstado.setTextColor(getStatusColor(activity.getStatus()));
            btDetalle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onDetailClick != null) {
                        onDetailClick.onDetailClick(activity);
                    }
                }
            });
        }
    }

    /**
     * Retorna el color asociado a cada status
     * @param status
     * @return
     */
    private static int getStatusColor(ActivityStatus status) {
        if (ActivityStatus.RUNNING.equals(status)) {
            return Color.parseColor("#4CAF50");
        } else if (ActivityStatus.PENDING.equals(status)) {
            return Color.parseColor("#EF5350");
        } else if (ActivityStatus.CLOSED.equals(status)) {
            return Color.parseColor("#90A4AE");
        }
        return Color.parseColor("#EF5350");
    }
}
