package cz.pisnickyakordy.android;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
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

import cz.pisnickyakordy.android.adapter.CustomListAdapter;
import cz.pisnickyakordy.android.adapter.InterpretersListAdapter;
import cz.pisnickyakordy.android.app.AppController;
import cz.pisnickyakordy.android.model.Interpreter;

public class InterpretersFragment extends Fragment {

	public InterpretersFragment(){}

    // Log tag
    private static final String TAG = MainActivity.class.getSimpleName();

    private boolean first = true;

    // Movies json url
    private static final String url = "http://www.pisnicky-akordy.cz/index.php?option=com_lyrics&task=interpreters.getList&format=json&tmpl=component";
    private ProgressDialog pDialog;
    private List<Interpreter> interpreterList = new ArrayList<Interpreter>();
    private GridView gridView;
    private InterpretersListAdapter adapter;


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
                                Interpreter interpreter = new Interpreter();
                                interpreter.setId(obj.getInt("id"));
                                interpreter.setName(obj.getString("name"));
                                interpreter.setOrdername(obj.getString("ordername"));
                                interpreter.setImage("http://www.pisnicky-akordy.cz/" + obj.getString("image"));
                                interpreter.setFavourite(obj.getInt("favourite"));
                                interpreter.setSongcount(obj.getInt("songcount"));

                                // adding movie to movies array
                                interpreterList.add(interpreter);

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

        View rootView = inflater.inflate(R.layout.fragment_interpreters, container, false);

        if (first) {
            pDialog = new ProgressDialog(rootView.getContext());
            // Showing progress dialog before making http request
            pDialog.setMessage("Loading...");
            pDialog.show();
        }

        gridView = (GridView) rootView.findViewById(R.id.fragment_interpreters_list);
        adapter = new InterpretersListAdapter(this.getActivity(), interpreterList);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
                String film = ((TextView) view.findViewById(R.id.id)).getText().toString();
                Toast.makeText(getActivity(), "Interpreter: " + film, Toast.LENGTH_LONG).show();
            }
        });

        setHasOptionsMenu(true);

        getActivity().setTitle("Interpreti");

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
