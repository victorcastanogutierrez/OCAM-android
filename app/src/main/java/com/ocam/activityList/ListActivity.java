package com.ocam.activityList;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.ocam.R;
import com.ocam.activityList.recycler.FragmentList;
import com.ocam.login.LoginActivity;

public class ListActivity extends AppCompatActivity {

    private Toolbar appbar;
    private DrawerLayout drawerLayout;
    private NavigationView navView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        this.appbar = (Toolbar)findViewById(R.id.appbar);
        this.navView = (NavigationView)findViewById(R.id.navview);
        setSupportActionBar(this.appbar);
        setUpNavView();

        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);

        setSelectedMenuItem(navView.getMenu().getItem(0));

        setUpToolbar();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.contenido, new FragmentList())
                .commit();
    }

    private void setUpToolbar() {
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, this.drawerLayout, this.appbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        this.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true); // show back button
                    appbar.setNavigationOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onBackPressed();
                        }
                    });
                } else {
                    //show hamburger
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    toggle.syncState();
                    appbar.setNavigationOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            drawerLayout.openDrawer(GravityCompat.START);
                        }
                    });
                }
            }
        });
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
            case R.id.refreshButton:
                //Lo manejamos en el fragment
                return false;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setUpNavView() {
        this.navView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {

                        Boolean fragmentTransaction = Boolean.FALSE;
                        Fragment fragment = null;

                        switch (menuItem.getItemId()) {
                            case R.id.menu_cerrar_sesion:
                                Intent i = new Intent(ListActivity.this, LoginActivity.class);
                                i.putExtra("CIERRA_SESION", Boolean.TRUE);
                                finish();
                                startActivity(i);
                                break;
                            case R.id.menu_item_1:
                                fragmentTransaction = Boolean.TRUE;
                                fragment = new FragmentList();
                                break;
                        }

                        if(fragmentTransaction) {
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.contenido, fragment)
                                    .commit();

                            setSelectedMenuItem(menuItem);
                        }

                        drawerLayout.closeDrawers();

                        return true;
                    }
                });
    }

    private void setSelectedMenuItem(MenuItem menuItem) {
        menuItem.setChecked(true);
        getSupportActionBar().setTitle(menuItem.getTitle());
    }
}
