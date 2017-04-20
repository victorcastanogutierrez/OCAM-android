package com.ocam.activityList.recycler;

import android.app.DatePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.Toast;

import com.google.gson.Gson;
import com.ocam.R;
import com.ocam.activity.FragmentActivity;
import com.ocam.activityList.ListActivityView;
import com.ocam.activityList.ListPresenter;
import com.ocam.activityList.ListPresenterImpl;
import com.ocam.model.Activity;
import com.ocam.util.ViewUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class FragmentList extends Fragment implements ListActivityView
{

    private ListPresenter listPresenter;
    private RecyclerView recyclerView;
    private ActivityAdapter adapter;
    private SwipeRefreshLayout refreshLayout;
    private List<Activity> originalData;
    private Calendar myCal;
    private View mView;

    public FragmentList() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_list, container, false);
        this.mView = view;
        setHasOptionsMenu(true);
        setUpSwipeRefresh(view);
        this.listPresenter = new ListPresenterImpl(this, getContext());
        this.recyclerView = (RecyclerView) view.findViewById(R.id.reciclerView);
        listPresenter.loadActivities();
        myCal = Calendar.getInstance();
        return view;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void showProgress() {
        this.refreshLayout.setRefreshing(true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void hideProgress() {
        if (this.refreshLayout.isRefreshing()) {
            this.refreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void setUpRecyclerView(List<Activity> datos) {
        this.originalData = datos;
        this.recyclerView.setHasFixedSize(true);
        this.adapter = new ActivityAdapter();
        this.adapter.add(datos);

        this.recyclerView.setAdapter(adapter);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));

        this.recyclerView.setItemAnimator(new DefaultItemAnimator());

        this.adapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activityDetail(adapter.getData().get(recyclerView.getChildAdapterPosition(v)));
            }
        });
    }

    @Override
    public void reloadRecyclerData(List<Activity> datos) {
        originalData = datos;
        adapter.replaceAll(new ArrayList(datos));
    }

    @Override
    public void notifyError(String err) {
        ViewUtils.showToast(getContext(), Toast.LENGTH_SHORT, err);
    }

    @Override
    public void notifySnackbar(String notification) {
        Snackbar.make(mView, notification, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {
            case R.id.refreshButton:
                this.listPresenter.reloadActivities();
                return true;
            case R.id.filterButton:
                Locale locale;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    locale = getContext().getResources().getConfiguration().getLocales().get(0);
                } else {
                    locale = getContext().getResources().getConfiguration().locale;
                }
                Locale.setDefault(locale);
                DatePickerFragment newFragment = getDatePicker();
                Bundle arguments = new Bundle();
                arguments.putLong("date", myCal.getTimeInMillis());
                newFragment.setArguments(arguments);
                newFragment.show(getActivity().getSupportFragmentManager(), "timePicker");
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Devuelve un DatePicker
     * @return
     */
    private DatePickerFragment getDatePicker() {
        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.setOnDateCallback(new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCal.set(Calendar.YEAR, year);
                myCal.set(Calendar.MONTH, monthOfYear);
                myCal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                filtrarPorFecha(myCal.getTime());
                String formattedDate = new SimpleDateFormat("dd/MM/yyyy").format(myCal.getTime());
                Snackbar.make(getView(), "Filtrado con fecha inferior a "+formattedDate, Snackbar.LENGTH_LONG)
                    .setAction("Deshacer", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            deshacerFiltros();
                        }
                    })
                    .setActionTextColor(getResources().getColor(android.R.color.holo_green_light ))
                    .show();
            }
        });
        return newFragment;
    }

    /**
     * Método que filtra la lista de actividades por fechas inferiores a la pasada por parámetro
     * @param date
     */
    private void filtrarPorFecha(Date date) {
        this.recyclerView.scrollToPosition(0);
        List<Activity> filtered = this.listPresenter.getFilteredList(date, this.originalData);
        this.adapter.replaceAll(filtered);
    }

    /**
     * Método que deshace los filtros aplicados en la lista
     */
    private void deshacerFiltros() {
        this.myCal.setTime(new Date());
        this.adapter.replaceAll(this.originalData);
    }

    /**
     * Carga el fragment del detalle de actividad
     * @param activity
     */
    public void activityDetail(Activity activity) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Actividad");
        FragmentActivity fragmentActivity = new FragmentActivity();
        Bundle args = new Bundle();
        args.putString("activity", new Gson().toJson(activity));
        fragmentActivity.setArguments(args);
        this.getFragmentManager().beginTransaction()
                .replace(R.id.contenido, fragmentActivity)
                .addToBackStack(null)
                .commit();

    }

    /**
     * Configura el SwiftRefreshLayout
     */
    private void setUpSwipeRefresh(View view) {
        this.refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefreshActivities);
        this.refreshLayout.setColorSchemeResources(
                R.color.colorPrimary, R.color.colorAccent);
        this.refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                listPresenter.reloadActivities();
            }
        });
    }
}
