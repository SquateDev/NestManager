package dev.squatedev.nestmanager.VirtualUtils;

import android.content.Context;

public class SyntaxJS {
    private static SyntaxJS instance;
    private Context context;

    private SyntaxJS(Context context){
        this.context = context;
    }

    public static synchronized SyntaxJS getInstance(Context ctx){
        if(instance == null){
            instance = new SyntaxJS(ctx);
        }
        return instance;
    }
}
