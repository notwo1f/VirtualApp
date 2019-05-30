package com.lody.virtual.client.env;

import android.Manifest;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.helper.utils.VLog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

/**
 * @author Lody
 */
public final class SpecialComponentList {

    private static final List<ComponentName> GMS_BLOCK_COMPONENT = Arrays.asList(
            new ComponentName("com.google.android.gms", "com.google.android.gms.update.SystemUpdateService"),
            new ComponentName("com.google.android.gsf", "com.google.android.gsf.update.SystemUpdateService")
    );

    private static final List<String> GMS_BLOCK_ACTION_LIST = Arrays.asList(
            "com.google.android.gms.update.START_SERVICE"
    );

    private static final List<String> ACTION_BLACK_LIST = new ArrayList<String>(2);
    private static final Map<String, String> PROTECTED_ACTION_MAP = new HashMap<>(5);
    private static final HashSet<String> WHITE_PERMISSION = new HashSet<>(3);
    private static final HashSet<String> BROADCAST_START_WHITE_LIST = new HashSet<>();
    private static final HashSet<String> INSTRUMENTATION_CONFLICTING = new HashSet<>(2);
    private static final HashSet<String> SPEC_SYSTEM_APP_LIST = new HashSet<>(3);
    private static final Set<String> SYSTEM_BROADCAST_ACTION = new HashSet<>(7);
    private static final Set<String> PRE_INSTALL_PACKAGES = new HashSet<>(7);
    private static final String PROTECT_ACTION_PREFIX = "_VA_protected_";

    static {
        SYSTEM_BROADCAST_ACTION.add(Intent.ACTION_SCREEN_ON);
        SYSTEM_BROADCAST_ACTION.add(Intent.ACTION_SCREEN_OFF);
        //SYSTEM_BROADCAST_ACTION.add(Intent.ACTION_NEW_OUTGOING_CALL);  //适配警信，安装警信后打电话慢
        SYSTEM_BROADCAST_ACTION.add(Intent.ACTION_TIME_TICK);
        SYSTEM_BROADCAST_ACTION.add(Intent.ACTION_TIME_CHANGED);
        SYSTEM_BROADCAST_ACTION.add(Intent.ACTION_TIMEZONE_CHANGED);
        SYSTEM_BROADCAST_ACTION.add(Intent.ACTION_BATTERY_CHANGED);
        SYSTEM_BROADCAST_ACTION.add(Intent.ACTION_BATTERY_LOW);
        SYSTEM_BROADCAST_ACTION.add(Intent.ACTION_BATTERY_OKAY);
        SYSTEM_BROADCAST_ACTION.add(Intent.ACTION_POWER_CONNECTED);
        SYSTEM_BROADCAST_ACTION.add(Intent.ACTION_POWER_DISCONNECTED);
        SYSTEM_BROADCAST_ACTION.add(Intent.ACTION_USER_PRESENT);
        SYSTEM_BROADCAST_ACTION.add("android.provider.Telephony.SMS_RECEIVED");
        SYSTEM_BROADCAST_ACTION.add("android.provider.Telephony.SMS_DELIVER");
        SYSTEM_BROADCAST_ACTION.add("android.net.wifi.STATE_CHANGE");
        SYSTEM_BROADCAST_ACTION.add("android.net.wifi.SCAN_RESULTS");
        SYSTEM_BROADCAST_ACTION.add("android.net.wifi.WIFI_STATE_CHANGED");
        SYSTEM_BROADCAST_ACTION.add("android.net.conn.CONNECTIVITY_CHANGE");
        SYSTEM_BROADCAST_ACTION.add("android.intent.action.ANY_DATA_STATE");
        SYSTEM_BROADCAST_ACTION.add("android.intent.action.SIM_STATE_CHANGED");
        SYSTEM_BROADCAST_ACTION.add("android.location.PROVIDERS_CHANGED");
        SYSTEM_BROADCAST_ACTION.add("android.location.MODE_CHANGED");
        SYSTEM_BROADCAST_ACTION.add(Intent.ACTION_HEADSET_PLUG);
        SYSTEM_BROADCAST_ACTION.add("android.media.VOLUME_CHANGED_ACTION");
        SYSTEM_BROADCAST_ACTION.add("android.intent.action.CONFIGURATION_CHANGED");
        SYSTEM_BROADCAST_ACTION.add("android.intent.action.DYNAMIC_SENSOR_CHANGED");
        SYSTEM_BROADCAST_ACTION.add("dynamic_sensor_change");
        SYSTEM_BROADCAST_ACTION.add("com.xdja.dialer.removecall");

        ACTION_BLACK_LIST.add(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        ACTION_BLACK_LIST.add(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE);

        WHITE_PERMISSION.add("android.permission.USE_XDJA_SIP_swbg");//税务办公
        WHITE_PERMISSION.add("com.google.android.gms.settings.SECURITY_SETTINGS");
        WHITE_PERMISSION.add("com.google.android.apps.plus.PRIVACY_SETTINGS");
        WHITE_PERMISSION.add(Manifest.permission.ACCOUNT_MANAGER);

        PROTECTED_ACTION_MAP.put(Intent.ACTION_PACKAGE_ADDED, Constants.ACTION_PACKAGE_ADDED);
        PROTECTED_ACTION_MAP.put(Intent.ACTION_PACKAGE_REMOVED, Constants.ACTION_PACKAGE_REMOVED);
        PROTECTED_ACTION_MAP.put(Intent.ACTION_PACKAGE_CHANGED, Constants.ACTION_PACKAGE_CHANGED);
        PROTECTED_ACTION_MAP.put(Intent.ACTION_BOOT_COMPLETED, Constants.ACTION_BOOT_COMPLETED);
        //TODO 是否需要改为仅内部媒体存储收到
        //update images/videos by media provider
        PROTECTED_ACTION_MAP.put(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        //xdja
        PROTECTED_ACTION_MAP.put("com.xdja.safety.action.enter", "com.xdja.safety.action.enter");
        PROTECTED_ACTION_MAP.put("com.xdja.safety.action.exit", "com.xdja.safety.action.exit");

        INSTRUMENTATION_CONFLICTING.add("com.qihoo.magic");
        INSTRUMENTATION_CONFLICTING.add("com.qihoo.magic_mutiple");
        INSTRUMENTATION_CONFLICTING.add("com.facebook.katana");

        SPEC_SYSTEM_APP_LIST.add("android");
        SPEC_SYSTEM_APP_LIST.add("com.google.android.webview");
        //PRE_INSTALL_PACKAGES.add("com.huawei.hwid");
    }

    public static Set<String> getPreInstallPackages() {
        return PRE_INSTALL_PACKAGES;
    }

    public static void addStaticBroadCastWhiteList(String pkg) {
        BROADCAST_START_WHITE_LIST.add(pkg);
    }

    public static boolean isSpecSystemPackage(String pkg) {
        return SPEC_SYSTEM_APP_LIST.contains(pkg);
    }

    public static boolean isConflictingInstrumentation(String packageName) {
        return INSTRUMENTATION_CONFLICTING.contains(packageName);
    }

    public static boolean shouldBlockIntent(Intent intent) {
        ComponentName component = intent.getComponent();
        if (component != null && GMS_BLOCK_COMPONENT.contains(component)) {
            return true;
        }
        String action = intent.getAction();
        if (action != null) {
            if (GMS_BLOCK_ACTION_LIST.contains(action)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if the action in the BlackList.
     *
     * @param action Action
     */
    public static boolean isActionInBlackList(String action) {
        return ACTION_BLACK_LIST.contains(action);
    }

    /**
     * Add an action to the BlackList.
     *
     * @param action action
     */
    public static void addBlackAction(String action) {
        ACTION_BLACK_LIST.add(action);
    }

    public static void protectIntentFilter(IntentFilter filter) {
        protectIntentFilter(filter, false);
    }

    public static void protectIntentFilter(IntentFilter filter, boolean ignoreBlack) {
        if (filter != null) {
            List<String> actions = mirror.android.content.IntentFilter.mActions.get(filter);
            ListIterator<String> iterator = actions.listIterator();
            while (iterator.hasNext()) {
                String action = iterator.next();
                if (!ignoreBlack && SpecialComponentList.isActionInBlackList(action)) {
                    iterator.remove();
                    continue;
                }
                String newAction = SpecialComponentList.protectAction(action);
                if (newAction != null) {
                    iterator.set(newAction);
                }
                VLog.d("lxf", action + "->" + newAction);
            }
        }
    }

    public static void protectIntent(Intent intent) {
        String protectAction = protectAction(intent.getAction());
        if (protectAction != null) {
            intent.setAction(protectAction);
        }
    }

    public static void unprotectIntent(Intent intent) {
        String unprotectAction = unprotectAction(intent.getAction());
        if (unprotectAction != null) {
            intent.setAction(unprotectAction);
        }
    }

    public static String protectAction(String originAction) {
        if (SYSTEM_BROADCAST_ACTION.contains(originAction)) {
            //处理A
            return originAction;
        }
        if (originAction == null) {
            return null;
        }
        //这种不用处理，大多数是主进程和应用进程通信的广播
        if (originAction.startsWith(VirtualCore.get().getHostPkg()+"_VA_")) {
            return originAction;
        }
        //test
        String newAction = PROTECTED_ACTION_MAP.get(originAction);
        if (newAction == null) {
            //处理B VirtualCore.get().getHostPkg() + PROTECT_ACTION_PREFIX + originAction
            newAction = PROTECT_ACTION_PREFIX + originAction;
        }
        //处理C，目的是广播隔离？
        VLog.e("lxf","protectAction "+newAction);
        return VirtualCore.get().getHostPkg() + newAction;
    }

    public static String unprotectAction(String action) {
        VLog.e("lxf","unprotectAction "+action);
        if (action == null) {
            return null;
        }
        if (SYSTEM_BROADCAST_ACTION.contains(action)) {
            //处理A
            return null;
        }
        //处理B
        if (action.startsWith(VirtualCore.get().getHostPkg() + PROTECT_ACTION_PREFIX)) {
            return action.substring((VirtualCore.get().getHostPkg() + PROTECT_ACTION_PREFIX).length());
        }
        //对应处理C
        if (action.startsWith(VirtualCore.get().getHostPkg())) {
            action = action.substring((VirtualCore.get().getHostPkg()).length());
        }
        //？？？
        for (Map.Entry<String, String> next : PROTECTED_ACTION_MAP.entrySet()) {
            String modifiedAction = next.getValue();
            if (modifiedAction.equals(action)) {
                VLog.e("lxf","unprotectAction next.getKey() "+next.getKey());
                return next.getKey();
            }
        }
        return null;
    }

    public static boolean isWhitePermission(String permission) {
        return WHITE_PERMISSION.contains(permission);
    }

    public static boolean allowedStartFromBroadcast(String str) {
        return BROADCAST_START_WHITE_LIST.contains(str);
    }
}
