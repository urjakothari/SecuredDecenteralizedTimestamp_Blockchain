package com.example.urja.securiiitb;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;


import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.SupportMapFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


/**
 * Created by urja on 7/4/18.
 */

public class MediaClass extends AppCompatActivity
{
    ImageButton  image,video,folder,galery,anyfile;
    private Uri file;
    public static String locationStr="Longitude: 77.66298122 Latitude: 12.84440368 My Current City is : Bengaluru";
    public static String fileName;
    String BASE_URL="http://53f18544.ngrok.io/";
    String URL1=BASE_URL+"api/Owner";
    String URL2=BASE_URL+"api/Trade";

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
    private static File getOutputMediaFile(String extr){
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "CameraDemo");

        if (!mediaStorageDir.exists()){
            if (!mediaStorageDir.mkdirs()){
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        fileName="IMG_"+ timeStamp + extr;
        return new File(mediaStorageDir.getPath() + File.separator +
                "IMG_"+ timeStamp + extr);

    }
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
    public boolean checkLocationPermission()
    {
        String permission = "android.permission.ACCESS_FINE_LOCATION";
        int res = this.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.media_layout);
        image=(ImageButton)findViewById(R.id.image);
        video=(ImageButton)findViewById(R.id.video);
        folder=(ImageButton)findViewById(R.id.folder);
        galery=(ImageButton)findViewById(R.id.galery);
        anyfile=(ImageButton)findViewById(R.id.anyfile);

        if(!checkLocationPermission())
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
          //  takePictureButton.setEnabled(false);
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
        }
        LocationManager locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener()
        {
            @Override
            public void onLocationChanged(Location location)
            {
                String longitude = "Longitude: " + location.getLongitude();
                String latitude = "Latitude: " + location.getLatitude();
                String cityName = null;
                Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
                List<Address> addresses;
                try {
                    addresses = gcd.getFromLocation(location.getLatitude(),
                            location.getLongitude(), 1);
                    if (addresses.size() > 0) {

                        cityName = addresses.get(0).getLocality();
                    }
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                String s = longitude + " " + latitude + " My Current City is:"
                        + cityName;
                locationStr=s;
                Log.i("location",locationStr);


            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 1, 0, locationListener);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                file = Uri.fromFile(getOutputMediaFile(".jpg"));

                intent.putExtra(MediaStore.EXTRA_OUTPUT, file);

                startActivityForResult(intent, 100);
            }
        });
        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent("android.media.action.VIDEO_CAPTURE");
                file = Uri.fromFile(getOutputMediaFile(".mp4"));
                intent.putExtra(MediaStore.EXTRA_OUTPUT, file);

                startActivityForResult(intent, 100);
            }
        });
        folder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent i=new Intent(Intent.ACTION_VIEW);
                File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES), "CameraDemo");
                Uri uri=Uri.fromFile(mediaStorageDir);
                i.setDataAndType(uri,"resource/folder");
                if(i.resolveActivityInfo(getPackageManager(),0)!=null)startActivity(i);


            }
        });
        galery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i= new Intent(getApplicationContext(),VerifyImage.class);
                startActivity(i);
            }
        });
        anyfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.setType("*/*");
                startActivityForResult(i, 12);
            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 100|| requestCode==12) {
            if (resultCode == RESULT_OK) {
                if(requestCode==12)
                {
                    file=data.getData();
                    fileName=file.getPath();
                }
                //imageView.setImageURI(file);
                final String myHash=fileToMD5(file.getEncodedPath());
                Log.i("Tag","Image captured");
                Log.i("Hash",myHash);
                Log.i("Address",locationStr);

                JSONObject jsonBody = new JSONObject();
                try {
                    jsonBody.put("Pk", myHash);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                final String mRequestBody = jsonBody.toString();
                JSONObject jsonBody2 = new JSONObject();
                try {
                    jsonBody2.put("co","org.acme.biznet.Coin#7738");
                    jsonBody2.put("newOwner","org.acme.biznet.Owner#"+myHash);
                    jsonBody2.put("loc",locationStr);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                final String mRequestBody2 = jsonBody2.toString();
                final StringRequest jsObjRequest2 = new StringRequest(Request.Method.POST, URL2, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response)
                    {

                        try
                        {
                            Log.i("response",response);
                            JSONObject jsonObject=new JSONObject(response);
                            String tId=jsonObject.getString("transactionId");
                            Log.i("transaction",tId);
                            File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                                    Environment.DIRECTORY_PICTURES), "CameraDemo");

                            String filename=mediaStorageDir.getPath()+File.separator+"Securiiitb.txt";
                            FileOutputStream fout=new FileOutputStream(new File(filename),true);
                            OutputStreamWriter osw=new OutputStreamWriter(fout);
                            BufferedWriter bw=new BufferedWriter(osw);
                            bw.write(fileName+"\t"+myHash+"\t"+tId);
                            bw.newLine();
                            bw.close();
                            fout.close();
                            AlertDialog.Builder builder;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                builder = new AlertDialog.Builder(MediaClass.this, android.R.style.Theme_Material_Dialog_Alert);
                            } else {
                                builder = new AlertDialog.Builder(MediaClass.this);
                            }
                            builder.setMessage("File "+fileName+" has been secured into blockchain with transaction Id "+tId);
                            builder.setCancelable(true);
                            AlertDialog alert=builder.create();
                            alert.show();

                        }

                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("error",new String(error.networkResponse.data));
                    }

                })

                {
                    @Override
                    public String getBodyContentType() {
                        return "application/json; charset=utf-8";
                    }

                    @Override
                    public byte[] getBody() throws AuthFailureError {
                        try {
                            return mRequestBody2 == null ? null : mRequestBody2.getBytes("utf-8");
                        } catch (UnsupportedEncodingException uee) {
                            VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", mRequestBody, "utf-8");
                            return null;
                        }
                    }

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put("Content-Type","application/json");

                        return params;
                    }
                };
                jsObjRequest2.setRetryPolicy(new DefaultRetryPolicy(30000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


                final StringRequest jsObjRequest = new StringRequest(Request.Method.POST, URL1, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response)
                    {

                        try
                        {
                            Log.i("response",response);
                            MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsObjRequest2);
                            //Log.i("size",""+binArray.length());
                            // JSONObject data=result.getJSONObject("item");
                    /*String status=result.getString("status");
                    Log.d("tag",status);*/

                        }

                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("error",new String(error.networkResponse.data));
                    }

                })

                {
                    @Override
                    public String getBodyContentType() {
                        return "application/json; charset=utf-8";
                    }

                    @Override
                    public byte[] getBody() throws AuthFailureError {
                        try {
                            return mRequestBody == null ? null : mRequestBody.getBytes("utf-8");
                        } catch (UnsupportedEncodingException uee) {
                            VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", mRequestBody, "utf-8");
                            return null;
                        }
                    }

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put("Content-Type","application/json");

                        return params;
                    }
                };
                jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(30000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsObjRequest);
                Log.i("request",jsObjRequest.toString());
            }
        }
    }


}
