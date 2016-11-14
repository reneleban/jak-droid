package de.codecamps.jakdroid;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import de.codecamps.jakdroid.auth.AccountGeneral;
import de.codecamps.jakdroid.data.ListElement;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class UpdateListElements extends AsyncTask<String, Object, List<ListElement>> {
    private BoardActivity boardActivity;

    public UpdateListElements(BoardActivity boardActivity) {
        this.boardActivity = boardActivity;
    }

    @Override
    protected List<ListElement> doInBackground(String... params) {
        JSONArray listItems = null;
        Log.d(AccountGeneral.ACCOUNT_NAME, "Fetching new Lists for Board "+params[0]);
        try {

            URL url = new URL("https://jak.codecamps.de/jak-list/lists/list/" + boardActivity.getAuthToken() + "/"+params[0]);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            if (connection != null && connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                try (InputStream in = new BufferedInputStream(connection.getInputStream());
                     Scanner s = new Scanner(in).useDelimiter("\\A");) {
                    String response = s.hasNext() ? s.next() : null;
                    listItems = new JSONArray(response);
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                } finally {
                    connection.disconnect();
                }
            }

            List<ListElement> listElementList = new ArrayList<>();
            if (listItems != null) {
                for (int i = 0; i < listItems.length(); i++) {
                    JSONObject listElement = listItems.getJSONObject(i);
                    listElementList.add(new ListElement(listElement));
                }
            }
            return listElementList;
        } catch (IOException | JSONException e) {
            Log.e(AccountGeneral.ACCOUNT_NAME, "Error while loading Boards", e);
            return new ArrayList<>();
        }
    }

    @Override
    protected void onPostExecute(List<ListElement> listItems) {
        ViewPager viewPager = (ViewPager) boardActivity.findViewById(R.id.viewpager);
        BoardActivity.Adapter adapter = (BoardActivity.Adapter) viewPager.getAdapter();
        Log.d(AccountGeneral.ACCOUNT_NAME, "Update adapterFragments before update: "+adapter.getCount());
        adapter.removeAllFragments();
        for(ListElement element: listItems){
            Bundle arguments = new Bundle();
            arguments.putString("auth_token", boardActivity.getAuthToken());
            arguments.putString("list_id", element.getList_id());
            arguments.putString("board_id", element.getBoard_id());
            arguments.putString("owner", element.getOwner());
            CardContentFragment cardContentFragment = new CardContentFragment();
            cardContentFragment.setArguments(arguments);
            adapter.addFragment(cardContentFragment, element.getName());
        }
        adapter.notifyDataSetChanged();
        Log.d(AccountGeneral.ACCOUNT_NAME, "Fragments after update: "+adapter.getCount());
    }
}
