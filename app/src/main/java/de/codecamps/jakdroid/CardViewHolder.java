package de.codecamps.jakdroid;

import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import de.codecamps.jakdroid.data.Card;

/**
 * card
 */
class CardViewHolder extends RecyclerView.ViewHolder {
    private ImageView picture;
    private TextView name;
    private TextView description;
    private Card card;

    CardViewHolder(final CardContentFragment cardContentFragment, LayoutInflater inflater, ViewGroup parent) {
        super(inflater.inflate(R.layout.item_card, parent, false));
        picture = (ImageView) itemView.findViewById(R.id.card_image);
        name = (TextView) itemView.findViewById(R.id.card_title);
        description = (TextView) itemView.findViewById(R.id.card_text);

        Button button = (Button) itemView.findViewById(R.id.action_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "Action is pressed",
                        Snackbar.LENGTH_LONG).show();
            }
        });

        ImageButton favoriteImageButton =
                (ImageButton) itemView.findViewById(R.id.favorite_button);
        favoriteImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "Added to Favorite",
                        Snackbar.LENGTH_LONG).show();
            }
        });

        ImageButton shareImageButton = (ImageButton) itemView.findViewById(R.id.delete_card_button);
        shareImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "Delete Card",
                        Snackbar.LENGTH_LONG).show();
                new DeleteCard(cardContentFragment.getArguments().getString("auth_token"), (RecyclerView) itemView.getParent()).execute(card.getCardId());
            }
        });
    }

    public ImageView getPicture() {
        return picture;
    }

    public TextView getName() {
        return name;
    }

    public TextView getDescription() {
        return description;
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }
}
