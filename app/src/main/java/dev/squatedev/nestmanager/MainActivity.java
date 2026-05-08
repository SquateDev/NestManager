package dev.squatedev.nestmanager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import dev.squatedev.nestmanager.Activity.Activty;
import dev.squatedev.nestmanager.Activity.FileViewerActivity;
import dev.squatedev.nestmanager.Activity.HexEditor;
import dev.squatedev.nestmanager.VirtualUtils.Utils;

public class MainActivity extends AppCompatActivity {
    private Utils utils;
    private Button button_patch, button_hex, button_donation, button_manager;
    private boolean hasPermission = false, okay = false;
    private AlertDialog permissionDialog = null;

    private final ActivityResultLauncher<String[]> permissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(),
            permissions -> {
                boolean allGranted = permissions.get(Manifest.permission.READ_EXTERNAL_STORAGE) == Boolean.TRUE;
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    allGranted = allGranted && permissions.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == Boolean.TRUE;
                }
                if (allGranted) {
                    hasPermission = true;
                    dismissDialog();
                    enableButtons();
                } else {
                    showPermissionDialog();
                }
            });

    private final ActivityResultLauncher<Intent> manageStorageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    if (Environment.isExternalStorageManager()) {
                        hasPermission = true;
                        dismissDialog();
                        enableButtons();
                    } else {
                        showPermissionDialog();
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        button_patch = findViewById(R.id.patch_button);
        button_hex = findViewById(R.id.hex_editor);
        button_donation = findViewById(R.id.donate);
        button_manager = findViewById(R.id.manager);
        utils = Utils.Companion.getInstance(getApplicationContext());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        checkPermission();
    }

    private void dismissDialog() {
        if (permissionDialog != null && permissionDialog.isShowing()) {
            permissionDialog.dismiss();
            permissionDialog = null;
        }
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                hasPermission = true;
                enableButtons();
            } else {
                requestManageStorage();
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                hasPermission = true;
                enableButtons();
            } else {
                requestPermissions();
            }
        } else {
            hasPermission = true;
            enableButtons();
        }
    }

    private void requestManageStorage() {
        permissionDialog = new AlertDialog.Builder(this)
                .setTitle("Доступ к хранилищу")
                .setMessage("Для работы приложения необходимо разрешение на доступ к файлам")
                .setPositiveButton("Разрешить", (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    intent.setData(Uri.parse("package:" + getPackageName()));
                    manageStorageLauncher.launch(intent);
                    permissionDialog.dismiss();
                    okay = true;
                })
                .setNegativeButton("Выйти", (dialog, which) -> finish())
                .setCancelable(false)
                .show();
    }

    private void requestPermissions() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            permissionLauncher.launch(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            });
        } else {
            permissionLauncher.launch(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE
            });
        }
    }

    private void showPermissionDialog() {
        permissionDialog = new AlertDialog.Builder(this)
                .setTitle("Доступ не получен")
                .setMessage("Без доступа к файлам приложение не может работать")
                .setPositiveButton("Попробовать снова", (dialog, which) -> checkPermission())
                .setNegativeButton("Выйти", (dialog, which) -> finish())
                .setCancelable(false)
                .show();
    }

    private void enableButtons() {
        button_patch.setEnabled(true);
        button_hex.setEnabled(true);
        button_manager.setEnabled(true);

        button_patch.setOnClickListener(v -> startActivity(new Intent(this, Activty.class)));
        button_hex.setOnClickListener(v -> startActivity(new Intent(this, HexEditor.class)));
        button_donation.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://www.donationalerts.com/r/squatedev"));
            startActivity(intent);
        });
        button_manager.setOnClickListener(v -> startActivity(new Intent(this, FileViewerActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!hasPermission && okay == true) {
            checkPermission();
        }
    }
}