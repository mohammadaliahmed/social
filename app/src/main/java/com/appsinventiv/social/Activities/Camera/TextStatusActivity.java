package com.appsinventiv.social.Activities.Camera;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.appsinventiv.social.NetworkResponses.AddPostResponse;
import com.appsinventiv.social.R;
import com.appsinventiv.social.Utils.AppConfig;
import com.appsinventiv.social.Utils.CommonUtils;
import com.appsinventiv.social.Utils.SharedPrefs;
import com.appsinventiv.social.Utils.UserClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.JsonObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class TextStatusActivity extends AppCompatActivity {

    ImageView pallete, text;
    RelativeLayout wholeLayout;
    ImageView send;
    EditText status_text;
    int[] colorList = {R.color.darkPurple, R.color.darkGrey, R.color.darkSilver,
            R.color.black, R.color.red, R.color.darkGreen, R.color.darkYellow,
            R.color.darkBlue, R.color.darkPink};

    int random = 1;
    RelativeLayout toBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_status);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        toBitmap = findViewById(R.id.toBitmap);
        status_text = findViewById(R.id.status_text);
        pallete = findViewById(R.id.pallete);
        text = findViewById(R.id.text);
        wholeLayout = findViewById(R.id.wholeLayout);
        send = findViewById(R.id.send);
        Random r = new Random();

        int randomNumber = r.nextInt(colorList.length);
        wholeLayout.setBackgroundColor(getResources().getColor(colorList[randomNumber]));
        toBitmap.setBackgroundColor(getResources().getColor(colorList[randomNumber]));


        pallete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Random r = new Random();
                int randomNumber = r.nextInt(colorList.length);
                wholeLayout.setBackgroundColor(getResources().getColor(colorList[randomNumber]));
                toBitmap.setBackgroundColor(getResources().getColor(colorList[randomNumber]));

            }
        });


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (status_text.getText().length() == 0) {
                    CommonUtils.showToast("Enter status");
                } else {
                    createBitmap();
                }
            }
        });

        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Random r = new Random();
                int randomNumber = r.nextInt(10);
                Typeface typeface = null;
                if (randomNumber == 1) {
                    typeface = ResourcesCompat.getFont(TextStatusActivity.this, R.font.aguafina_script);

                } else if (randomNumber == 2) {
                    typeface = ResourcesCompat.getFont(TextStatusActivity.this, R.font.atomic_age);

                } else if (randomNumber == 3) {
                    typeface = ResourcesCompat.getFont(TextStatusActivity.this, R.font.caveat);

                } else if (randomNumber == 4) {
                    typeface = ResourcesCompat.getFont(TextStatusActivity.this, R.font.alegreya);

                } else if (randomNumber == 5) {
                    typeface = ResourcesCompat.getFont(TextStatusActivity.this, R.font.alfa_slab_one);

                } else if (randomNumber == 6) {
                    typeface = ResourcesCompat.getFont(TextStatusActivity.this, R.font.crafty_girls);

                } else if (randomNumber == 7) {
                    typeface = ResourcesCompat.getFont(TextStatusActivity.this, R.font.denk_one);

                } else if (randomNumber == 8) {
                    typeface = ResourcesCompat.getFont(TextStatusActivity.this, R.font.graduate);

                } else if (randomNumber == 9) {
                    typeface = ResourcesCompat.getFont(TextStatusActivity.this, R.font.zeyada);

                } else if (randomNumber == 10) {
                    typeface = ResourcesCompat.getFont(TextStatusActivity.this, R.font.sacramento);

                }
                status_text.setTypeface(typeface);
            }
        });


    }

    private void createBitmap() {
        int height = toBitmap.getWidth();
        viewToBitmap(toBitmap, toBitmap.getWidth(), toBitmap.getHeight());
    }

    public Bitmap viewToBitmap(View view, int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
//        saveImage(bitmap, status_text.getText().toString().substring(0, 5));
        addPost(getImageUri(bitmap));
        return bitmap;
    }


    public Uri getImageUri(Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private void addPost(Uri uri) {
        uploadFile(CommonUtils.getRealPathFromURI(uri));
    }

    private void uploadFile(String abc) {
        // create upload service client
        File file = new File(abc);

        UserClient service = AppConfig.getRetrofit().create(UserClient.class);

        RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("photo", file.getName(), requestBody);
        RequestBody filename = RequestBody.create(MediaType.parse("text/plain"), file.getName());

        // finally, execute the request
        Call<ResponseBody> call = service.uploadFile(fileToUpload, filename);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    try {
                        String liveUrl = response.body().string();
                        uploadPost(liveUrl);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                CommonUtils.showToast(t.getMessage());
            }
        });

    }

    private void uploadPost(String liveUrls) {

        stopService(new Intent(getApplicationContext(), UploadPostService.class));
        JsonObject map = new JsonObject();
        map.addProperty("api_username", AppConfig.API_USERNAME);
        map.addProperty("api_password", AppConfig.API_PASSOWRD);
        map.addProperty("id", SharedPrefs.getUserModel().getId());
        map.addProperty("images_url", liveUrls);
        map.addProperty("post_type", "image");
        map.addProperty("time", System.currentTimeMillis());
        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);
        Call<AddPostResponse> call = getResponse.addPost(map);
        call.enqueue(new Callback<AddPostResponse>() {
            @Override
            public void onResponse(Call<AddPostResponse> call, Response<AddPostResponse> response) {
                stopService(new Intent(getApplicationContext(), UploadPostService.class));
                if (response.code() == 200) {

                    CommonUtils.showToast(response.body().getMessage());

                    finish();
                } else {
                    CommonUtils.showToast(response.code() + " " + response.message());
                }
            }

            @Override
            public void onFailure(Call<AddPostResponse> call, Throwable t) {
                stopService(new Intent(getApplicationContext(), UploadPostService.class));
                CommonUtils.showToast(t.getMessage());

            }
        });

    }


    private void uploadPicture(String path) {
//        finish();
//        StorageReference mStorageRef;
//        mStorageRef = FirebaseStorage.getInstance().getReference();
//
//        String imgName = Long.toHexString(Double.doubleToLongBits(Math.random()));
//
//
//        Uri file = Uri.fromFile(new File(path));
//
//
//        StorageReference riversRef = mStorageRef.child("Photos").child(imgName);
//
//        riversRef.putFile(file)
//                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    @SuppressWarnings("VisibleForTests")
//                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                        // Get a URL to the uploaded content
//
//                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
//                        mDatabase.child("Posts").child("Posts").child(model.getId()).child("pictureUrl").setValue("" + downloadUrl).addOnSuccessListener(new OnSuccessListener<Void>() {
//                            @Override
//                            public void onSuccess(Void aVoid) {
//                                CommonUtils.showToast("Post Added");
//                                finish();
//                            }
//                        });
//
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception exception) {
//                        // Handle unsuccessful uploads
//                        // ...
//                        CommonUtils.showToast("" + exception.getMessage());
//                    }
//                });

    }
}
