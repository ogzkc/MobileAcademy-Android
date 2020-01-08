package com.kc.Util;

import com.Wsdl2Code.WebServices.MAService.Alert;
import com.kc.Model.User;
import com.kc.mobileacademy.R;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Oguz on 17.02.2016.
 */
public class Config {


    public static Alert versionControl = new Alert();
    public static Alert Announcement = new Alert();

    public final static int platformIdAndroid = 1;

    public final static String ubuntuMed = "fonts/Ubuntu-L.ttf";
    public final static String ubuntuLight = "fonts/Ubuntu-M.ttf";
    public final static String ubuntuBold = "fonts/Ubuntu-M.ttf";

    List<String> permissionNeeds = Arrays.asList("public_profile", "email", "user_friends", "user_education_history");

    public final static User user = new User();

    public static Map<Integer, Integer> departmentLogoRes;

    static {
        departmentLogoRes = new HashMap<Integer, Integer>();
        departmentLogoRes.put(1, R.drawable.ic_maths);
        departmentLogoRes.put(2, R.drawable.ic_physics);
        departmentLogoRes.put(3, R.drawable.ic_chemistry);
        departmentLogoRes.put(4, R.drawable.ic_biology);
        departmentLogoRes.put(5, R.drawable.ic_computereng);
    }

}
