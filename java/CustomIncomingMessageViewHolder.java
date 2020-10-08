package com.abhigam.www.foodspot;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.stfalcon.chatkit.messages.MessageHolders;

import org.w3c.dom.Text;

/**
 * Created by sourabhzalke on 19/02/18.
 */

public class CustomIncomingMessageViewHolder extends
        MessageHolders.IncomingTextMessageViewHolder<Message> {

    public CustomIncomingMessageViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void onBind(Message message) {
        super.onBind(message);
        time.setText(message.getStatus() + " " + time.getText());
    }


}
