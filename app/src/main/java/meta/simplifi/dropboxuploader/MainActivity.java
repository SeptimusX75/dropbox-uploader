package meta.simplifi.dropboxuploader;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.dropbox.core.android.Auth;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.users.FullAccount;

import java.text.DateFormat;

import meta.simplifi.dropboxuploader.databinding.ActivityMainBinding;
import meta.simplifi.dropboxuploader.databinding.NavHeaderMainBinding;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String AUTH_TOKEN = "auth-token";
    public static final int SELECT_PICTURE = 0;
    public static final String SCHEME_PACKAGE = "package";
    private String mToken;
    private ActivityMainBinding mBinding;
    private NavHeaderMainBinding mHeaderBinding;
    private FullAccountViewModel mAccountVm;
    private SharedPreferences mPreferences;
    private LogInViewModel mLogIn = new LogInViewModel();
    private View.OnClickListener mFabClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (!hasToken()) {
                Snackbar.make(view, R.string.dropbox_log_in, Snackbar.LENGTH_LONG)
                        .setAction(android.R.string.ok, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Auth.startOAuth2Authentication(MainActivity.this, getString(R.string.app_key));
                            }
                        }).show();
            } else {
                MainActivityPermissionsDispatcher.openGalleryWithCheck(MainActivity.this);
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        mPreferences = getSharedPreferences(AUTH_TOKEN, MODE_PRIVATE);
        verifyAuth();
        mLogIn.setLoggedIn(hasToken());
    }

    private void verifyAuth() {
        mToken = mPreferences.getString(AUTH_TOKEN, null);

        if (mToken == null) {
            mToken = Auth.getOAuth2Token();
            if (mToken != null) {
                mPreferences.edit().putString(AUTH_TOKEN, mToken).apply();
                DropboxClientFactory.setDbxClient(mToken);
                getAccountInfo();
            }
        } else {
            DropboxClientFactory.setDbxClient(mToken);
            getAccountInfo();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        Toolbar toolbar = mBinding.appBarMain.toolbar;
        setSupportActionBar(toolbar);

        FloatingActionButton fab = mBinding.appBarMain.fab;
        fab.setOnClickListener(mFabClickListener);

        DrawerLayout drawer = mBinding.drawerLayout;
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        );
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = mBinding.navView;
        navigationView.setNavigationItemSelectedListener(this);

        mHeaderBinding = DataBindingUtil.bind(navigationView.getHeaderView(0));
        mHeaderBinding.setLogIn(mLogIn);
        mHeaderBinding.logInButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (hasToken()) {
                            mToken = null;
                            mPreferences.edit().putString(AUTH_TOKEN, mToken).apply();
                            mHeaderBinding.setAccount(null);
                            mLogIn.setLoggedIn(hasToken());
                            Toast.makeText(MainActivity.this, "Successfully logged out", Toast.LENGTH_SHORT).show();
                        } else {
                            Auth.startOAuth2Authentication(MainActivity.this, getString(R.string.app_key));
                        }
                    }
                }
        );
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
        startActivityForResult(
                Intent.createChooser(intent, getString(R.string.pictures_choose)),
                SELECT_PICTURE
        );
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @OnShowRationale(Manifest.permission.READ_EXTERNAL_STORAGE)
    void onShowStorageRationale(final PermissionRequest request) {
        showPermissionRationaleDialog(request);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @OnPermissionDenied(Manifest.permission.READ_EXTERNAL_STORAGE)
    void onPermissionDenied() {
        Toast.makeText(MainActivity.this, R.string.pictures_no_access, Toast.LENGTH_SHORT).show();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @OnNeverAskAgain(Manifest.permission.READ_EXTERNAL_STORAGE)
    void onNeverAskAgain() {
        showPermissionSnackbar();
    }

    private void showPermissionRationaleDialog(final PermissionRequest request) {
        new AlertDialog.Builder(this)
                .setMessage(R.string.storage_permission_rationale)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        request.proceed();
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        request.cancel();
                    }
                })
                .show();
    }

    private void showPermissionSnackbar() {
        final Snackbar snackbar = Snackbar.make(
                mBinding.appBarMain.fab,
                R.string.storage_permission_rationale,
                Snackbar.LENGTH_INDEFINITE
        );

        snackbar.setAction(
                getString(android.R.string.ok),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri uri = Uri.fromParts(
                                SCHEME_PACKAGE,
                                getApplicationContext().getPackageName(),
                                null
                        );
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, uri);
                        startActivity(intent);
                        snackbar.dismiss();
                    }
                }
        ).show();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = mBinding.drawerLayout;
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

        switch (id) {
            case R.id.nav_uploads:
                break;
        }

        mBinding.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ((resultCode != RESULT_OK)) return;

        switch (requestCode) {
            case SELECT_PICTURE:
                uploadPicture(data.getData());
                break;
            default:
                break;
        }
    }

    private ProgressDialog createUploadProgressDialog() {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(false);
        dialog.setMessage(getString(R.string.upload_progress));
        return dialog;
    }

    private void getAccountInfo() {
        new AccountInfoTask(DropboxClientFactory.getClient(), new AccountInfoTask.Callback() {
            @Override
            public void onComplete(FullAccount result) {
                if (mAccountVm == null) {
                    mAccountVm = new FullAccountViewModel(result);
                } else {
                    mAccountVm.setAccount(result);
                }

                // Only need to set if we removed the reference
                if (mHeaderBinding.getAccount() == null) {
                    mHeaderBinding.setAccount(mAccountVm);
                }
            }

            @Override
            public void onError(Exception e) {

            }
        }).execute();
    }

    private void uploadPicture(Uri fileUri) {
        final ProgressDialog dialog = createUploadProgressDialog();
        dialog.show();

        new UploadTask(this, DropboxClientFactory.getClient(), new UploadTask.UploadCallback() {
            @Override
            public void onSuccess(FileMetadata metadata) {
                dialog.dismiss();

                String message = metadata.getName() + " size " + metadata.getSize() + " modified " +
                        DateFormat.getDateTimeInstance().format(metadata.getClientModified());
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();

                // Reload the folder
            }

            @Override
            public void onFailure(Exception e) {
                dialog.dismiss();

                Log.e(MainActivity.this.getClass().getSimpleName(), getString(R.string.upload_failed), e);
                Toast.makeText(MainActivity.this, R.string.error_message, Toast.LENGTH_SHORT).show();
            }
        }).execute(fileUri.toString(), "");
    }
}
