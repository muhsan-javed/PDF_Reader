package com.mjpdfreader.pdfreader;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView lv_pdf;


    public static ArrayList<File> fileList = new ArrayList<File>();
    PDFAdapter obj_adapter;
    public static int REQUEST_PERMISSION = 1;
    boolean boolean_permission;
    File dir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lv_pdf = findViewById(R.id.listView_pdf);
        lv_pdf.setAdapter(obj_adapter);

        dir = new File(Environment.getExternalStorageDirectory().toString());

        permission_fun();

        lv_pdf.setOnItemClickListener((parent, view, position, id) -> {

            Intent intent = new Intent(getApplicationContext(),ViewPDFFiles.class);
            intent.putExtra("position",position);
            startActivity(intent);

        });



    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to Exit")
                .setNegativeButton("No",null)
                .setPositiveButton("Yes", (dialog, which) -> finishAffinity()).show();
    }

    private void permission_fun() {
        if ((ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)){

            if ((ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE))){
            }else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_PERMISSION);
            }

        } else {

            boolean_permission =true;
            getfile(dir);
            obj_adapter = new PDFAdapter(getApplicationContext(),fileList);
            lv_pdf.setAdapter(obj_adapter);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION){

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                boolean_permission =true;
                getfile(dir);
                obj_adapter = new PDFAdapter(getApplicationContext(),fileList);
                lv_pdf.setAdapter(obj_adapter);
            }else {
                Toast.makeText(this, "Please Allow the Permission", Toast.LENGTH_SHORT).show();
            }
        }

    }

    public ArrayList<File> getfile(File dir){

        File listFile[] = dir.listFiles();

        if (listFile!=null && listFile.length>0){

            for (File file : listFile) {

                if (file.isDirectory()) {
                    getfile(file);
                } else {

                    boolean boolean_pdf = false;
                    if (file.getName().endsWith(".pdf")) {

                        for (int j = 0; j < fileList.size(); j++) {

                            if (fileList.get(j).getName().equals(file.getName())) {
                                boolean_pdf = true;
                            }
                        }

                        if (boolean_pdf) {
                            boolean_pdf = false;
                        } else {
                            fileList.add(file);
                        }

                    }

                }

            }

        }
        return fileList;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_toolbar,menu);

        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Search");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                obj_adapter.getFilter().filter(newText);
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }
}