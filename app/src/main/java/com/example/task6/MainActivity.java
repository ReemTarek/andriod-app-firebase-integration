package com.example.task6;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.time.Instant;
import java.util.Date;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
private  static  final int PICK_FILE_REQUEST_CODE = 300;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        View button = findViewById(R.id.uploadfile);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectFile();
            }
        });

    }
    private void selectFile()
    {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        Log.e("activity now","select file");

        startActivityForResult(intent,PICK_FILE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==PICK_FILE_REQUEST_CODE && resultCode==RESULT_OK)
        {
            Log.e("now selected","data");
            Uri filepath = data.getData();
            uploadFile(filepath);
            Log.e("now  uploaded","data");

        }
    }
    private  void uploadFile(Uri FilePath)
    {       Log.e("calling","firabase");
        FirebaseApp.initializeApp(this);
        StorageReference root= FirebaseStorage.getInstance().getReference();
        Random rand = new Random();

        // Generate random integers in range 0 to 999
        int rand_int = rand.nextInt(1000);
       final StorageReference fileRef = root.child("images/"+rand_int+FilePath.getLastPathSegment());

      UploadTask upload = fileRef.putFile(FilePath);
       upload.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
           @Override
           public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
               float percentage = (taskSnapshot.getBytesTransferred()/(float)taskSnapshot.getTotalByteCount())*100.0f;

           }
       });
       upload.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
           @Override
           public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        Log.e("new link",task.getResult().toString());
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference myRef = database.getReference("imagefolder");
                        Random rand = new Random();

                        // Generate random integers in range 0 to 999
                        int rand_int = rand.nextInt(1000);
                        myRef.child("img-link1"+rand_int).setValue(task.getResult().toString());
                    }
                });
           }
       });
    }
}
