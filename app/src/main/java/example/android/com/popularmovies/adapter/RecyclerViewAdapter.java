package example.android.com.popularmovies.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import example.android.com.popularmovies.R;
import example.android.com.popularmovies.model.Movie;
import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

  private ItemClickListener mClickListener;
  private final ArrayList<Movie> movie;
  private Context mContext;

  // Data is passed into the constructor
  public RecyclerViewAdapter(Context context, @NonNull ArrayList<Movie> movie) {
    this.movie = movie;
    this.mContext = context;
  }

  // Inflates the cell layout from xml when needed
  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    // Inflate the Layout recyclerview_item.xml
    View view = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.recyclerview_item, parent, false);
    return new ViewHolder(view);
  }

  // Binds the data to the textView and imageView in each cell
  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {

    // Getting the current item from the list
    final Movie ni = movie.get(position);

    // Add the static start URL to the specific URL for each movie
    Picasso.with(mContext).load(ni.getMoviePoster())
        .placeholder(R.drawable.poster_placeholder)
        .error(R.drawable.poster_placeholder)
        .into(holder.moviePosterIv);
    // Get the movie title and add it to the TextView
    String newMovieTitle = ni.getMovieTitle();
    holder.movieNameTv.setText(newMovieTitle);
  }

  // Total number of cells
  @Override
  public int getItemCount() {
    return movie.size();
  }

  // Stores and recycles views as they are scrolled off screen
  public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    ImageView moviePosterIv;
    TextView movieNameTv;

    public ViewHolder(View itemView) {
      super(itemView);
      moviePosterIv = (ImageView) itemView.findViewById(R.id.movie_poster);
      movieNameTv = (TextView) itemView.findViewById(R.id.movie_name);
      itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
      if (mClickListener != null) {
        mClickListener.onItemClick(view, getAdapterPosition());
      }
    }
  }

  // Convenience method for getting data at click position
  Movie getItem(int id) {
    return movie.get(id);
  }

  // allows clicks events to be caught
  public void setClickListener(ItemClickListener itemClickListener) {
    this.mClickListener = itemClickListener;
  }

  // parent activity will implement this method to respond to click events
  public interface ItemClickListener {

    void onItemClick(View view, int position);
  }

  // Clear the current list
  public void clear() {
    int size = movie.size();
    movie.clear();
    notifyItemRangeRemoved(0, size);
  }

  // Refresh the list with current data
  public void addAll(ArrayList<Movie> movies) {
    movie.addAll(movies);
    notifyDataSetChanged();
  }

}
