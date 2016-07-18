package edu.illinois.ncsa.bwmon;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by ylin9 on 7/18/2016.
 */
public class UserLocalStore {
    public static final String SP_NAME = "userDetails";
    SharedPreferences userLocalDatabase;

    /**
     * Constructor of UserLocalStore
     * @param context the context of the calling activity
     */
    public UserLocalStore(Context context)
    {
        userLocalDatabase = context.getSharedPreferences(SP_NAME, 0);
    }

    public void storeUserData(ArrayList<String> select_list)
    {
        SharedPreferences.Editor spEditor = userLocalDatabase.edit();
        String temp = "";
        for (int i = 0; i < select_list.size();i++)
        {
            temp = temp.concat(select_list.get(i)).concat(",");
        }
        temp = temp.substring(0,temp.length()-1);
        spEditor.putString("select_list",temp);
        spEditor.commit();
    }

    public ArrayList<String> getUserSelection()
    {
        ArrayList<String> select_list = new ArrayList<String>(Arrays.asList(userLocalDatabase.getString("select_list", "").split(",")));
        return select_list;
    }

    public boolean getUserSelected()
    {
        if (userLocalDatabase.getBoolean("selected", false))
            return true;
        else
            return false;
    }

    public void setUserSelected(boolean selected)
    {
        SharedPreferences.Editor spEditor = userLocalDatabase.edit();
        spEditor.putBoolean("selected", selected);
        spEditor.commit();
    }

    /**
     * Clear the userdata on the local storage
     */
    public void clearUserData()
    {
        SharedPreferences.Editor spEditor = userLocalDatabase.edit();
        spEditor.clear();
        spEditor.commit();
    }

}
