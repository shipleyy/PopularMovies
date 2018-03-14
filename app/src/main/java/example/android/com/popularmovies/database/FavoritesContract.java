package example.android.com.popularmovies.database;

import android.provider.BaseColumns;

public final class FavoritesContract {

  // Making the constructor private to unable it to be accidentally instantiated
  private FavoritesContract() {
  }

  // Inner class that defines the table contents
  public static class FavoriteEntry implements BaseColumns {

    public static final String TABLE_NAME = "favorites";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_MOVIE_ID = "movieid";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_RATING = "rating";
    public static final String COLUMN_RELEASED = "released";
  }
}