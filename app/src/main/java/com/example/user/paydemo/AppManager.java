package com.example.user.paydemo;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;

import java.util.Stack;

/**
 * App管理Activity类
 */
public class AppManager {
    private static Stack<Activity> activityStack = new Stack<>();

    /**
     * 添加Activity到堆栈
     */
    public static void addActivity(Activity activity) {
        activityStack.push(activity);
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    public static Activity currentActivity() {
        ActivityManager am = (ActivityManager) activityStack.firstElement().getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
        Activity activity = null;
        for (Activity activityItem : activityStack) {
            if (cn.getClassName().contains(activityItem.getLocalClassName())) {
                activity = activityItem;
                break;
            }
        }
        return activity == null ? activityStack.lastElement() : activity;
    }

    /**
     * 结束当前Activity（堆栈中最后一个压入的）
     */
    public static void finishCurrentActivity() {
        Activity activity = activityStack.pop();
        activity.finish();
    }

    /**
     * 结束指定的Activity
     */
    public static void finishActivity(Activity activity) {
        if (activity != null) {
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }

    /**
     * 结束指定类名的Activity
     */
    public static void finishActivity(Class<?> cls) {
        Activity factivity = null;
        for (Activity activity : activityStack) {
            if (activity.getClass().equals(cls)) {
                factivity = activity;
            }
        }
        finishActivity(factivity);
    }

    /**
     * 结束所有Activity
     */
    public static void finishAllActivity() {
        for (Activity activity : activityStack) {
            if (activity != null) {
                activity.finish();
                activity = null;
            }
        }
        activityStack.clear();
    }

    /**
     * 根据class获取activity
     */
    public static Activity getBaseActivity(Class cls) {
        Activity baseActivity = null;
        for (Activity activity : activityStack) {
            if (activity.getClass().equals(cls)) {
                baseActivity = activity;
            }
        }
        return baseActivity;
    }

    /**
     * 退出应用程序
     */
    public static void AppExit(Context context) {
        try {
            finishAllActivity();
            ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            manager.killBackgroundProcesses(context.getPackageName());
            //System.exit(0);
            android.os.Process.killProcess(android.os.Process.myPid());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void removeActivity(Activity activity) {
        activityStack.remove(activity);
        if (!activity.isFinishing()) {
            activity.finish();
        }
    }

    public static Application getApplication() {
        return currentActivity().getApplication();
    }
}
