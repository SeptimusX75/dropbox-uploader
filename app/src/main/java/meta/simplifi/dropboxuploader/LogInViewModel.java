package meta.simplifi.dropboxuploader;

import android.databinding.Bindable;
import android.support.annotation.StringRes;
import android.view.View;

import meta.simplifi.core.viewmodel.BaseViewModel;

/**
 * Created by SeptimusX75 (msilva28.dev@gmail.com) on 6/13/2016.
 */
public class LoginViewModel extends BaseViewModel {

    private boolean mLoggedIn;

    @Bindable
    public boolean isLoggedIn() {
        return mLoggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        mLoggedIn = loggedIn;
        notifyChange();
    }

    @Bindable
    public int getDropboxLogoVisibility() {
        return mLoggedIn ? View.GONE : View.VISIBLE;
    }

    @Bindable
    @StringRes
    public int getLogInButtonTextResId() {
        return mLoggedIn ? R.string.log_out : R.string.log_in;
    }

    @Override
    public int getLayoutId() {
        return R.layout.nav_header_main;
    }

    @Override
    public int getVariableId() {
        return BR.login;
    }
}
