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
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ListEditor extends AppCompatActivity {

    ListsDatabase db;
    Button add_data;
    EditText add_task;
    EditText title;

    ArrayList<String> listItems;
    ArrayList<String> SelectedItems;
    ArrayAdapter adapter;

    ListView tasklist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_editor);


        db = new ListsDatabase(this);

        listItems = new ArrayList<>();

        title = findViewById(R.id.list_title);
        add_data = findViewById(R.id.new_task_button);
        add_task = findViewById(R.id.new_task);
        tasklist = findViewById(R.id.task_list);
        tasklist.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        registerForContextMenu(tasklist);

        fillTasks();

        add_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String task = add_task.getText().toString();


                if (db.taskExistOnList("hola", task)) {
                    Toast.makeText(ListEditor.this, "La tarea ya existe", Toast.LENGTH_SHORT).show();

                } else {
                    if (!task.isEmpty() && db.insertData(task)) {
                        Toast.makeText(ListEditor.this, "Tarea añadida", Toast.LENGTH_LONG).show();
                        add_task.setText("");
                        listItems.clear();
                        fillTasks();
                    } else {
                        Toast.makeText(ListEditor.this, "Tarea no añadida", Toast.LENGTH_LONG).show();
                    }
                }


            }
        });

        tasklist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = ((TextView) view).getText().toString();

                if (tasklist.isItemChecked(position) == true) {

                    db.updateTask("hola", tasklist.getItemAtPosition(position).toString(), true);
                } else {
                    db.updateTask("hola", tasklist.getItemAtPosition(position).toString(), false);
                }
            }
        });



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
                listItems.clear();
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
        ArrayList<Integer> aux = new ArrayList<>();
        Cursor cursor = db.viewList();
        Log.w("myApp", "entra ");
        if (cursor.getCount() == 0) {

            Toast.makeText(this, "No data to show", Toast.LENGTH_SHORT).show();
        } else {

            while (cursor.moveToNext()) {
                Log.w("myApp", "entra2 ");
                listItems.add(cursor.getString(0));
                Log.w("myApp", cursor.getString(1));
                if (cursor.getString(1).contains("Enable")) {
                    Log.w("myApp", "entra3 ");
                    aux.add(cursor.getPosition());
                }

            }

            adapter = new ArrayAdapter(this, R.layout.row_list_editor, listItems);
            tasklist.setAdapter(adapter);
            for (Integer i : aux) {
                Log.w("myApp", "entra4");
                tasklist.setItemChecked(i, true);
            }


            Log.w("myApp", "TamañoTasks2: " + listItems.size());
        }
    }

}
