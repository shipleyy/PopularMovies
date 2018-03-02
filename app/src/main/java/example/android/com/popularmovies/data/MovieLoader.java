package example.android.com.popularmovies.data;

import android.content.Context;
import example.android.com.popularmovies.model.Movie;
import java.util.ArrayList;

public class MovieLoader extends android.content.AsyncTaskLoader<ArrayList<Movie>> {

  private String apiUrl;

  public MovieLoader(Context context, String url) {
    super(context);
    apiUrl = url;
    onContentChanged();
  }

  @Override
  public ArrayList<Movie> loadInBackground() {
    if (apiUrl == null) {
      return null;
    }
    return FetchApiData.getMovieData(apiUrl);
  }

  @Override
  protected void onStartLoading() {
    if (takeContentChanged())
      forceLoad();
  }

  @Override
  protected void onStopLoading() {
    cancelLoad();
  }
}
