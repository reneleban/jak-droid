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
    private BoardActivity boardActivity;


    public DeleteBoard(String authToken, BoardActivity boardActivity) {
        Log.d(AccountGeneral.ACCOUNT_NAME, "DeleteCard instanciate");
        this.authToken = authToken;
        this.boardActivity = boardActivity;
    }

    @Override
    protected String doInBackground(String... params) {
        Log.d(AccountGeneral.ACCOUNT_NAME, String.format("DeleteBoard / doInBackground  list: %s", params[0]));

        String boardId = params[0];
        /**
         * fetch lists
         */
        try {
            List<ListElement> listElementsList = AsyncTaskHelpers.retrieveListElements(authToken, boardId);
            /**
             * delete all lists
             */
            for(ListElement listElement : listElementsList){
                AsyncTaskHelpers.deleteListAndCards(authToken, listElement.getList_id());
            }
            /**
             * delete board
             */
            AsyncTaskHelpers.deleteBoard(authToken, boardId);

            return boardId;

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String boardId) {
        Log.d(AccountGeneral.ACCOUNT_NAME, String.format("DeleteBoard / onPostExecute  item: %s", boardId));

        super.onPostExecute(boardId);

        new UpdateBoardList(boardActivity).execute();

    }
}
