package ma.wanam.youtubeadaway;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

import de.robv.android.xposed.XposedBridge;
import io.github.neonorbit.dexplore.Dexplore;
import io.github.neonorbit.dexplore.filter.ClassFilter;
import io.github.neonorbit.dexplore.filter.DexFilter;
import io.github.neonorbit.dexplore.filter.MethodFilter;
import io.github.neonorbit.dexplore.filter.ReferenceTypes;
import io.github.neonorbit.dexplore.result.MethodData;

public class FindDex {

    public static MethodData findMethodAdsInVideo(Dexplore dexplore){
        DexFilter dexFilter = new DexFilter.Builder()
                .setPreferredDexNames("classes5.dex")
                .build();
        ClassFilter classFilter = new ClassFilter.Builder()
                .setReferenceTypes(ReferenceTypes.builder().addString().build())
                .setReferenceFilter(pool ->
                        pool.contains("markFillRequested")
                ).build();
        MethodFilter methodFilter = new MethodFilter.Builder()
                .setReferenceTypes(ReferenceTypes.builder().addString().build())
                .setReferenceFilter(pool ->
                        pool.contains("markFillRequested")
                ).setParamSize(1)
                .setModifiers(Modifier.PUBLIC)
                .build();
        MethodData result = dexplore.findMethod(dexFilter, classFilter, methodFilter);

        if(result == null){
            XposedBridge.log("Failed find adsInVideo Method");
            return null;
        }
        return result;
    }


    public static MethodData[] findMethodsAdsGeneral(Dexplore dexplore){
        ArrayList<MethodData> list = new ArrayList<>();
        ClassFilter classFilter;
        MethodFilter methodFilter;
        MethodData result;

        classFilter = new ClassFilter.Builder()
                .setReferenceTypes(ReferenceTypes.builder().addString().build())
                .setReferenceFilter(pool ->
                        pool.contains("Error while converting %s")
                ).build();

        methodFilter = new MethodFilter.Builder()
                .setReferenceTypes(ReferenceTypes.builder().addString().build())
                .setReferenceFilter(pool ->
                        pool.contains("Error while converting %s")
                ).setParamSize(7)
                .setModifiers(Modifier.PUBLIC)
                .build();

        result = dexplore.findMethod(DexFilter.MATCH_ALL, classFilter, methodFilter);

        if(result == null){
            XposedBridge.log("Failed to find filterMethod");
            return null;
        }
        list.add(result);

        classFilter = new ClassFilter.Builder()
                .setReferenceTypes(ReferenceTypes.builder().addString().build())
                .setReferenceFilter(pool ->
                        pool.contains("EmptyComponent")
                ).build();

        methodFilter = new MethodFilter.Builder()
                .setParamSize(1)
                .setModifiers(Modifier.PUBLIC | Modifier.STATIC)
                .build();

        result = dexplore.findMethod(DexFilter.MATCH_ALL, classFilter, methodFilter);
        if(result == null){
            XposedBridge.log("Failed hook EmptyComponent");
            return null;
        }
        list.add(result);
        return list.toArray(new MethodData[0]);
    }

}
