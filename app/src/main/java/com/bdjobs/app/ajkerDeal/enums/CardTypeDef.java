package com.bdjobs.app.ajkerDeal.enums;

import static java.lang.annotation.RetentionPolicy.SOURCE;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;

public class CardTypeDef {

    // constant expression
    public static final String COD = "Manual";
    public static final String CARD = "Card";
    public static final String bKash = "bKashGateway";
    public static final String ROCKET = "ROCKET";
    public static final String ADC = "Internal";
    public static final String EMI = "EMI";

    @Retention(SOURCE)
    @StringDef({
            COD,CARD,bKash,ROCKET,ADC,EMI
    })
    public @interface CartType {}

}
