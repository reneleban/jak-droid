package de.codecamps.jakdroid;

import android.os.AsyncTask;
import android.util.Log;
import de.codecamps.jakdroid.auth.AccountGeneral;
import de.codecamps.jakdroid.helpers.AsyncTaskHelpers;

import java.io.IOException;

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
            return AsyncTaskHelpers.deleteListAndCards(authToken, params[0]);
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
