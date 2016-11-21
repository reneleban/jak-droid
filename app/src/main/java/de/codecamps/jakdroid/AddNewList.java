package de.codecamps.jakdroid;

import android.net.Uri;
import android.os.AsyncTask;
import de.codecamps.jakdroid.helpers.AsyncTaskHelpers;
import org.json.JSONObject;

import java.io.*;


class AddNewList extends AsyncTask<String, Object, JSONObject> {
    private BoardActivity boardActivity;

    public AddNewList(BoardActivity boardActivity) {
        this.boardActivity = boardActivity;
    }

    @Override
    protected JSONObject doInBackground(String... params) {
        try {
            Uri.Builder builder = new Uri.Builder().appendQueryParameter("name", params[0]);
            String query = builder.build().getEncodedQuery();
            return AsyncTaskHelpers.addNewList(boardActivity.getAuthToken(), boardActivity.getActiveBoardId(), query);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected void onPostExecute(JSONObject jsonObject) {
        new UpdateListElements(boardActivity).execute(boardActivity.getActiveBoardId());
    }
}
