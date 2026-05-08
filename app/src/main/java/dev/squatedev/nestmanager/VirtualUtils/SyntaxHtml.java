package dev.squatedev.nestmanager.VirtualUtils;

import android.content.Context;

public class SyntaxHtml {
    private static SyntaxHtml instance;
    private Context context;

    private SyntaxHtml(Context context){
        this.context = context;
    }

    public static synchronized SyntaxHtml getInstance(Context ctx){
        if(instance == null){
            instance = new SyntaxHtml(ctx);
        }
        return instance;
    }
}
