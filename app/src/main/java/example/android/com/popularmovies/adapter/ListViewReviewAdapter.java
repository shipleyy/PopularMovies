package example.android.com.popularmovies.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import example.android.com.popularmovies.R;
import example.android.com.popularmovies.model.MovieReview;
import java.util.List;

public class ListViewReviewAdapter extends ArrayAdapter<MovieReview> {

  public ListViewReviewAdapter(@NonNull Context context,
      List<MovieReview> items) {
    super(context, R.layout.review_listview_item, items);
  }

  @SuppressLint("InflateParams")
  @NonNull
  @Override
  public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
    View v = convertView;

    if (v == null) {
      LayoutInflater vi;
      vi = LayoutInflater.from(getContext());
      v = vi.inflate(R.layout.review_listview_item, null);
    }

    MovieReview review = getItem(position);

    if (review != null) {
      TextView reviewAuthor = v.findViewById(R.id.review_author_tv);

      if (reviewAuthor != null) {
        reviewAuthor.setText(review.getReviewAuthor());
      }

      TextView reviewContent = v.findViewById(R.id.review_content_tv);
      if (reviewContent != null) {
        reviewContent.setText(review.getReviewContent());
      }
    }
    return v;
  }
}
