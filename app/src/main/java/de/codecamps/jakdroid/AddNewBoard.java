package de.codecamps.jakdroid;

import android.net.Uri;
import android.os.AsyncTask;
import de.codecamps.jakdroid.helpers.AsyncTaskHelpers;
import org.json.JSONObject;

import java.io.*;


class AddNewBoard extends AsyncTask<String, Object, JSONObject> {
    private BoardActivity boardActivity;

    public AddNewBoard(BoardActivity boardActivity) {
        this.boardActivity = boardActivity;
    }

    @Override
    protected JSONObject doInBackground(String... params) {
        try {
            Uri.Builder builder = new Uri.Builder().appendQueryParameter("name", params[0]);
            String query = builder.build().getEncodedQuery();

            return AsyncTaskHelpers.addNewBoard(boardActivity.getAuthToken(), query);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected void onPostExecute(JSONObject jsonObject) {
        new UpdateBoardList(boardActivity).execute();
    }
}
