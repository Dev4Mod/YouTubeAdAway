package ma.wanam.youtubeadaway;

import android.text.TextUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
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
import io.github.neonorbit.dexplore.DexFactory;
import io.github.neonorbit.dexplore.Dexplore;
import io.github.neonorbit.dexplore.filter.ClassFilter;
import io.github.neonorbit.dexplore.filter.DexFilter;
import io.github.neonorbit.dexplore.filter.MethodFilter;
import io.github.neonorbit.dexplore.filter.ReferenceFilter;
import io.github.neonorbit.dexplore.filter.ReferenceTypes;
import io.github.neonorbit.dexplore.result.MethodData;

public class DexHook {

    private static final List<String> filterAds = Arrays.asList(
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
            "carousel_ad"
    );

    private static final List<String> filterIgnore = Arrays.asList(
            "home_video_with_context",
            "related_video_with_context",
            "comment_thread",
            "|comment.",
            "download_",
            "library_recent_shelf",
            "playlist_add_to_option_wrapper");


    private final XC_LoadPackage.LoadPackageParam lpparam;

    public DexHook(XC_LoadPackage.LoadPackageParam lpparam){
        this.lpparam = lpparam;
        Dexplore dexplore = DexFactory.load(lpparam.appInfo.sourceDir);
        hookAdsInVideo(dexplore);
        hookAdsGeneral(dexplore);
    }

    private void hookAdsInVideo(Dexplore dexplore){
        MethodData result = FindDex.findMethodAdsInVideo(dexplore);
        if(result == null)return;
        Method method = result.loadMethod(lpparam.classLoader);
        XposedBridge.log("Hooked ads in video:"+ result);
        XposedBridge.hookMethod(method, XC_MethodReplacement.returnConstant(null));
    }

    private void hookAdsGeneral(Dexplore dexplore){
        MethodData[] results = FindDex.findMethodsAdsGeneral(dexplore);
        if(results == null)return;
        List<Method> methods = Arrays.asList(results).parallelStream()
                .map(methodData -> methodData.loadMethod(lpparam.classLoader))
        .collect(Collectors.toList());
        hookGeneral(methods.get(0),methods.get(1));
        XposedBridge.log("YouTube AdAway: Hooked General ads method"+results[0]);
    }

    private void hookGeneral(Method cMethod, final Method emptyMethod) {
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
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                String val = (String) filterMethod.get().invoke(param.args[1], "");
                if (!TextUtils.isEmpty(val)
                        && filterIgnore.parallelStream().noneMatch(filter -> val.contains(filter))
                        && filterAds.parallelStream().anyMatch(filter -> val.contains(filter))) {
                    Object x = emptyMethod.invoke(null, param.args[0]);
                    Object y = XposedHelpers.getObjectField(x, "a");
                    param.setResult(y);
                }
            }
        });
    }

}
