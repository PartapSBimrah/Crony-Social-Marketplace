package com.abhigam.www.foodspot;

import android.widget.ArrayAdapter;

import com.stfalcon.chatkit.commons.models.IDialog;
import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.IUser;

import java.util.ArrayList;

/**
 * Created by sourabhzalke on 20/02/18.
 */

public class DefaultDialog implements IDialog {

/*...*/
     private String id,dialogPhoto,dialogName;
     private int unreadCount;
     private IMessage lastMessage;
     private ArrayList<IUser> users;

     public DefaultDialog(String id_p, String dialogPhoto_p, String dialogName_p,
                          ArrayList<IUser> users_p){
         this.id = id_p;
         this.dialogPhoto = dialogPhoto_p;
         this.dialogName = dialogName_p;
         this.users = users_p;
     }


    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getDialogPhoto() {
        return dialogPhoto;
    }

    @Override
    public String getDialogName() {
        return dialogName;
    }

    @Override
    public ArrayList<IUser> getUsers() {
        return users;
    }

    @Override
    public IMessage getLastMessage() {
        return lastMessage;
    }

    @Override
    public void setLastMessage(IMessage lastMessage) {
        this.lastMessage = lastMessage;
    }

    @Override
    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    }