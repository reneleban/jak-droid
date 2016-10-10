package de.codecamps.jakdroid;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.NavigationView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import de.codecamps.jakdroid.auth.AccountGeneral;
import de.codecamps.jakdroid.data.Board;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.Collator;
import java.util.*;

class UpdateBoardList extends AsyncTask<String, Object, List<Board>> {
    private BoardActivity boardActivity;

    public UpdateBoardList(BoardActivity boardActivity) {
        this.boardActivity = boardActivity;
    }

    @Override
    protected List<Board> doInBackground(String... params) {
        JSONArray boards = null;

        try {

            URL url = new URL("https://jak.codecamps.de/jak-board/board/" + boardActivity.getAuthToken());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            if (connection != null && connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                try (InputStream in = new BufferedInputStream(connection.getInputStream());
                     Scanner s = new Scanner(in).useDelimiter("\\A");) {
                    String response = s.hasNext() ? s.next() : null;
                    boards = new JSONArray(response);
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                } finally {
                    connection.disconnect();
                }
            }

            List<Board> boardList = new ArrayList<>();

            for (int i = 0; i < boards.length(); i++) {
                JSONObject board = boards.getJSONObject(i);
                boardList.add(new Board(board));
            }
            return boardList;
        } catch (IOException | JSONException e) {
            Log.e(AccountGeneral.ACCOUNT_NAME, "Error while loading Boards", e);
            return new ArrayList<>();
        }
    }

    @Override
    protected void onPostExecute(List<Board> boards) {
        NavigationView navigationView = (NavigationView) boardActivity.findViewById(R.id.nav_view);
        Menu navMenu = navigationView.getMenu();

        int menuId = -1;
        for(int i=0; i< navMenu.size(); i++){
            if(boardActivity.getString(R.string.menu_boards).equalsIgnoreCase(navMenu.getItem(i).getTitle().toString())){
                menuId = i;
            }
        }

        SubMenu subMenu;
        if(menuId == -1)
            subMenu = navMenu.addSubMenu(R.string.menu_boards);
        else
            subMenu = navMenu.getItem(menuId).getSubMenu();

        List<Board> boardList = boards;
        final Collator c = Collator.getInstance();
        Collections.sort(boardList, new Comparator<Board>() {
            @Override
            public int compare(Board lhs, Board rhs) {
                return c.compare(lhs.getName(), rhs.getName());
            }
        });

        for (Board b : boardList) {
            MenuItem m = subMenu.add(b.getName());
            Intent intent = new Intent();
            intent.putExtra("board_id", b.getId());
            intent.putExtra("board_name", b.getName());
            m.setIntent(intent);
        }

    }
}
