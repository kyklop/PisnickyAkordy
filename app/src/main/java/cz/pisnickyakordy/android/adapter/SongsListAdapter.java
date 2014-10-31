package cz.pisnickyakordy.android.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;
import java.util.List;

import cz.pisnickyakordy.android.R;
import cz.pisnickyakordy.android.app.AppController;
import cz.pisnickyakordy.android.model.Song;

public class SongsListAdapter extends BaseAdapter implements Filterable {
	private Activity activity;
	private LayoutInflater inflater;
	private List<Song> songItems;
    private List<Song> orig_songItems;
    private Filter songFilter;
	ImageLoader imageLoader = AppController.getInstance().getImageLoader();

	public SongsListAdapter(Activity activity, List<Song> songItems) {
		this.activity = activity;
		this.songItems = songItems;
        this.orig_songItems = songItems;
	}

	@Override
	public int getCount() {
		return songItems.size();
	}

	@Override
	public Object getItem(int location) {
		return songItems.get(location);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

    @Override
    public Filter getFilter() {
        if (songFilter == null)
            songFilter = new songFilter();

        return songFilter;
    }

    private class songFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            // We implement here the filter logic
            if (constraint == null || constraint.length() == 0) {
                // No filter implemented we return all the list
                results.values = orig_songItems;
                results.count = orig_songItems.size();
            } else {
                // We perform filtering operation
                List<Song> nSongList = new ArrayList<Song>();

                for (Song m : songItems) {
                    if (m.getName().toUpperCase().contains(constraint.toString().toUpperCase()))
                        nSongList.add(m);
                }

                results.values = nSongList;
                results.count = nSongList.size();

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
                songItems = (List<Song>) results.values;
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
            convertView = inflater.inflate(R.layout.list_song, null);
            /*
            // Now we can fill the layout with the right values
            TextView tv = (TextView) convertView.findViewById(R.id.title);
            TextView distView = (TextView) convertView.findViewById(R.id.dist);

            holder.planetNameView = tv;
            holder.distView = distView;

            convertView.setTag(holder);
            */
        }

        TextView id = (TextView) convertView.findViewById(R.id.id);
		ImageView image = (ImageView) convertView.findViewById(R.id.image);
		TextView name = (TextView) convertView.findViewById(R.id.name);
		TextView favourite = (TextView) convertView.findViewById(R.id.favourite);

		// getting data for the row
		Song m = songItems.get(position);

        id.setText(String.valueOf(m.getId()));
		image.setImageResource(convertView.getResources().getIdentifier(m.getImage(), null, null));
		name.setText(m.getName());
        favourite.setText(String.valueOf(m.getFavourite()));

		return convertView;
	}

}