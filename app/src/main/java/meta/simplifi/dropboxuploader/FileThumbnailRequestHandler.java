package meta.simplifi.dropboxuploader;

import android.net.Uri;

import com.dropbox.core.DbxDownloader;
import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.ThumbnailFormat;
import com.dropbox.core.v2.files.ThumbnailSize;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Request;
import com.squareup.picasso.RequestHandler;

import java.io.IOException;

/**
 * Borrowed from https://github.com/dropbox/dropbox-sdk-java
 * For loading thumbnails of files after they've been uploaded
 */
public class FileThumbnailRequestHandler extends RequestHandler {

    private static final String SCHEME =  "dropbox";
    private static final String HOST = "dropbox";
    private final DbxClientV2 mDbxClient;

    public FileThumbnailRequestHandler(DbxClientV2 dbxClient) {
        mDbxClient = dbxClient;
    }

    /**
     * Builds a {@link Uri} for a Dropbox file thumbnail suitable for handling by this handler
     */
    public static Uri buildPicassoUri(FileMetadata file) {
        return new Uri.Builder()
                .scheme(SCHEME)
                .authority(HOST)
                .path(file.getPathLower()).build();
    }

    @Override
    public boolean canHandleRequest(Request data) {
        return SCHEME.equals(data.uri.getScheme()) && HOST.equals(data.uri.getHost());
    }

    @Override
    public Result load(Request request, int networkPolicy) throws IOException {

        try {
            DbxDownloader<FileMetadata> downloader =
                    mDbxClient.files().getThumbnailBuilder(request.uri.getPath())
                            .withFormat(ThumbnailFormat.JPEG)
                            .withSize(ThumbnailSize.W1024H768)
                            .start();

            return new Result(downloader.getInputStream(), Picasso.LoadedFrom.NETWORK);
        } catch (DbxException e) {
            throw new IOException(e);
        }
    }
}
