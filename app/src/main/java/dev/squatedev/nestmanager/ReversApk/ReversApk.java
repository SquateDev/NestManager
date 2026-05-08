package dev.squatedev.nestmanager.ReversApk;

import android.content.Context;

import java.lang.reflect.Type;
import java.util.List;

import dev.squatedev.nestmanager.Immutable.Immutable;
import dev.squatedev.nestmanager.VirtualUtils.Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

public class ReversApk {
    private Context cotext;
    private static ReversApk instance;
    private static Utils utils;
    String sign_patch = "NestManager/sign_list/", file_name = "sign_apk.cfg";

    private ReversApk(Context ctx){
        this.cotext = ctx;
    }

    public static synchronized ReversApk getInstance(Context ctx){
            if(instance == null){
                instance = new ReversApk(ctx);
                utils = Utils.Companion.getInstance(ctx);
            }
        return instance;
    }

    public void addSaveApksSign(List<Immutable> list){
        if(utils.fileExists(sign_patch, file_name) == false){
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String json = gson.toJson(list);
            utils.createFile(sign_patch, file_name);
            utils.writeFile(sign_patch, file_name, json);
        }
    }

    public String getOriginalSign(String packageName) {
        String fileContent = utils.readFile(sign_patch, file_name);

        if (fileContent == null || fileContent.isEmpty()) {
            return null;
        }

        Gson gson = new Gson();
        JsonArray jsonArray = gson.fromJson(fileContent, JsonArray.class);

        for (JsonElement element : jsonArray) {
            JsonObject obj = element.getAsJsonObject();
            String pkg = obj.get("b").getAsString();

            if (pkg.equals(packageName)) {
                return obj.get("d").getAsString();
            }
        }
        return null;
    }
}
