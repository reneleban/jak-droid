package de.codecamps.jakdroid.data;

import org.json.JSONException;
import org.json.JSONObject;

public class Card {
    private String listId;
    private String cardId;
    private String name;
    private String description;
    private String owner;

    public Card(JSONObject cardElement)  throws JSONException {
        this.listId = cardElement.getString("list_id");
        this.cardId = cardElement.getString("card_id");
        this.name = cardElement.getString("name");
        this.description = cardElement.getString("description");
        this.owner = cardElement.getString("owner");

    }

    public String getListId() {
        return listId;
    }

    public void setListId(String listId) {
        this.listId = listId;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}
