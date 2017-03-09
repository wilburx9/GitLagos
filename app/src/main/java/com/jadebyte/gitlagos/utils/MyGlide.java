/**
 * Created by William Wilbur on 3/8/17.
 */
package com.jadebyte.gitlagos.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.jadebyte.gitlagos.R;

import java.lang.ref.WeakReference;

public class MyGlide{
    public static void load(Context context, ImageView view, String url, final ProgressBar progressBar) {
        final WeakReference<Context> weakCxt = new WeakReference<>(context);
        final WeakReference<ImageView> weakView = new WeakReference<>(view);
        final WeakReference<ProgressBar> weakProg = new WeakReference<>(progressBar);
        Glide.with(weakCxt.get())
                .load(url)
                .asBitmap()
                .error(R.drawable.ic_default_avatar)
                .placeholder(R.drawable.ic_default_avatar)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .centerCrop()
                .listener(new RequestListener<String, Bitmap>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
                        if (progressBar != null) {
                            progressBar.setVisibility(View.GONE);
                        }
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        if (progressBar != null) {
                            progressBar.setVisibility(View.GONE);
                        }
                        return false;
                    }
                })
                .into(new BitmapImageViewTarget(weakView.get()) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable bitmapDrawable = RoundedBitmapDrawableFactory
                                .create(weakCxt.get().getResources(), resource);
                        bitmapDrawable.setCircular(true);
                        weakView.get().setImageDrawable(bitmapDrawable);
                    }
                });
    }
}
