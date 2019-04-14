package com.example.libreta;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ListEditor extends AppCompatActivity {

    ListsDatabase db;
    Button add_data;
    EditText add_task;
    EditText title;

    ArrayList<String> listItems;
    ArrayList<String> SelectedItems;
    ArrayAdapter adapter;


    static String list_name = null;

    ListView tasklist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_editor);

        db = new ListsDatabase(this);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        list_name = "LIST_" + sdf.format(new Date());
        Log.w("myApp", "listname es:" + list_name);

        listItems = new ArrayList<>();

        title = findViewById(R.id.list_title);
        add_data = findViewById(R.id.new_task_button);
        add_task = findViewById(R.id.new_task);
        tasklist = findViewById(R.id.task_list);
        tasklist.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        registerForContextMenu(tasklist);

        //Load a note if exist
        String tempholder = getIntent().getStringExtra("TitleList");
        if (!TextUtils.isEmpty(tempholder)) {
            title.setText(tempholder);
            list_name = tempholder;
            db.setTableName();
        }

        add_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String task = add_task.getText().toString();

                if (!task.isEmpty()) {
                    if (listItems.contains(task)) {
                        Toast.makeText(ListEditor.this, "La tarea ya existe", Toast.LENGTH_SHORT).show();
                    } else {
                        if (db.insertData(task)) {
                            add_task.setText("");
                            listItems.clear();
                            fillTasks();
                        } else {
                            Toast.makeText(ListEditor.this, "La tarea no se ha podido añadir", Toast.LENGTH_LONG).show();
                        }
                    }
                } else {
                    Toast.makeText(ListEditor.this, "No ha introducido ninguna tarea", Toast.LENGTH_LONG).show();
                }




            }
        });

        tasklist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = ((TextView) view).getText().toString();

                if (tasklist.isItemChecked(position) == true) {

                    db.updateTask(list_name, tasklist.getItemAtPosition(position).toString(), true);
                } else {
                    db.updateTask(list_name, tasklist.getItemAtPosition(position).toString(), false);
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
                db.deleteTask(name);
                listItems.clear();
                fillTasks();
            default:
                return super.onContextItemSelected(item);
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        String title_list = title.getText().toString();
        boolean emptyTaskList = listItems.size() == 0;
        switch (item.getItemId()) {
            case android.R.id.home:
                //Alert dialog if title is empty but not if title is empty and task list
                if (TextUtils.isEmpty(title_list) && !emptyTaskList) {
                    Log.w("myApp", "El numero de elementos es:" + listItems.size());
                    AlertDialog.Builder dialog = new AlertDialog.Builder(ListEditor.this);
                    dialog.setTitle("Precaución");
                    dialog.setMessage("Se va a guardar la lista sin título");
                    dialog.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                    dialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            title.requestFocus();
                        }
                    });
                    dialog.show();
                } else {
                    finish();
                }

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onResume() {
        super.onResume();
        if (!title.getText().toString().isEmpty()) {
            fillTasks();
        }

        title.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (!title.getText().toString().isEmpty()) {
                        db.changeTableNameOnDB(title.getText().toString());
                    } else {
                        db.changeTableNameOnDB(list_name);
                    }
                }
            }
        });

    }

    @Override
    public void finish() {
        super.finish();
        Log.w("myApp", "El titulo del activity en el finish es:" + title.getText().toString());
        Log.w("myApp", "El nombre de la lista en el finish es:" + list_name);
        if (!title.getText().toString().equals(list_name)) {
            Log.w("myApp", "Entra en equals?");
            db.changeTableNameOnDB(title.getText().toString());
        }

        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void fillTasks() {
        ArrayList<Integer> aux = new ArrayList<>();

        Cursor cursor = db.viewList();
        /**if (cursor.getCount() == 0) {
            Toast.makeText(this, "No data to show", Toast.LENGTH_SHORT).show();
         } else {**/

            while (cursor.moveToNext()) {
                listItems.add(cursor.getString(0));
                if (cursor.getString(1).contains("Enable")) {
                    aux.add(cursor.getPosition());
                }

            }

            adapter = new ArrayAdapter(this, R.layout.row_list_editor, listItems);
            tasklist.setAdapter(adapter);
            for (Integer i : aux) {
                tasklist.setItemChecked(i, true);
            }
        //}
    }

    public String getListName() {
        return list_name;
    }

}
