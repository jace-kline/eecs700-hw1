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

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.NoSuchPaddingException;

public class MainActivity extends AppCompatActivity {

    EditText fpassphrase;
    Button encryptbutton,decryptbutton;
    Context mContext;

    private static final int REQUEST = 112;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        encryptbutton = (Button)findViewById(R.id.btnencrypt);
        fpassphrase = (EditText)findViewById(R.id.fpassphrase);
        decryptbutton = (Button)findViewById(R.id.btndecrypt);
        mContext = this;

        // instantiate our encryption/decryption agent
        FileEncryptionAgent feaCreate = null;
        try {
            feaCreate = new FileEncryptionAgent("/sdcard/Download/", "password", "AES");
        } catch (Exception e) {
            e.printStackTrace();
        }
        FileEncryptionAgent fea = feaCreate;

        Log.d("files", fea.getFileNames());

        encryptbutton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                String[] PERMISSIONS = {android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
                ActivityCompat.requestPermissions((Activity) mContext, PERMISSIONS, REQUEST);

                if (fea.encryptFiles()) {
                    Toast.makeText(getApplicationContext(), "Files encrypted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Encryption error", Toast.LENGTH_SHORT).show();
                }

                Log.d("files", fea.getFileNames());
            }
        });


        decryptbutton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                String passphraseInput = fpassphrase.getText().toString();
                Log.d("input", passphraseInput);
                Log.d("storedpassphrase", fea.getPassphrase());
                if (passphraseInput.trim().equals(fea.getPassphrase())) {
                    if(fea.decryptFiles()) {
                        Toast.makeText(getApplicationContext(), "Files decrypted", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Decryption error", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Incorrect decryption passphrase", Toast.LENGTH_SHORT).show();
                }

                Log.d("files", fea.getFileNames());
            }
        });
    }
}