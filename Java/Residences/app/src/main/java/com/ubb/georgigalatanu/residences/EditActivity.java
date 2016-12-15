package com.ubb.georgigalatanu.residences;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class EditActivity extends AppCompatActivity {

    private PieChart mChart;
    private int id;
    private EditText txtDescription;
    private EditText txtTitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        txtDescription = (EditText)findViewById(R.id.editText);
        txtTitle = (EditText)findViewById(R.id.editTitle);
        mChart = (PieChart)findViewById(R.id.chart);
        id = getIntent().getIntExtra("id",-1);

        txtDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                calculateWordFreq();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if(getIntent().getStringExtra("title")!=null && getIntent().getStringExtra("description")!=null) {
            txtTitle.setText(getIntent().getStringExtra("title").toString());
            txtDescription.setText(getIntent().getStringExtra("description").toString());
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("id",id);
                returnIntent.putExtra("title",txtTitle.getText().toString());
                returnIntent.putExtra("description", txtDescription.getText().toString());
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mChart.setUsePercentValues(true);
        mChart.getDescription().setEnabled(false);
        mChart.setExtraOffsets(5, 10, 5, 5);

        mChart.setDragDecelerationFrictionCoef(0.95f);

        //mChart.setCenterTextTypeface(mTfLight);
        //mChart.setCenterText(generateCenterSpannableText());

        mChart.setDrawHoleEnabled(true);
        mChart.setHoleColor(Color.WHITE);

        mChart.setTransparentCircleColor(Color.WHITE);
        mChart.setTransparentCircleAlpha(110);

        mChart.setHoleRadius(58f);
        mChart.setTransparentCircleRadius(61f);

        mChart.setDrawCenterText(true);

        mChart.setRotationAngle(0);
        // enable rotation of the chart by touch
        mChart.setRotationEnabled(true);
        mChart.setHighlightPerTapEnabled(true);

    }

    private void calculateWordFreq(){
        HashMap<String,Integer> words = new HashMap<>();
        for(String word: txtDescription.getText().toString().split(" ")){
            Integer fq = words.get(word);
            if(fq==null)fq=0;
            words.put(word,fq+1);
        }

        ArrayList<PieEntry> entries = new ArrayList<>();
        Iterator it = words.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry<String,Integer> pair = (Map.Entry)it.next();
            entries.add(new PieEntry(pair.getValue(),pair.getKey()));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Word frequency");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        ArrayList<Integer> colors = new ArrayList<Integer>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());

        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);
        mChart.setData(data);

        // undo all highlights
        mChart.highlightValues(null);

        mChart.invalidate();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
           finish();
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_send_mail) {
            Intent sendIntent = new Intent(Intent.ACTION_VIEW);
            sendIntent.setType("plain/text");
            sendIntent.setClassName("com.google.android.gm", "com.google.android.gm.ComposeActivityGmail");
            sendIntent.putExtra(Intent.EXTRA_SUBJECT, txtTitle.getText().toString());
            sendIntent.putExtra(Intent.EXTRA_TEXT, txtDescription.getText().toString()+"\n\nSent from Memos app.");
            startActivity(sendIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
