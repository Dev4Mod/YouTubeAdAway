package ma.wanam.youtubeadaway;

import android.util.Log;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import ma.wanam.youtubeadaway.utils.Constants;
import ma.wanam.youtubeadaway.utils.Utils;

public class Xposed implements IXposedHookLoadPackage {

    @Override
    public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {

        if (lpparam.packageName.equals(Constants.GOOGLE_YOUTUBE_PACKAGE)) {
            try {
                String ytVersion = Utils.getPackageVersion(lpparam);
                XposedBridge.log("Hooking YouTube version: " + lpparam.packageName + " " + ytVersion);
                new BFAsync().execute(lpparam);
            } catch (Throwable t) {
                XposedBridge.log(t);
            }
        }

        if (lpparam.packageName.equals(BuildConfig.APPLICATION_ID)) {
            XposedBridge.log("YouTube AdAway version: " + Utils.getPackageVersion(lpparam));
            try {
                XposedHelpers.findAndHookMethod(BuildConfig.APPLICATION_ID + ".XChecker", lpparam.classLoader,
                        "isEnabled", XC_MethodReplacement.returnConstant(Boolean.TRUE));
            } catch (Throwable t) {
                XposedBridge.log(t);
            }
        }

    }




}
