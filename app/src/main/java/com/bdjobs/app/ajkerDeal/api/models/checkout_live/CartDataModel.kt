package com.bdjobs.app.ajkerDeal.api.models.checkout_live

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

//@Entity(tableName = "cart_table", indices = [Index(value = ["deal_id"], unique = true)])
@Parcelize
data class CartDataModel(

    //@PrimaryKey(autoGenerate = true)
    //@ColumnInfo(name = "uid")
    var uid: Long = 0,

    //@ColumnInfo(name = "deal_id")
    var dealId: Int = 0,

    //@ColumnInfo(name = "deal_title")
    var dealTitle: String? = null,

    //@ColumnInfo(name = "deal_quantity")
    var dealQuantity: Int = 0,

    //@ColumnInfo(name = "deal_size")
    var dealSize: String? = null,

    //@ColumnInfo(name = "deal_color")
    var dealColor: String? = null,

    //@ColumnInfo(name = "deal_image")
    var dealImage: String? = null,

    //@ColumnInfo(name = "deal_add_to_cart_date")
    var dealAddDate: Long = 0,

    //@ColumnInfo(name = "deal_merchant")
    var dealMerchantId: Int = 0,

    //@ColumnInfo(name = "deal_price")
    var dealPrice: Float = 0f,

    //@ColumnInfo(name = "deal_regular_price")
    var dealRegularPrice: Int = 0,

    // S L
    //@ColumnInfo(name = "deal_delivery_size")
    var dealDeliverySize: String? = "S",

    //@ColumnInfo(name = "deal_commission_per_coupon")
    var dealCommission: Int = 0,

    //@ColumnInfo(name = "is_deal_perishable")
    var isDealPerishable: Boolean = false,

    //@ColumnInfo(name = "flag_express_delivery")
    var flagExpressDelivery: Int = 0,

    //@ColumnInfo(name = "flag_unlock_commission")
    var flagUnlockCommission: Int = 0,

    //@ColumnInfo(name = "deal_event_id")
    var eventId: Int = 0,

    //@ColumnInfo(name = "deal_event_type")
    var eventType: Int = 0,

    //@ColumnInfo(name = "deal_24hour_delivery")
    var deal24HourDelivery: Int = 0,

    //@ColumnInfo(name = "deal_max_limit")
    var dealCartMaxLimit: Int = 0,

    //@ColumnInfo(name = "deal_merchant_name")
    var dealMerchantName: String? = null,

    //@ColumnInfo(name = "deal_collectionChargePercent")
    var collectionChargePercent: Int = 0,

    //@ColumnInfo(name = "deal_quantityAfterBooking")
    var quantityAfterBooking: Int = 0,

    //@ColumnInfo(name = "cart_type")
    var cardGroup: Int = 0,

) : Parcelable {

    //if (dealMerchantId == 17390) 1 else 0 chaldal
    constructor(
        dealId: Int, dealTitle: String?, dealQuantity: Int, dealSize: String?, dealColor: String?,
        dealImage: String?, dealAddDate: Long, dealMerchantId: Int, dealPrice: Float, dealPerishable: Boolean, eventId: Int, eventType: Int,
        deal24HourDelivery: Int, dealMerchantName: String?, collectionChargePercent: Int, cardGroup: Int
    ) : this(
        0, dealId, dealTitle, dealQuantity, dealSize, dealColor, dealImage, dealAddDate, dealMerchantId, dealPrice, dealPrice.toInt(),
        "", 0,  dealPerishable,
          0, 0, eventId, eventType, deal24HourDelivery, 5,
        dealMerchantName, collectionChargePercent, -1, cardGroup
    )


}