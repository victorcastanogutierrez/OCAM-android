package com.ocam.activityList;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ocam.R;
import com.ocam.model.Activity;
import com.ocam.util.ViewUtils;

import java.util.ArrayList;
import java.util.List;


public class FragmentList extends Fragment implements ListActivityView{

    private ListPresenter listPresenter;
    private Dialog mOverlayDialog;
    private ProgressBar mProgress;
    private RecyclerView recyclerView;
    private ActivityAdapter adapter;

    public FragmentList() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_list, container, false);
        setHasOptionsMenu(true);
        this.listPresenter = new ListPresenterImpl(this, getContext());
        this.mOverlayDialog = new Dialog(getContext(), android.R.style.Theme_Panel);
        this.mProgress = (ProgressBar) view.findViewById(R.id.progressBar);
        listPresenter.loadActivities();

        return view;
    }

    @Override
    public void showProgress() {
        mOverlayDialog.setCancelable(false);
        mOverlayDialog.show();
        this.mProgress.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        mOverlayDialog.cancel();
        this.mProgress.setVisibility(View.GONE);
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
}
