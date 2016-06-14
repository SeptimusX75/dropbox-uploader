package meta.simplifi.dropboxuploader;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.http.OkHttp3Requestor;
import com.dropbox.core.v2.DbxClientV2;

/**
 * Borrowed from https://github.com/dropbox/dropbox-sdk-java
 */
public class DropboxClientFactory {
    private static DbxClientV2 sDbxClient;

    public static void setDbxClient(String accessToken) {
        if (sDbxClient == null) {
            DbxRequestConfig requestConfig = DbxRequestConfig.newBuilder("Android Cloud Image Backup")
                    .withHttpRequestor(OkHttp3Requestor.INSTANCE)
                    .build();

            sDbxClient = new DbxClientV2(requestConfig, accessToken);
        }
    }

    public static DbxClientV2 getClient() {
        if (sDbxClient == null) {
            throw new IllegalStateException("Dropbox client not initialized");
        }
        return sDbxClient;
    }
}
