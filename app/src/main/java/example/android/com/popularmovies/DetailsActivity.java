package example.android.com.popularmovies;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.squareup.picasso.Picasso;
import example.android.com.popularmovies.model.Movie;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DetailsActivity extends AppCompatActivity {

  @BindView(R.id.poster_iv)
  ImageView posterIv;
  @BindView(R.id.title_tv)
  TextView titleTv;
  @BindView(R.id.original_title_tv)
  TextView originalTitleTv;
  @BindView(R.id.rating_tv)
  TextView ratingTv;
  @BindView(R.id.release_date_tv)
  TextView releaseDateTv;
  @BindView(R.id.description_tv)
  TextView descriptionTv;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_details);
    ButterKnife.bind(this);

    ActionBar actionBar = this.getSupportActionBar();

    if (actionBar != null) {
      actionBar.setDisplayHomeAsUpEnabled(true);
    }

    // Get the selected movie from the Intent
    Movie selectedMovie = getIntent().getParcelableExtra("movieDetails");

    Context context = getApplicationContext();

    // Load the Movie values to the views
    Picasso.with(context).load(selectedMovie.getMoviePoster()).into(posterIv);
    titleTv.setText(selectedMovie.getMovieTitle());
    originalTitleTv.setText(selectedMovie.getMovieOriginalTitle());
    String formattedRating = NumberFormat.getInstance()
        .format(selectedMovie.getMovieRating());
    ratingTv.setText(formattedRating);
    releaseDateTv.setText(formatReleaseDate(selectedMovie.getMovieReleaseDate()));
    descriptionTv.setText(selectedMovie.getMovieDescription());
  }

  // Method to format the release date received from JSON to each users locale format
  private String formatReleaseDate(String releaseDate) {
    Locale current = getResources().getConfiguration().locale;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", current);
    Date date = new Date();
    try {
      date = sdf.parse(releaseDate);
      Log.i("DetailsActivity", "Date formatted to:" + date.toString());
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return DateFormat.getDateInstance(DateFormat.SHORT).format(date);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();
    if (id == android.R.id.home) {
      NavUtils.navigateUpFromSameTask(this);
    }
    return super.onOptionsItemSelected(item);
  }
}
