package meta.simplifi.dropboxuploader;

import android.content.Context;

import com.dropbox.core.v2.DbxClientV2;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

/**
 * Borrowed from https://github.com/dropbox/dropbox-sdk-java
 * Specifically for downloading thumbnails of uploaded pictures after the fact
 */
public class ImageLibClient {

    private static Picasso sPicasso;

    public static void init(Context context, DbxClientV2 dbxClient) {
        sPicasso = new Picasso.Builder(context)
                .downloader(new OkHttpDownloader(context))
                .addRequestHandler(new FileThumbnailRequestHandler(dbxClient))
                .build();
    }

    public static Picasso getPicasso() {
        return sPicasso;
    }
}
