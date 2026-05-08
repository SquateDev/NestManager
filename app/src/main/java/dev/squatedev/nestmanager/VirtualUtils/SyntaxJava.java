package dev.squatedev.nestmanager.VirtualUtils;

import android.content.Context;

public class SyntaxJava {
    private static SyntaxJava instance;
    private Context context;

    private SyntaxJava(Context context){
        this.context = context;
    }

    public static synchronized SyntaxJava getInstance(Context ctx){
        if(instance == null){
            instance = new SyntaxJava(ctx);
        }
        return instance;
    }
}
