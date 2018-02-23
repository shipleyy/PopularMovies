package example.android.com.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Movie implements Parcelable {

  private String movieTitle, movieDescription, moviePoster, movieOriginalTitle, movieReleaseDate;
  private Double movieRating;
  private static final String API_MOVIE_POSTER_START = "http://image.tmdb.org/t/p/w185";

  /**
   * No args constructor for use in serialization
   */
  public Movie() {
  }

  public Movie(String movieTitle, String movieDescription, String moviePoster,
      Double movieRating, String movieOriginalTitle, String movieReleaseDate) {
    this.movieTitle = movieTitle;
    this.movieDescription = movieDescription;
    this.moviePoster = moviePoster;
    this.movieRating = movieRating;
    this.movieOriginalTitle = movieOriginalTitle;
    this.movieReleaseDate = movieReleaseDate;
  }

  protected Movie(Parcel in) {
    movieTitle = in.readString();
    movieDescription = in.readString();
    moviePoster = in.readString();
    movieOriginalTitle = in.readString();
    movieReleaseDate = in.readString();
    if (in.readByte() == 0) {
      movieRating = null;
    } else {
      movieRating = in.readDouble();
    }
  }

  public static final Creator<Movie> CREATOR = new Creator<Movie>() {
    @Override
    public Movie createFromParcel(Parcel in) {
      return new Movie(in);
    }

    @Override
    public Movie[] newArray(int size) {
      return new Movie[size];
    }
  };

  public String getMovieTitle() {
    return movieTitle;
  }

  public void setMovieTitle(String movieTitle) {
    this.movieTitle = movieTitle;
  }

  public String getMovieDescription() {
    return movieDescription;
  }

  public void setMovieDescription(String movieDescription) {
    this.movieDescription = movieDescription;
  }

  public String getMoviePoster() {
    return API_MOVIE_POSTER_START + moviePoster;
  }

  public void setMoviePoster(String moviePoster) {
    this.moviePoster = moviePoster;
  }

  public Double getMovieRating() {
    return movieRating;
  }

  public void setMovieRating(Double movieRating) {
    this.movieRating = movieRating;
  }

  public void setMovieOriginalTitle(String movieOriginalTitle) {
    this.movieOriginalTitle = movieOriginalTitle;
  }

  public String getMovieOriginalTitle() {
    return movieOriginalTitle;
  }

  public String getMovieReleaseDate() {
    return movieReleaseDate;
  }

  public void setMovieReleaseDate(String movieReleaseDate) {
    this.movieReleaseDate = movieReleaseDate;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel parcel, int i) {

    parcel.writeString(movieTitle);
    parcel.writeString(movieDescription);
    parcel.writeString(moviePoster);
    parcel.writeString(movieOriginalTitle);
    parcel.writeString(movieReleaseDate);
    if (movieRating == null) {
      parcel.writeByte((byte) 0);
    } else {
      parcel.writeByte((byte) 1);
      parcel.writeDouble(movieRating);
    }
  }
}
