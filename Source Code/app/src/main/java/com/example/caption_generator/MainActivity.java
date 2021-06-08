package com.example.caption_generator;


import android.app.ProgressDialog;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;

import org.json.JSONException;
import org.json.JSONObject;
import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.graphics.Bitmap;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;



public class MainActivity extends AppCompatActivity {


    private Interpreter tfLite;
    ImageView imageView;
    Button btn_camera, btn_gallery;
    static final int maxeng=5001;
    ArrayList<String> englishTokenList = new ArrayList<>();
    TextView outputSentence;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView) findViewById(R.id.imageView);
        btn_camera = (Button) findViewById(R.id.Button1);
        btn_gallery = (Button) findViewById(R.id.Button2);
        outputSentence = (TextView) findViewById(R.id.textView);

        try {
            tfLite = new Interpreter(loadModelFile(this.getAssets(),"model.tflite"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        loadJson();

        btn_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //To take picture from camera

                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePicture, 0);//zero can be replaced with any action code

            }
        });
        btn_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //To pick photo from gallery

                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto, 1);//one can be replaced with any action code
            }
        });

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch (requestCode) {
            case 0:
                if (resultCode == RESULT_OK) {
                    Bundle extras = imageReturnedIntent.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    imageView.setImageBitmap(imageBitmap);
                    ByteBuffer b = convertBitmapToByteBuffer(imageBitmap);
                    String res = runModel(b,englishTokenList);
                    outputSentence.setText(res);

                }

                break;
            case 1:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = imageReturnedIntent.getData();

                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                        imageView.setImageBitmap(bitmap);
                        ByteBuffer b = convertBitmapToByteBuffer(bitmap);
                        String res = runModel(b,englishTokenList);
                        outputSentence.setText(res);
                        b.flip();
                        while (b.hasRemaining()) {
                            System.out.print((char) b.get());
                        }
                        System.out.println();
                    }
                    catch (Exception e) {
                        //handle exception
                    }


                }
                break;
        }
    }

    public ByteBuffer convertBitmapToByteBuffer(Bitmap bitmap){
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * 299 * 299 * 3);
        byteBuffer.order(ByteOrder.nativeOrder());
        bitmap=Bitmap.createScaledBitmap(bitmap, 299, 299, false);
        for (int i = 0; i < 299; i++) {
            for (int j = 0; j < 299; j++) {

                int colour = bitmap.getPixel(i, j);

                int red = Color.red(colour);
                int blue = Color.blue(colour);
                int green = Color.green(colour);

                byteBuffer.putFloat((float)(((float) red - 127.5) / 127.5));
                byteBuffer.putFloat((float)(((float) green - 127.5) / 127.5));
                byteBuffer.putFloat((float)(((float) blue - 127.5) / 127.5));
            }
        }
        return byteBuffer;
    }


    private void loadJson(){

        ProgressDialog pd = new ProgressDialog(MainActivity.this);

        pd.setMessage("Loading Data..");

        pd.show();

        JSONObject englishJsonObject =  null;
        try {
            englishJsonObject = new JSONObject(loadJSONFromAsset("wd_itow.json"));

            for(int i = 1; i< maxeng; i++)
                englishTokenList.add((String)englishJsonObject.get(String.valueOf(i)));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        pd.dismiss();

    }

    private static MappedByteBuffer loadModelFile(AssetManager assets, String modelFilename)throws IOException {
        AssetFileDescriptor fileDescriptor = assets.openFd(modelFilename);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    public String loadJSONFromAsset(String name) {
        String json = null;
        try {
            InputStream is = MainActivity.this.getAssets().open(name);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    private String runModel(ByteBuffer byteBuffer,ArrayList<String> list){

        long [] outputVal = new long [22];

        //Log.d("ADebugTag", "Value: " + Arrays.deepToString(inputVal));

        tfLite.run(byteBuffer,outputVal);

        Log.d("ADebugTag2", "Value: " + Arrays.toString(outputVal));

        StringBuilder stringBuilder = new StringBuilder();

        long [] aint = outputVal;

        for (long a:aint) {
            int tmp = (int) a;
            stringBuilder.append(getWordFromToken(list,tmp));
            stringBuilder.append(" ");
        }

        return stringBuilder.toString();
    }


    private String getWordFromToken(ArrayList<String> list,int key){

        if((key == 0) || (key == 5001))
            return "";
        else
            return list.get(key-1);

    }


}