package net.m_kawato.storagetest;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class StorageTest extends Activity implements View.OnClickListener {
    static final String TAG = "StorageTest";
    private Map<Integer, File> dirMap; // view ID -> File object

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage_test);

        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_PERMISSIONS);
        } catch (PackageManager.NameNotFoundException e) {
        }

        boolean hasPermission = false;
        if (packageInfo != null && packageInfo.requestedPermissions != null) {
            List<String> permissionList = Arrays.asList(packageInfo.requestedPermissions);
            for(String p: permissionList) {
                Log.d(TAG, "permissionList: " + p);
            }
            if (permissionList.contains("android.permission.WRITE_EXTERNAL_STORAGE")) {
                hasPermission = true;
            }
        }

        TextView textPermission = (TextView) findViewById(R.id.text_permission);
        if (hasPermission) {
            textPermission.setText("WRITE_EXTERNAL_STORAGE ON");
        } else {
            textPermission.setText("WRITE_EXTERNAL_STORAGE OFF");
        }

        String externalDir =  Environment.getExternalStorageDirectory().getPath();
        TextView textExternalDir = (TextView) findViewById(R.id.text_external_dir);
        textExternalDir.setText("ExternalStorageDirectory: " + externalDir);

        File[] dirs = getExternalFilesDirs(null);
        Log.d(TAG, "dirs.length = " + dirs.length);

        File appDir, parentDir;
        Button button;

        // buttons for testing write permission to dirs[0]
        if (dirs.length >= 1) {
            appDir = dirs[0];
            parentDir = getParentDir(appDir);
            this.dirMap = new HashMap<Integer, File>();
            this.dirMap.put(R.id.button_write_appdir0, appDir);
            this.dirMap.put(R.id.button_write_parentdir0, parentDir);
            TextView textStorageDir0 = (TextView) findViewById(R.id.text_storage_dir0);
            textStorageDir0.setText(getPathText(appDir, parentDir));
            button = (Button) findViewById(R.id.button_write_appdir0);
            button.setOnClickListener(this);
            button = (Button) findViewById(R.id.button_write_parentdir0);
            button.setOnClickListener(this);
        }

        // buttons for testing write permission to dirs[1]
        if (dirs.length >= 2) {
            appDir = dirs[1];
            parentDir = getParentDir(appDir);
            this.dirMap.put(R.id.button_write_appdir1, appDir);
            this.dirMap.put(R.id.button_write_parentdir1, parentDir);
            TextView textStorageDir1 = (TextView) findViewById(R.id.text_storage_dir1);
            textStorageDir1.setText(getPathText(appDir, parentDir));
            button = (Button) findViewById(R.id.button_write_appdir1);
            button.setOnClickListener(this);
            button = (Button) findViewById(R.id.button_write_parentdir1);
            button.setOnClickListener(this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.storage_test, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*
    Tries to write a file in the selected directory
     */
    @Override
    public void onClick(View v) {
        boolean success = false;
        String errorMessage = "";
        File dir = this.dirMap.get(v.getId());
        if (dir != null) {
            String path = dir + "/test.txt";
            File file = new File(path);
            try {
                PrintWriter writer = new PrintWriter(file);
                writer.println("Hello, world.");
                writer.flush();
                writer.close();
                success = true;
            } catch (Exception e) {
                errorMessage = e.getMessage();
            }
            String message = success ? "Success: " + path : errorMessage;
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(message);
            builder.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
            builder.create().show();
        }
    }

    private File getParentDir(File appDir) {
        return appDir.getParentFile().getParentFile().getParentFile().getParentFile();
    }

    private String getPathText(File appDir, File parentDir) {
        return "appDir: " + appDir.getPath() + "\n" +
            "parentDir: " + parentDir.getPath() + "\n";
    }
}


