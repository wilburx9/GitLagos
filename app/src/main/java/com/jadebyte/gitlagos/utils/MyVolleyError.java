package com.jadebyte.gitlagos.utils;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.jadebyte.gitlagos.R;

import java.lang.ref.WeakReference;public class MyVolleyError {

    public static String errorMessage(VolleyError error, Context activity) {
        WeakReference<Context> weakContext = new WeakReference<>(activity);
        Context context = weakContext.get();
        String errorString = null;
        if (context != null && error != null) {

            if (error instanceof TimeoutError) {
                errorString = context.getString(R.string.connect_time_o);
            } else if (error instanceof NoConnectionError) {
                errorString = context.getString(R.string.no_connect_err);
            } else if (error instanceof AuthFailureError) {
                errorString = context.getString(R.string.auth_error);
            } else if (error instanceof ServerError) {
                errorString = context.getString(R.string.server_error);
            } else if (error instanceof NetworkError) {
                errorString = context.getString(R.string.network_error);
            } else if (error instanceof ParseError) {
                errorString = context.getString(R.string.parse_error);
            } else {
                errorString = context.getString(R.string.sth_wrong);
            }
        }

        return errorString;
    }
}
