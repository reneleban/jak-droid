package de.codecamps.jakdroid;

import android.os.AsyncTask;
import android.util.Log;
import de.codecamps.jakdroid.auth.AccountGeneral;
import de.codecamps.jakdroid.data.ListElement;
import de.codecamps.jakdroid.helpers.AsyncTaskHelpers;
import org.json.JSONException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class DeleteBoard extends AsyncTask<String, Object, String> {
    private String authToken;


    public DeleteBoard(String authToken) {
        Log.d(AccountGeneral.ACCOUNT_NAME, "DeleteCard instanciate");
        this.authToken = authToken;
    }

    @Override
    protected String doInBackground(String... params) {
        Log.d(AccountGeneral.ACCOUNT_NAME, String.format("DeleteList / doInBackground  list: %s", params[0]));

        /**
         * fetch lists
         */
        try {
            List<ListElement> listElementsList = AsyncTaskHelpers.retrieveListElements(authToken, params[0]);
            /**
             * delete all lists
             */
            for(ListElement listElement : listElementsList){
                AsyncTaskHelpers.deleteListAndCards(authToken, listElement.getList_id());
            }


        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }




        /**
         * delete board
         */










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
