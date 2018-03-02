package example.android.com.popularmovies.database;

import android.provider.BaseColumns;

public final class FavoritesContract {


  // Making the constructor private to unable it to be accidentally instantiated
  private FavoritesContract(){}


  public static class FavoriteEntry implements BaseColumns {
    public static final String TABLE_NAME = "favorites";
    public static final String COLUMN_NAME_= "title";
  }



}
