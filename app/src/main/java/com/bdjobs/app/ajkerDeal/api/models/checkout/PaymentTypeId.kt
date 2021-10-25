package com.bdjobs.app.ajkerDeal.api.models.checkout

import androidx.annotation.IntDef

const val BIKASH = 1
const val AD_CASH = 2
const val CARD = 3
const val COD = 4
const val EMI = 5
const val ROCKET = 6
const val NO_DELIVERY = 8

@IntDef(BIKASH, AD_CASH, CARD, COD, EMI, ROCKET, NO_DELIVERY)
@Retention(AnnotationRetention.SOURCE)
annotation class PaymentTypeId