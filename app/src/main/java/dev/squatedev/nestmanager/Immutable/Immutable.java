package dev.squatedev.nestmanager.Immutable;

import android.graphics.drawable.Drawable;

public final class Immutable {
    private final String apk_name;

    private final String package_name;

    private final String version_apk;

    private final String sign_apk;

    private final Drawable apk_icon;

    public Immutable(String apk_name, String package_name, String version_apk, String sign_apk, Drawable apk_icon){
        this.apk_name = apk_name;
        this.package_name = package_name;
        this.version_apk = version_apk;
        this.sign_apk = sign_apk;
        this.apk_icon = apk_icon;
    }

    public String getApk_name(){
        return apk_name;
    }

    public String getPackage_name(){
        return package_name;
    }

    public String getVersion_apk(){
        return version_apk;
    }

    public String getSign_apk(){
        return sign_apk;
    }

    public Drawable getApk_icon(){
        return apk_icon;
    }
}