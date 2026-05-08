package dev.squatedev.nestmanager.Activity;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;
import dev.squatedev.nestmanager.DataManager.DataManager;
import dev.squatedev.nestmanager.Immutable.Immutable;
import dev.squatedev.nestmanager.R;
import dev.squatedev.nestmanager.ArrayAddapter.ImmutableAddaptor;
import dev.squatedev.nestmanager.ReversApk.ReversApk;
import dev.squatedev.nestmanager.TaskSync.CustomAsync;
import dev.squatedev.nestmanager.VirtualUtils.Utils;

public class Activty extends AppCompatActivity {
    private ListView listView;
    private ImmutableAddaptor immutableAddaptor;
    private DataManager dataManager;
    private Utils utils;
    private CustomAsync customAsync;
    private ReversApk reversApk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.layout_patch);

        dataManager = DataManager.getInstance();
        utils = Utils.Companion.getInstance(getBaseContext());
        customAsync = CustomAsync.Companion.getInstance(getBaseContext());
        reversApk = ReversApk.getInstance(getBaseContext());
        listView = findViewById(R.id.listView);

        loadInstalledApps();

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Immutable immutable = dataManager.getImmutableAt(position);
            if (immutable != null) {
                utils.toast("Sign : "+reversApk.getOriginalSign(immutable.getPackage_name()));
            } else {
                utils.toast("Элемент не найден");
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.linearLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void loadInstalledApps() {
        customAsync.executeAsync(
                "LoadAllApps",
                () -> {
                    List<Immutable> apps = new ArrayList<>();
                    PackageManager pm = getPackageManager();
                    List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

                    for (ApplicationInfo appInfo : packages) {
                        if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                            String packageName = appInfo.packageName;
                            String appName = pm.getApplicationLabel(appInfo).toString();
                            String version = "Unknown";
                            String signature = "Unknown";
                            Drawable iconRes = null;

                            try {
                                iconRes = pm.getApplicationIcon(appInfo);
                            } catch (Exception e) {
                                iconRes = getResources().getDrawable(R.drawable.apk);
                            }
                            try {
                                android.content.pm.PackageInfo pkgInfo = pm.getPackageInfo(packageName, 0);
                                if (pkgInfo.versionName != null) {
                                    version = pkgInfo.versionName;
                                }
                            } catch (Exception e) {
                                version = "Unknown";
                            }

                            try {
                                String sig = utils.getRealSignature(packageName);
                                if (sig != null && !sig.equals("Unknown")) {
                                    signature = sig;
                                }
                            } catch (Exception e) {
                                signature = "Unknown";
                            }

                            Immutable immutable = new Immutable(appName, packageName, version, signature, iconRes);
                            apps.add(immutable);
                        }
                    }
                    return apps;
                },
                null,
                (result) -> {
                    if (result != null && !result.isEmpty()) {
                        dataManager.clearAll();
                        for (Immutable app : result) {
                            dataManager.addList(app);
                        }
                        List<Immutable> apkLists = dataManager.getImmutableList();
                        immutableAddaptor = new ImmutableAddaptor(getApplicationContext(), apkLists);
                        listView.setAdapter(immutableAddaptor);
                        Toast.makeText(Activty.this, "Загружено: " + result.size() + " приложений", Toast.LENGTH_SHORT).show();
                        reversApk.addSaveApksSign(apkLists);
                    } else {
                        Toast.makeText(Activty.this, "Приложения не найдены", Toast.LENGTH_SHORT).show();
                    }
                    return kotlin.Unit.INSTANCE;
                },
                null,
                null
        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(listView != null) {
            listView.setAdapter(null);
        }
    }
}