package de.codecamps.jakdroid.data;

import org.json.JSONException;
import org.json.JSONObject;

public class Board {
    private String name;
    private String id;

    public Board(JSONObject board) throws JSONException{
        this.id = board.getString("board_id");
        this.name = board.getString("name");
    }

    public Board(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
