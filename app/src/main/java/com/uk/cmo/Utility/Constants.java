package com.uk.cmo.Utility;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by usman on 25-04-2018.
 */

public class Constants {

    //Do not change the position of elements in the below array
    public static final ArrayList<String> blood_groups
            = new ArrayList<>(Arrays.asList("A+","A-","B+","B-","AB+","AB-","O+","O-"));

    //Nodes
    public static final String USERS = "Users";

    public static final String REPRESENTATIVES = "Representatives";

    public static final String POSTS = "Posts";

    public static final String MEMBERS = "Members";

    public static final String ALLUSERS = "AllUsers";


    //Attributes of Representative Class

    public static final String LOWERCASE_NAME = "name_lower_case";

    //Attributes of User Class

    public static final String USERS_TOKEN = "token";

    public static final String LEGIT = "legit";


    //Firebase Storage;

    public static final String POST_PICS = "PostPics";

    public static final String PROFILE_PICS = "ProfilePics";


}
