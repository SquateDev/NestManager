package dev.squatedev.nestmanager.ArrayAddapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;
import dev.squatedev.nestmanager.Immutable.Immutable;
import dev.squatedev.nestmanager.R;

public class ImmutableAddaptor extends ArrayAdapter<Immutable> {
    private Context context;
    private List<Immutable> immutable;

    static class ViewHolder {
        ImageView imageView_apk;
        TextView name_apk;
        TextView package_name;
        TextView sign_apk;
    }

    public ImmutableAddaptor(Context context, List<Immutable> immutables) {
        super(context, 0, immutables);
        this.context = context;
        this.immutable = immutables;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.apk_layout, parent, false);
            holder = new ViewHolder();
            holder.imageView_apk = convertView.findViewById(R.id.file_icon);
            holder.name_apk = convertView.findViewById(R.id.file_name);
            holder.package_name = convertView.findViewById(R.id.data_edit);
            holder.sign_apk = convertView.findViewById(R.id.file_sign_id);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Immutable immutable1 = immutable.get(position);

        if (immutable1.getApk_icon() != null) {
            holder.imageView_apk.setImageDrawable(immutable1.getApk_icon());
        } else {
            holder.imageView_apk.setImageResource(R.drawable.apk);
        }

        holder.name_apk.setText(immutable1.getApk_name());
        holder.package_name.setText(immutable1.getPackage_name());
        holder.sign_apk.setText(immutable1.getSign_apk());

        return convertView;
    }

    @Override
    public int getCount() {
        return immutable != null ? immutable.size() : 0;
    }
}