package com.example.tmdb;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.ViewHolder>  {
    List<Movie> movies;
    List<Genre> genres;
    Context context;
    RecyclerViewOnClick recyclerViewOnClick;
    public MoviesAdapter(Context context,List<Movie> movies, List<Genre> genres, RecyclerViewOnClick recyclerViewOnClick) {
        this.context = context;
        this.movies = movies;
        this.genres = genres;
        this.recyclerViewOnClick = recyclerViewOnClick;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie,parent,false);
        ViewHolder holder= new MoviesAdapter.ViewHolder(row);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Movie movie = movies.get(position);
        String genresText = "";
        List<Integer> genresArray = movie.getGenre_ids();
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
        String url = "https://image.tmdb.org/t/p/w500";
        url = url.concat(movie.getPoster_path());
        Glide.with(context)
                .load(url)
                .placeholder(R.drawable.progress_bar)
                .into(holder.imageViewMoviePoster);
        holder.textViewMovieTitle.setText(movie.getTitle());
        holder.textViewMovieOverview.setText(movie.getOverview());
        holder.textViewMovieGenres.setText(genresText);
        holder.textViewMovieAverageVote.setText( Double.toString(movie.getVote_average()));

    }


    @Override
    public int getItemCount() {
        return movies.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imageViewMoviePoster;
        TextView textViewMovieTitle;
        TextView textViewMovieOverview;
        TextView textViewMovieGenres;
        TextView textViewMovieAverageVote;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewMoviePoster = itemView.findViewById(R.id.image_movie_poster);
            textViewMovieTitle = itemView.findViewById(R.id.textview_title);
            textViewMovieOverview = itemView.findViewById(R.id.textview_overview);
            textViewMovieGenres = itemView.findViewById(R.id.textview_genres);;
            textViewMovieAverageVote = itemView.findViewById(R.id.textview_average_vote);;
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recyclerViewOnClick.onItemClick(getAdapterPosition());
                }
            });
        }
    }

}
