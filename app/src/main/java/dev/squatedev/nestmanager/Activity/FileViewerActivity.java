package dev.squatedev.nestmanager.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.Settings;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.security.cert.Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import dev.squatedev.nestmanager.FIleDataManager.FileDataManager;
import dev.squatedev.nestmanager.FileArrayAddatper.FileArrayAddapter;
import dev.squatedev.nestmanager.FileImuttable.FileImmutable;
import dev.squatedev.nestmanager.R;

public class FileViewerActivity extends AppCompatActivity {
    private ListView listView1;
    private FileArrayAddapter fileArrayAddapter;
    private FileDataManager fileDataManager;
    private String currentPath;
    private String filterType = "все";
    private String targetFilePath = null;
    private TextView title_text;
    private ImageView img_folder, img_folder2;
    private LinearLayout lin_folder, lin_folder2;
    private ViewFlipper viewFlipper;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.layout_file);

        listView1 = findViewById(R.id.listView2);
        title_text = findViewById(R.id.title_path);

        //Image
        img_folder = findViewById(R.id.img_folder1);
        img_folder2 = findViewById(R.id.img_folder2);

        // ViewFlipper
        viewFlipper = findViewById(R.id.viewFlipper);

        // Linearlayout
        lin_folder = findViewById(R.id.lin_folder1);
        lin_folder2 = findViewById(R.id.lin_folder2);
        lin_folder.setOnClickListener(v -> folder_chossen());
        lin_folder2.setOnClickListener(v -> folder2_chossen());

        fileDataManager = FileDataManager.getInstance();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.linearLayout99), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            handleViewIntent(intent);
        } else {
            currentPath = Environment.getExternalStorageDirectory().getAbsolutePath();
            loadFilesFromPath(currentPath);
        }

        listView1.setOnItemClickListener((parent, view, position, id) -> {
            FileImmutable selectedFile = fileDataManager.getImmutableAt(position);
            if (selectedFile == null) return;

            String fileName = selectedFile.File_get_name();
            String fileType = selectedFile.File_getType();

            if (fileName.equals("...")) {
                navigateUp();
            } else if (fileType.equals("папка")) {
                String newPath = currentPath + "/" + fileName;
                File newDir = new File(newPath);
                if (newDir.exists() && newDir.isDirectory()) {
                    currentPath = newPath;
                    loadFilesFromPath(currentPath);
                    if(!currentPath.isEmpty()){
                        String normal_file_patch = currentPath.replace("/storage/emulated/0", ""), replace_text = normal_file_patch.length()  > 40 ? normal_file_patch.substring(0, 39) : normal_file_patch;
                        title_text.setText(replace_text);
                    }
                } else {
                    Toast.makeText(this, "Не удалось открыть папку", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Файл: " + fileName, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleViewIntent(Intent intent) {
        Uri uri = intent.getData();
        if (uri == null) {
            currentPath = Environment.getExternalStorageDirectory().getAbsolutePath();
            loadFilesFromPath(currentPath);
            return;
        }

        String path = getPathFromUri(uri);
        if (path == null) {
            currentPath = Environment.getExternalStorageDirectory().getAbsolutePath();
            loadFilesFromPath(currentPath);
            return;
        }

        File file = new File(path);
        if (!file.exists()) {
            currentPath = Environment.getExternalStorageDirectory().getAbsolutePath();
            loadFilesFromPath(currentPath);
            return;
        }

        if (file.isDirectory()) {
            currentPath = path;
            loadFilesFromPath(currentPath);
        } else {
            targetFilePath = path;
            currentPath = file.getParent();
            loadFilesFromPath(currentPath);
        }
    }

    private String getPathFromUri(Uri uri) {
        String path = null;

        if (DocumentsContract.isDocumentUri(this, uri)) {
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.externalstorage.documents".equals(uri.getAuthority())) {
                String[] parts = docId.split(":");
                if (parts.length > 1) {
                    path = "/storage/" + parts[0] + "/" + parts[1];
                }
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            path = uri.getPath();
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            try {
                String[] projection = {"_data"};
                var cursor = getContentResolver().query(uri, projection, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndexOrThrow("_data");
                    path = cursor.getString(columnIndex);
                    cursor.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (path != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            path = URLDecoder.decode(path);
        }

        return path;
    }

    private void loadFilesFromPath(String path) {
        new Thread(() -> {
            List<FileImmutable> fileList = new ArrayList<>();
            File directory = new File(path);

            if (!directory.exists() || !directory.isDirectory()) {
                runOnUiThread(() -> {
                    fileDataManager.clearAll();
                    fileArrayAddapter = new FileArrayAddapter(getBaseContext(), fileDataManager.getImmutableList());
                    listView1.setAdapter(fileArrayAddapter);
                });
                return;
            }

            File[] files = directory.listFiles();
            if (files == null || files.length == 0) {
                runOnUiThread(() -> {
                    fileDataManager.clearAll();
                    fileArrayAddapter = new FileArrayAddapter(getBaseContext(), fileDataManager.getImmutableList());
                    listView1.setAdapter(fileArrayAddapter);
                });
                return;
            }

            boolean isRoot = path.equals("/storage/emulated/0") || path.equals("/");
            if (!isRoot) {
                fileList.add(new FileImmutable("...", "", "", "папка"));
            }

            int targetPosition = -1;
            int currentPosition = fileList.size();

            for (File file : files) {
                if (file.isHidden()) continue;

                String fileName = file.getName();
                String fileType = file.isDirectory() ? "папка" : "файл";
                String lastModified = "";
                String signatureOrTime = "";

                if (file.isDirectory()) {
                    lastModified = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(new Date(file.lastModified()));
                    signatureOrTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date(file.lastModified()));
                } else if (fileName.toLowerCase().endsWith(".apk")) {
                    lastModified = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(new Date(file.lastModified()));
                    String signature = getApkSignature(file);
                    signatureOrTime = signature.isEmpty() ? "Нет подписи" : signature;
                } else {
                    lastModified = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(new Date(file.lastModified()));
                    signatureOrTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date(file.lastModified()));
                }

                if (filterType.equals("файл") && file.isDirectory()) continue;
                if (filterType.equals("папка") && file.isFile()) continue;

                FileImmutable fileImmutable = new FileImmutable(fileName, lastModified, signatureOrTime, fileType);
                fileList.add(fileImmutable);

                if (targetFilePath != null && file.getAbsolutePath().equals(targetFilePath)) {
                    targetPosition = currentPosition;
                }
                currentPosition++;
            }

            fileList.sort((o1, o2) -> {
                if (o1.File_get_name().equals("...")) return -1;
                if (o2.File_get_name().equals("...")) return 1;
                if (o1.File_getType().equals("папка") && !o2.File_getType().equals("папка")) return -1;
                if (!o1.File_getType().equals("папка") && o2.File_getType().equals("папка")) return 1;
                return o1.File_get_name().toLowerCase().compareTo(o2.File_get_name().toLowerCase());
            });

            if (targetFilePath != null) {
                for (int i = 0; i < fileList.size(); i++) {
                    String fullPath = path + "/" + fileList.get(i).File_get_name();
                    if (fullPath.equals(targetFilePath)) {
                        targetPosition = i;
                        break;
                    }
                }
            }

            List<FileImmutable> finalList = fileList;
            int finalTargetPosition = targetPosition;

            runOnUiThread(() -> {
                fileDataManager.clearAll();
                for (FileImmutable file : finalList) {
                    fileDataManager.addList(file);
                }
                fileArrayAddapter = new FileArrayAddapter(getBaseContext(), fileDataManager.getImmutableList());
                listView1.setAdapter(fileArrayAddapter);

                if (finalTargetPosition >= 0 && finalTargetPosition < fileArrayAddapter.getCount()) {
                    listView1.smoothScrollToPosition(finalTargetPosition);
                    listView1.setSelection(finalTargetPosition);
                    targetFilePath = null;
                }
            });
        }).start();
    }

    private String getApkSignature(File apkFile) {
        try (JarFile jarFile = new JarFile(apkFile)) {
            JarEntry jarEntry = jarFile.getJarEntry("META-INF/CERT.RSA");
            if (jarEntry == null) {
                jarEntry = jarFile.getJarEntry("META-INF/CERT.DSA");
            }
            if (jarEntry == null) {
                return "";
            }

            Certificate[] certs = java.security.cert.CertificateFactory.getInstance("X.509")
                    .generateCertificates(jarFile.getInputStream(jarEntry))
                    .toArray(new Certificate[0]);

            if (certs.length == 0) return "";

            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] fingerprint = md.digest(certs[0].getEncoded());
            StringBuilder hexString = new StringBuilder();

            for (int i = 0; i < fingerprint.length; i++) {
                String hex = Integer.toHexString(0xFF & fingerprint[i]);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
                if (i < fingerprint.length - 1) {
                    hexString.append(":");
                }
            }

            return hexString.toString().toUpperCase();
        } catch (Exception e) {
            return "";
        }
    }

    private void navigateUp() {
        File currentDir = new File(currentPath);
        String parentPath = currentDir.getParent();
        if (parentPath != null && !currentPath.equals("/storage/emulated/0")) {
            currentPath = parentPath;
            if(!currentPath.isEmpty()){
                String normal_file_patch = currentPath.replace("/storage/emulated/0", ""), replace_text = normal_file_patch.length()  > 40 ? normal_file_patch.substring(0, 39) : normal_file_patch;
                title_text.setText(replace_text);
            }
            loadFilesFromPath(currentPath);
        } else {
            Toast.makeText(this, "Вы в корневой папке", Toast.LENGTH_SHORT).show();
        }
    }

    private void setFilterType(String type) {
        this.filterType = type;
        loadFilesFromPath(currentPath);
    }

    private void folder_chossen(){
        img_folder.setImageResource(R.drawable.folder);
        img_folder2.setImageResource(R.drawable.folder_no_fill);
        viewFlipper.setDisplayedChild(0);
    }
    private void folder2_chossen(){
        img_folder.setImageResource(R.drawable.folder_no_fill);
        img_folder2.setImageResource(R.drawable.folder);
        viewFlipper.setDisplayedChild(1);
    }

    @SuppressLint("GestureBackNavigation")
    @Override
    public void onBackPressed() {
        if (!currentPath.equals("/storage/emulated/0")) {
            navigateUp();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle("Вы точно хотите выйти?")
                    .setPositiveButton("ДА", (dialog, which) -> super.onBackPressed())
                    .setNegativeButton("НЕТ", (dialog, which) -> dialog.dismiss())
                    .setCancelable(false)
                    .show();
        }
    }
}