/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.codecamps.jakdroid;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import de.codecamps.jakdroid.auth.AccountGeneral;
import de.codecamps.jakdroid.data.Card;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class CardContentFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        RecyclerView recyclerView = (RecyclerView) inflater.inflate(
                R.layout.recycler_view, container, false);

//        try {
//            List<Card> cardList = new UpdateCardList(getArguments().getString("auth_token")).execute(getArguments().getString("list_id")).get();
//            ContentAdapter adapter = new ContentAdapter(cardList, getContext(), getArguments().getString("list_id"));
//            recyclerView.setAdapter(adapter);
//            recyclerView.setHasFixedSize(true);
//            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//            return recyclerView;
//
//        } catch (ExecutionException | InterruptedException e) {
//            e.printStackTrace();
//        }
//        return null;

        return recyclerView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView = (RecyclerView) view;
        try {
            List<Card> cardList = new UpdateCardList(getArguments().getString("auth_token")).execute(getArguments().getString("list_id")).get();
            ContentAdapter adapter = new ContentAdapter(cardList, getContext(), getArguments().getString("list_id"));
            recyclerView.setAdapter(adapter);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * data for card list
     */
    class ContentAdapter extends RecyclerView.Adapter<ViewHolder> {
        private Context context;
        private String list_id;
        private List<Card> cardList;

        ContentAdapter(List<Card> cardList, Context context, String list_id) {
            this.cardList = cardList;
            this.context = context;
            this.list_id = list_id;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()), parent);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Log.d(AccountGeneral.ACCOUNT_NAME, String.format("Retrieving item %d from list %s", position, list_id));
            Card card = cardList.get(position);
            if(card!=null) {
                holder.picture.setImageDrawable(context.getDrawable(R.drawable.a));
                holder.name.setText(card.getName());
                holder.description.setText(card.getDescription());
                holder.card = card;
            }
        }

        @Override
        public int getItemCount() {
            return cardList.size();
        }

        void add(Card c){
            cardList.add(c);
        }
        void remove(String cardId){
            if(cardId==null)
                return;

            for(Card c : cardList){
                if(c.getCardId().equals(cardId)){
                    cardList.remove(c);
                    break;
                }
            }
        }

        public String getList_id() {
            return list_id;
        }
    }


    /**
     * card
     */
     class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView picture;
        private TextView name;
        private TextView description;

        private Card card;

        ViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.item_card, parent, false));
            picture = (ImageView) itemView.findViewById(R.id.card_image);
            name = (TextView) itemView.findViewById(R.id.card_title);
            description = (TextView) itemView.findViewById(R.id.card_text);

            Button button = (Button)itemView.findViewById(R.id.action_button);
            button.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Snackbar.make(v, "Action is pressed",
                            Snackbar.LENGTH_LONG).show();
                }
            });

            ImageButton favoriteImageButton =
                    (ImageButton) itemView.findViewById(R.id.favorite_button);
            favoriteImageButton.setOnClickListener(new View.OnClickListener(){
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
                   new DeleteCard(getArguments().getString("auth_token"), (RecyclerView) itemView.getParent()).execute(card.getCardId());
                }
            });
        }
    }
}

