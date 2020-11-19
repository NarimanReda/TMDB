package com.example.tmdb;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.sqlite.db.SimpleSQLiteQuery;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import static java.lang.Integer.parseInt;

public class HomeActivity extends AppCompatActivity implements RecyclerViewOnClick{
    RecyclerView recyclerViewHome;
    Button buttonFilter;
    List<Movie> movies=new ArrayList<>();
    List<Genre> genres=new ArrayList<>();
    AlertDialog.Builder dialogBuilder;
    AlertDialog dialog;
    Button buttonCancelAvgVotePopup;
    Button buttonDoneAvgVotePopup;
    Button buttonAdd;
    Button buttonSubtract;
    EditText  editTextSelectAvgVote;
    TextView textViewMovieDetailsTitle;
    TextView textViewMovieDetailsOverview;
    TextView textViewMovieDetailsGenres;
    TextView textViewMovieDetailsReleaseDate;
    TextView textViewMovieDetailsVote;
    TextView textViewMovieDetailsLanguage;
    double avgVote;
    Boolean voteFilter;
    Boolean genreFilter;
    String []genresList;
    boolean[] checkedGenres;
    ArrayList<Integer> selectedGenres = new ArrayList<>();
    List<Integer> filterGenres = new ArrayList<Integer>();
    ImageView imageViewMovieDetailsPoster;
    ImageView imageViewProgressBar;
    TextView installingMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        voteFilter = false;
        genreFilter = false;
        avgVote = 0;
        genresList = getResources().getStringArray(R.array.genres);
        checkedGenres = new boolean[genresList.length];
        recyclerViewHome = findViewById(R.id.recyclerview_homepage);
        buttonFilter = findViewById(R.id.button_filter);
        imageViewProgressBar =findViewById(R.id.recyclerview_progress_bar);
        installingMessage = findViewById(R.id.textview_message);
        recyclerViewHome.setLayoutManager(new LinearLayoutManager(HomeActivity.this));
        buttonFilter.setOnClickListener(new View.OnClickListener() {

        //On click menu item
        @Override
        public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(getApplicationContext(),buttonFilter);
                popupMenu.getMenuInflater().inflate(R.menu.filter_menu,popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.filter_average_vote:
                                createVoteDialog();
                                return true;
                            case R.id.filter_genres:
                                createGenreDialog();
                                return true;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this,APIsService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 1, intent, 0);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if(!prefs.getBoolean("firstTime", false)) {
            InsertMoviesFirstTime insertMoviesFirstTime =  new InsertMoviesFirstTime();
            insertMoviesFirstTime.execute();
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("firstTime", true);
            editor.commit();
        }
        InsertMovies insertMovies = new InsertMovies();
        insertMovies.execute();
    }

    //Show movie details on item click
    @Override
    public void onItemClick(int position) {
        int i = movies.get(position).getUid();
        GetMovieDetails getMovieDetails = new GetMovieDetails();
         getMovieDetails.execute(i);
    }
    //Create pop up genre filter
    public void createGenreDialog(){
        voteFilter = false;
        avgVote = 0;
        dialogBuilder = new AlertDialog.Builder(this,R.style.dialog_theme);
        dialogBuilder.setTitle(R.string.select_genres);
        dialogBuilder.setMultiChoiceItems(genresList, checkedGenres, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                if(isChecked){
                    selectedGenres.add(which);
                }else{
                    selectedGenres.remove((Integer.valueOf(which)));
                }
            }
        });
        if(genreFilter==false){
            for (int i = 0; i < checkedGenres.length; i++) {
                checkedGenres[i] = false;
                selectedGenres.clear();
                filterGenres.clear();
            }
        }
        dialogBuilder.setCancelable(false);
        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                filterGenres.clear();
                for(int i=0; i<selectedGenres.size(); i++){
                    for(int j=0; j<genres.size(); j++){
                        if(String.valueOf(genresList[selectedGenres.get(i)]).equals(String.valueOf(genres.get(j).getName()))){
                            filterGenres.add(genres.get(j).getUid());
                        }
                    }
                }
                if(!(filterGenres.size() == 0)){
                    genreFilter = true;
                }
                else if(filterGenres.size()==0){
                    genreFilter = false;
                }
                InsertMovies insertMovies = new InsertMovies();
                insertMovies.execute();
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        dialogBuilder.setNeutralButton("Clear All", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                for (int i = 0; i < checkedGenres.length; i++) {
                    checkedGenres[i] = false;
                    selectedGenres.clear();
                    filterGenres.clear();
                }
            }
        });
        dialog = dialogBuilder.create();
        dialog.show();
    }
    //Create pop up vote filter
    public void createVoteDialog() {
        genreFilter = false;
        dialogBuilder = new AlertDialog.Builder(this);
        final View avgVoteDialog = getLayoutInflater().inflate(R.layout.popup_avg_vote, null);
        dialogBuilder.setView(avgVoteDialog);
        buttonCancelAvgVotePopup = (Button) avgVoteDialog.findViewById(R.id.button_cancel_avg_vote_popup);
        buttonDoneAvgVotePopup = (Button) avgVoteDialog.findViewById(R.id.button_done_avg_vote_popup);
        editTextSelectAvgVote = (EditText) avgVoteDialog.findViewById(R.id.edittext_select_average_vote);
        buttonAdd = (Button) avgVoteDialog.findViewById(R.id.button_add);
        buttonSubtract = (Button) avgVoteDialog.findViewById(R.id.button_minus);
        editTextSelectAvgVote.setText(String.valueOf(avgVote) );
        dialog = dialogBuilder.create();
        dialog.show();
        buttonCancelAvgVotePopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        buttonDoneAvgVotePopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                avgVote = Double.parseDouble(editTextSelectAvgVote.getText().toString());
                if(avgVote<0 || avgVote>10){
                    voteFilter = false;
                    avgVote = 0.0;
                    editTextSelectAvgVote.setText("0");
                    Toast.makeText(HomeActivity.this, "Please enter a number between 0 and 10", Toast.LENGTH_SHORT).show();

                }
                else{
                    voteFilter = true;
                    InsertMovies insertMovies = new InsertMovies();
                    insertMovies.execute();
                    dialog.dismiss();
                }


            }
        });

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Double vote = Double.parseDouble(editTextSelectAvgVote.getText().toString());
                if (vote + 0.1 <= 10.0) {
                    vote = vote + 0.1;
                    vote = new BigDecimal(vote).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
                    editTextSelectAvgVote.setText(vote.toString());
                } else if (vote + 0.1 > 10.0) {
                    Toast.makeText(HomeActivity.this, "Please enter a number between 0 and 10", Toast.LENGTH_SHORT).show();
                }

            }
        });

        buttonSubtract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Double vote = Double.parseDouble(editTextSelectAvgVote.getText().toString());
                if (vote - 0.1 >= 0.0) {
                    vote = vote - 0.1;
                    vote = new BigDecimal(vote).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
                    editTextSelectAvgVote.setText(vote.toString());
                } else if (vote - 0.1 < 0.0) {
                    Toast.makeText(HomeActivity.this, "Please enter a number between 0 and 10", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    //Create movie details dialog
    public void createMovieDetailsDialog(Movie movie){
        dialogBuilder = new AlertDialog.Builder(this);
        final View dialogMovieDetails = getLayoutInflater().inflate(R.layout.dialog_movie_details, null);
        dialogBuilder.setView(dialogMovieDetails);
        textViewMovieDetailsGenres = dialogMovieDetails.findViewById(R.id.textview_movie_details_genres);
        textViewMovieDetailsOverview = dialogMovieDetails.findViewById(R.id.textview_movie_details_overview);
        textViewMovieDetailsReleaseDate = dialogMovieDetails.findViewById(R.id.textview_movie_details_release_date);
        textViewMovieDetailsTitle = dialogMovieDetails.findViewById(R.id.textview_movie_details_title);
        textViewMovieDetailsVote = dialogMovieDetails.findViewById(R.id.textview_movie_details_vote);
        textViewMovieDetailsLanguage = dialogMovieDetails.findViewById(R.id.textview_movie_details_language);
        imageViewMovieDetailsPoster = dialogMovieDetails.findViewById(R.id.image_movie_details_poster);
        dialog = dialogBuilder.create();
        dialog.show();
        String url = "https://image.tmdb.org/t/p/w500";
        url = url.concat(movie.getPoster_path());
        Glide.with(HomeActivity.this)
                .load(url)
                .placeholder(R.drawable.progress_bar)
                .into(imageViewMovieDetailsPoster);

        List<Integer> genresArray = movie.getGenre_ids();
        String genresText = "";
        for(int i=0; i<genresArray.size(); i++){
            for(int j=0; j<genres.size(); j++){
                if(String.valueOf(genresArray.get(i)).equals(String.valueOf(genres.get(j).getUid()))){
                    genresText = genresText.concat(genres.get(j).getName());
                }
            }
            if(i+1<genresArray.size()){
                genresText = genresText.concat(", ");
            }
        }
        String vote = String.valueOf(movie.getVote_average());
        vote = vote.concat("/10");
        textViewMovieDetailsOverview.setText(movie.getOverview());
        textViewMovieDetailsReleaseDate.setText(movie.getRelease_date());
        textViewMovieDetailsTitle.setText(movie.getTitle());
        textViewMovieDetailsGenres.setText(genresText);
        textViewMovieDetailsVote.setText(vote);
        textViewMovieDetailsLanguage.setText(movie.getOriginal_language());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }
    //Get movie details from database
    class GetMovieDetails extends AsyncTask<Integer,Void,Movie>{
        @Override
        protected Movie doInBackground(Integer... integers) {
            Movie movie = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase()
                    .movieDao()
                    .getMovieByID(integers[0]);
            return movie;
        }

        @Override
        protected void onPostExecute(Movie movie) {
            super.onPostExecute(movie);
            createMovieDetailsDialog( movie);

        }
    }
    //Get movies list from database
    class InsertMovies extends AsyncTask<Void,Void,Void> {
        @Override
        protected Void doInBackground(Void... voids) {

            if(voteFilter == true){
                movies = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase()
                 .movieDao()
                  .getMoviesWithMinVote(avgVote);
            }
            else if(genreFilter == true) {
                String queryGenre = "SELECT * FROM Movie WHERE genre_ids LIKE '%' || ";
                for (int i = 0; i < filterGenres.size(); i++) {
                    queryGenre = queryGenre.concat(String.valueOf(filterGenres.get(i)));
                    queryGenre = queryGenre.concat(" || '%'");
                    if (i + 1 < filterGenres.size()) {
                        queryGenre = queryGenre.concat(" UNION SELECT * FROM Movie WHERE genre_ids LIKE '%' ||  ");
                    }
                }
                movies = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase()
                        .movieDao()
                        .getMovieGenreQuery(new
                                SimpleSQLiteQuery(queryGenre));
            }
            else if(voteFilter == false && genreFilter ==false){
                movies = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase()
                        .movieDao()
                        .getAll();
            }
            genres = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase()
                    .genreDao()
                    .getAll();
            if(genres.size() ==0 & voteFilter==false & genreFilter ==false){
                Genre genre1 = new Genre(28,"Action");
                Genre genre2 = new Genre(37,"Western");
                Genre genre3 = new Genre(10752,"War");
                Genre genre4 = new Genre(53,"Thriller");
                Genre genre5 = new Genre(10770,"TV Movie");
                Genre genre6 = new Genre(878,"Science Fiction");
                Genre genre7 = new Genre(10749,"Romance");
                Genre genre8 = new Genre(9648,"Mystery");
                Genre genre9 = new Genre(10402,"Music");
                Genre genre10 = new Genre(27,"Horror");
                Genre genre11 = new Genre(36,"History");
                Genre genre12 = new Genre(14,"Fantasy");
                Genre genre13 = new Genre(10751,"Family");
                Genre genre14 = new Genre(18,"Drama");
                Genre genre15 = new Genre(99,"Documentary");
                Genre genre16 = new Genre(80,"Crime");
                Genre genre17 = new Genre(35,"Comedy");
                Genre genre18 = new Genre(16,"Animation");
                Genre genre19 = new Genre(12,"Adventure");
                genres.add(genre1);
                genres.add(genre2);
                genres.add(genre3);
                genres.add(genre4);
                genres.add(genre5);
                genres.add(genre6);
                genres.add(genre7);
                genres.add(genre8);
                genres.add(genre9);
                genres.add(genre10);
                genres.add(genre11);
                genres.add(genre12);
                genres.add(genre13);
                genres.add(genre14);
                genres.add(genre15);
                genres.add(genre16);
                genres.add(genre17);
                genres.add(genre18);
                genres.add(genre19);
                DatabaseClient.getInstance(getApplicationContext()).getAppDatabase()
                        .genreDao()
                        .insertAll(genres);
                genres = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase()
                        .genreDao()
                        .getAll();
            }
            if(movies.size() ==0 & voteFilter==false & genreFilter ==false){
                try {
                    getData(1);
                    Thread.sleep(200000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                DatabaseClient.getInstance(getApplicationContext()).getAppDatabase()
                        .movieDao()
                        .insertAll(movies);
                movies = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase()
                        .movieDao()
                        .getAll();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            installingMessage.setVisibility(View.INVISIBLE);
            imageViewProgressBar.setVisibility(View.INVISIBLE);
            MoviesAdapter moviesAdapter = new MoviesAdapter(HomeActivity.this, movies,genres,HomeActivity.this::onItemClick);
            recyclerViewHome.setAdapter(moviesAdapter);
        }
    }
    //Insert movies in database for first time
    class InsertMoviesFirstTime extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                getData(1);
                Thread.sleep(200000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Log.e("hi", String.valueOf(movies.size()));
            DatabaseClient.getInstance(getApplicationContext()).getAppDatabase()
                    .movieDao()
                    .insertAll(movies);
            List<Genre>genres = new ArrayList<>();
            Genre genre1 = new Genre(28,"Action");
            Genre genre2 = new Genre(37,"Western");
            Genre genre3 = new Genre(10752,"War");
            Genre genre4 = new Genre(53,"Thriller");
            Genre genre5 = new Genre(10770,"TV Movie");
            Genre genre6 = new Genre(878,"Science Fiction");
            Genre genre7 = new Genre(10749,"Romance");
            Genre genre8 = new Genre(9648,"Mystery");
            Genre genre9 = new Genre(10402,"Music");
            Genre genre10 = new Genre(27,"Horror");
            Genre genre11 = new Genre(36,"History");
            Genre genre12 = new Genre(14,"Fantasy");
            Genre genre13 = new Genre(10751,"Family");
            Genre genre14 = new Genre(18,"Drama");
            Genre genre15 = new Genre(99,"Documentary");
            Genre genre16 = new Genre(80,"Crime");
            Genre genre17 = new Genre(35,"Comedy");
            Genre genre18 = new Genre(16,"Animation");
            Genre genre19 = new Genre(12,"Adventure");
            genres.add(genre1);
            genres.add(genre2);
            genres.add(genre3);
            genres.add(genre4);
            genres.add(genre5);
            genres.add(genre6);
            genres.add(genre7);
            genres.add(genre8);
            genres.add(genre9);
            genres.add(genre10);
            genres.add(genre11);
            genres.add(genre12);
            genres.add(genre13);
            genres.add(genre14);
            genres.add(genre15);
            genres.add(genre16);
            genres.add(genre17);
            genres.add(genre18);
            genres.add(genre19);
            DatabaseClient.getInstance(getApplicationContext()).getAppDatabase()
                    .genreDao()
                    .insertAll(genres);
            Log.e("hi", String.valueOf(genres.size()));
            return null;
        }
    }
    //APIs call
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
                            //MoviesResponse moviesResponse = gson.fromJson(response, MoviesResponse.class);
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