package example.android.com.popularmovies.database;

import android.net.Uri;
import android.provider.BaseColumns;

public final class FavoritesContract {

  // Declaring the authority for for the Content Provider
  public static final String AUTHORITY = "example.android.com.popularmovies";

  // Declaring the base content uri
  public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

  // This is the path for the favorites directory
  public static final String PATH_FAVORITES = "favorites";

  // Making the constructor private to unable it to be accidentally instantiated
  private FavoritesContract() {
  }

  // Inner class that defines the table contents
  public static class FavoriteEntry implements BaseColumns {

    // The content Uri for the favorite movies
    public static final Uri CONTENT_URI =
        BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITES).build();


    public static final String TABLE_NAME = "favorites";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_RATING = "rating";
    public static final String COLUMN_RELEASED = "released";
  }
}