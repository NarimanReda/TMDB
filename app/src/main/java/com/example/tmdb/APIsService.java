package com.example.tmdb;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class APIsService extends Service {
    List<Movie> movies=new ArrayList<>();
    @Override
    public void onCreate() {
        super.onCreate();
        new InsertMoviesService().execute();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    class InsertMoviesService extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            DatabaseClient.getInstance(getApplicationContext()).getAppDatabase()
                .movieDao()
                .deleteAll();
            try {
                getData(1);
                Thread.sleep(200000);
            } catch (InterruptedException e) {
               e.printStackTrace();
            }
            DatabaseClient.getInstance(getApplicationContext()).getAppDatabase()
                  .movieDao()
                 .insertAll(movies);
            return null;
        }
    }

    final int[] totalpages = {0};
    private  void  getData(int page){
        String url = "https://api.themoviedb.org/3/movie/popular?api_key=060d5f6ea3c7ef6f079124703d8ba5bc&language=en-US&page=";
        url = url.concat(Integer.toString(page));
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Gson gson = new Gson();
                            MoviesResult moviesResult = gson.fromJson(response, MoviesResult.class);
                            if(page==1){
                                totalpages[0] =moviesResult.getTotalPages();
                            }
                            {
                                List<MoviesResponse> moviesResponses=moviesResult.getResults();
                                for(int i=0; i<moviesResponses.size(); i++){
                                    Movie movie = new Movie();
                                    movie.setUid(moviesResponses.get(i).getId());
                                    movie.setAdult(moviesResponses.get(i).getAdult());
                                    movie.setPoster_path(moviesResponses.get(i).getPosterPath());
                                    movie.setOverview(moviesResponses.get(i).getOverview());
                                    movie.setRelease_date(moviesResponses.get(i).getReleaseDate());
                                    movie.setGenre_ids(moviesResponses.get(i).getGenreIds());
                                    movie.setOriginal_title(moviesResponses.get(i).getOriginalTitle());
                                    movie.setOriginal_language(moviesResponses.get(i).getOriginalLanguage());
                                    movie.setTitle(moviesResponses.get(i).getTitle());
                                    movie.setBackdrop_path(moviesResponses.get(i).getBackdropPath());
                                    movie.setPopularity(moviesResponses.get(i).getPopularity());
                                    movie.setVote_count(moviesResponses.get(i).getVoteCount());
                                    movie.setVote_average(moviesResponses.get(i).getVoteAverage());
                                    movie.setVideo(moviesResponses.get(i).getVideo());
                                    movies.add(movie);
                                }
                                if(page+1 <= totalpages[0]){
                                    getData(page+1);
                                }

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e("Error: ", response);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }) {


            @Override
            public byte[] getBody() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                return new JSONObject(params).toString().getBytes();
            }
            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };

        stringRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 20 * 1000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 20 * 1000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {
                error.printStackTrace();
            }
        });

        VolleySingelton volleySingleton = VolleySingelton.getInstance(this);
        volleySingleton.getRequestQueue().add(stringRequest);

    }
}
