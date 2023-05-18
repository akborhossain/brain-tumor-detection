package com.example.braintumordetection;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.TextView;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

import java.io.ByteArrayOutputStream;

public class Predictions extends AppCompatActivity {
    private ImageView img,xaiimg;
    private TextView results;
    BitmapDrawable bitmapDrawable;
    Bitmap bitmap;
    String imageString;

    @SuppressLint("WrongThread")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_predictions);
        img=findViewById(R.id.originalimg);
        xaiimg=findViewById(R.id.xaiimg);
        results=findViewById(R.id.results);
        Uri imageUri = getIntent().getParcelableExtra("image");
        img.setImageURI(imageUri);
        results.setText("This image has identify as this tumor");

        if(!Python.isStarted()) {

            Python.start(new AndroidPlatform(this));
            final Python py =Python.getInstance();
            bitmapDrawable= (BitmapDrawable) img.getDrawable();
            bitmap=bitmapDrawable.getBitmap();
            imageString=getImageString(bitmap);

            PyObject pyo =py.getModule("myscript");
            PyObject obj=pyo.callAttr("imageProcessing",imageString);
            String str=obj.toString();
            byte data[]= android.util.Base64.decode(str,Base64.DEFAULT);
            Bitmap bmp= BitmapFactory.decodeByteArray(data,0,data.length);
            img.setImageBitmap(bmp);
        }
    }


    private String getImageString(Bitmap bitmap) {
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,baos);
        byte[] imageBytes= baos.toByteArray();
        String encodedImage=android.util.Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }
}