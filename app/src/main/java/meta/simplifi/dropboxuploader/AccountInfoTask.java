package meta.simplifi.dropboxuploader;

import android.os.AsyncTask;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.users.FullAccount;

/**
 * Created by SeptimusX75 (msilva28.dev@gmail.com) on 6/13/2016.
 */
public class AccountInfoTask extends AsyncTask<Void, Void, FullAccount> {
    private final DbxClientV2 mDbxClient;
    private final Callback mCallback;
    private Exception mException;

    public interface Callback {
        void onComplete(FullAccount result);

        void onError(Exception e);
    }

    AccountInfoTask(DbxClientV2 dbxClient, Callback callback) {
        mDbxClient = dbxClient;
        mCallback = callback;
    }

    @Override
    protected void onPostExecute(FullAccount account) {
        super.onPostExecute(account);
        if (mException != null) {
            mCallback.onError(mException);
        } else {
            mCallback.onComplete(account);
        }
    }

    @Override
    protected FullAccount doInBackground(Void... params) {

        try {
            return mDbxClient.users().getCurrentAccount();

        } catch (DbxException e) {
            mException = e;
        }

        return null;
    }
}
