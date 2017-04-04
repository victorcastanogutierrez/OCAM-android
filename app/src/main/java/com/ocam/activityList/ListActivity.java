package com.ocam.activityList;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import com.ocam.activityList.DividerItemDecoration;

import com.ocam.R;
import com.ocam.login.LoginActivity;
import com.ocam.model.Activity;

import java.util.List;

public class ListActivity extends AppCompatActivity implements ListActivityView{

    private Toolbar appbar;
    private DrawerLayout drawerLayout;
    private NavigationView navView;
    private ListPresenter listPresenter;
    private Dialog mOverlayDialog;
    private ProgressBar mProgress;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        this.appbar = (Toolbar)findViewById(R.id.appbar);
        this.navView = (NavigationView)findViewById(R.id.navview);
        this.listPresenter = new ListPresenterImpl(this, ListActivity.this);
        this.mOverlayDialog = new Dialog(ListActivity.this, android.R.style.Theme_Panel);
        this.mProgress = (ProgressBar) findViewById(R.id.progressBar);
        setSupportActionBar(this.appbar);
        setUpNavView();

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_nav_menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);

        listPresenter.loadActivities();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setUpNavView() {
        this.navView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {

                       // boolean fragmentTransaction = false;

                        switch (menuItem.getItemId()) {
                            case R.id.menu_cerrar_sesion:
                                Intent i = new Intent(ListActivity.this, LoginActivity.class);
                                i.putExtra("CIERRA_SESION", Boolean.TRUE);
                                finish();
                                startActivity(i);
                                break;
                        }

                        /*if(fragmentTransaction) {
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.content_frame, fragment)
                                    .commit();

                            menuItem.setChecked(true);
                            getSupportActionBar().setTitle(menuItem.getTitle());
                        }*/

                        drawerLayout.closeDrawers();

                        return true;
                    }
                });
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
        this.mProgress.setVisibility(View.INVISIBLE);
    }

    @Override
    public void setUpRecyclerView(List<Activity> datos) {
        this.recyclerView = (RecyclerView) findViewById(R.id.reciclerView);
        this.recyclerView.setHasFixedSize(true);
        final ActivityAdapter adapter = new ActivityAdapter(datos);

        adapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("DemoRecView", "Pulsado el elemento " + recyclerView.getChildPosition(v));
            }
        });

        this.recyclerView.setAdapter(adapter);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));

        this.recyclerView.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));

        this.recyclerView.setItemAnimator(new DefaultItemAnimator());
    }
}
