package example.android.com.popularmovies.model;

public class MovieTrailer {

  private String trailerKey;
  private String trailerTitle;

  public MovieTrailer(String trailerKey, String trailerTitle) {
    this.trailerKey = trailerKey;
    this.trailerTitle = trailerTitle;
  }

  public String getTrailerKey() {
    return trailerKey;
  }

  public String getTrailerTitle() {
    return trailerTitle;
  }

}
