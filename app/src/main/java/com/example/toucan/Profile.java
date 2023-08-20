package com.example.toucan;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.net.URI;
import java.util.Objects;

public class Profile extends AppCompatActivity {
    private Button uploadBtn;
    private ShapeableImageView songImg;
    private EditText songName;
    private StorageReference reference;
    private DatabaseReference databaseReference;
    private static final int REQUEST_CODE = 1;
    private final int GALLERY_REQ_CODE = 1000;
    private static Uri songThumbnail;
    private static Uri songFile;
    private static String songTitle;
    private static String songDownloadURL;
    private static String songThumbURL;
    public static int clickCount;
    public DatabaseReference fruitReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        uploadBtn = findViewById(R.id.uploadBtn);
        reference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference("audio");
        songImg = findViewById(R.id.songImg);
        songName = findViewById(R.id.songName);
        fruitReference = FirebaseDatabase.getInstance().getReference("cnt");

        songImg.setOnClickListener(view->{
            Intent iGallery = new Intent(Intent.ACTION_PICK);
            iGallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(iGallery, GALLERY_REQ_CODE);
        });
        uploadBtn.setOnClickListener(view->{
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            songTitle = String.valueOf(songName.getText());
            intent.setType("audio/*");
            startActivityForResult(intent, REQUEST_CODE);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null){
            songFile = data.getData();
            uploadFile();
        }
        if(resultCode==RESULT_OK && requestCode == GALLERY_REQ_CODE && data != null && data.getData() != null){
            songThumbnail = data.getData();
            songImg.setImageURI(songThumbnail);
        }
    }

    private void uploadFile() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading files...");
        progressDialog.show();
        StorageReference upRef = reference.child("Uploads/"+songFile.getLastPathSegment());
        upRef.putFile(songFile)
                .addOnSuccessListener(taskSnapshot -> {
                    upRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        songDownloadURL = uri.toString();
                    });
                    progressDialog.dismiss();
                })
                .addOnProgressListener(snapshot -> {
                    double progress = (double)(100 * snapshot.getBytesTransferred())/snapshot.getTotalByteCount();
                    progressDialog.setMessage("Uploaded: "+(int)progress+"%");
                })
                .addOnFailureListener(exception-> {
                    progressDialog.dismiss();
                });
        final ProgressDialog progressDialog2 = new ProgressDialog(this);
        progressDialog.setTitle("Uploading Thumbnail...");
        progressDialog.show();
        StorageReference upThumb = reference.child("Uploads/"+songThumbnail.getLastPathSegment());
        upThumb.putFile(songThumbnail)
                .addOnSuccessListener(taskSnapshot -> {
                    upThumb.getDownloadUrl().addOnSuccessListener(uri -> {
                        songThumbURL = uri.toString();
                        storeInDatabase();
                    });
                    progressDialog2.dismiss();
                    Toast.makeText(getApplicationContext(), "Successfully uploaded", Toast.LENGTH_SHORT).show();
                })
                .addOnProgressListener(snapshot -> {
                    double progress = (double)(100 * snapshot.getBytesTransferred())/snapshot.getTotalByteCount();
                    progressDialog2.setMessage("Uploaded: "+(int)progress+"%");
                })
                .addOnFailureListener(exception-> {
                    Toast.makeText(getApplicationContext(), "Failed to upload", Toast.LENGTH_SHORT).show();
                    progressDialog2.dismiss();
                });
    }

    private void storeInDatabase(){
        updateFruitClickCount();
    }
    private void updateFruitClickCount() {
        fruitReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                clickCount = 0;
                if (dataSnapshot.exists()) {
                    clickCount = dataSnapshot.getValue(Integer.class);
                }
                databaseReference.child(String.valueOf(clickCount)).setValue(new MusicClass(songDownloadURL, songThumbURL, songTitle));
                clickCount++;
                fruitReference.setValue(clickCount);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
    });
}
}