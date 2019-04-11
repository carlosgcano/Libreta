package com.example.libreta;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class ListEditor extends AppCompatActivity {

    ListsDatabase db;
    Button add_data;
    EditText add_task;

    ArrayList<String> listItem;
    ArrayAdapter adapter;

    ListView tasklist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_editor);


        db = new ListsDatabase(this);

        listItem = new ArrayList<>();

        add_data = findViewById(R.id.new_task_button);
        add_task = findViewById(R.id.new_task);
        tasklist = findViewById(R.id.task_list);
        registerForContextMenu(tasklist);

        fillTasks();

        add_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String task = add_task.getText().toString();
                if (!task.isEmpty() && db.insertData(task)) {
                    Toast.makeText(ListEditor.this, "Tarea añadida", Toast.LENGTH_LONG).show();
                    add_task.setText("");
                    listItem.clear();
                    fillTasks();
                } else {
                    Toast.makeText(ListEditor.this, "Tarea no añadida", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void onResume() {
        super.onResume();
        //fillTasks();
    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = this.getMenuInflater();
        inflater.inflate(R.menu.main_context_menu_task_editor, menu);
    }

    //Delete item on task list
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.delete_task:
                int pos = ((AdapterView.AdapterContextMenuInfo) item.getMenuInfo()).position;
                String name = (String) tasklist.getItemAtPosition(pos);
                db.deleteTask("Lists", name);
                listItem.clear();
                fillTasks();


            default:
                return super.onContextItemSelected(item);
        }
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

    private void fillTasks() {

        Cursor cursor = db.viewList();
        if (cursor.getCount() == 0) {

            Toast.makeText(this, "No data to show", Toast.LENGTH_SHORT).show();
        } else {

            while (cursor.moveToNext()) {
                listItem.add(cursor.getString(0));
            }
            Log.w("myApp", "TamañoTasks: " + listItem.size());
            adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, listItem);
            tasklist.setAdapter(adapter);
            Log.w("myApp", "TamañoTasks2: " + listItem.size());
        }
    }


}
