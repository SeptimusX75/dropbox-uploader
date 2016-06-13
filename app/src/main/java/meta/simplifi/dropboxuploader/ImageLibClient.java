package meta.simplifi.dropboxuploader;

import android.content.Context;

import com.dropbox.core.v2.DbxClientV2;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

/**
 * Created by SeptimusX75 (msilva28.dev@gmail.com) on 6/13/2016.
 */
public class ImageLibClient {

    private static Picasso sPicasso;

    public static void init(Context context, DbxClientV2 dbxClient) {
        sPicasso = new Picasso.Builder(context)
                .downloader(new OkHttpDownloader(context))
                .addRequestHandler(null)
                .build();
    }

    public static Picasso getPicasso() {
        return sPicasso;
    }
}
