package com.bdjobs.app.ajkerDeal.enums;

import static java.lang.annotation.RetentionPolicy.SOURCE;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;

public class PaymentTypeDef {
    // constant expression
    public static final String COD = "MPD";
    public static final String CARD = "OPS";
    public static final String bKash = "MPS";
    public static final String ROCKET = "MPS";
    public static final String ADC = "ADC";
    public static final String EMI = "EMI";

    @Retention(SOURCE)
    @StringDef({
            COD,CARD,bKash,ROCKET,ADC,EMI
    })
    public @interface PaymentType {}
}
