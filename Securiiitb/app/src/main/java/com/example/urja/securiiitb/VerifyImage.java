package com.example.urja.securiiitb;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by urja on 20/4/18.
 */

public class VerifyImage extends AppCompatActivity {
    Button upload, verify;
    EditText filepath,tid;
    TextView message;
    String URL="http://53f18544.ngrok.io/api/Trade/";
    public static String hash="hash";
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.verify_layout);
        upload = (Button) findViewById(R.id.upload);
        verify = (Button) findViewById(R.id.verify);
        filepath=(EditText)findViewById(R.id.filepath);
        tid=(EditText)findViewById(R.id.tid);
        message=(TextView)findViewById(R.id.details);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.setType("*/*");
                startActivityForResult(i, 12);
            }
        });
        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                message.setText("");
                if(filepath.getText().toString() == null || tid.getText().toString() == null)
                    message.setText("select file or Enter tid");
                else
                {
                    String transactionid=tid.getText().toString();

                    final StringRequest jsObjRequest2 = new StringRequest(Request.Method.GET, URL+transactionid, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response)
                        {

                            try
                            {
                                String loca="loc",time="time";
                                Log.i("response",response);
                                message.setText(response);
                                JSONObject jsonObject=new JSONObject(response);
                                String newhash=jsonObject.getString("newOwner").substring(31);
                                if(hash.equals(newhash)) {
                                    loca = jsonObject.getString("loc");
                                    time = jsonObject.getString("timestamp");
                                    message.setText("Your File has been verified \n" + "Location : " + loca + "\n TimeStamp :" + time);
                                }
                                else
                                {
                                    message.setText("Not verified");
                                }



                            }

                            catch (Exception e)
                            {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            message.setText("Invalid Transaction id");
                            Log.i("error",new String(error.networkResponse.data));
                        }

                    });
                    jsObjRequest2.setRetryPolicy(new DefaultRetryPolicy(30000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                    MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsObjRequest2);


            }

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == 12){
        if (resultCode == RESULT_OK) {
            String path = data.getData().getPath();
            filepath.setText(path);
            hash = fileToMD5(path);
            Log.i("File hash............", hash);

        }
        }
    }
    public String fileToMD5(String filePath) {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(filePath); // Create an FileInputStream instance according to the filepath
            byte[] buffer = new byte[1024]; // The buffer to read the file
            MessageDigest digest = MessageDigest.getInstance("SHA-256"); // Get a MD5 instance
            int numRead = 0; // Record how many bytes have been read
            while (numRead != -1) {
                numRead = inputStream.read(buffer);
                if (numRead > 0)
                    digest.update(buffer, 0, numRead); // Update the digest
            }
            byte [] md5Bytes = digest.digest(); // Complete the hash computing
            return convertHashToString(md5Bytes); // Call the function to convert to hex digits
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close(); // Close the InputStream
                } catch (Exception e) { }
            }
        }
    }
    public String convertHashToString(byte[] data)
    {
        return String.format("%0"+(data.length*2)+"X",new BigInteger(1,data));
    }
}
