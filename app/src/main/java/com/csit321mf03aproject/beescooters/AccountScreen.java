package com.csit321mf03aproject.beescooters;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AccountScreen extends AppCompatActivity {

    private static final String URL = "http://beescooters.net/admin/getUserData.php";
    private static final int REQUEST_PICK_GALLERY = 0;
    private static final int REQUEST_TAKE_PHOTO = 1;
    HashMap<String, String> paramHash;
    SharedPreferences preferences;
    private String userID, fullName, email, userType, userName, registerDate, address;
    ImageView imgPic;
    TextView txtFullName;
    TextView txtEmail;
    TextView txtUsername;
    TextView txtAddress;
    TextView txtMemberSince;
    TextView txtTotalRides;
    TextView txtUserType;
    private String mCurrentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // Retrieve the content view that renders the map.
        setContentView(R.layout.user_account_screen);

        getSupportActionBar().setTitle("Account");
        txtFullName = findViewById(R.id.txtFullName);
        txtEmail = findViewById(R.id.txtEmail);
        txtUsername = findViewById(R.id.txtUsername);
        txtAddress = findViewById(R.id.txtAddress);
        txtMemberSince = findViewById(R.id.txtMemberSince);
        txtTotalRides = findViewById(R.id.txtTotalRides);
        txtUserType = findViewById(R.id.txtUserType);
        imgPic = findViewById(R.id.imgPic);

        preferences = this.getSharedPreferences("MYPREFS", Context.MODE_PRIVATE);
        userID = preferences.getString("userID","ERROR getting User ID");

        userType = preferences.getString("userType","ERROR getting User Type");
        email = preferences.getString("email","ERROR getting User email");
        userName = preferences.getString("name","ERROR getting User name");
        fullName = preferences.getString("fullName","ERROR getting User ID");
        registerDate = preferences.getString("registerDate","ERROR getting User register date");
        address = preferences.getString("address","ERROR getting User address");

        txtEmail.setText(email);
        txtUsername.setText(userName);
        txtFullName.setText(fullName);
        txtAddress.setText(address);
        txtMemberSince.setText(registerDate);
        txtUserType.setText(userType);

        Log.d("USER_ID", ""+userID);

        getNumberOfTransactions();

        imgPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showUploadPicDialog();
            }
        });

    }

    //call php which will return number of transactions
   private void getNumberOfTransactions ()
    {
        paramHash = new HashMap<>();
        paramHash.put("userID", userID);
        paramHash.put("userType", userType);

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Receiving data...");
        progressDialog.show();
        RequestQueue queue = Volley.newRequestQueue(AccountScreen.this);

        //send request to checkout.php
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                            progressDialog.dismiss();
                            txtTotalRides.setText(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(AccountScreen.this, "Error Retrieving Data from Database! Try Again Later.", Toast.LENGTH_SHORT).show();
                Log.d("BEE_LOGS4", error.toString());
            }
        })
        {
            // Ctrl + O

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // if somehow paramHash didint contain the arguments taken from dropinrequest
                if (paramHash == null)
                {
                    return null;
                }

                //moving over all data from HashMap paramHash to Map params
                Map<String, String> params = new HashMap<>();
                for (String key:paramHash.keySet())
                {
                    params.put(key,paramHash.get(key));
                    Log.d("BEE_LOGS5", "here");
                }
                return params;
            }

            // Ctrl + O

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map <String, String> params = new HashMap<>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };

        //add request to queue
        queue.add(stringRequest);
        Log.d("BEE_LOG", stringRequest.toString());
    }

    //show dialog to users which allows them to choose image from gallery or take a picture
    private void showUploadPicDialog ()
    {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.upload_pic_dialog);
        dialog.setTitle("Upload Account Picture");
        Button btnPickGallery = dialog.findViewById(R.id.btnPickFromGallery);
        Button btnTakePic = dialog.findViewById(R.id.btnTakePic);

        btnPickGallery.setCompoundDrawablesWithIntrinsicBounds(null, ResourcesCompat.getDrawable(getResources(), R.drawable.ic_from_gallery, null) ,null , null);
        // Handle pick image from gallery
        btnPickGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(pickPhoto, REQUEST_PICK_GALLERY);
            dialog.dismiss();

            }
        });
        btnTakePic.setCompoundDrawablesWithIntrinsicBounds(null, ResourcesCompat.getDrawable(getResources(), R.drawable.ic_take_picture, null) ,null , null);
        // Handle take pic action using camera intent
        btnTakePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // Ensure that there's a camera activity to handle the intent
                if (takePictureIntent.resolveActivity(getPackageManager()) !=null){
                    // Create the File where the photo should go
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (Exception e){
                        // Error occurred while creating the File
                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        Uri photoURI = FileProvider.getUriForFile(AccountScreen.this,
                                "com.csit321mf03aproject.beescooters.fileprovider",
                                photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                    }
                } else{
                    Log.d("PHOTO", "photoFile == null !!!");
                }
            dialog.dismiss();
            }
        });
            dialog.show();
        }

    //Create file to store image taken from camera
    private File createImageFile() {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = null;
        try {
            image = File.createTempFile(
                    imageFileName, //prefix
                    ".jpg", //suffix
                    storageDir      //directory
            );
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    //Get image data from intent
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            case (REQUEST_TAKE_PHOTO ) :
                if (resultCode == RESULT_OK) {
                    Bitmap imageBitmap = null;
                    try {
                        imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.fromFile(new File(mCurrentPhotoPath)));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //set image at Image View
                    imgPic.setImageBitmap(imageBitmap);
                    try (FileOutputStream out = new FileOutputStream("userAccountPic.png")) {
                        imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
                        // PNG is a lossless format, the compression factor (100) is ignored
                        out.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }

            break;

            case (REQUEST_PICK_GALLERY ) :
                if (resultCode == RESULT_OK) {
                Uri selectedImage = data.getData();
                //set image at Image View
                imgPic.setImageURI(selectedImage);            }
            break;

        }
    }

}
