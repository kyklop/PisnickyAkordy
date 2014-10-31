package cz.pisnickyakordy.android.app;

import cz.pisnickyakordy.android.MainActivity;
import cz.pisnickyakordy.android.R;
import cz.pisnickyakordy.android.model.Movie;
import cz.pisnickyakordy.android.util.DatabaseHandler;
import cz.pisnickyakordy.android.util.LruBitmapCache;
import cz.pisnickyakordy.android.util.ServiceHandler;

import android.app.AlertDialog;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AppController extends Application {

    public static final String TAG = AppController.class.getSimpleName();
    DatabaseHandler db = new DatabaseHandler(this);

    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    private static AppController mInstance;

    String StoredVersionName = "";
    String VersionName;
    SharedPreferences prefs;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        CheckFirstRun();
    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    public static String getVersionName(Context context, Class cls) {
        try {
            ComponentName comp = new ComponentName(context, cls);
            PackageInfo pinfo = context.getPackageManager().getPackageInfo(
                    comp.getPackageName(), 0);
            return pinfo.versionName;
        } catch (android.content.pm.PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    public void CheckFirstRun() {

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.fail)
                        .setContentTitle("Onstart app")
                        .setContentText("První spuštění");
        int mNotificationNew = 001;


        NotificationCompat.Builder uBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.fail)
                        .setContentTitle("Onstart app")
                        .setContentText("Upgrade");
        int mNotificationUpg = 002;

        NotificationCompat.Builder nBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.success)
                        .setContentTitle("Onstart app")
                        .setContentText("Normalní běh");
        int mNotificationNor = 003;

        NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        VersionName = getVersionName(this, this.getClass());
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        StoredVersionName = (prefs.getString("versionName", null));
        if (StoredVersionName == null || StoredVersionName.length() == 0) {
            mNotifyMgr.notify(mNotificationNew, mBuilder.build());
            // readFavouriteSongsRequests();
            // Calling async task to get json
            new GetMovies().execute();
        } else if (!StoredVersionName.equals(VersionName)) {
            mNotifyMgr.notify(mNotificationUpg, uBuilder.build());
        } else {
            mNotifyMgr.notify(mNotificationNor, nBuilder.build());
        }
        prefs.edit().putString("versionName", VersionName).commit();
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(this.mRequestQueue,
                    new LruBitmapCache());
        }
        return this.mImageLoader;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    public void readFavouriteSongsRequests() {
        // Movies json url
        final String url = "http://api.androidhive.info/json/movies.json";
        final String TAG_MOVIES = "movies";
        // contacts JSONArray
        JSONArray mvs = null;

        ServiceHandler sh = new ServiceHandler();

        // Making a request to url and getting response
        String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);

        Log.d("Response: ", "> " + jsonStr);

        if (jsonStr != null) {
            try {
                JSONObject jsonObj = new JSONObject(jsonStr);

                // Getting JSON Array node
                mvs = jsonObj.getJSONArray(TAG_MOVIES);

                // looping through All Contacts
                for (int i = 0; i < mvs.length(); i++) {
                    try {

                        JSONObject obj = mvs.getJSONObject(i);
                        Movie movie = new Movie();
                        movie.setId(i);
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

                        db.addSong(movie);
                        Log.d("DB Insert: ", i + " " + movie.getTitle() + " " + movie.getThumbnailUrl());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Log.e("ServiceHandler", "Couldn't get any data from the url");
        }
    }


    /**
     * Async task class to get json by making HTTP call
     * */
    private class GetMovies extends AsyncTask<Void, Void, Void> {

        // Movies json url
        final String url = "http://api.androidhive.info/json/movies.json";
        final String TAG_MOVIES = "movies";
        // contacts JSONArray
        JSONArray mvs = null;

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);

            Log.d("Response: ", "> " + jsonStr);

            if (jsonStr != null) {
                try {
                    // JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    mvs = new JSONArray(jsonStr);

                    // looping through All Contacts
                    for (int i = 0; i < mvs.length(); i++) {
                        try {

                            JSONObject obj = mvs.getJSONObject(i);
                            Movie movie = new Movie();
                            movie.setId(i);
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

                            db.addSong(movie);
                            Log.d("DB Insert: ", i + " " + movie.getTitle() + " " + movie.getThumbnailUrl() );

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }

            return null;
        }

    }

}