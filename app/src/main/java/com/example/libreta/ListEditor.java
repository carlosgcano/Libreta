package com.example.libreta;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ListEditor extends AppCompatActivity {

    ListsDatabase db;
    Button add_data;
    EditText add_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_editor);

        db = new ListsDatabase(this);

        add_data = findViewById(R.id.new_task_button);
        add_name = findViewById(R.id.new_task);

        add_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String task = add_name.getText().toString();
                if (!task.isEmpty() && db.insertData(task)) {
                    Toast.makeText(ListEditor.this, "Data added", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(ListEditor.this, "Data not added", Toast.LENGTH_LONG).show();
                }
            }
        });
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
