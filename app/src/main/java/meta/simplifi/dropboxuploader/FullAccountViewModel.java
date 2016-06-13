package meta.simplifi.dropboxuploader;

import android.databinding.Bindable;

import com.dropbox.core.v2.users.FullAccount;

import meta.simplifi.core.viewmodel.BaseViewModel;

/**
 * Created by SeptimusX75 (msilva28.dev@gmail.com) on 6/13/2016.
 */
public class FullAccountViewModel extends BaseViewModel {
    private FullAccount mAccount;

    public FullAccountViewModel(FullAccount account) {
        setAccount(account);
    }

    @Bindable
    public FullAccount getAccount() {
        return mAccount;
    }

    public void setAccount(FullAccount account) {
        mAccount = account;
        notifyChange();
    }

    @Bindable
    public String getName() {
        return mAccount.getName().getDisplayName();
    }

    @Bindable
    public String getEmail() {
        return mAccount.getEmail();
    }

    @Bindable
    public String getPhotoUrl() {
        String url = mAccount.getProfilePhotoUrl();
        if (url == null) {
            return UriUtil.getPathToResource(android.R.drawable.sym_def_app_icon);
        }
        return url;
    }

    @Override
    public int getLayoutId() {
        return R.layout.nav_header_main;
    }

    @Override
    public int getVariableId() {
        return BR.account;
    }
}
