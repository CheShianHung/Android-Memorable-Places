package com.cheshianhung.memorableplaces;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    static ArrayList<String> aryList = new ArrayList<String>();
    static ArrayList<LatLng> locationList = new ArrayList<LatLng>();
    static ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView list;
        list = (ListView) findViewById(R.id.list);

        aryList = new ArrayList<String>();
        aryList.add("Add a new place...");

        arrayAdapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1, aryList) {
            @NonNull
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = (TextView) view.findViewById(android.R.id.text1);

                textView.setTextColor(Color.BLACK);

                return view;
            }
        };


        list.setAdapter(arrayAdapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(),MapsActivity.class);
                intent.putExtra("placeNumber", position);
                startActivity(intent);
            }
        });
    }
}
