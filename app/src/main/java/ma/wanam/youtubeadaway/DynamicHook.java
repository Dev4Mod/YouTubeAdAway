package ma.wanam.youtubeadaway;

import android.text.TextUtils;
import android.util.Log;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import io.luckypray.dexkit.DexKitBridge;
import io.luckypray.dexkit.descriptor.member.DexMethodDescriptor;
import ma.wanam.youtubeadaway.utils.Opcode;

public class DynamicHook {

    private static final String filterAds = new StringBuffer().append(".*(").append(String.join("|",new String[]{
            "ads_video_with_context",
            "banner_text_icon",
            "square_image_layout",
            "watch_metadata_app_promo",
            "video_display_full_layout",
            "browsy_bar",
            "compact_movie",
            "horizontal_movie_shelf",
            "movie_and_show_upsell_card",
            "compact_tvfilm_item",
            "video_display_full_buttoned_layout",
            "full_width_square_image_layout",
            "_ad_with",
            "landscape_image_wide_button_layout",
            "carousel_ad",
            "in_feed_survey",
            "compact_banner",
            "medical_panel",
            "paid_content_overlay",
            "product_carousel"
    })).append(").*").toString();

    private static final String filterIgnore = new StringBuffer().append(".*(").append(String.join("|",new String[]{
            "home_video_with_context",
            "related_video_with_context",
            "comment_thread",
            "comment\\.",
            "download_",
            "library_recent_shelf",
            "playlist_add_to_option_wrapper"
    })).append(").*").toString();

    public static final String TAG = "DexUtils";

    static {
        System.loadLibrary("dexkit");
    }

    private final XC_LoadPackage.LoadPackageParam lpparam;

    public DynamicHook(XC_LoadPackage.LoadPackageParam loadPackageParam) {
        this.lpparam = loadPackageParam;
        try {
            String apkPath = lpparam.appInfo.sourceDir;
            DexKitBridge dexKitBridge = DexKitBridge.create(apkPath);
            if (dexKitBridge == null) {
                Log.e(TAG, "DexKitBridge create failed");
                return;
            }
            hookBGEnabled(dexKitBridge);
            hookAdsInVideo(dexKitBridge);
            hookAdsInGeneral(dexKitBridge);
            Log.e(TAG, "HOOKED BY DEV");
        } catch (Exception ignored) {
        }
    }

    private void hookBGEnabled(DexKitBridge dexKitBridge) {
        Instant start = Instant.now();
        List<DexMethodDescriptor> result;
        try {
            result = dexKitBridge.findMethodUsingOpCodeSeq(new int[]{18,56,82,213,56,84,57,98,82,20,51,84,57},"","","",null,null);
            for(DexMethodDescriptor dexMethodDescriptor : result) {
                Log.i(TAG,"A:"+result);
                Class<?> cls = XposedHelpers.findClass(dexMethodDescriptor.getDeclaringClassName(), lpparam.classLoader);
                List<Method> fMethods = Arrays.asList(cls.getDeclaredMethods()).parallelStream().filter(method -> method.getParameterTypes().length == 1
                        && method.getParameterTypes()[0].getName().length() == 4
                        && method.getReturnType().equals(boolean.class)
                        && method.getName().equals(method.getName().toLowerCase())
                        && java.lang.reflect.Modifier.isStatic(method.getModifiers())
                        && java.lang.reflect.Modifier.isPublic(method.getModifiers())
                ).collect(Collectors.toList());

                if(fMethods.size() > 5) {
                    XposedBridge.hookMethod(fMethods.get(0), XC_MethodReplacement.returnConstant(true));
                    XposedBridge.log("YoutubeAdway: BG playback Hooked class:" + result.get(0));
                    XposedBridge.log("YoutubeAdway: BG playback Hooked in " + Duration.between(start, Instant.now()).getSeconds() + " seconds!");
                    break;
                }
            }

            result = dexKitBridge.findMethodUsingOpCodeSeq(Stream.of(
                    Opcode.INVOKE_VIRTUAL,
                    Opcode.MOVE_RESULT,
                    Opcode.INVOKE_VIRTUAL,
                    Opcode.MOVE_RESULT,
                    Opcode.IF_EQZ,
                    Opcode.IF_NEZ,
                    Opcode.GOTO,
                    Opcode.IGET_OBJECT,
                    Opcode.CHECK_CAST).mapToInt(Opcode::getValue).toArray(),"","","",null,null);

            for(DexMethodDescriptor dexMethodDescriptor : result) {
                Class<?> cls = XposedHelpers.findClass(dexMethodDescriptor.getDeclaringClassName(), lpparam.classLoader);
                List<Method> fMethods = Arrays.asList(cls.getDeclaredMethods()).parallelStream().filter(
                        method -> method.getParameterTypes().length == 0
                        && method.getReturnType().equals(boolean.class)
                        && java.lang.reflect.Modifier.isFinal(method.getModifiers())
                        && java.lang.reflect.Modifier.isPublic(method.getModifiers())
                ).collect(Collectors.toList());

                if(fMethods.size() > 5) {
                    XposedBridge.hookMethod(fMethods.get(2), XC_MethodReplacement.returnConstant(true));
                    XposedBridge.log("YoutubeAdway: BG playback Setting Hooked class:" + fMethods.get(2));
                    XposedBridge.log("YoutubeAdway: BG playback Setting Hooked in " + Duration.between(start, Instant.now()).getSeconds() + " seconds!");
                    break;
                }
            }
        } catch (Throwable e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }


    private void hookAdsInVideo(DexKitBridge dexKitBridge) {
        Instant start = Instant.now();
        try {
            List<DexMethodDescriptor> result = dexKitBridge.findMethodUsingString("markFillRequested", false, "", "", "", null, true, null);
            if (result.size() > 0) {
                Method method = result.get(0).getMethodInstance(lpparam.classLoader);
                XposedBridge.hookMethod(method, XC_MethodReplacement.returnConstant(null));
                XposedBridge.log("YoutubeAdway: adsInVideo Hooked class:" + result.get(0));
                XposedBridge.log("YoutubeAdway: adsInVideo Hooked in " + Duration.between(start, Instant.now()).getSeconds() + " seconds!");
            }
        } catch (Throwable e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }

    private void hookAdsInGeneral(DexKitBridge dexKitBridge) {
        Instant start = Instant.now();
        List<DexMethodDescriptor> result;
        Method emptyComponentMethod = null;

        try {
            result = dexKitBridge.findMethodUsingString("EmptyComponent", false, "", "", "", null, false, null);

            for(DexMethodDescriptor dexMethodDescriptor : result){
                Class<?> cls = XposedHelpers.findClass(dexMethodDescriptor.getDeclaringClassName(), lpparam.classLoader);
                List<Method> fMethods = Arrays.asList(cls.getDeclaredMethods()).parallelStream().filter(method ->
                        Modifier.isPublic(method.getModifiers())
                                && Modifier.isStatic(method.getModifiers())
                                && method.getParameterTypes().length == 1
                ).collect(Collectors.toList());
                if(fMethods.isEmpty())continue;
                emptyComponentMethod = fMethods.get(0);
                break;
            }

            if (emptyComponentMethod == null) {
                Log.i(TAG, "Failed to find EmptyComponent");
                XposedBridge.log("Failed to find EmptyComponent");
                return;
            }

            result = dexKitBridge.findMethodUsingString("Error while converting", false, "", "", "", null, true, null);
            if (!result.isEmpty()) {
                Method fmethod = result.get(0).getMethodInstance(lpparam.classLoader);
                hookGeneralMethods(fmethod, emptyComponentMethod);
                XposedBridge.log("YoutubeAdway: AdsInGeneral Hooked class:" + result.get(0));
                XposedBridge.log("YoutubeAdway: AdsInGeneral Hooked in " + Duration.between(start, Instant.now()).getSeconds() + " seconds!");
            }
        } catch (Throwable e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }


    private void hookGeneralMethods(Method cMethod, final Method emptyMethod) {
        final Optional<Method> filterMethod = Arrays.asList(cMethod.getParameterTypes()[1].getDeclaredMethods()).parallelStream().filter(method ->
                Modifier.isPublic(method.getModifiers())
                        && Modifier.isFinal(method.getModifiers())
                        && method.getName().length() == 1
                        && method.getParameterTypes().length == 1
                        && method.getParameterTypes()[0].equals(String.class)
        ).findFirst();

        if (!filterMethod.isPresent()) {
            XposedBridge.log("YouTube AdAway: Failed find filter method");
            return;
        }

        XposedBridge.hookMethod(cMethod, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                String val = (String) filterMethod.get().invoke(param.args[1], "");
                if(!TextUtils.isEmpty(val) && !val.matches(filterIgnore) && val.matches(filterAds)){
                    Object x = emptyMethod.invoke(null, param.args[0]);
                    Object y = XposedHelpers.getObjectField(x, "a");
                    param.setResult(y);
                }
            }
        });
    }


}
