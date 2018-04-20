package example.android.com.popularmovies;

import static example.android.com.popularmovies.database.FavoritesContract.FavoriteEntry.COLUMN_DESCRIPTION;
import static example.android.com.popularmovies.database.FavoritesContract.FavoriteEntry.COLUMN_RATING;
import static example.android.com.popularmovies.database.FavoritesContract.FavoriteEntry.COLUMN_RELEASED;
import static example.android.com.popularmovies.database.FavoritesContract.FavoriteEntry.COLUMN_TITLE;
import static example.android.com.popularmovies.database.FavoritesContract.FavoriteEntry.CONTENT_URI;
import static example.android.com.popularmovies.database.FavoritesContract.FavoriteEntry._ID;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import example.android.com.popularmovies.adapter.RecyclerViewAdapter;
import example.android.com.popularmovies.data.MovieLoader;
import example.android.com.popularmovies.model.Movie;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements
    RecyclerViewAdapter.ItemClickListener, LoaderManager.LoaderCallbacks<ArrayList<Movie>>,
    SharedPreferences.OnSharedPreferenceChangeListener {

  // The loader ID
  private static final int MOVIE_LOADER_ID = 1;
  // The first part of the API URL
  public static final String API_QUERY_START = "https://api.themoviedb.org/3/movie";
  // The URL for the popular movies query
  private static final String API_QUERY_POPULAR = "/popular?api_key=";
  // The URL end for the top rated movies query
  private static final String API_QUERY_TOP_RATED = "/top_rated?api_key=";
  // The unique API key for themoviedb.org
  private static final String API_KEY = BuildConfig.API_KEY;
  // The complete API URL unique for each query
  private String apiUrl;
  // The sorting preference chosen from settings
  private String movie_sort_pref;
  // The RecyclerViewAdapter for the RecyclerView
  private RecyclerViewAdapter mAdapter;
  // The ArrayList of Movie objects in use
  ArrayList<Movie> movieList;
  // The ArrayList used for the favorite movies
  ArrayList<Movie> favoriteMovieList;
  // Constant int for requests in startActivityForResult
  static final int UPDATE_MOVIE_FAVORITES = 1;
  // Boolean to check if the favorites db was updated
  boolean hasFavoritesUpdated;


  @BindView(R.id.recyclerView)
  RecyclerView recyclerView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.bind(this);

    // Number of columns in the RecyclerView
    // if in landscape show 3 columns, if in portrait show 2
    int numberOfColumns;
    Configuration config = getResources().getConfiguration();
    if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
      numberOfColumns = 3;
    } else {
      numberOfColumns = 2;
    }

    // Setting up the RecyclerView
    recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
    // ArrayList for the movies retrieved from API
    movieList = new ArrayList<>();
    // ArrayList of the movies retrieved from the db
    favoriteMovieList = new ArrayList<>();
    // Setting up the adapter
    mAdapter = new RecyclerViewAdapter(this, movieList);
    mAdapter.setClickListener(this);

    // Connecting the adapter to the RecyclerView
    recyclerView.setAdapter(mAdapter);

    // Checks for network connectivity
    ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
    assert cm != null;
    NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
    boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

    setupSharedPreferences();

    //Check if there is internet connection. If not, no need to do the background task
    if (isConnected) {

      // Use LoaderManager to make sure AsyncTask is not recreated if activity is stopped
      final LoaderManager loaderManager = getLoaderManager();

      // Check which preference is used for sorting the movies
      if (movie_sort_pref
          .equals(getResources().getStringArray(R.array.settings_sort_entry_values)[0])) {
        apiUrl = API_QUERY_START + API_QUERY_POPULAR + API_KEY;
        // Start loading the information in a background task
        loaderManager.initLoader(MOVIE_LOADER_ID, null, this).forceLoad();
      } else if (movie_sort_pref
          .equals(getResources().getStringArray(R.array.settings_sort_entry_values)[1])) {
        apiUrl = API_QUERY_START + API_QUERY_TOP_RATED + API_KEY;
        // Start loading the information in a background task
        loaderManager.initLoader(MOVIE_LOADER_ID, null, this).forceLoad();
      } else if (movie_sort_pref
          .equals(getResources().getStringArray(R.array.settings_sort_entry_values)[2])) {
        getAllFavorites();
        mAdapter.clear();
        mAdapter.addAll(favoriteMovieList);
        mAdapter.notifyDataSetChanged();
      }
    } else {
      Toast.makeText(MainActivity.this, R.string.no_internet,
          Toast.LENGTH_LONG).show();

      // If no Internet, but Favorites is selected, show list of Favorites
      if (movie_sort_pref
          .equals(getResources().getStringArray(R.array.settings_sort_entry_values)[2])) {
        getAllFavorites();
        mAdapter.clear();
        mAdapter.addAll(favoriteMovieList);
        mAdapter.notifyDataSetChanged();
      }
    }
  }

  // Handle clicks on each cell in the RecyclerView to go to the DetailsActivity
  @Override
  public void onItemClick(View view, int position) {
    Intent startDetailsActivity = new Intent(this, DetailsActivity.class);
    startDetailsActivity.putExtra("movieDetails", movieList.get(position));

    startActivityForResult(startDetailsActivity, UPDATE_MOVIE_FAVORITES);
  }

  // Using the loader to get API data in a background thread.
  @Override
  public Loader<ArrayList<Movie>> onCreateLoader(int i, Bundle bundle) {
    return new MovieLoader(this, apiUrl);
  }

  // When data is loaded, make sure previous data is removed, and add new data to the adapter
  @Override
  public void onLoadFinished(Loader<ArrayList<Movie>> loader, ArrayList<Movie> data) {
    mAdapter.clear();
    mAdapter.addAll(data);
  }

  @Override
  public void onLoaderReset(Loader<ArrayList<Movie>> loader) {
    mAdapter.clear();
  }

  // Creating the options menu
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater menuInflater = getMenuInflater();
    menuInflater.inflate(R.menu.movies_menu, menu);
    return true;
  }

  // Handle the click on the settings option
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();
    if (id == R.id.action_settings) {
      Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
      startActivity(startSettingsActivity);
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  // Getting the sharedPreferences and registering the OnSharedPreferenceListener
  // to register preference changes
  private void setupSharedPreferences() {
    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    movie_sort_pref = sharedPreferences
        .getString(getResources().getString(R.string.pref_list_sort), "popular");
    sharedPreferences.registerOnSharedPreferenceChangeListener(this);
  }

  // Update the RecyclerView with new data if the preferences are changed
  @Override
  public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {

    if (s.equals(getString(R.string.pref_list_sort))) {
      movie_sort_pref = sharedPreferences
          .getString(getString(R.string.pref_list_sort), "settings_sort_entry_values");
      if (movie_sort_pref
          .equals(getResources().getStringArray(R.array.settings_sort_entry_values)[0])) {
        apiUrl = API_QUERY_START + API_QUERY_POPULAR + API_KEY;
        getLoaderManager().restartLoader(MOVIE_LOADER_ID, null, this);
      } else if (movie_sort_pref
          .equals(getResources().getStringArray(R.array.settings_sort_entry_values)[1])) {
        apiUrl = API_QUERY_START + API_QUERY_TOP_RATED + API_KEY;
        getLoaderManager().restartLoader(MOVIE_LOADER_ID, null, this);
      } else if (movie_sort_pref
          .equals(getResources().getStringArray(R.array.settings_sort_entry_values)[2])) {
        getAllFavorites();
        mAdapter.clear();
        mAdapter.addAll(favoriteMovieList);
        mAdapter.notifyDataSetChanged();
      }
    }
  }

  // A method to query the local SQLite db for all favorite movies
  private void getAllFavorites() {

    String[] projection = {
        _ID,
        COLUMN_TITLE,
        COLUMN_DESCRIPTION,
        COLUMN_RATING,
        COLUMN_RELEASED
    };

    String sortOrder =
        _ID + " ASC";

    Cursor cursor = getContentResolver().query(CONTENT_URI, projection, null, null, sortOrder);
    favoriteMovieList.clear();

    assert cursor != null;
    for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
      String title = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE));
      String description = cursor
          .getString(cursor.getColumnIndex(COLUMN_DESCRIPTION));
      Double rating = Double
          .parseDouble(cursor.getString(cursor.getColumnIndex(COLUMN_RATING)));
      String released = cursor.getString(cursor.getColumnIndex(COLUMN_RELEASED));
      int id = cursor.getInt(cursor.getColumnIndex(_ID));
      Movie favoriteMovie = new Movie(title, description, null, rating, null, released, id);

      favoriteMovieList.add(favoriteMovie);
    }
    cursor.close();
  }

  // Check to see if the favorites database was updated in the DetailsActivity
  // If it was updated, refresh the RecyclerViewAdapter
  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    if (requestCode == UPDATE_MOVIE_FAVORITES) {
      // If the request was successful
      if (resultCode == RESULT_OK) {
        Log.i("MainActivity", "resultCode = RESULT_OK");
        hasFavoritesUpdated = true;
      } else {
        hasFavoritesUpdated = false;
      }
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
    if (movie_sort_pref
        .equals(getResources().getStringArray(R.array.settings_sort_entry_values)[2])) {
      if (hasFavoritesUpdated) {
        Log.i("MainActivity", "hasFavoriteUpdated = true");
        mAdapter.clear();
        getAllFavorites();
        mAdapter.addAll(favoriteMovieList);
        mAdapter.notifyDataSetChanged();
      }
    }
  }

  // Remember to unregister the OnSharedPreferenceChangeListener and to close the db
  @Override
  protected void onDestroy() {
    super.onDestroy();
    PreferenceManager.getDefaultSharedPreferences(this)
        .unregisterOnSharedPreferenceChangeListener(this);
  }
}
