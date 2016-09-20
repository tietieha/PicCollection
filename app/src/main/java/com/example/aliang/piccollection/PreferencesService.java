package com.example.aliang.piccollection;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ALIANG on 2016/4/26.
 */
public class PreferencesService {
    private Context context;

    public PreferencesService(Context context) {
        this.context = context;
        SharedPreferences preferences = context.getSharedPreferences("myset", Context.MODE_PRIVATE);
    }
    public void savecompanyID(Integer which) {
        //获得SharedPreferences对象
        SharedPreferences preferences = context.getSharedPreferences("myset", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("companyID", which);
        editor.commit();
    }
    public void savecompanyName(String companyName) {
        //获得SharedPreferences对象
        SharedPreferences preferences = context.getSharedPreferences("myset", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("companyName", companyName);
        editor.commit();
    }
    public void savecode(String code) {
        //获得SharedPreferences对象
        SharedPreferences preferences = context.getSharedPreferences("myset", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("code", code);
        editor.commit();
    }
    public void savename(String name) {
        //获得SharedPreferences对象
        SharedPreferences preferences = context.getSharedPreferences("myset", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("name", name);
        editor.commit();
    }

    public void savetype(Integer which) {
        //获得SharedPreferences对象
        SharedPreferences preferences = context.getSharedPreferences("myset", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("type", which);
        editor.commit();
    }
    public void savetypecontent(String name) {
        //获得SharedPreferences对象
        SharedPreferences preferences = context.getSharedPreferences("myset", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("typecontent", name);
        editor.commit();
    }

    public void saveRoad(String road) {
        //获得SharedPreferences对象
        SharedPreferences preferences = context.getSharedPreferences("myset", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("road", road);

        editor.commit();
    }
    public void saverodecontent(String name) {
        //获得SharedPreferences对象
        SharedPreferences preferences = context.getSharedPreferences("myset", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("roadcontent", name);
        editor.commit();
    }


    public void savetransparent(Integer which) {
        //获得SharedPreferences对象
        SharedPreferences preferences = context.getSharedPreferences("myset", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putInt("transparent", which);
        editor.commit();
    }

    public void savetranparentcontent(String name) {
        //获得SharedPreferences对象
        SharedPreferences preferences = context.getSharedPreferences("myset", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("transparentcontent", name);
        editor.commit();
    }



    public void saveposition(Integer which) {
        //获得SharedPreferences对象
        SharedPreferences preferences = context.getSharedPreferences("myset", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putInt("position", which);
        editor.commit();
    }
    public void savepositioncontent(String name) {
        //获得SharedPreferences对象
        SharedPreferences preferences = context.getSharedPreferences("myset", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("positioncontent", name);
        editor.commit();
    }


    public void savesave(Integer which) {
        //获得SharedPreferences对象
        SharedPreferences preferences = context.getSharedPreferences("myset", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putInt("savePosition", which);
        editor.commit();
    }

    public void savesavePositioncontent(String name) {
        //获得SharedPreferences对象
        SharedPreferences preferences = context.getSharedPreferences("myset", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("savepositioncontent", name);
        editor.commit();
    }


    public void saveresolution(Integer which) {
        //获得SharedPreferences对象
        SharedPreferences preferences = context.getSharedPreferences("myset", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putInt("resolution", which);
        editor.commit();
    }

    public void saveresolutioncontent(String name) {
        //获得SharedPreferences对象
        SharedPreferences preferences = context.getSharedPreferences("myset", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("resolutioncontent", name);
        editor.commit();
    }


    public void saveisShow(Integer which) {
        //获得SharedPreferences对象
        SharedPreferences preferences = context.getSharedPreferences("myset", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putInt("isShow", which);
        editor.commit();
    }

    public void saveshowcontent(String name) {
        //获得SharedPreferences对象
        SharedPreferences preferences = context.getSharedPreferences("myset", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("showcontent", name);
        editor.commit();
    }



    /**
     * 获取各项参数
     * @return
     */
    public Map<String, String> getPerferences() {
        Map<String, String> params = new HashMap<String, String>();
        SharedPreferences preferences = context.getSharedPreferences("myset", Context.MODE_PRIVATE);
        params.put("companyName", preferences.getString("companyName", ""));
        params.put("code", preferences.getString("code", ""));
        params.put("name", preferences.getString("name", ""));
        params.put("road", preferences.getString("road", ""));

        params.put("companyID", String.valueOf(preferences.getInt("companyID", 0)));
        params.put("type", String.valueOf(preferences.getInt("type", 0)));
        params.put("transparent", String.valueOf(preferences.getInt("transparent", 0)));
        params.put("position", String.valueOf(preferences.getInt("position", 0)));
        params.put("savePosition", String.valueOf(preferences.getInt("savePosition", 0)));
        params.put("resolution", String.valueOf(preferences.getInt("resolution", 0)));
        params.put("isShow", String.valueOf(preferences.getInt("isShow", 0)));

        params.put("typecontent",preferences.getString("typecontent", ""));
        params.put("transparentcontent",preferences.getString("transparentcontent", ""));
        params.put("positioncontent", preferences.getString("positioncontent", ""));
        params.put("savepositioncontent", preferences.getString("savepositioncontent", ""));
        params.put("resolutioncontent", preferences.getString("resolutioncontent", ""));
        params.put("showcontent", preferences.getString("showcontent", ""));
        return params;
    }


}
