package dev.squatedev.nestmanager.VirtualUtils;

import android.content.Context;

public class SyntaxKotlin {
    private static SyntaxKotlin instance;
    private Context context;

    private SyntaxKotlin(Context context){
        this.context = context;
    }

    public static synchronized SyntaxKotlin getInstance(Context ctx){
        if(instance == null){
            instance = new SyntaxKotlin(ctx);
        }
        return instance;
    }
}
