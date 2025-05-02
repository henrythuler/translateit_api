package com.bureau.translateit.utils;

import java.util.regex.Pattern;

public class CheckIsValidEmail {

    public static boolean isValid(String email) {

        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

        Pattern p = Pattern.compile(emailRegex);

        return email != null && p.matcher(email).matches();
    }

}
