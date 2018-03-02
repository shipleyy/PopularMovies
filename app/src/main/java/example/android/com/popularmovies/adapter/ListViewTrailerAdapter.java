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
import example.android.com.popularmovies.model.MovieTrailer;
import java.util.List;

public class ListViewTrailerAdapter extends ArrayAdapter<MovieTrailer> {

  public ListViewTrailerAdapter(Context context, List<MovieTrailer> items) {
    super(context, R.layout.trailer_listview_item, items);
  }

  @SuppressLint("InflateParams")
  @NonNull
  @Override
  public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
    View v = convertView;

    if (v == null) {
      LayoutInflater vi;
      vi = LayoutInflater.from(getContext());
      v = vi.inflate(R.layout.trailer_listview_item, null);
    }

    MovieTrailer trailer = getItem(position);

    if (trailer != null) {
      TextView trailerTitle = v.findViewById(R.id.trailer_name_tv);

      if (trailerTitle != null) {
        trailerTitle.setText(trailer.getTrailerTitle());
      }
    }
    return v;
  }
}
