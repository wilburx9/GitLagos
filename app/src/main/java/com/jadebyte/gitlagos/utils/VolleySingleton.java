package com.jadebyte.gitlagos.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

@SuppressWarnings("unused")
public class VolleySingleton {
    private static VolleySingleton sVolleySingleton;
    private Context sContext;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    public VolleySingleton(Context context) {
        sContext = context;
        this.mRequestQueue = getRequestQueue();

        mImageLoader = new ImageLoader(mRequestQueue,
                new  ImageLoader.ImageCache() {
                    private final LruCache<String, Bitmap>
                    mCache = new LruCache<>(20);

                    @Override
                    public Bitmap getBitmap(String url) {
                        return mCache.get(url);
                    }

                    @Override
                    public void putBitmap(String url, Bitmap bitmap) {
                        mCache.put(url, bitmap);
                    }

                });
    }

    public static synchronized VolleySingleton getInstance(Context context) {
        if (sVolleySingleton == null) {
            sVolleySingleton = new VolleySingleton(context);
        }
        return sVolleySingleton;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(sContext.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    public ImageLoader getImageLoader() {
        return mImageLoader;
    }
}