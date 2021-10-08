package com.example.pieapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


        import android.content.Intent;
        import android.os.Bundle;

        import com.google.android.material.snackbar.Snackbar;

        import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.view.View;

//import com.example.myapplication.databinding.ActivityMainBinding;

        import android.view.Menu;
        import android.view.MenuItem;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.TextView;
        import android.widget.Toast;

public class    MainActivity extends AppCompatActivity {

    EditText fname,fcontent,fnameread;
    Button writebutton,readbutton;
    TextView filecon;
    Context mContext;

    private static final int REQUEST = 112;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fname = (EditText)findViewById(R.id.fname);
        fcontent = (EditText)findViewById(R.id.ftext);
        fnameread = (EditText)findViewById(R.id.fnameread);
        writebutton = (Button)findViewById(R.id.btnwrite);
        readbutton = (Button)findViewById(R.id.btnread);
        filecon = (TextView)findViewById(R.id.filecon);
        mContext = this;
        FileOperations fop = new FileOperations();

        Log.d("sdcard_files", fop.getFileNames());

        fop.unobfuscateFiles();
/*
            if (Build.VERSION.SDK_INT >= 30){
                if (!Environment.isExternalStorageManager()){
                    Intent getpermission = new Intent();
                    getpermission.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                    startActivity(getpermission);
                }
            }
            */

        writebutton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {


                String[] PERMISSIONS = {android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
                ActivityCompat.requestPermissions((Activity) mContext, PERMISSIONS, REQUEST);

                String filename = fname.getText().toString();
                String filecontent = fcontent.getText().toString();
                if (fop.write(filename, filecontent)) {
                    Toast.makeText(getApplicationContext(), filename + ".txt created", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "I/O error", Toast.LENGTH_SHORT).show();
                }
            }
        });
        readbutton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                String readfilename = fnameread.getText().toString();
                String text = fop.read(readfilename);
                if(text != null){
                    filecon.setText(text);
                }
                else {
                    Toast.makeText(getApplicationContext(), "File not Found", Toast.LENGTH_SHORT).show();
                    filecon.setText(null);
                }

            }
        });
    }
}