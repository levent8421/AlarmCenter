package com.berrontech.huali.alarmcenter;

import android.app.Activity;

public class ViewUtils {
    @SuppressWarnings("unchecked")
    public static <T> T find(Activity activity, int viewId) {
        return (T) activity.findViewById(viewId);
    }
}
