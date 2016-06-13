package meta.simplifi.dropboxuploader;

import android.databinding.BindingAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by SeptimusX75 (msilva28.dev@gmail.com) on 6/13/2016.
 */
public class BindingAdapters {

    @BindingAdapter("android:src")
    public static void setSrc(ImageView imageView, String src) {
        Picasso.with(imageView.getContext()).load(src).fit().centerInside().into(imageView);
    }
}
