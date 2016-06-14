package meta.simplifi.dropboxuploader;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.dropbox.core.android.Auth;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;

import java.util.List;

import meta.simplifi.core.adapter.BindingRecyclerAdapter;
import meta.simplifi.core.fragment.BindingRecyclerFragment;

/**
 * A fragment displaying recently uploaded files.
 */
public class UploadedFilesFragment extends BindingRecyclerFragment<BindingRecyclerAdapter<FileItemViewModel>> {

    public UploadedFilesFragment() {
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        DbxClientV2 client = DropboxClientFactory.getClient();
        if (client != null) {
            DropboxClientFactory.setDbxClient(Auth.getOAuth2Token());
        }
        new ListFolderTask(
                client,
                new ListFolderTask.Callback() {
                    @Override
                    public void onDataLoaded(ListFolderResult result) {
                        List<Metadata> entries = result.getEntries();
                        for (Metadata entry : entries) {
                            if (entry instanceof FileMetadata) {
                                getAdapter().add(new FileItemViewModel((FileMetadata) entry));
                            }
                        }
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                }
        ).execute("");
    }

    @NonNull
    @Override
    protected BindingRecyclerAdapter<FileItemViewModel> createBindingAdapter() {
        return new BindingRecyclerAdapter<>();
    }

    @Override
    public void onItemClick(View v, int position) {

    }
}
