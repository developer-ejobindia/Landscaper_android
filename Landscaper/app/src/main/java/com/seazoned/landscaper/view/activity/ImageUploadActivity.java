package com.seazoned.landscaper.view.activity;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.system.ErrnoException;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.seazoned.landscaper.R;
import com.seazoned.landscaper.app.AppData;
import com.seazoned.landscaper.service.api.Api;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ImageUploadActivity extends AppCompatActivity {

    private CropImageView mCropImageView;

    private Uri mCropImageUri;
    private final int PERMISSION_CODE = 100;
    private Button btnSave;
    private String fromLocation = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_crop);
        mCropImageView = (CropImageView) findViewById(R.id.CropImageView);
        btnSave = (Button) findViewById(R.id.btnSave);

        if (getIntent().getExtras() != null) {
            fromLocation = getIntent().getExtras().getString("fromLocation");
        }
        checkPermissions();
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap cropped = mCropImageView.getCroppedImage(500, 500);


                try {
                    if (cropped != null) {
                        // mCropImageView.setImageBitmap(cropped);
                        AppData.sCompleteImage=cropped;
                        String selectedPath = savebitmap(cropped);
                        saveImage(selectedPath);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void checkPermissions() {
        int cameraPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int storagePermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (Build.VERSION.SDK_INT >= 23) {
            if (cameraPermission != PackageManager.PERMISSION_GRANTED ||
                    storagePermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_CODE);
            } else {
                //my_location();
                startActivityForResult(getPickImageChooserIntent(), 200);
            }
        } else {
            //my_location();
            startActivityForResult(getPickImageChooserIntent(), 200);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0) {

                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    startActivityForResult(getPickImageChooserIntent(), 200);
                    if (mCropImageUri != null && grantResults.length > 0 && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                        mCropImageView.setImageUriAsync(mCropImageUri);
                    } else {
                        //Toast.makeText(this, "Required permissions are not granted", Toast.LENGTH_LONG).show();
                    }
                } else {
                    finish();

                }
            }
        }
    }

    /**
     * On load image button click, start pick image chooser activity.
     */


    /**
     * Crop the image and set it back to the cropping view.
     */


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Uri imageUri = getPickImageResultUri(data);

            // For API >= 23 we need to check specifically that we have permissions to read external storage,
            // but we don't know if we need to for the URI so the simplest is to try open the stream and see if we get error.
            boolean requirePermissions = false;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                    checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                    isUriRequiresPermissions(imageUri)) {

                // request permissions and handle the result in onRequestPermissionsResult()
                requirePermissions = true;
                mCropImageUri = imageUri;
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            }

            if (!requirePermissions) {
                mCropImageView.setImageUriAsync(imageUri);
            }
        } else {
            finish();
        }
    }


    /**
     * Create a chooser intent to select the source to get image from.<br/>
     * The source can be camera's (ACTION_IMAGE_CAPTURE) or gallery's (ACTION_GET_CONTENT).<br/>
     * All possible sources are added to the intent chooser.
     */
    public Intent getPickImageChooserIntent() {

        // Determine Uri of camera image to save.
        Uri outputFileUri = getCaptureImageOutputUri();

        List<Intent> allIntents = new ArrayList<>();
        PackageManager packageManager = getPackageManager();

        // collect all camera intents
        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            if (outputFileUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            }
            allIntents.add(intent);
        }

        // collect all gallery intents
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        List<ResolveInfo> listGallery = packageManager.queryIntentActivities(galleryIntent, 0);
        for (ResolveInfo res : listGallery) {
            Intent intent = new Intent(galleryIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            allIntents.add(intent);
        }

        // the main intent is the last in the list (fucking android) so pickup the useless one
        Intent mainIntent = allIntents.get(allIntents.size() - 1);
        for (Intent intent : allIntents) {
            if (intent.getComponent().getClassName().equals("com.android.documentsui.DocumentsActivity")) {
                mainIntent = intent;
                break;
            }
        }
        allIntents.remove(mainIntent);

        // Create a chooser from the main intent
        Intent chooserIntent = Intent.createChooser(mainIntent, "Select source");

        // Add all other intents
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[allIntents.size()]));

        return chooserIntent;
    }

    /**
     * Get URI to image received from capture by camera.
     */
    private Uri getCaptureImageOutputUri() {
        Uri outputFileUri = null;
        File getImage = getExternalCacheDir();
        if (getImage != null) {
            outputFileUri = Uri.fromFile(new File(getImage.getPath(), "pickImageResult.jpeg"));
        }
        return outputFileUri;
    }

    /**
     * Get the URI of the selected image from {@link #getPickImageChooserIntent()}.<br/>
     * Will return the correct URI for camera and gallery image.
     *
     * @param data the returned data of the activity result
     */
    public Uri getPickImageResultUri(Intent data) {
        boolean isCamera = true;
        if (data != null && data.getData() != null) {
            String action = data.getAction();
            isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
        }
        return isCamera ? getCaptureImageOutputUri() : data.getData();
    }

    /**
     * Test if we can open the given Android URI to test if permission required error is thrown.<br>
     */
    public boolean isUriRequiresPermissions(Uri uri) {
        try {
            ContentResolver resolver = getContentResolver();
            InputStream stream = resolver.openInputStream(uri);
            stream.close();
            return false;
        } catch (FileNotFoundException e) {
            if (e.getCause() instanceof ErrnoException) {
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }

    //save image to server
    public String savebitmap(Bitmap bmp) throws IOException {
        //String dir = Environment.getExternalStorageDirectory() + "/TakePhoto";
        //createfolder
        File folder = new File(getFilesDir(),"TakePhoto");
        if (!folder.exists()) {
            folder.mkdirs();
        }
        //creatname file
        String simpleDate = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String nameImage = "PH_" + simpleDate;

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        bmp.compress(Bitmap.CompressFormat.JPEG, 60, bytes);

        File f = new File(folder.getPath() + File.separator + nameImage + ".jpg");
        f.createNewFile();
        FileOutputStream fo = new FileOutputStream(f);
        fo.write(bytes.toByteArray());
        fo.close();
        return f.getPath();
    }

    private void saveImage(final String path) {
        try {
            if (path != null && !path.equals("")) {
                if (path != null) {
                    new Thread(new Runnable() {
                        public String uploadkey;
                        public String url = "";

                        @Override
                        public void run() {
                            try {
                                //String url = AppData.host + "/insertPhoto?from_loc=product&product_id=" + AppData.parking_space_product_id + "&source=android&user_id=" + AppData.user_id;
                                if (fromLocation.equalsIgnoreCase("ViewProfileActivity"))
                                {
                                    uploadkey="profile_picture";
                                    url = Api.sAddProfileImage + "user_id=" + AppData.sUserId + "&source=" + "android";
                                }
                                else if (fromLocation.equalsIgnoreCase("ProfileActivity"))
                                {
                                    url = Api.sAddFeatureImage + "user_id=" + AppData.sUserId + "&source=" + "android";
                                    uploadkey="feature_image";
                                }
                                else if (fromLocation.equalsIgnoreCase("CompleteJobSubmitActivity"))
                                {
                                    url = Api.sCompleteJobImage + "upload_by=" + AppData.sUserId +"&bookServiceId="+AppData.sBookingId+ "&source=" + "android";
                                    uploadkey="property_image";
                                }

                                uploadFile(path, url,uploadkey);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                } else {
                    Toast.makeText(this, "Please Choose a File First", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Please Choose a File First", Toast.LENGTH_SHORT).show();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int uploadFile(final String selectedFilePath, String SERVER_URL,String uploadKey) {

        int serverResponseCode = 0;

        HttpURLConnection connection;
        DataOutputStream dataOutputStream;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";


        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File selectedFile = new File(selectedFilePath);


        String[] parts = selectedFilePath.split("/");
        final String fileName = parts[parts.length - 1];

        if (!selectedFile.isFile()) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //tvFileName.setText("Source File Doesn't Exist: " + selectedFilePath);
                }
            });
            return 0;
        } else {
            try {
                FileInputStream fileInputStream = new FileInputStream(selectedFile);
                URL url = new URL(SERVER_URL);
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);//Allow Inputs
                connection.setDoOutput(true);//Allow Outputs
                connection.setUseCaches(false);//Don't use a cached Copy
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setRequestProperty("ENCTYPE", "multipart/form-data");
                connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                connection.setRequestProperty(uploadKey, selectedFilePath);
                // connection.setRequestProperty("user_id","4");


                //creating new dataoutputstream
                dataOutputStream = new DataOutputStream(connection.getOutputStream());

                //writing bytes to data outputstream
                dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
                dataOutputStream.writeBytes("Content-Disposition: form-data; name=\""+uploadKey+"\";filename=\""
                        + selectedFilePath + "\"" + lineEnd);

                dataOutputStream.writeBytes(lineEnd);

                //returns no. of bytes present in fileInputStream
                bytesAvailable = fileInputStream.available();
                //selecting the buffer size as minimum of available bytes or 1 MB
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                //setting the buffer as byte array of size of bufferSize
                buffer = new byte[bufferSize];

                //reads bytes from FileInputStream(from 0th index of buffer to buffersize)
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                //loop repeats till bytesRead = -1, i.e., no bytes are left to read
                while (bytesRead > 0) {
                    //write the bytes read from inputstream
                    dataOutputStream.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }

                dataOutputStream.writeBytes(lineEnd);
                dataOutputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                serverResponseCode = connection.getResponseCode();
                //String serverResponseMessage = connection.getResponseMessage();


                //response code of 200 indicates the server status OK
                if (serverResponseCode == 200) {
                    AppData.sCompletejobFlag=1;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //AppData.iflag = 0;
                            finish();
                        }
                    });
                } else {

                    Toast.makeText(ImageUploadActivity.this, "Internet Connection Unavailable", Toast.LENGTH_SHORT).show();


                }
                //closing the input and output streams
                fileInputStream.close();
                dataOutputStream.flush();
                dataOutputStream.close();


            } catch (FileNotFoundException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        Toast.makeText(ImageUploadActivity.this, "File Not Found", Toast.LENGTH_SHORT).show();


                    }
                });
            } catch (MalformedURLException e) {
                e.printStackTrace();

                Toast.makeText(ImageUploadActivity.this, "URL ERROR", Toast.LENGTH_SHORT).show();

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(ImageUploadActivity.this, "Cannot Read/Write File", Toast.LENGTH_SHORT).show();

            }

            return serverResponseCode;
        }
    }


}
