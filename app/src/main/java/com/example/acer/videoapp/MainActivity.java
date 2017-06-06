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
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;


public class MainActivity extends AppCompatActivity {
    private String TAG = MainActivity.class.getSimpleName();
    private ProgressDialog pDialog;
    Toolbar toolbar;
    ListView listView;

    ArrayList<HashMap<String, String>> subjectList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.listView);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("විෂයන්");
        toolbar.setTitleTextColor(getColor(R.color.white));

        subjectList = new ArrayList<>();
        listView = (ListView) findViewById(R.id.listView);
        new GetSubjects().execute();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                HashMap<String, String> lesson = subjectList.get(i);
                intent.putExtra("description", lesson.get("description"));
                intent.putExtra("engdescription", lesson.get("engdescription"));
                startActivity(intent);
            }
        });

    }

    /* Async task class to get json by making HTTP call */
    private class GetSubjects extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String url = getResources().getString(R.string.subjects_url);
            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    // Getting JSON Array
                    JSONArray subjects = new JSONArray(jsonStr);

                    // looping through All lessons
                    for (int i = 0; i < subjects.length(); i++) {
                        JSONObject c = subjects.getJSONObject(i);

                        String description = c.getString("description");
                        String engdescription = c.getString("engdescription");

                        // tmp hash map for single lesson
                        HashMap<String, String> subject = new HashMap<>();

                        // adding each child node to HashMap key => value
                        subject.put("description", description);
                        subject.put("engdescription", engdescription);

                        // adding lesson to lesson list
                        subjectList.add(subject);
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Json parsing error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });

                }
            } else {
                Log.e(TAG, "Please check your Internet Connection");
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
            if (pDialog.isShowing())
                pDialog.dismiss();
            /**
             * Updating parsed JSON data into ListView
             * */
            ListAdapter adapter = new SimpleAdapter(
                    MainActivity.this, subjectList,R.layout.list_item, new String[]{"description","engdescription",}, new int[]{R.id.lname,R.id.lname1});
            listView.setAdapter(adapter);



        }


    }
}

