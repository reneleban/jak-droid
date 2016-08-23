package de.codecamps.jakdroid;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.*;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import de.codecamps.jakdroid.auth.AccountGeneral;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner;

public class BoardActivity extends AppCompatActivity {
    private String authToken = null;

    private ListView mBoardList;
    private BoardAdapter adapter;


    // https://github.com/codepath/android_guides/wiki/Using-an-ArrayAdapter-with-ListView
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_board);
        Log.d(AccountGeneral.ACCOUNT_NAME, "BoardActivity started");
        authToken = getSharedPreferences(AccountGeneral.ACCOUNT_TYPE, Context.MODE_PRIVATE).getString(AccountGeneral.ACCOUNT_NAME, null);

        mBoardList = (ListView) findViewById(R.id.boardList);
        adapter = new BoardAdapter(this, new ArrayList<Board>());

        mBoardList.setAdapter(adapter);

        mBoardList.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Board clickedBoard = (Board)parent.getItemAtPosition(position);
                Log.d(AccountGeneral.ACCOUNT_NAME, "Clicked Board{id="+clickedBoard.getId()+"; name="+clickedBoard.getName()+"}");
            }
        });

        new UpdateBoardList().execute();


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.board, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.newBoardToolbar:
                showNewBoardDialog();
                break;
            default:
                super.onOptionsItemSelected(item);
                break;
        }
        return true;
    }

    public void showNewBoardDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_new_board, null);
        dialogBuilder.setView(dialogView);

        final EditText edt = (EditText) dialogView.findViewById(R.id.new_board_title);

        dialogBuilder.setTitle(getString(R.string.add_new_board));
        dialogBuilder.setMessage(getString(R.string.add_new_board_name));
        dialogBuilder.setPositiveButton(getString(R.string.add_new_board_button_positive), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                new AddNewBoard().execute(edt.getText().toString());
            }
        });
        dialogBuilder.setNegativeButton(getString(R.string.add_new_board_button_negative), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                edt.setText("");
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }


    private class AddNewBoard extends AsyncTask<String, Object, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... params) {
            JSONObject board = null;
            try {
                Uri.Builder builder = new Uri.Builder().appendQueryParameter("name", params[0]);
                String query = builder.build().getEncodedQuery();

                URL url = new URL("https://jak.codecamps.de/jak-board/board/" + authToken);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("PUT");
                connection.setRequestProperty("charset", "utf-8");
                connection.setDoInput(true);
                connection.setDoOutput(true);

                try (OutputStream os = connection.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));) {
                    writer.write(query);
                    writer.flush();
                    if (connection != null && connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        try (InputStream in = new BufferedInputStream(connection.getInputStream());
                             Scanner s = new Scanner(in).useDelimiter("\\A");) {
                            String response = s.hasNext() ? s.next() : null;
                            board = new JSONObject(response);
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        } finally {
                            connection.disconnect();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
                showMessage("Error while loading Boards");
            }
            return board;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            try {
                adapter.add(new Board(jsonObject.getString("board_id"), jsonObject.getString("name")));
                final Collator collator = Collator.getInstance();
                adapter.sort(new Comparator<Board>() {
                    @Override
                    public int compare(Board lhs, Board rhs) {
                        return collator.compare(lhs.getName(), rhs.getName());
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class UpdateBoardList extends AsyncTask<String, Object, JSONArray> {

        @Override
        protected JSONArray doInBackground(String... params) {
            JSONArray boards = null;
            try {
                URL url = new URL("https://jak.codecamps.de/jak-board/board/" + authToken);
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
            } catch (IOException e) {
                e.printStackTrace();
                showMessage(getString(R.string.error_loading_boards));
            }
            return boards;
        }

        @Override
        protected void onPostExecute(JSONArray boards) {
            Log.d(AccountGeneral.ACCOUNT_NAME, "Boards retrieved, adding through adapter: " + boards.toString());
            for (int i = 0; i < boards.length(); i++) {
                try {
                    JSONObject jsonObject = (JSONObject) boards.get(i);
                    adapter.add(new Board(jsonObject.getString("board_id"), jsonObject.getString("name")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            final Collator collator = Collator.getInstance();
            adapter.sort(new Comparator<Board>() {
                @Override
                public int compare(Board lhs, Board rhs) {
                    return collator.compare(lhs.getName(), rhs.getName());
                }
            });
        }
    }

    private void showMessage(final String msg) {
        if (TextUtils.isEmpty(msg))
            return;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
