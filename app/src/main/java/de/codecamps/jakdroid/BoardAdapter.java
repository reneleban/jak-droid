package de.codecamps.jakdroid;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import de.codecamps.jakdroid.auth.AccountGeneral;

import java.util.ArrayList;

public class BoardAdapter extends ArrayAdapter<Board>{
    public BoardAdapter(Context context, ArrayList<Board> boards){
        super(context, 0, boards);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Board board = getItem(position);
        Log.d(AccountGeneral.ACCOUNT_NAME, "BoardAdapter, Board Position: "+ position);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.listitem_board, parent, false);
        }
        TextView boardName = (TextView)convertView.findViewById(R.id.boardName);
        boardName.setText(board.getName());
        Log.d(AccountGeneral.ACCOUNT_NAME, "BoardAdapter, name set: "+ board.getName());
        return convertView;
    }


}
