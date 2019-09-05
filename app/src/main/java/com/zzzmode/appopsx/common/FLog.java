package com.zzzmode.appopsx.common;

import android.util.Log;
import com.zzzmode.appopsx.BuildConfig;

/**
 * Created by zl on 2017/2/26.
 */
public class FLog {
  public static void log(String log) {
    if (BuildConfig.DEBUG)
      Log.d("appopsx", log);
  }

  public static void log(Throwable e) {
    log(Log.getStackTraceString(e));
  }
}

