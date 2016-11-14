package de.codecamps.jakdroid;

import android.os.AsyncTask;
import android.util.Log;
import de.codecamps.jakdroid.auth.AccountGeneral;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class DeleteList extends AsyncTask<String, Object, String> {
    private String authToken;


    public DeleteList(String authToken) {
        Log.d(AccountGeneral.ACCOUNT_NAME, "DeleteCard instanciate");
        this.authToken = authToken;
    }

    @Override
    protected String doInBackground(String... params) {
        Log.d(AccountGeneral.ACCOUNT_NAME, String.format("DeleteList / doInBackground  list: %s", params[0]));
        try {
            URL url = new URL("https://jak.codecamps.de/jak-card/cards/list/" + authToken + "/" + params[0]);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("DELETE");
            connection.setRequestProperty("charset", "utf-8");
            connection.connect();
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                url = new URL("https://jak.codecamps.de/jak-list/lists/list/" + authToken + "/" + params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("DELETE");
                connection.setRequestProperty("charset", "utf-8");
                connection.connect();
                return connection.getResponseCode() == HttpURLConnection.HTTP_OK ? params[0] : null;
            } else return null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String listId) {
        Log.d(AccountGeneral.ACCOUNT_NAME, String.format("DeleteList / onPostExecute  item: %s", listId));

        super.onPostExecute(listId);
    }
}
