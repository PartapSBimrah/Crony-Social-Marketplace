package com.abhigam.www.foodspot;

import com.stfalcon.chatkit.commons.models.IUser;

/**
 * Created by sourabhzalke on 18/02/18.
 */

public class Author implements IUser {

    private String user_id,avatar,avatar_url;

    public Author(String id,String pAvatar){
        user_id = id;
        avatar = pAvatar;
        avatar_url = "http://13.233.234.79/uploads/profile_pic/"+avatar+"_profile.jpg";

    }

    /*...*/
    @Override
    public String getId() {
        return user_id;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getAvatar() {

        return avatar_url;
    }
}