package dev.squatedev.nestmanager.FileArrayAddatper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

import dev.squatedev.nestmanager.FileImuttable.FileImmutable;
import dev.squatedev.nestmanager.R;

public class FileArrayAddapter extends ArrayAdapter<FileImmutable> {
    private Context context;
    private List<FileImmutable> fileImmutableList;

    static class ViewHolder {
        ImageView icon_file;
        TextView file_name;
        TextView data_file;
        TextView key_sign_file;
    }

    public FileArrayAddapter(@NonNull Context context, List<FileImmutable> fileImmutableList) {
        super(context, 0, fileImmutableList);
        this.context = context;
        this.fileImmutableList = fileImmutableList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder_file;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.file_list, parent, false);
            holder_file = new ViewHolder();
            holder_file.icon_file = convertView.findViewById(R.id.file_icon);
            holder_file.file_name = convertView.findViewById(R.id.file_name);
            holder_file.data_file = convertView.findViewById(R.id.data_edit);
            holder_file.key_sign_file = convertView.findViewById(R.id.file_sign_id);
            convertView.setTag(holder_file);

        } else {
            holder_file = (ViewHolder) convertView.getTag();
        }
        FileImmutable fileImmutable1 = fileImmutableList.get(position);
        if (fileImmutable1.File_get_name().endsWith(".js")) {
            holder_file.icon_file.setImageResource(R.drawable.javascript);
        } else if (fileImmutable1.File_get_name().endsWith(".java")) {
                holder_file.icon_file.setImageResource(R.drawable.java);
            } else if (fileImmutable1.File_get_name().endsWith(".kt") || fileImmutable1.File_getdata_name().endsWith(".kts")) {
                holder_file.icon_file.setImageResource(R.drawable.kotlin);
            } else if (fileImmutable1.File_get_name().endsWith(".txt")) {
                holder_file.icon_file.setImageResource(R.drawable.txt_icon);
            } else if (fileImmutable1.File_get_name().endsWith(".html")) {
                holder_file.icon_file.setImageResource(R.drawable.html_icon);
            } else if (fileImmutable1.File_get_name().endsWith(".so")) {
                holder_file.icon_file.setImageResource(R.drawable.code_no_fill);
            } else if (fileImmutable1.File_getType().equals("папка")) {
                holder_file.icon_file.setImageResource(R.drawable.folder);
            } else if (fileImmutable1.File_get_name().endsWith(".py")){
            holder_file.icon_file.setImageResource(R.drawable.python_file);
            }else {
                holder_file.icon_file.setImageResource(R.drawable.file_non);
            }
            holder_file.file_name.setText(fileImmutable1.File_get_name());
            holder_file.data_file.setText(fileImmutable1.File_getdata_name());
            holder_file.key_sign_file.setText(fileImmutable1.File_getSign_file());
            return convertView;
        }
    }
