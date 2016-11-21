package de.codecamps.jakdroid.data;

import org.json.JSONException;
import org.json.JSONObject;

public class ListElement {
    private String board_id;
    private String list_id;
    private String name;
    private String owner;

    public ListElement(JSONObject listElement) throws JSONException {
        this.board_id = listElement.getString("board_id");
        this.list_id = listElement.getString("list_id");
        this.name = listElement.getString("name");
        this.owner = listElement.getString("owner");
    }

    public String getBoard_id() {
        return board_id;
    }

    public void setBoard_id(String board_id) {
        this.board_id = board_id;
    }

    public String getList_id() {
        return list_id;
    }

    public void setList_id(String list_id) {
        this.list_id = list_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}
