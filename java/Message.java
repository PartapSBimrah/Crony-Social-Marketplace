package com.abhigam.www.foodspot;

import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.MessageContentType;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by sourabhzalke on 18/02/18.
 */

public class Message implements IMessage,MessageContentType.Image {

   /*...*/

    private String user_id, chat_text;
    private int status=0;
    private Date chattime;
    private Author author;
    private String image = null;
    private String seen;

    public Message(String id, String text, Author author_1, Date time,int i_status,String image,
                   String seen) {
        this.user_id = id;
        this.chat_text = text;
        this.author = author_1;
        this.chattime = time;
        this.status = i_status;
        this.image = image;
        this.seen = seen;
    }



    @Override
    public String getId() {
        return user_id;
    }

    @Override
    public String getText() {
        return chat_text;
    }

    @Override
    public Author getUser() {
        return author;
    }

    @Override
    public Date getCreatedAt() {
            return chattime;
    }

    @Override
    public String getImageUrl() {
        return image;
    }

    public String getStatus(){
        if(status == 0){
            return "Not Sent";
        }
        else if(status == 2){
            return "";
        }else{
            return "Sent";
        }
    }

    public String getSeen(){
        return seen;
    }

}
