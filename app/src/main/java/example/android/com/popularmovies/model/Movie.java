package example.android.com.popularmovies.model;

public class Movie {

  private String movieTitle, movieDescription, moviePoster, movieOriginalTitle, movieReleaseDate;
  private Float movieRating;

  /**
   * No args constructor for use in serialization
   */
  public Movie() {
  }


  public Movie(String movieTitle, String movieDescription, String moviePoster,
      Float movieRating, String movieOriginalTitle, String movieReleaseDate) {
    this.movieTitle = movieTitle;
    this.movieDescription = movieDescription;
    this.moviePoster = moviePoster;
    this.movieRating = movieRating;
    this.movieOriginalTitle = movieOriginalTitle;
    this.movieReleaseDate = movieReleaseDate;
  }

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
    return moviePoster;
  }

  public void setMoviePoster(String moviePoster) {
    this.moviePoster = moviePoster;
  }

  public Float getMovieRating() {
    return movieRating;
  }

  public void setMovieRating(Float movieRating) {
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
}
