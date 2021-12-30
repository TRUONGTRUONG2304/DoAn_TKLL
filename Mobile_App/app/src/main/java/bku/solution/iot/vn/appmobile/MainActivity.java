package bku.solution.iot.vn.appmobile;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    HomeFragment homeFragment = new HomeFragment();
    DeviceFragment deviceFragment = new DeviceFragment();
    FragmentManager fm = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(MainActivity.this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        FragmentTransaction ft = fm.beginTransaction();
//        ft.replace(R.id.fragment_container, homeFragment).addToBackStack(null);
//        ft.commitAllowingStateLoss();
        if(fm.findFragmentByTag("homeFr") != null){
            ft.show(fm.findFragmentByTag("homeFr"));
        }
        else{
            ft.add(R.id.fragment_container, homeFragment, "homeFr");
        }
        ft.commit();
        navigationView.setCheckedItem(R.id.nav_home);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        FragmentTransaction ft = fm.beginTransaction();
        switch (item.getItemId()) {
            case R.id.nav_home:
                if(fm.findFragmentByTag("homeFr") != null){
                    ft.show(fm.findFragmentByTag("homeFr"));
                }
                else{
                    ft.add(R.id.fragment_container, homeFragment, "homeFr");
                }
                if(fm.findFragmentByTag("deviceFr") != null){
                    ft.hide(fm.findFragmentByTag("deviceFr"));
                }
                ft.commit();
                break;
            case R.id.nav_device:
                if(fm.findFragmentByTag("deviceFr") != null){
                    ft.show(fm.findFragmentByTag("deviceFr"));
                }
                else{
                    ft.add(R.id.fragment_container, deviceFragment, "deviceFr");
                }
                if(fm.findFragmentByTag("homeFr") != null){
                    ft.hide(fm.findFragmentByTag("homeFr"));
                }
                ft.commit();
                break;
            case R.id.nav_profile:
                Toast.makeText(MainActivity.this, "Profile", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_share:
                Toast.makeText(MainActivity.this, "Share", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_help:
                Toast.makeText(MainActivity.this, "Help", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

    }
}
