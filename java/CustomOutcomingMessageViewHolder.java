package com.abhigam.www.foodspot;

import android.view.View;

import com.stfalcon.chatkit.messages.MessageHolders;

/**
 * Created by sourabhzalke on 19/02/18.
 */

public class CustomOutcomingMessageViewHolder
        extends MessageHolders.OutcomingTextMessageViewHolder<Message> {

    public CustomOutcomingMessageViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void onBind(Message message) {
        super.onBind(message);

        if(message.getSeen().equals("1")){
            time.setText(message.getStatus() + " " + time.getText()+" \uD83D\uDC41️");
        }else {
            time.setText(message.getStatus() + " " + time.getText()+" ✓ ");
        }
    }
}