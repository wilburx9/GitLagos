/**
 * Created by William Wilbur on 3/8/17.
 */
package com.jadebyte.gitlagos.activities;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;

public class MyFile {
    public static String readFromAssets(Context context, String filename){
        WeakReference<Context> weakCxt = new WeakReference<>(context);
        BufferedReader reader;
        try {
            reader = new BufferedReader(new InputStreamReader(weakCxt.get().getAssets().open(filename)));

            StringBuilder sb = new StringBuilder();
            String mLine = reader.readLine();
            while (mLine != null) {
                sb.append(mLine); // process line
                mLine = reader.readLine();
            }
            reader.close();
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
