package de.codecamps.jakdroid;

import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import de.codecamps.jakdroid.auth.AccountGeneral;
import de.codecamps.jakdroid.helpers.AsyncTaskHelpers;

import java.io.IOException;

public class DeleteCard extends AsyncTask<String, Object, String> {
    private String authToken;
    private RecyclerView recyclerView;

    public DeleteCard(String authToken, RecyclerView recyclerView) {
        Log.d(AccountGeneral.ACCOUNT_NAME, "DeleteCard instanciate");

        this.authToken = authToken;
        this.recyclerView = recyclerView;
    }

    @Override
    protected String doInBackground(String... params) {
        Log.d(AccountGeneral.ACCOUNT_NAME, String.format("UpdateCardList / doInBackground  Card: %s", params[0]));
        try {
            return AsyncTaskHelpers.deleteCard(authToken, params[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String cardId) {
        Log.d(AccountGeneral.ACCOUNT_NAME, String.format("DeleteCard / onPostExecute  item: %s", cardId));

        super.onPostExecute(cardId);

        CardContentAdapter adapter = (CardContentAdapter) recyclerView.getAdapter();
        adapter.remove(cardId);
        adapter.notifyDataSetChanged();
        recyclerView.refreshDrawableState();
    }
}
