package de.codecamps.jakdroid;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import de.codecamps.jakdroid.auth.AccountGeneral;
import de.codecamps.jakdroid.data.ListElement;
import de.codecamps.jakdroid.helpers.AsyncTaskHelpers;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class UpdateListElements extends AsyncTask<String, Object, List<ListElement>> {
    private BoardActivity boardActivity;

    public UpdateListElements(BoardActivity boardActivity) {
        this.boardActivity = boardActivity;
    }

    @Override
    protected List<ListElement> doInBackground(String... params) {
        Log.d(AccountGeneral.ACCOUNT_NAME, "Fetching new Lists for Board " + params[0]);
        try {
            List<ListElement> listElementList = AsyncTaskHelpers.retrieveListElements(boardActivity.getAuthToken(), params[0]);
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
        Log.d(AccountGeneral.ACCOUNT_NAME, "Update adapterFragments before update: " + adapter.getCount());
        adapter.removeAllFragments();
        for (ListElement element : listItems) {
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
        Log.d(AccountGeneral.ACCOUNT_NAME, "Fragments after update: " + adapter.getCount());
    }
}
