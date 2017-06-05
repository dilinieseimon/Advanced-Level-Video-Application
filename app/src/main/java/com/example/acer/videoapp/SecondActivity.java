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


public class SecondActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();
    private ProgressDialog pDialog;
    private ListView listView1;
    Toolbar toolbar1;
    String subjectName;

    ArrayList<HashMap<String, String>> lessonList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        //setting title to toolbar
        toolbar1 = (Toolbar) findViewById(R.id.toolbar1);

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null) {
            toolbar1.setTitle(bundle.getString("Subject"));
            subjectName=toolbar1.getTitle().toString();
        }

        lessonList = new ArrayList<>();
        listView1 = (ListView) findViewById(R.id.list);
        new GetLessons().execute();

        listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                Intent intent = new Intent(SecondActivity.this, ThirdActivity.class);
                HashMap<String, String> lesson = lessonList.get(i);
                intent.putExtra("number", lesson.get("number"));
                intent.putExtra("name", lesson.get("name"));
                intent.putExtra("engname", lesson.get("engname"));
                startActivity(intent);
            }
        });

        //set back button on toolbar
        toolbar1.setNavigationIcon(R.drawable.back);
        toolbar1.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });



    }

    /* Async task class to get json by making HTTP call */
    private class GetLessons extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(SecondActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String url = getResources().getString(R.string.lessons_url, subjectName);
            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    // Getting JSON Array
                    JSONArray lessons = new JSONArray(jsonStr);

                    // looping through All lessons
                    for (int i = 0; i < lessons.length(); i++) {
                        JSONObject c = lessons.getJSONObject(i);

                        String number = c.getString("lessonNo");
                        String name = c.getString("lessonName");
                        String engname = c.getString("lessonEngName");

                        // tmp hash map for single lesson
                        HashMap<String, String> lesson = new HashMap<>();

                        // adding each child node to HashMap key => value
                        lesson.put("number", number);
                        lesson.put("name", name);
                        lesson.put("engname", engname);



                        // adding lesson to lesson list
                        lessonList.add(lesson);
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),"Json parsing error: " + e.getMessage(),Toast.LENGTH_LONG).show();
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
            if (pDialog.isShowing())
                pDialog.dismiss();
            /**
             * Updating parsed JSON data into ListView
             * */
            ListAdapter adapter = new SimpleAdapter(
                    SecondActivity.this, lessonList,R.layout.list_item, new String[]{"number", "name", "engname",}, new int[]{R.id.lnumber,R.id.lname,R.id.lname1});
            listView1.setAdapter(adapter);



        }


    }

}
