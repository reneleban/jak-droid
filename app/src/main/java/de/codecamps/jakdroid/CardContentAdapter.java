package de.codecamps.jakdroid;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import de.codecamps.jakdroid.auth.AccountGeneral;
import de.codecamps.jakdroid.data.Card;

import java.util.List;

/**
 * data for card list
 */
class CardContentAdapter extends RecyclerView.Adapter<CardViewHolder> {
    private CardContentFragment cardContentFragment;
    private Context context;
    private String list_id;
    private List<Card> cardList;

    CardContentAdapter(CardContentFragment cardContentFragment, List<Card> cardList, Context context, String list_id) {
        this.cardContentFragment = cardContentFragment;
        this.cardList = cardList;
        this.context = context;
        this.list_id = list_id;
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CardViewHolder(cardContentFragment, LayoutInflater.from(parent.getContext()), parent);
    }

    @Override
    public void onBindViewHolder(CardViewHolder holder, int position) {
        Log.d(AccountGeneral.ACCOUNT_NAME, String.format("Retrieving item %d from list %s", position, list_id));
        Card card = cardList.get(position);
        if (card != null) {
            holder.getPicture().setImageDrawable(context.getDrawable(R.drawable.a));
            holder.getName().setText(card.getName());
            holder.getDescription().setText(card.getDescription());
            holder.setCard(card);
        }
    }

    @Override
    public int getItemCount() {
        return cardList.size();
    }

    void add(Card c) {
        cardList.add(c);
    }

    void remove(String cardId) {
        if (cardId == null)
            return;

        for (Card c : cardList) {
            if (c.getCardId().equals(cardId)) {
                cardList.remove(c);
                break;
            }
        }
    }

    public String getList_id() {
        return list_id;
    }
}
