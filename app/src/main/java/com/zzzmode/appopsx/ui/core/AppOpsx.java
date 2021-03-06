package com.zzzmode.appopsx.ui.core;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

import com.zzzmode.appopsx.OpsxManager;
import com.zzzmode.appopsx.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by zl on 2016/11/19.
 */

public class AppOpsx {

  private static OpsxManager sManager;

  public static OpsxManager getInstance(Context context) {
    if (sManager == null) {
      synchronized (AppOpsx.class) {
        if (sManager == null) {
          OpsxManager.Config config = new OpsxManager.Config();
          updateConfig(context, config);
          sManager = new OpsxManager(context.getApplicationContext(), config);
        }
      }
    }
    return sManager;
  }

  public static void updateConfig(Context context) {
  }


  private static void updateConfig(Context context, OpsxManager.Config config) {
  }

  public static String readLogs(Context context) {
    return "";
  }


}
