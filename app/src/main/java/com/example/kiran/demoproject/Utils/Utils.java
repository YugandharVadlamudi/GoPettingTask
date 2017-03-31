package com.example.kiran.demoproject.Utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.example.kiran.demoproject.R;

/**
 * Created by Kiran on 30-03-2017.
 */

public class Utils {
    public static final String URL_BOOKS = "https://guidebook.com/service/v2/upcomingGuides/";

    public static void printLog(char type, String name, String response) {
        switch (type) {
            case 'd':
                Log.d(name, "printLog: " + response);
                break;
            case 'e':
                Log.e(name, "printLog: " + response);
                break;
        }
    }

    public static ProgressDialog dialogLoading(Context context,String text) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(text);
        progressDialog.setIndeterminate(true);
        progressDialog.setCanceledOnTouchOutside(false);
        return progressDialog;
    }


}
