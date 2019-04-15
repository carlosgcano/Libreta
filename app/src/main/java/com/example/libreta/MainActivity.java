package com.example.libreta;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private SectionsPageAdapter mSectionsPageAdapter;
    private ViewPager mViewPager;
    TabLayout tabLayout;

    private ListView lista;
    private ArrayAdapter<String> listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.new_element);


        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());
        mViewPager = findViewById(R.id.container);
        setupViewPager(mViewPager);

        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        //Create new note or task
        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Integer tab = tabLayout.getSelectedTabPosition();

                //Log.w("myApp", tab.toString());

                startEditor(tab);

            }
        });

    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    protected void onRestoreInstanceState(Bundle inState) {
        super.onRestoreInstanceState(inState);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.threedot, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.gdriveid:
                Intent intent = new Intent(this, gdrive.class);
                startActivity(intent);
        }
        return true;
    }

    public void startEditor(Integer tab) {
        Intent intent;
        if (tab == 0) {
            intent = new Intent(this, NoteEditor.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        } else if (tab == 1) {
            intent = new Intent(this, ListEditor.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        } else {
            Toast.makeText(this, "Exception. Tab number exceeded: " + tab.toString(), Toast.LENGTH_LONG).show();
        }


    }

    private void setupViewPager(ViewPager viewPager) {
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new NoteLister(), getString(R.string.tab_name_notes));
        adapter.addFragment(new ListLister(), getString(R.string.tab_name_lists));
        viewPager.setAdapter(adapter);
    }
}
