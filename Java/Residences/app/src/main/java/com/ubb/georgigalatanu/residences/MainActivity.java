package com.ubb.georgigalatanu.residences;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private int editingPos = -1;
    private ListView listView;
    private SimpleAdapter listAdapter;
    private List<Map<String, String>> listData;
    private Context context;
    private List<Residence> db_residences;
    private DBHandler dbHandler;

    private void refreshFromDB(){
        db_residences =dbHandler.getAllResidences();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        listView = (ListView)findViewById(R.id.listView);
        dbHandler = new DBHandler(this);

        db_residences = dbHandler.getAllResidences();



        final Intent edit_intent = new Intent(this,EditActivity.class);
        context=this;
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                edit_intent.putExtra("id", db_residences.get(position).getId());
                edit_intent.putExtra("title",listData.get(position).get("title"));
                edit_intent.putExtra("description",listData.get(position).get("subtitle"));
                editingPos=position;
                startActivityForResult(edit_intent,1);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                listData.remove(position);
                                db_residences.remove(position);
                                refreshFromDB();
                                listAdapter.notifyDataSetChanged();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Are you sure you want to delete the selected item?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
                return true;
            }
        });
        listData = new ArrayList<>();

        for(Residence m: db_residences){
            Map<String, String> datum = new HashMap<>(2);
            datum.put("title", m.getTitle());
            datum.put("subtitle", m.getDescription());
            listData.add(datum);
        }

        listAdapter = new SimpleAdapter(this,listData,android.R.layout.simple_list_item_2,new String[] {"title", "subtitle"},
                new int[] {android.R.id.text1,
                        android.R.id.text2});
        listView.setAdapter(listAdapter);
        setSupportActionBar(toolbar);
        final Intent intent = new Intent(this,EditActivity.class);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(intent,0);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode==0){
            if(resultCode == Activity.RESULT_OK)
            {
                Log.d("onActivityResult", "here");
                Map<String, String> datum = new HashMap<>(2);
                String title = ""+data.getStringExtra("title");
                String description = ""+data.getStringExtra("description");
                dbHandler.addResidence(new Residence(0,title,description));
                refreshFromDB();
                datum.put("title", title);
                datum.put("subtitle", description);
                listData.add(datum);
                listAdapter.notifyDataSetChanged();
            }
        }
        else if(requestCode==1){
            if(resultCode==Activity.RESULT_OK){
                if(editingPos!=-1){
                    listData.remove(editingPos);
                    Map<String, String> datum = new HashMap<>(2);
                    int id = data.getIntExtra("id",-1);
                    String title = ""+data.getStringExtra("title");
                    String description = ""+data.getStringExtra("description");
                    if(id!=-1){
                        dbHandler.updateResidence(new Residence(id,title,description));
                        refreshFromDB();
                    }
                    datum.put("title", title);
                    datum.put("subtitle", description);
                    listData.add(editingPos,datum);
                    listAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent i = new Intent(this,SettingsActivity.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
