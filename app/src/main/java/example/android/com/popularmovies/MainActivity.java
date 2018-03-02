package example.android.com.popularmovies;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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

  @BindView(R.id.recyclerView)
  RecyclerView recyclerView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.bind(this);

    int numberOfColumns = 2;

    // Setting up the RecyclerView
    recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
    movieList = new ArrayList<>();
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
      } else if (movie_sort_pref
          .equals(getResources().getStringArray(R.array.settings_sort_entry_values)[1])) {
        apiUrl = API_QUERY_START + API_QUERY_TOP_RATED + API_KEY;
      }
      // Start loading the information in a background task
      loaderManager.initLoader(MOVIE_LOADER_ID, null, this).forceLoad();

    } else {
      Toast.makeText(MainActivity.this, R.string.no_internet,
          Toast.LENGTH_LONG).show();
    }
  }

  // Handle clicks on each cell in the RecyclerView to go to the DetailsActivity
  @Override
  public void onItemClick(View view, int position) {
    Intent startDetailsActivity = new Intent(this, DetailsActivity.class);
    startDetailsActivity.putExtra("movieDetails", movieList.get(position));

    startActivity(startDetailsActivity);
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
      } else if (movie_sort_pref
          .equals(getResources().getStringArray(R.array.settings_sort_entry_values)[1])) {
        apiUrl = API_QUERY_START + API_QUERY_TOP_RATED + API_KEY;
      }
      getLoaderManager().restartLoader(MOVIE_LOADER_ID, null, this);
    }
  }

  // Remember to unregister the OnSharedPreferenceChangeListener
  @Override
  protected void onDestroy() {
    super.onDestroy();
    PreferenceManager.getDefaultSharedPreferences(this)
        .unregisterOnSharedPreferenceChangeListener(this);
  }
}
