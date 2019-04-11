package com.example.libreta;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class ListLister extends Fragment {

    private ListView lista;
    private ArrayAdapter<String> listAdapter;

    ListsDatabase db;

    ArrayList<String> listItems;
    ArrayAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.lists_list, container, false);

        db = new ListsDatabase(this.getContext());
        fillData();
        
        //Fill the note list
        lista = view.findViewById(R.id.list_lists_listview);


        /**
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
                        Intent intent = new Intent(ListLister.this.getActivity(), NoteEditor.class);
                        intent.putExtra("Titulo", Templistview);
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }
                }
        );
         **/
        return view;
    }

    private void fillData() {

        Cursor cursor = db.getLists();

        if (cursor.getCount() == 0) {

            Toast.makeText(this.getContext(), "No hay datos", Toast.LENGTH_SHORT).show();

        } else {
            while (cursor.moveToNext()) {
                listItems.add(cursor.getString(0));
            }

            listAdapter = new ArrayAdapter<String>(getActivity(), R.layout.support_simple_spinner_dropdown_item, listItems);
            lista.setAdapter(listAdapter);
        }
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.main_context_menu_task_list, menu);
    }
/**
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
 **/
}
