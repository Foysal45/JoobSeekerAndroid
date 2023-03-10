package com.bdjobs.app.ajkerDeal.utilities;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by haider_kazal on 12/19/15.
 */
public class Validator {
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    public static final Pattern VALID_MOBILE_NUMBER_REGEX =
            Pattern.compile("(^(\\+88|0088)?(01){1}[3456789]{1}(\\d){8})$", Pattern.CASE_INSENSITIVE);
//Pattern.compile("^(01|০১)?[0-9০১২৩৪৫৬৭৮৯]{9}$", Pattern.CASE_INSENSITIVE);

   public static  final  Pattern VALID_MOBILE_NUMBER_REGEXAgain=
            Pattern.compile("(8801|৮৮০১)?[0-9০১২৩৪৫৬৭৮৯]{11}$", Pattern.CASE_INSENSITIVE);
    public static boolean isValidEmail(String email) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(email);
        return matcher.find();
    }

    public static boolean isValidMobileNumber(String mobileNumber) {
        Matcher matcher = VALID_MOBILE_NUMBER_REGEX.matcher(mobileNumber);
       // return matcher.find();
        return matcher.find() && matcher.group().equals(mobileNumber);
    }
    public static boolean isValidMobileNumberAgain(String mobileNumber1) {
        Matcher matcher = VALID_MOBILE_NUMBER_REGEXAgain.matcher(mobileNumber1);
        return matcher.find();
    }

}
