package meta.simplifi.dropboxuploader;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.dropbox.core.android.Auth;

import meta.simplifi.dropboxuploader.databinding.ActivityMainBinding;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String AUTH_TOKEN = "auth-token";
    private String mToken;
    private ActivityMainBinding mBinding;

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences preferences = getSharedPreferences(AUTH_TOKEN, MODE_PRIVATE);
        mToken = preferences.getString(AUTH_TOKEN, null);
        if (mToken == null) {
            mToken = Auth.getOAuth2Token();
            if (mToken != null) {
                preferences.edit().putString(AUTH_TOKEN, mToken).apply();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        Toolbar toolbar = mBinding.appBarMain.toolbar;
        setSupportActionBar(toolbar);

        FloatingActionButton fab = mBinding.appBarMain.fab;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!hasToken()) {
                    Snackbar.make(view, "Log in to Dropbox to upload pictures", Snackbar.LENGTH_LONG)
                            .setAction("Go", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Auth.startOAuth2Authentication(MainActivity.this, getString(R.string.app_key));
                                }
                            }).show();
                } else {
                    MainActivityPermissionsDispatcher.openGalleryWithCheck(MainActivity.this);
                }
            }
        });

        DrawerLayout drawer = mBinding.drawerLayout;
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = mBinding.navView;
        navigationView.setNavigationItemSelectedListener(this);
    }

    private boolean hasToken() {
        return mToken != null;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Choose a picture to upload"), 0);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @OnShowRationale(Manifest.permission.READ_EXTERNAL_STORAGE)
    void onShowStorageRationale(PermissionRequest request) {
        showPermissionSnackbar();
        request.proceed();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @OnNeverAskAgain(Manifest.permission.READ_EXTERNAL_STORAGE)
    void onNeverAskAgain() {
        showPermissionSnackbar();
    }

    private void showPermissionSnackbar() {
        Snackbar.make(
                mBinding.appBarMain.fab,
                "We need storage permissions to upload pictures",
                Snackbar.LENGTH_INDEFINITE
        ).setAction(
                getString(android.R.string.ok),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MainActivityPermissionsDispatcher
                                .openGalleryWithCheck(MainActivity.this);
                    }
                }
        ).show();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }
}
