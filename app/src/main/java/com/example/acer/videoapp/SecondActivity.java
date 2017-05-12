package com.example.acer.videoapp;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
    private ListView lv;
    Toolbar toolbar1;

    // URL to get contacts JSON
    private static String url = "http://alvideobackend.azurewebsites.net/lesson/Chemistry";

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
        }

        lessonList = new ArrayList<>();

        lv = (ListView) findViewById(R.id.list);

        new GetLessons().execute();
    }

    /* Async task class to get json by making HTTP call
    */
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
            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {

                    JSONArray lessons = new JSONArray(jsonStr);
                    //JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    // JSONArray lessons = jsonObj.getJSONArray("");

                    // looping through All Contacts
                    for (int i = 0; i < lessons.length(); i++) {
                        JSONObject c = lessons.getJSONObject(i);

                        String number = c.getString("lessonNo");
                        String name = c.getString("lessonName");

                        // tmp hash map for single contact
                        HashMap<String, String> lesson = new HashMap<>();

                        // adding each child node to HashMap key => value
                        lesson.put("number", number);
                        lesson.put("name", name);


                        // adding contact to contact list
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
                    SecondActivity.this, lessonList,R.layout.list_item, new String[]{"number", "name",}, new int[]{R.id.lnumber,R.id.lname});
            lv.setAdapter(adapter);
        }


    }

}
