package example.android.com.popularmovies.database;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import example.android.com.popularmovies.database.FavoritesContract.FavoriteEntry;

public class FavoritesDbHelper extends SQLiteOpenHelper {

  // The name of the local db containing the favorite movies
  private static final String DATABASE_NAME = "favorites.db";

  //The version of the favorites database
  private static final int DATABASE_VERSION = 1;

  public FavoritesDbHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase sqLiteDatabase) {
    // Creating the create SQL table statement with the constants from the FavoritesContract class
    final String SQL_CREATE_FAVORITES_TABLE = "CREATE TABLE "
        + FavoriteEntry.TABLE_NAME
        + " ("
        + FavoriteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
        + FavoriteEntry.COLUMN_TITLE + " TEXT NOT NULL,"
        + FavoriteEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL,"
        + FavoriteEntry.COLUMN_DESCRIPTION + " TEXT,"
        + FavoriteEntry.COLUMN_RATING + " TEXT,"
        + FavoriteEntry.COLUMN_RELEASED + " TEXT"
        + ");";

    // Executing the create table command
    sqLiteDatabase.execSQL(SQL_CREATE_FAVORITES_TABLE);
  }

  @Override
  public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FavoriteEntry.TABLE_NAME);
    onCreate(sqLiteDatabase);
  }
}
