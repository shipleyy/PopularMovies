package example.android.com.popularmovies.utils;

import android.text.TextUtils;
import android.util.Log;
import example.android.com.popularmovies.model.Movie;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonUtils {

  private static final String LOG_TAG = JsonUtils.class.getName();

  // themoviedb.org API keys for the necessary information
  public static final String MOVIE_KEY_RESULTS = "results";
  private static final String MOVIE_KEY_TITLE = "title";
  private static final String MOVIE_KEY_POSTER_PATH = "poster_path";
  private static final String MOVIE_KEY_OVERVIEW = "overview";
  private static final String MOVIE_KEY_ORIGINAL_TITLE = "original_title";
  private static final String MOVIE_KEY_VOTE_AVERAGE = "vote_average";
  private static final String MOVIE_KEY_RELEASE_DATE = "release_date";
  private static final String MOVIE_KEY_ID = "id";

  public static ArrayList<Movie> parseMovies(String jsonResponse) {
    // If the JSON string is empty or null, then return early.
    if (TextUtils.isEmpty(jsonResponse)) {
      return null;
    }

    // Create an empty ArrayList that we can start adding movies to
    ArrayList<Movie> movies = new ArrayList<>();

    // Try to parse the jsonResponse. If there's a problem with the way the JSON
    // is formatted, a JSONException exception object will be thrown.
    // Catch the exception so the app doesn't crash, and print the error message to the logs.

    try {
      // Create a JSONObject from the JSON response string
      JSONObject root = new JSONObject(jsonResponse);

      // results array represents a list of movies
      JSONArray results = root.getJSONArray(MOVIE_KEY_RESULTS);

      // For each movie in the array, create a {@link Movie} object
      for (int i = 0; i < results.length(); i++) {
        JSONObject f = results.getJSONObject(i);

        String movieTitle = f.getString(MOVIE_KEY_TITLE);
        String moviePoster = f.getString(MOVIE_KEY_POSTER_PATH);
        String movieDescription = f.getString(MOVIE_KEY_OVERVIEW);
        String movieOriginalTitle = f.getString(MOVIE_KEY_ORIGINAL_TITLE);
        Double movieRating = f.getDouble(MOVIE_KEY_VOTE_AVERAGE);
        String movieReleaseDate = f.getString(MOVIE_KEY_RELEASE_DATE);
        int movieId = f.getInt(MOVIE_KEY_ID);

        // Save the data in the Movie class
        Movie newMovie = new Movie(movieTitle, movieDescription, moviePoster, movieRating,
            movieOriginalTitle, movieReleaseDate, movieId);

        // Add the new movie to the ArrayList
        movies.add(newMovie);
      }

    } catch (JSONException e) {
      // If an error is thrown when executing any of the above statements in the "try" block,
      // catch the exception here, so the app doesn't crash. Print a log message
      // with the message from the exception.
      Log.e(LOG_TAG, "Problem parsing the movies JSON results", e);
    }

    // Return the list of movies
    return movies;
  }
}
