package com.ocam.activityList.recycler;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.ocam.R;
import com.ocam.activity.FragmentActivity;
import com.ocam.activityList.ListActivityView;
import com.ocam.activityList.ListPresenter;
import com.ocam.activityList.ListPresenterImpl;
import com.ocam.model.Activity;
import com.ocam.util.ViewUtils;

import java.util.ArrayList;
import java.util.List;


public class FragmentList extends Fragment implements ListActivityView
{

    private ListPresenter listPresenter;
    private RecyclerView recyclerView;
    private ActivityAdapter adapter;
    private SwipeRefreshLayout refreshLayout;

    public FragmentList() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_list, container, false);
        setHasOptionsMenu(true);
        setUpSwipeRefresh(view);
        this.listPresenter = new ListPresenterImpl(this, getContext());
        listPresenter.loadActivities();
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
        this.refreshLayout.setRefreshing(false);
    }

    @Override
    public void setUpRecyclerView(List<Activity> datos) {
        this.recyclerView = (RecyclerView) getView().findViewById(R.id.reciclerView);
        this.recyclerView.setHasFixedSize(true);
        this.adapter = new ActivityAdapter(datos);

        this.recyclerView.setAdapter(adapter);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));

        this.recyclerView.addItemDecoration(
                new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));

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
        adapter.setData(new ArrayList(datos));
        recyclerView.removeAllViews();
        adapter.notifyItemRangeInserted(0, datos.size()-1);
    }

    @Override
    public void notifyError(String err) {
        ViewUtils.showToast(getContext(), Toast.LENGTH_SHORT, err);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {
            case R.id.refreshButton:
                this.listPresenter.reloadActivities();
                return true;
        }

        return super.onOptionsItemSelected(item);
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
