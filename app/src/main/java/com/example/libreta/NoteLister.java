package com.example.libreta;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class NoteLister extends Fragment {

    private ListView lista;
    private ArrayAdapter<String> listAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.note_list, container, false);

        //Fill the note list
        lista = view.findViewById(R.id.lista_libreta);
        //The folder Libreta must be created
        final List<String> listItems = getList(new File(Environment.getExternalStorageDirectory(), "Libreta"));
        listAdapter = new ArrayAdapter<String>(getActivity(), R.layout.support_simple_spinner_dropdown_item, listItems);
        lista.setAdapter(listAdapter);

        registerForContextMenu(lista);

        lista.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override

                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                        String Templistview = listItems.get(position);
                        Templistview = Templistview.substring(0, Templistview.lastIndexOf('.'));
                        Intent intent = new Intent(NoteLister.this.getActivity(), NoteEditor.class);
                        intent.putExtra("Titulo", Templistview);
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }
                }
        );

        return view;
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
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
                listAdapter = new ArrayAdapter<String>(this.getActivity(), R.layout.support_simple_spinner_dropdown_item, listItems);
                lista.setAdapter(listAdapter);
            default:
                return super.onContextItemSelected(item);
        }
    }

    public void onResume() {
        super.onResume();
        final List<String> listItems = getList(new File(Environment.getExternalStorageDirectory(), "Libreta"));
        listAdapter = new ArrayAdapter<String>(this.getActivity(), R.layout.support_simple_spinner_dropdown_item, listItems);
        lista.setAdapter(listAdapter);
        Log.w("myApp", "Tamaño: " + listItems.size());
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
            Toast.makeText(this.getActivity(), "La nota ha sido borrada", Toast.LENGTH_LONG).show();
        } catch (Throwable t) {
            Toast.makeText(this.getActivity(), "Exception: " + t.toString(), Toast.LENGTH_LONG).show();
        }
    }

}
