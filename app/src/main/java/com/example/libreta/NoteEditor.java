package com.example.libreta;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NoteEditor extends AppCompatActivity {

    EditText title;
    EditText body_text;
    File raiz = new File(Environment.getExternalStorageDirectory(), "Libreta");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_editor);

        title = findViewById(R.id.title);
        body_text = findViewById(R.id.body_text);

        //Load a note if exist
        String tempholder = getIntent().getStringExtra("Titulo");
        if (!TextUtils.isEmpty(tempholder)) {
            title.setText(tempholder);
            File fichero = new File(raiz, tempholder + ".txt");
            StringBuilder cuerpo = new StringBuilder();

            try {
                BufferedReader br = new BufferedReader(new FileReader(fichero));
                String line;

                while ((line = br.readLine()) != null) {
                    cuerpo.append(line);
                    cuerpo.append('\n');
                }
                br.close();
            } catch (IOException e) {
                Toast.makeText(this, "Error al cargar el cuerpo del fichero", Toast.LENGTH_SHORT).show();
            }
            body_text.setText(cuerpo);
        }

        FloatingActionButton save_noteButton = findViewById(R.id.save_note);
        save_noteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Alert dialog if title is empty
                if (TextUtils.isEmpty(title.getText().toString())) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(NoteEditor.this);
                    dialog.setTitle("Precaución");
                    dialog.setMessage("Se va a guardar la nota sin título");
                    dialog.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HHmmss");
                            //String ahora_text = sdf.format(new Date()).g;
                            String titleText = sdf.format(new Date());
                            title.setText(titleText);
                            Save();
                        }
                    });
                    dialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    dialog.show();
                } else {
                    Save();
                }
            }
        });
    }

    public void Save() {
        boolean state;
        try {
            File file = new File(raiz, title.getText().toString() + ".txt");
            if (raiz.exists()) {
                BufferedWriter out = new BufferedWriter(new FileWriter(file));
                out.write(body_text.getText().toString());
                out.close();
            } else {
                raiz.mkdirs();
                BufferedWriter out = new BufferedWriter(new FileWriter(file));
                out.write(body_text.getText().toString());
                out.close();
            }
            state = true;
            notificar(state);
        } catch (Throwable t) {
            state = false;
            notificar(state);
            Toast.makeText(this, "Exception: " + t.toString(), Toast.LENGTH_LONG).show();
        }
    }

    public void notificar(boolean state) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setAutoCancel(true);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        if (state) {
            builder.setContentTitle("Archivo guardado");
            builder.setContentText("El archivo ha sido guardado correctamente");
        } else {
            builder.setContentTitle("Error al guardar");
        }
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.notify(1, builder.build());
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }


}
