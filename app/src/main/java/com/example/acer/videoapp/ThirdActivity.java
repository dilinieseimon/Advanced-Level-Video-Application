package com.example.acer.videoapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class ThirdActivity extends AppCompatActivity {

    private String TAG = SecondActivity.class.getSimpleName();
    private ProgressDialog pDialog1;
    private ListView listView2;
    Toolbar toolbar2;
    String lessonNo;

    ArrayList<HashMap<String, String>> experimentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);

        //setting title to toolbar
        toolbar2 = (Toolbar) findViewById(R.id.toolbar2);

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null) {
            toolbar2.setTitle(bundle.getString("name"));
            lessonNo=bundle.getString("number");
        }

        experimentList = new ArrayList<>();
        listView2 = (ListView) findViewById(R.id.list2);
        new GetExperiments().execute();

        listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                Intent intent = new Intent(ThirdActivity.this, FourthActivity.class);
                HashMap<String, String> lesson = experimentList.get(i);
                intent.putExtra("name", lesson.get("name"));
                intent.putExtra("link", lesson.get("link"));
                startActivity(intent);
            }
        });

        //set back button on toolbar
        toolbar2.setNavigationIcon(R.drawable.back);
        toolbar2.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


    }

    /* Async task class to get json by making HTTP call */
    private class GetExperiments extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog1 = new ProgressDialog(ThirdActivity.this);
            pDialog1.setMessage("Please wait...");
            pDialog1.setCancelable(false);
            pDialog1.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String url = getResources().getString(R.string.experiments_url,lessonNo);
            Log.d("URL",url);
            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    // Getting JSON Array
                    JSONArray experiments = new JSONArray(jsonStr);

                    // looping through All lessons
                    for (int i = 0; i < experiments.length(); i++) {
                        JSONObject c = experiments.getJSONObject(i);

                        String name = c.getString("expName");
                        String link = c.getString("expLink");

                        // tmp hash map for single lesson
                        HashMap<String, String> experiment = new HashMap<>();

                        // adding each child node to HashMap key => value
                        experiment.put("name", name);
                        experiment.put("link", link);
                        Log.d("name", name);
                        Log.d("link",link);


                        // adding lesson to lesson list
                        experimentList.add(experiment);
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Json parsing error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });

                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),"Couldn't get json from server. Check LogCat for possible errors!",Toast.LENGTH_LONG).show();
                    }
                });

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog1.isShowing())
                pDialog1.dismiss();
            /**
             * Updating parsed JSON data into ListView
             * */
            ListAdapter adapter1 = new SimpleAdapter(
                    //SecondActivity.this, lessonList,R.layout.list_item, new String[]{"number", "name",}, new int[]{R.id.lnumber,R.id.lname});
                    ThirdActivity.this, experimentList,R.layout.list_item, new String[]{"name"}, new int[]{R.id.lname});
            listView2.setAdapter(adapter1);




        }


    }

}
