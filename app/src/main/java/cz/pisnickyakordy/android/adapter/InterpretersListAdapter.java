package cz.pisnickyakordy.android.adapter;

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

import java.util.ArrayList;
import java.util.List;

import cz.pisnickyakordy.android.R;
import cz.pisnickyakordy.android.app.AppController;
import cz.pisnickyakordy.android.model.Interpreter;
import cz.pisnickyakordy.android.model.Movie;

public class InterpretersListAdapter extends BaseAdapter implements Filterable {
	private Activity activity;
	private LayoutInflater inflater;
	private List<Interpreter> interpreterItems;
    private List<Interpreter> orig_interpreterItems;
    private Filter interpreterFilter;
	ImageLoader imageLoader = AppController.getInstance().getImageLoader();

	public InterpretersListAdapter(Activity activity, List<Interpreter> interpreterItems) {
		this.activity = activity;
		this.interpreterItems = interpreterItems;
        this.orig_interpreterItems = interpreterItems;
	}

	@Override
	public int getCount() {
		return interpreterItems.size();
	}

	@Override
	public Object getItem(int location) {
		return interpreterItems.get(location);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

    @Override
    public Filter getFilter() {
        if (interpreterFilter == null)
            interpreterFilter = new InterpreterFilter();

        return interpreterFilter;
    }

    private class InterpreterFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            // We implement here the filter logic
            if (constraint == null || constraint.length() == 0) {
                // No filter implemented we return all the list
                results.values = orig_interpreterItems;
                results.count = orig_interpreterItems.size();
            } else {
                // We perform filtering operation
                List<Interpreter> nInterpreterList = new ArrayList<Interpreter>();

                for (Interpreter m : interpreterItems) {
                    if (m.getName().toUpperCase().contains(constraint.toString().toUpperCase()))
                        nInterpreterList.add(m);
                }

                results.values = nInterpreterList;
                results.count = nInterpreterList.size();

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
                interpreterItems = (List<Interpreter>) results.values;
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
            convertView = inflater.inflate(R.layout.list_interpreter, null);
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

        TextView id = (TextView) convertView.findViewById(R.id.id);
		NetworkImageView image = (NetworkImageView) convertView.findViewById(R.id.image);
		TextView name = (TextView) convertView.findViewById(R.id.name);
		TextView ordername = (TextView) convertView.findViewById(R.id.ordername);
		TextView songcount = (TextView) convertView.findViewById(R.id.songcount);
		TextView favourite = (TextView) convertView.findViewById(R.id.favourite);

		// getting data for the row
		Interpreter m = interpreterItems.get(position);

        id.setText(String.valueOf(m.getId()));
		image.setImageUrl(m.getImage(), imageLoader);
		name.setText(m.getName());
		ordername.setText(m.getOrdername());
		songcount.setText(String.valueOf(m.getSongcount()));
        favourite.setText(String.valueOf(m.getFavourite()));

		return convertView;
	}

}