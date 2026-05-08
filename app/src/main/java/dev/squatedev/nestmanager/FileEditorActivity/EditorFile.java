package dev.squatedev.nestmanager.FileEditorActivity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import dev.squatedev.nestmanager.VirtualUtils.SyntaxHtml;
import dev.squatedev.nestmanager.VirtualUtils.SyntaxJS;
import dev.squatedev.nestmanager.VirtualUtils.SyntaxJava;
import dev.squatedev.nestmanager.VirtualUtils.SyntaxKotlin;
import dev.squatedev.nestmanager.VirtualUtils.SyntaxTxt;

public class EditorFile extends AppCompatActivity {
    private SyntaxHtml syntaxHtml;
    private SyntaxJava syntaxJava;
    private SyntaxJS syntaxJS;
    private SyntaxKotlin syntaxKotlin;
    private SyntaxTxt syntaxTxt;

    @Override
    protected void onCreate(Bundle isBundl){
        super.onCreate(isBundl);
        syntaxHtml = SyntaxHtml.getInstance(this);
        syntaxJava = SyntaxJava.getInstance(this);
        syntaxJS = SyntaxJS.getInstance(this);
        syntaxKotlin = SyntaxKotlin.getInstance(this);
        syntaxTxt = SyntaxTxt.getInstance(this);
    }
}
