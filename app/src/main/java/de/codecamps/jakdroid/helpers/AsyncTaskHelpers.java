package de.codecamps.jakdroid.helpers;

import de.codecamps.jakdroid.R;
import de.codecamps.jakdroid.data.Board;
import de.codecamps.jakdroid.data.Card;
import de.codecamps.jakdroid.data.ListElement;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class AsyncTaskHelpers {

    public static final String REQUEST_METHOD_DELETE = "DELETE";

    public static JSONObject addNewBoard(String authToken, String query) throws IOException {
        URL url = new URL(App.getContext().getString(R.string.ADD_NEW_BOARD_URL, authToken));
        return getJsonObject("PUT", url, query);
    }

    public static List<Board> retrieveBoards(String authToken) throws IOException, JSONException {
        JSONArray boards;
        URL url = new URL(App.getContext().getString(R.string.RETRIEVE_BOARDS_URL, authToken));
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        boards = parseListItems(connection);

        List<Board> boardList = new ArrayList<>();

        for (int i = 0; i < boards.length(); i++) {
            JSONObject board = boards.getJSONObject(i);
            boardList.add(new Board(board));
        }
        return boardList;
    }

    public static String deleteBoard(String authToken, String boardId) throws IOException {
        URL url = new URL(App.getContext().getString(R.string.DELETE_BOARDS_URL, authToken, boardId));
        HttpURLConnection connection = getHttpURLConnection(url, REQUEST_METHOD_DELETE);
        return connection.getResponseCode() == HttpURLConnection.HTTP_OK ? boardId : null;
    }

    public static JSONObject addNewList(String authToken, String boardId, String query) throws IOException {
        URL url = new URL(App.getContext().getString(R.string.ADD_NEW_LIST_URL, authToken, boardId));
        return getJsonObject("POST", url, query);
    }

    public static List<ListElement> retrieveListElements(String authToken, String boardId) throws IOException, JSONException {
        URL url = new URL(App.getContext().getString(R.string.RETRIEVE_LISTS_URL, authToken, boardId));
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        JSONArray listItems = parseListItems(connection);

        List<ListElement> listElementList = new ArrayList<>();
        if (listItems != null) {
            for (int i = 0; i < listItems.length(); i++) {
                JSONObject listElement = listItems.getJSONObject(i);
                listElementList.add(new ListElement(listElement));
            }
        }
        return listElementList;
    }

    public static String deleteListAndCards(String authToken, String listId) throws IOException {
        URL url = new URL(App.getContext().getString(R.string.DELETE_CARDS_FROM_LIST_URL, authToken, listId));
        HttpURLConnection connection = getHttpURLConnection(url, REQUEST_METHOD_DELETE);
        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            url = new URL(App.getContext().getString(R.string.DELETE_LIST_URL, authToken, listId));
            connection = getHttpURLConnection(url, REQUEST_METHOD_DELETE);
            return connection.getResponseCode() == HttpURLConnection.HTTP_OK ? listId : null;
        } else return null;
    }

    public static JSONObject addNewCard(String authToken, String listId, String query) throws IOException {
        URL url = new URL(App.getContext().getString(R.string.ADD_NEW_CARD_URL, authToken, listId));
        return getJsonObject("POST", url, query);
    }

    public static List<Card> retrieveCards(String authToken, String listId) throws IOException, JSONException {
        JSONArray cardArray;
        URL url = new URL(App.getContext().getString(R.string.RETRIEVE_CARDS_URL, authToken, listId));
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        cardArray = parseListItems(connection);

        List<Card> cardList = new ArrayList<>();
        if (cardArray != null) {
            for (int i = 0; i < cardArray.length(); i++) {
                JSONObject cardElement = cardArray.getJSONObject(i);
                cardList.add(new Card(cardElement));
            }
        }
        return cardList;
    }

    public static String deleteCard(String authToken, String cardId) throws IOException {
        URL url = new URL(App.getContext().getString(R.string.RETRIEVE_CARDS_URL, authToken, cardId));
        HttpURLConnection connection = getHttpURLConnection(url, REQUEST_METHOD_DELETE);
        return connection.getResponseCode() == HttpURLConnection.HTTP_OK ? cardId : null;
    }

    private static HttpURLConnection getHttpURLConnection(URL url, String requestMethod) throws IOException {
        HttpURLConnection connection;
        connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(requestMethod);
        connection.setRequestProperty("charset", "utf-8");
        connection.connect();
        return connection;
    }

    private static JSONArray parseListItems(HttpURLConnection connection) throws IOException {
        JSONArray listItems = null;
        if (connection != null && connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            try (InputStream in = new BufferedInputStream(connection.getInputStream());
                 Scanner s = new Scanner(in).useDelimiter("\\A")) {
                String response = s.hasNext() ? s.next() : null;
                listItems = new JSONArray(response);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            } finally {
                connection.disconnect();
            }
        }
        return listItems;
    }

    private static JSONObject getJsonObject(String method, URL url, String query) throws IOException {
        JSONObject jsonObject = null;
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(method);
        connection.setRequestProperty("charset", "utf-8");
        connection.setDoInput(true);
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream();
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"))) {
            writer.write(query);
            writer.flush();
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                try (InputStream in = new BufferedInputStream(connection.getInputStream());
                     Scanner s = new Scanner(in).useDelimiter("\\A")) {
                    String response = s.hasNext() ? s.next() : null;
                    jsonObject = new JSONObject(response);
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                } finally {
                    connection.disconnect();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

}
