package si.uni_lj.fe.tnuv.snapmap;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CoordinateManager {
    private static final String COORDINATE_LIST_KEY = "coord_list";
    private static final Gson GSON = new Gson();

    public static void saveCoord(Context context, LatLng coordinate) {
        List<LatLng> coordinateList = getCoord(context);
        coordinateList.add(coordinate);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        String json = GSON.toJson(coordinateList);
        editor.putString(COORDINATE_LIST_KEY, json);
        editor.apply();
    }

    public static List<LatLng> getCoord(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String json = preferences.getString(COORDINATE_LIST_KEY, null);
        if (json != null) {
            Type type = new TypeToken<List<LatLng>>() {}.getType();
            return GSON.fromJson(json, type);
        }
        return new ArrayList<>();
    }

}


