package example.android.com.popularmovies.data;

import static example.android.com.popularmovies.database.FavoritesContract.FavoriteEntry.CONTENT_URI;
import static example.android.com.popularmovies.database.FavoritesContract.FavoriteEntry.TABLE_NAME;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import example.android.com.popularmovies.database.FavoritesContract;
import example.android.com.popularmovies.database.FavoritesDbHelper;
import java.util.Objects;

public class MovieContentProvider extends ContentProvider {

  // Member variable for a FavoritesDbHelper that is initiated in the onCreate() method
  private FavoritesDbHelper mFavoriteDbHelper;

  // Integer constants for the directory of favorites (100) and a single favorite (101)
  public static final int FAVORITES = 100;
  public static final int FAVORITES_WITH_ID = 101;

  public static final UriMatcher sUriMatcher = buildUriMatcher();

  public static UriMatcher buildUriMatcher() {
    UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Setting up the UriMatcher to recognize the two different Uri paths used
    uriMatcher.addURI(FavoritesContract.AUTHORITY, FavoritesContract.PATH_FAVORITES, FAVORITES);
    uriMatcher.addURI(FavoritesContract.AUTHORITY, FavoritesContract.PATH_FAVORITES + "/#",
        FAVORITES_WITH_ID);

    return uriMatcher;
  }

  @Override
  public boolean onCreate() {
    Context context = getContext();
    mFavoriteDbHelper = new FavoritesDbHelper(context);

    return true;
  }

  @Nullable
  @Override
  public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
      @Nullable String[] selectionArgs, @Nullable String sortOrder) {

    final SQLiteDatabase db = mFavoriteDbHelper.getReadableDatabase();

    int match = sUriMatcher.match(uri);

    Cursor retCursor;

    switch (match) {
      // Query for the Favorites directory
      case FAVORITES:
        retCursor = db.query(TABLE_NAME,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            sortOrder);
        break;

        // Query for a single row of data by ID
      case FAVORITES_WITH_ID:
        // Uses selection and selectionArgs
        String id = uri.getPathSegments().get(1);

        String mSelection = "_id=?";
        String[] mSelectionArgs = new String[]{id};

        retCursor = db.query(TABLE_NAME,
            projection,
            mSelection,
            mSelectionArgs,
            null,
            null,
            sortOrder);
        break;
        // The default behavior is to throw an exception
      default:
        throw new UnsupportedOperationException("Unknown Uri " + uri);
    }

    retCursor.setNotificationUri(Objects.requireNonNull(getContext()).getContentResolver(), uri);

    return retCursor;
  }

  @Nullable
  @Override
  public String getType(@NonNull Uri uri) {
    return null;
  }

  @Nullable
  @Override
  public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
    final SQLiteDatabase db = mFavoriteDbHelper.getWritableDatabase();

    int match = sUriMatcher.match(uri);

    Uri returnUri;

    switch (match) {
      case FAVORITES:
        // Inserting the values into the Favorites table
        Long id = db.insert(TABLE_NAME, null, contentValues);
        if (id > 0) {
          // success
          returnUri = ContentUris.withAppendedId(CONTENT_URI, id);
        } else {
          throw new android.database.SQLException("Failed to insert row into " + uri);
        }
        break;
      default:
        throw new UnsupportedOperationException("Unknown Uri " + uri);
    }

    getContext().getContentResolver().notifyChange(uri, null);

    return returnUri;
  }

  @Override
  public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

    final SQLiteDatabase db = mFavoriteDbHelper.getWritableDatabase();
    int match = sUriMatcher.match(uri);
    int moviesDeleted;

    switch (match) {
      case FAVORITES_WITH_ID:
        String id = uri.getPathSegments().get(1);
        moviesDeleted = db.delete(
            TABLE_NAME,
            "_id=?",
            new String[]{id});
        break;
        default:
          throw new UnsupportedOperationException("Unknown Uri " + uri);
      }

      if (moviesDeleted != 0) {
      getContext().getContentResolver().notifyChange(uri, null);
      }

    return moviesDeleted;
  }

  @Override
  public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s,
      @Nullable String[] strings) {
    return 0;
  }
}
