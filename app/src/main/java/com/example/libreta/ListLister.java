package com.example.libreta;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

import java.util.ArrayList;

public class ListLister extends Fragment {

    private ListView listlists;
    private ArrayAdapter<String> listAdapter;

    ListsDatabase db;

    ArrayAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.lists_list, container, false);

        db = new ListsDatabase(this.getContext());


        //Fill the list of task lists
        listlists = view.findViewById(R.id.list_lists_listview);
        registerForContextMenu(listlists);

        listlists.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        String Templistview = listlists.getAdapter().getItem(position).toString();
                        Intent intent = new Intent(ListLister.this.getActivity(), ListEditor.class);

                        intent.putExtra("TitleList", Templistview);
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
        inflater.inflate(R.menu.main_context_menu_lists_list, menu);
    }

    //Delete item on lists list
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.deletelist_id:
                int pos = ((AdapterView.AdapterContextMenuInfo) item.getMenuInfo()).position;
                String name = (String) listlists.getItemAtPosition(pos);

                deleteList(name);
                fillLists();
            default:
                return super.onContextItemSelected(item);
        }
    }

    public void onResume() {
        super.onResume();
        fillLists();
    }

    public void deleteList(String name) {
        if (db.deleteList(name)) {
            Toast.makeText(this.getActivity(), "La lista ha sido borrada", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this.getActivity(), "La lista no ha podido ser borrada ", Toast.LENGTH_SHORT).show();
        }
    }


    private ArrayList<String> fillLists() {
        ArrayList<String> lists = new ArrayList<String>();
        Cursor cursor = db.getLists();
        if (!(cursor.getCount() == 0)) {
            while (cursor.moveToNext()) {
                lists.add(cursor.getString(0));
            }
        }
        adapter = new ArrayAdapter(this.getActivity(), R.layout.support_simple_spinner_dropdown_item, lists);
        listlists.setAdapter(adapter);
        return lists;
    }
}
