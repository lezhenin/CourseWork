package ru.spbstu.icc.kspt.inspace.androidapp;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

public class MainActivity extends AppCompatActivity {

    private int d = 5;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.planets_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        new DownloadPlanetsListTask().execute("https://frozen-stream-78027.herokuapp.com/planets/");

    }

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

        private JSONArray objects;

        class ViewHolder extends RecyclerView.ViewHolder {

            TextView planetName;
            TextView planetPosition;
            TextView planetSize;
            ImageView planetImage;

            ViewHolder(View v) {
                super(v);
                planetName = (TextView) v.findViewById(R.id.planetName);
                planetSize = (TextView) v.findViewById(R.id.planetSize);
                planetPosition = (TextView) v.findViewById(R.id.planetPosition);
                planetImage = (ImageView) v.findViewById(R.id.planetImage);
            }
        }

        MyAdapter(JSONArray objects) {
            this.objects = objects;
        }

        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.planet_layout, parent, false);

            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            try {
                JSONObject planetObject = objects.getJSONObject(position);
                Log.d("json", planetObject.toString());
                String name = planetObject.getString("name");
                JSONObject posObject = planetObject.getJSONObject("position");
                String pos = "(" + posObject.getString("numberOfSystem") + ", " +
                        posObject.getString("numberOfPlanet") + ")";
                int planetNumber = posObject.getInt("numberOfPlanet");

                if(planetNumber < 5) {
                    holder.planetImage.setImageDrawable(
                            getResources().getDrawable(R.drawable.planet_hot));
                } else if (planetNumber < 10) {
                    holder.planetImage.setImageDrawable(
                            getResources().getDrawable(R.drawable.planet));
                } else  {
                    holder.planetImage.setImageDrawable(
                            getResources().getDrawable(R.drawable.planet_cold));
                }

                holder.planetName.setText(name);
                holder.planetSize.setText("32");
                holder.planetPosition.setText(pos);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return objects.length();
        }
    }


    private class DownloadPlanetsListTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
            return restTemplate.getForObject(params[0], String.class);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                JSONArray planets = new JSONArray(s);
                Log.d("json", planets.toString());
                mAdapter = new MyAdapter(planets);
                mRecyclerView.setAdapter(mAdapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
