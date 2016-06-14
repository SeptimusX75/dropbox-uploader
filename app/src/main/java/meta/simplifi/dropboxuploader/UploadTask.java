package meta.simplifi.dropboxuploader;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.WriteMode;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Borrowed from https://github.com/dropbox/dropbox-sdk-java
 */
public class UploadTask extends AsyncTask<String, Void, FileMetadata> {

    private final Context mContext;
    private final DbxClientV2 mDbxClient;
    private final Callback mCallback;
    private Exception mException;

    public UploadTask(Context context, DbxClientV2 dbxClient, Callback callback) {
        mContext = context;
        mDbxClient = dbxClient;
        mCallback = callback;
    }

    @Override
    protected void onPostExecute(FileMetadata metadata) {
        super.onPostExecute(metadata);
        if (mException != null) {
            mCallback.onFailure(mException);
        } else if (metadata == null) {
            mCallback.onFailure(null);
        } else {
            mCallback.onSuccess(metadata);
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected FileMetadata doInBackground(String... params) {
        File file = UriUtil.getFileForUri(mContext, Uri.parse(params[0]));
        if (file != null) {
            String remoteFolderPath = params[1];

            // Note - this is not ensuring the name is a valid dropbox file name
            String remoteFileName = file.getName();

            try (InputStream inputStream = new FileInputStream(file)) {
                return mDbxClient.files().uploadBuilder(remoteFolderPath + "/" + remoteFileName)
                        .withMode(WriteMode.OVERWRITE)
                        .uploadAndFinish(inputStream);
            } catch (DbxException | IOException e) {
                mException = e;
            }
        }

        return null;
    }

    public interface Callback {
        void onSuccess(FileMetadata metadata);

        void onFailure(Exception e);
    }
}
