package example.android.com.popularmovies.model;

public class MovieReview {

  private String reviewAuthor;
  private String reviewContent;
  private String reviewUrl;

  public MovieReview(String reviewAuthor, String reviewContent, String reviewUrl) {
    this.reviewAuthor = reviewAuthor;
    this.reviewContent = reviewContent;
    this.reviewUrl = reviewUrl;
  }

  public String getReviewAuthor() {
    return reviewAuthor;
  }

  public String getReviewContent() {
    return reviewContent;
  }

  public String getReviewUrl() {
    return reviewUrl;
  }
}
