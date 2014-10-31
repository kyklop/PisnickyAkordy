package cz.pisnickyakordy.android.adapter;

import cz.pisnickyakordy.android.R;
import cz.pisnickyakordy.android.app.AppController;
import cz.pisnickyakordy.android.model.Movie;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

public class CustomListAdapter extends BaseAdapter implements Filterable {
	private Activity activity;
	private LayoutInflater inflater;
	private List<Movie> movieItems;
    private List<Movie> orig_movieItems;
    private Filter movieFilter;
	ImageLoader imageLoader = AppController.getInstance().getImageLoader();

	public CustomListAdapter(Activity activity, List<Movie> movieItems) {
		this.activity = activity;
		this.movieItems = movieItems;
        this.orig_movieItems = movieItems;
	}

	@Override
	public int getCount() {
		return movieItems.size();
	}

	@Override
	public Object getItem(int location) {
		return movieItems.get(location);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

    @Override
    public Filter getFilter() {
        if (movieFilter == null)
            movieFilter = new MovieFilter();

        return movieFilter;
    }

    private class MovieFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            // We implement here the filter logic
            if (constraint == null || constraint.length() == 0) {
                // No filter implemented we return all the list
                results.values = orig_movieItems;
                results.count = orig_movieItems.size();
            } else {
                // We perform filtering operation
                List<Movie> nMovieList = new ArrayList<Movie>();

                for (Movie m : movieItems) {
                    if (m.getTitle().toUpperCase().contains(constraint.toString().toUpperCase()))
                        nMovieList.add(m);
                }

                results.values = nMovieList;
                results.count = nMovieList.size();

            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {

            // Now we have to inform the adapter about the new list filtered
            if (results.count == 0)
                notifyDataSetInvalidated();
            else {
                movieItems = (List<Movie>) results.values;
                notifyDataSetChanged();
            }

        }
    }

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (inflater == null)
			inflater = (LayoutInflater) activity
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_row, null);
            /*
            // Now we can fill the layout with the right values
            TextView tv = (TextView) convertView.findViewById(R.id.title);
            TextView distView = (TextView) convertView.findViewById(R.id.dist);

            holder.planetNameView = tv;
            holder.distView = distView;

            convertView.setTag(holder);
            */
        }

		if (imageLoader == null)
			imageLoader = AppController.getInstance().getImageLoader();

		NetworkImageView thumbNail = (NetworkImageView) convertView.findViewById(R.id.thumbnail);
		TextView title = (TextView) convertView.findViewById(R.id.title);
		TextView rating = (TextView) convertView.findViewById(R.id.rating);
		TextView genre = (TextView) convertView.findViewById(R.id.genre);
		TextView year = (TextView) convertView.findViewById(R.id.releaseYear);

		// getting movie data for the row
		Movie m = movieItems.get(position);

		// thumbnail image
		thumbNail.setImageUrl(m.getThumbnailUrl(), imageLoader);
		
		// title
		title.setText(m.getTitle());
		
		// rating
		rating.setText("Rating: " + String.valueOf(m.getRating()));
		
		// genre
		String genreStr = "";
		for (String str : m.getGenre()) {
			genreStr += str + ", ";
		}
		genreStr = genreStr.length() > 0 ? genreStr.substring(0,
				genreStr.length() - 2) : genreStr;
		genre.setText(genreStr);
		
		// release year
		year.setText(String.valueOf(m.getYear()));

		return convertView;
	}

}