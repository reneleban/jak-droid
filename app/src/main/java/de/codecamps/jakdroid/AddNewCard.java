package de.codecamps.jakdroid;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import de.codecamps.jakdroid.data.Card;
import de.codecamps.jakdroid.helpers.AsyncTaskHelpers;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


class AddNewCard extends AsyncTask<String, Object, JSONObject> {
    private String authToken;
    private RecyclerView recyclerView;

    public AddNewCard(String authToken, RecyclerView recyclerView) {
        this.authToken = authToken;
        this.recyclerView = recyclerView;
    }

    @Override
    protected JSONObject doInBackground(String... params) {
        try {
            Uri.Builder builder = new Uri.Builder().appendQueryParameter("name", params[1]).appendQueryParameter("description", "");
            String query = builder.build().getEncodedQuery();
            return AsyncTaskHelpers.addNewCard(authToken, params[0], query);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected void onPostExecute(JSONObject jsonObject) {
        CardContentAdapter adapter = (CardContentAdapter) recyclerView.getAdapter();
        try {
            adapter.add(new Card(jsonObject));
            adapter.notifyDataSetChanged();
            recyclerView.refreshDrawableState();
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}
