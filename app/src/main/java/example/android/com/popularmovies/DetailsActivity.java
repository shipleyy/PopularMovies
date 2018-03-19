package example.android.com.popularmovies;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable.ConstantState;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;
import example.android.com.popularmovies.adapter.ListViewReviewAdapter;
import example.android.com.popularmovies.adapter.ListViewTrailerAdapter;
import example.android.com.popularmovies.database.FavoritesContract;
import example.android.com.popularmovies.database.FavoritesContract.FavoriteEntry;
import example.android.com.popularmovies.database.FavoritesDbHelper;
import example.android.com.popularmovies.model.Movie;
import example.android.com.popularmovies.model.MovieReview;
import example.android.com.popularmovies.model.MovieTrailer;
import example.android.com.popularmovies.utils.JsonUtils;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DetailsActivity extends AppCompatActivity {

  private static final String LOG_TAG = DetailsActivity.class.getName();

  @BindView(R.id.poster_iv)
  ImageView posterIv;
  @BindView(R.id.title_tv)
  TextView titleTv;
  @BindView(R.id.original_title_tv)
  TextView originalTitleTv;
  @BindView(R.id.rating_tv)
  TextView ratingTv;
  @BindView(R.id.release_date_tv)
  TextView releaseDateTv;
  @BindView(R.id.description_tv)
  TextView descriptionTv;
  @BindView(R.id.favorite_iv)
  ImageView favoriteIv;
  @BindView(R.id.favorite_label_tv)
  TextView favoriteLabelTv;
  @BindView(R.id.trailer_listview)
  NonScrollListView trailerListview;
  @BindView(R.id.review_listview)
  NonScrollListView reviewListview;
  @BindView(R.id.details_scrollview)
  ScrollView detailsScrollview;

  // Declaring database objects
  private SQLiteDatabase mDb;
  FavoritesDbHelper dbHelper;

  // The id of the current movie
  private int movieId;
  // The movie title
  private String movieTitle;
  // The movie description
  private String movieDescription;
  // The movie rating
  private String movieRating;
  // The movie release date
  private String movieReleased;

  private boolean isFavorite;

  // Declaring the API constants
  private static String API_TRAILER_KEY = "key";
  private static String API_TRAILER_NAME = "name";
  private static String API_REVIEW_AUTHOR = "author";
  private static String API_REVIEW_CONTENT = "content";
  private static String API_REVIEW_URL = "url";

  // Declaring the arrays used
  ArrayList<MovieTrailer> movieTrailers;
  ArrayList<MovieReview> movieReviews;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_details);
    ButterKnife.bind(this);

    // Declaring and initializing the DbHelper class
    dbHelper = new FavoritesDbHelper(this);

    // Display the actionbar and show the back button
    ActionBar actionBar = this.getSupportActionBar();
    if (actionBar != null) {
      actionBar.setDisplayHomeAsUpEnabled(true);
    }

    // Get the selected movie from the Intent
    Movie selectedMovie = getIntent().getParcelableExtra("movieDetails");

    isFavorite = false;


    final Context context = getApplicationContext();

    // Load the Movie values to the views
    Picasso.with(context).load(selectedMovie.getMoviePoster()).into(posterIv);
    movieTitle = selectedMovie.getMovieTitle();
    titleTv.setText(movieTitle);
    originalTitleTv.setText(selectedMovie.getMovieOriginalTitle());

    movieRating = NumberFormat.getInstance()
        .format(selectedMovie.getMovieRating());
    ratingTv.setText(movieRating);
    movieReleased = selectedMovie.getMovieReleaseDate();
    releaseDateTv.setText(formatReleaseDate(movieReleased));
    movieDescription = selectedMovie.getMovieDescription();
    descriptionTv.setText(movieDescription);

    // Get the movie ID from the Intent to start a new JSON query for the trailer and reviews
    movieId = selectedMovie.getMovieId();

    // Request the movie data via API
    requestMovieApiData();

    // Setting onItemClickListener on the trailerListView to allow the user to open the trailer in
    // either the YouTube app, or in the web browser if YouTube is not installed
    trailerListview.setOnItemClickListener(new OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        MovieTrailer movieTrailer = movieTrailers.get(i);
        String key = movieTrailer.getTrailerKey();

        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + key));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
            Uri.parse("http://www.youtube.com/watch?v=" + key));
        try {
          context.startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
          context.startActivity(webIntent);
        }
      }
    });

    // Set onClickListener on the reviewListView to enable opening the review in a web browser
    reviewListview.setOnItemClickListener(new OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        MovieReview movieReview = movieReviews.get(i);
        String url = movieReview.getReviewUrl();

        Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        context.startActivity(webIntent);
      }
    });

    // TODO Allow for the current movie to be added or removed from the database if the star is clicked
    favoriteIv.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {

        // If the image is the empty star, save the current movie to the database and change the
        // image resource to the filled out star
        if (!isFavorite) {
          isFavorite = true;
          favoriteIv.setImageResource(R.drawable.ic_favorite_set);
          favoriteLabelTv.setText(R.string.details_favorite_label_remove);

          // Get a writable database and store it in the mDb variable
          mDb = dbHelper.getWritableDatabase();

          addFavoriteMovie(movieId, movieTitle, movieDescription, movieRating, movieReleased);
        }

        // If the star is the filled out star, it means the movie is already a favorite, and it
        // needs to be removed from the database and the image changed to the empty star
        else {
          favoriteIv.setImageResource(R.drawable.ic_favorite_not_set);
          favoriteLabelTv.setText(R.string.details_favorite_label_add);
          isFavorite = false;
          removeFavoriteMovie(movieId);
        }
      }
    });
  }

  // Method to format the release date received from JSON to each users locale format
  private String formatReleaseDate(String releaseDate) {
    Locale current = getResources().getConfiguration().locale;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", current);
    Date date = new Date();
    try {
      date = sdf.parse(releaseDate);
      Log.i("DetailsActivity", "Date formatted to:" + date.toString());
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return DateFormat.getDateInstance(DateFormat.SHORT).format(date);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();
    if (id == android.R.id.home) {
      NavUtils.navigateUpFromSameTask(this);
    }
    return super.onOptionsItemSelected(item);
  }
/*
  @SuppressWarnings("deprecation")
  @SuppressLint("NewApi")
  public static boolean checkImageResource(Context ctx, ImageView imageView) {
    boolean result = false;

    if (ctx != null && imageView != null && imageView.getDrawable() != null) {
      ConstantState constantState;

      if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
        constantState = ctx.getResources()
            .getDrawable(R.drawable.ic_favorite_not_set, ctx.getTheme())
            .getConstantState();
      } else {
        constantState = ctx.getResources().getDrawable(R.drawable.ic_favorite_not_set)
            .getConstantState();
      }
      if (imageView.getDrawable().getConstantState() == constantState) {
        result = true;
      }
    }

    Log.i(LOG_TAG, "checkImageResource run - Result = " + result);
    return result;
  }
  */

  private void requestMovieApiData() {

    // Using Volley to request trailer youtube links from API and reviews
    // Instantiate the RequestQueue
    RequestQueue queue = Volley.newRequestQueue(this);

    // Constants for the API urls
    String API_TRAILER_END = "/videos?api_key=";
    String API_REVIEW_END = "/reviews?api_key=";

    // Creating the trailer url
    String trailerUrl =
        MainActivity.API_QUERY_START + "/" + movieId + API_TRAILER_END + BuildConfig.API_KEY;

    // Request a JSON response from the provided URL to get the trailers
    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Method.GET, trailerUrl, null,
        new Listener<JSONObject>() {
          @Override
          public void onResponse(JSONObject response) {

            try {
              // Create an empty ArrayList that we can start adding trailers to
              movieTrailers = new ArrayList<>();

              JSONArray results = response.getJSONArray(JsonUtils.MOVIE_KEY_RESULTS);

              // For each trailer in the array, create a {@link MovieTrailer} object
              for (int i = 0; i < results.length(); i++) {
                JSONObject f = results.getJSONObject(i);

                String trailerKey = f.getString(API_TRAILER_KEY);
                String trailerTitle = f.getString(API_TRAILER_NAME);

                MovieTrailer newMovieTrailer = new MovieTrailer(trailerKey, trailerTitle);

                movieTrailers.add(newMovieTrailer);

                ListViewTrailerAdapter listViewTrailerAdapter = new ListViewTrailerAdapter(
                    getApplicationContext(),
                    movieTrailers);

                trailerListview.setAdapter(listViewTrailerAdapter);
              }

            } catch (JSONException e) {
              // If an error is thrown when executing any of the above statements in the "try" block,
              // catch the exception here, so the app doesn't crash. Print a log message
              // with the message from the exception.
              Log.e(LOG_TAG, "Problem parsing the trailers JSON results", e);
            }
          }
        }, null);

    // Request a JSON response from the provided URL to get the reviews
    String reviewUrl =
        MainActivity.API_QUERY_START + "/" + movieId + API_REVIEW_END + BuildConfig.API_KEY;
    JsonObjectRequest jsonObjectRequest1 = new JsonObjectRequest(Method.GET, reviewUrl, null,
        new Listener<JSONObject>() {
          @Override
          public void onResponse(JSONObject response) {

            try {

              // Create an empty ArrayList that we can start adding the reviews to
              movieReviews = new ArrayList<>();

              JSONArray results = response.getJSONArray(JsonUtils.MOVIE_KEY_RESULTS);

              // For each review in the array, create a {@link MovieReview} object
              for (int i = 0; i < results.length(); i++) {
                JSONObject f = results.getJSONObject(i);

                String reviewAuthor = f.getString(API_REVIEW_AUTHOR);
                String reviewContent = f.getString(API_REVIEW_CONTENT);
                String reviewUrl = f.getString(API_REVIEW_URL);

                MovieReview newMovieReview = new MovieReview(reviewAuthor, reviewContent,
                    reviewUrl);

                movieReviews.add(newMovieReview);

                ListViewReviewAdapter listViewReviewAdapter = new ListViewReviewAdapter(
                    getApplicationContext(),
                    movieReviews);

                reviewListview.setAdapter(listViewReviewAdapter);
              }
            } catch (JSONException e) {
              // If an error is thrown when executing any of the above statements in the "try" block,
              // catch the exception here, so the app doesn't crash. Print a log message
              // with the message from the exception.
              Log.e(LOG_TAG, "Problem parsing the review JSON results", e);
            }
          }
        }, null);

    queue.add(jsonObjectRequest);
    queue.add(jsonObjectRequest1);
  }

  private long addFavoriteMovie(int movieId, String movieTitle, String movieDescription, String movieRating, String movieReleased) {

    // Create and add the current movie data to a ContentValues object
    ContentValues cv = new ContentValues();
    cv.put(FavoriteEntry._ID, movieId);
    cv.put(FavoriteEntry.COLUMN_TITLE, movieTitle);
    cv.put(FavoriteEntry.COLUMN_DESCRIPTION, movieDescription);
    cv.put(FavoriteEntry.COLUMN_RATING, movieRating);
    cv.put(FavoriteEntry.COLUMN_RELEASED, movieReleased);

    Toast.makeText(getApplicationContext(), "Added a movie with the ID " + movieId, Toast.LENGTH_SHORT).show();
    return mDb.insert(FavoriteEntry.TABLE_NAME, null, cv);
  }

  private boolean removeFavoriteMovie(int id){
    Toast.makeText(getApplicationContext(), "Deleted the movie with ID " + id, Toast.LENGTH_SHORT).show();
    return mDb.delete(FavoriteEntry.TABLE_NAME, FavoriteEntry._ID + "=" + id, null) > 0;
  }
}
