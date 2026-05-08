package dev.squatedev.nestmanager.VirtualUtils;

import android.content.Context;

public class SyntaxTxt {
    private static SyntaxTxt instance;
    private Context context;

    private SyntaxTxt(Context context){
        this.context = context;
    }

    public static synchronized SyntaxTxt getInstance(Context ctx){
        if(instance == null){
            instance = new SyntaxTxt(ctx);
        }
        return instance;
    }
}
