package com.example.libreta;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView lista;
    private ArrayAdapter<String> listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.new_note);

        //Create new note
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity2();

            }
        });

        //Fill the note list
        lista = (ListView) findViewById(R.id.lista_libreta);
        //The folder Libreta must be created
        final List<String> listItems = getList(new File(Environment.getExternalStorageDirectory(), "Libreta"));
        listAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, listItems);
        lista.setAdapter(listAdapter);

        registerForContextMenu(lista);

        lista.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        String Templistview = listItems.get(position);
                        Templistview = Templistview.substring(0, Templistview.lastIndexOf('.'));
                        Intent intent = new Intent(MainActivity.this, Main2Activity.class);
                        intent.putExtra("Titulo", Templistview);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }
                }
        );


    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    protected void onRestoreInstanceState(Bundle inState) {
        super.onRestoreInstanceState(inState);

    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_context_menu, menu);
    }

    //Delete item on note list
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.delete_id:
                int pos = ((AdapterView.AdapterContextMenuInfo) item.getMenuInfo()).position;
                String name = (String) lista.getItemAtPosition(pos);
                deleteNote(name);

                final List<String> listItems = getList(new File(Environment.getExternalStorageDirectory(), "Libreta"));
                listAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, listItems);
                lista.setAdapter(listAdapter);
            default:
                return super.onContextItemSelected(item);
        }
    }

    public void onResume() {
        super.onResume();
        final List<String> listItems = getList(new File(Environment.getExternalStorageDirectory(), "Libreta"));
        listAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, listItems);
        lista.setAdapter(listAdapter);
    }

    public void startActivity2() {
        Intent intent = new Intent(this, Main2Activity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }


    private List<String> getList(File directory) {

        ArrayList<String> Files = new ArrayList<String>();
        String[] fileNames = directory.list();
        for (String fileName : fileNames) {
            if (fileName.endsWith(".txt")) {
                Files.add(fileName);
            }
        }
        return Files;
    }


    public void deleteNote(String name) {
        File file = new File(Environment.getExternalStorageDirectory() + "/Libreta", name);
        try {
            file.delete();
            Toast.makeText(this, "La nota ha sido borrada", Toast.LENGTH_LONG).show();
        } catch (Throwable t) {
            Toast.makeText(this, "Exception: " + t.toString(), Toast.LENGTH_LONG).show();
        }
    }

}
