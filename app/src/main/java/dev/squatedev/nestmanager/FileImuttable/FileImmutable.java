package dev.squatedev.nestmanager.FileImuttable;

import android.graphics.drawable.Drawable;

public class FileImmutable {
    private final String file_name;

    private final String data_editor;

    private final String sign_apk;

    private final String type;

    public FileImmutable(String file_name, String data_editor, String sign_apk, String type){
        this.file_name = file_name;
        this.data_editor = data_editor;
        this.sign_apk = sign_apk;
        this.type = type;
    }

    public String File_get_name(){
        return file_name;
    }

    public String File_getdata_name(){
        return data_editor;
    }

    public String File_getSign_file(){
        return sign_apk;
    }

    public String File_getType() {return type;}
}
