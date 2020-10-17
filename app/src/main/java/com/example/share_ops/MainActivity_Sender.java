package com.example.wifishareit;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.Formatter;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class MainActivity_sender extends AppCompatActivity{
    ServerSocket serverSocket;
    Socket sSocket;
    int SERVERPORT = 2935;
    Handler handler;

    public final static int QRcodeWidth = 500 ;
    int PERMISSION_REQUEST_CODE = 1;
    Bitmap bitmap ;

    TextView listenText;
    TextView serverStatus;
    ImageView img_QR;

    String filePath;
    String IP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_sender);

        Intent fileIntent = getIntent();
        filePath = fileIntent.getStringExtra("path");

        Toast.makeText(this,filePath,Toast.LENGTH_SHORT).show();

        img_QR = (ImageView)findViewById(R.id.imageView_QR);

        listenText = (TextView)findViewById(R.id.text_listen);
        listenText.setText("Not Listening");
        serverStatus = (TextView)findViewById(R.id.text_serverStatus);
        serverStatus.setText("Disconnected");

        handler = new Handler();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted.
                //Toast.makeText(this,"permission WIFI_ACCESS",Toast.LENGTH_SHORT).show();
                IP = IPgen();
                Toast.makeText(this,IP,Toast.LENGTH_LONG).show();
            }
        }
    }

    public void startServer(View view){

        Thread serverThread = new Thread(new ServerThread());
        serverThread.start();
        ipGenerator();
    }


    public void ipGenerator(){
        try {
            int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_WIFI_STATE}, PERMISSION_REQUEST_CODE);
            } else {
                //Toast.makeText(this,"permission for WIFI already granted",Toast.LENGTH_SHORT).show();
                IP = IPgen();
                Toast.makeText(this,IP,Toast.LENGTH_LONG).show();
            }
            System.out.println("File path is :"+filePath);
            String [] segments = filePath.split("/");

            System.out.println("File Size in Byte /"+(new File(filePath).length()));
            Toast.makeText(this,segments[(segments.length)-1],Toast.LENGTH_LONG).show();
            bitmap = TextToImageEncode(IP+"/"+segments[(segments.length)-1]+"/"+(new File(filePath).length()));
            img_QR.setImageBitmap(bitmap);

        } catch ( Exception e) {
            e.printStackTrace();
        }
    }


}