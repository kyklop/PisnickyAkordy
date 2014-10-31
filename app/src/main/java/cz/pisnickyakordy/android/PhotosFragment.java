package cz.pisnickyakordy.android;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.pisnickyakordy.android.R;
import cz.pisnickyakordy.android.adapter.CustomListAdapter;
import cz.pisnickyakordy.android.app.AppController;
import cz.pisnickyakordy.android.model.Movie;

public class PhotosFragment extends Fragment {
	
	public PhotosFragment(){}

    // Log tag
    private static final String TAG = MainActivity.class.getSimpleName();

    private boolean first = true;

    // Movies json url
    private static final String url = "http://api.androidhive.info/json/movies.json";
    private ProgressDialog pDialog;
    private List<Movie> movieList = new ArrayList<Movie>();
    private ListView listView;
    private CustomListAdapter adapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // Creating volley request obj
        JsonArrayRequest movieReq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());
                        hidePDialog();
                        // Parsing json
                        for (int i = 0; i < response.length(); i++) {
                            try {

                                JSONObject obj = response.getJSONObject(i);
                                Movie movie = new Movie();
                                movie.setTitle(obj.getString("title"));
                                movie.setThumbnailUrl(obj.getString("image"));
                                movie.setRating(((Number) obj.get("rating"))
                                        .doubleValue());
                                movie.setYear(obj.getInt("releaseYear"));

                                // Genre is json array
                                JSONArray genreArry = obj.getJSONArray("genre");
                                ArrayList<String> genre = new ArrayList<String>();
                                for (int j = 0; j < genreArry.length(); j++) {
                                    genre.add((String) genreArry.get(j));
                                }
                                movie.setGenre(genre);

                                // adding movie to movies array
                                movieList.add(movie);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        // notifying list adapter about data changes
                        // so that it renders the list view with updated data
                        adapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                hidePDialog();

            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(movieReq);
    }

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        // changing action bar color
        // rootView.getActionBar().setBackgroundDrawable(                 new ColorDrawable(Color.parseColor("#1b1b1b")));

        View rootView = inflater.inflate(R.layout.fragment_photos, container, false);

        if (first) {
            pDialog = new ProgressDialog(rootView.getContext());
            // Showing progress dialog before making http request
            pDialog.setMessage("Loading...");
            pDialog.show();
        }

        listView = (ListView) rootView.findViewById(R.id.list_fragment);
        adapter = new CustomListAdapter(this.getActivity(), movieList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int arg2,
                                    long arg3) {
                // on selecting a single movie

                /*
                // TrackListActivity will be launched to show tracks inside the album
                Intent i = new Intent(getApplicationContext(), TrackListActivity.class);

                // send album id to tracklist activity to get list of songs under that album
                String album_id = ((TextView) view.findViewById(R.id.album_id)).getText().toString();
                i.putExtra("album_id", album_id);

                startActivity(i);
                */
                String film = ((TextView) view.findViewById(R.id.title)).getText().toString();
                Toast.makeText(getActivity(), "Clicked movie: " + film, Toast.LENGTH_LONG).show();
            }
        });

        setHasOptionsMenu(true);

        getActivity().setTitle("Fotky");

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(
        Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.photos, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.photos_search).getActionView();
        searchView.setIconifiedByDefault(false);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));

        SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener()
        {
            public boolean onQueryTextChange(String newText)
            {
                // this is your adapter that will be filtered
                adapter.getFilter().filter(newText);
                return true;
            }

            public boolean onQueryTextSubmit(String query)
            {
                // this is your adapter that will be filtered
                adapter.getFilter().filter(query);
                return true;
            }
        };
        searchView.setOnQueryTextListener(queryTextListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hidePDialog();
    }

    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
            first = false;
        }
    }

}
