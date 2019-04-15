package com.example.libreta;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;

public class gdrive extends AppCompatActivity {


    private static final String TAG = "drive-quickstart";
    private static final int REQUEST_CODE_SIGN_IN = 0;
    private static final int REQUEST_CODE_CAPTURE_IMAGE = 1;
    private static final int REQUEST_CODE_CREATOR = 2;

    private DriveResourceClient mDriveResourceClient;

    //SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
    //String gdrive_folder = "LIBRETA" + sdf.format(new Date());
    File raiz = new File(Environment.getExternalStorageDirectory(), "Libreta");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gdrive);

        GoogleSignInClient GoogleSignInClient = buildGoogleSignInClient();
        startActivityForResult(GoogleSignInClient.getSignInIntent(), REQUEST_CODE_SIGN_IN);
    }

    private GoogleSignInClient buildGoogleSignInClient() {
        GoogleSignInOptions signInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestScopes(Drive.SCOPE_FILE)
                        .build();
        return GoogleSignIn.getClient(this, signInOptions);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_SIGN_IN:
                if (resultCode == RESULT_OK) {
                    mDriveResourceClient =
                            Drive.getDriveResourceClient(this, GoogleSignIn.getLastSignedInAccount(this));
                    Toast.makeText(this.getApplicationContext(), "Autenticado correctamente", Toast.LENGTH_SHORT).show();
                }

                final Task<DriveFolder> rootFolderTask = mDriveResourceClient.getRootFolder();
                final Task<DriveContents> createContentsTask = mDriveResourceClient.createContents();

                //Create folder
                /**Tasks.whenAll(rootFolderTask, createContentsTask)
                 .continueWithTask(task -> {

                 MetadataChangeSet folderset = new MetadataChangeSet.Builder()
                 .setTitle(gdrive_folder)
                 .setMimeType("application/vnd.google-apps.folder")
                 .build();
                 DriveFolder parent1 = rootFolderTask.getResult();
                 return mDriveResourceClient.createFolder(parent1, folderset);

                 });**/

                //Create files
                ArrayList<String> Files = new ArrayList<String>();
                for (String fileName : raiz.list()) {
                    if (fileName.endsWith(".txt")) {
                        Files.add(fileName);
                    }
                    for (String s : Files) {
                        Tasks.whenAll(rootFolderTask, createContentsTask)
                                .continueWithTask(task -> {
                                    File fichero = new File(raiz, s);
                                    StringBuilder cuerpo = new StringBuilder();
                                    BufferedReader br = new BufferedReader(new FileReader(fichero));
                                    String line;

                                    while ((line = br.readLine()) != null) {
                                        cuerpo.append(line);
                                        cuerpo.append('\n');
                                    }
                                    br.close();
                                    DriveFolder parent = rootFolderTask.getResult();
                                    DriveContents contents = createContentsTask.getResult();
                                    OutputStream outputStream = contents.getOutputStream();
                                    Writer writer = new OutputStreamWriter(outputStream);
                                    writer.write(cuerpo.toString());

                                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                            .setTitle(s)
                                            .setMimeType("text/plain")
                                            .setStarred(true)
                                            .build();
                                    Toast.makeText(this.getApplicationContext(), "Fichero" + s + " cargado", Toast.LENGTH_SHORT).show();
                                    return mDriveResourceClient.createFile(parent, changeSet, contents);
                                })
                                .addOnSuccessListener(this,
                                        driveFile -> {

                                            finish();
                                        })
                                .addOnFailureListener(this, e -> {
                                    Log.e(TAG, "Error al crear el fichero", e);
                                    finish();
                                });
                }
        }
    }

    }
}
