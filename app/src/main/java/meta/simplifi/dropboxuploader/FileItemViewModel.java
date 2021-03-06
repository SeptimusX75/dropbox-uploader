package meta.simplifi.dropboxuploader;

import android.databinding.Bindable;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import com.dropbox.core.v2.files.FileMetadata;

import meta.simplifi.core.viewmodel.BaseViewModel;

/**
 * Created by SeptimusX75 (msilva28.dev@gmail.com) on 6/13/2016.
 * Controls display elements for file row items
 */
public class FileItemViewModel extends BaseViewModel {

    private FileMetadata mFileMetadata;

    public FileItemViewModel(FileMetadata fileMetadata) {
        setFileMetadata(fileMetadata);
    }

    public FileMetadata getFileMetadata() {
        return mFileMetadata;
    }

    public void setFileMetadata(FileMetadata fileMetadata) {
        mFileMetadata = fileMetadata;
        notifyChange();
    }

    @Bindable
    public String getName() {
        return mFileMetadata.getName();
    }

    @Bindable
    public Uri getFileThumbnailUri() {
        MimeTypeMap typeMap = MimeTypeMap.getSingleton();
        String extension = mFileMetadata.getName().substring(mFileMetadata.getName().indexOf(".") + 1);
        String type = typeMap.getMimeTypeFromExtension(extension);

        // Check if the file is an image or not
        if (type == null || !type.startsWith("image/")) {
            return Uri.parse("");
        } else {
            return FileThumbnailRequestHandler.buildPicassoUri(mFileMetadata);
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.layout_file_item;
    }

    @Override
    public int getVariableId() {
        return BR.fileItem;
    }
}
