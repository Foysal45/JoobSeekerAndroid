package com.bdjobs.app.ajkerDeal.api.models.checkout;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PlaceOrder {

    @SerializedName("dealId")
    @Expose
    private int dealId;
    @SerializedName("customerId")
    @Expose
    private int customerId;
    @SerializedName("couponQtn")
    @Expose
    private int couponQtn;
    @SerializedName("couponPrice")
    @Expose
    private double couponPrice;
    @SerializedName("paymentType")
    @Expose
    private String paymentType;
    @SerializedName("customerBillingAddress")
    @Expose
    private String customerBillingAddress;
    @SerializedName("customerMobile")
    @Expose
    private String customerMobile;
    @SerializedName("deliveryDist")
    @Expose
    private int deliveryDist;
    @SerializedName("thanaId")
    @Expose
    private int thanaId;
    @SerializedName("deliveryCharge")
    @Expose
    private int deliveryCharge;
    @SerializedName("sizes")
    @Expose
    private String sizes;
    @SerializedName("colors")
    @Expose
    private String colors;
    @SerializedName("commission")
    @Expose
    private int commission;
    @SerializedName("shopCartId")
    @Expose
    private int shopCartId;
    @SerializedName("orderFrom")
    @Expose
    private String orderFrom;
    @SerializedName("customerAlternateMobile")
    @Expose
    private String customerAlternateMobile;
    @SerializedName("orderSource")
    @Expose
    private String orderSource;
    @SerializedName("merchantId")
    @Expose
    private int merchantId;
    @SerializedName("voucherId")
    @Expose
    private int voucherId;
    @SerializedName("eventId")
    @Expose
    private int eventId;
    @SerializedName("isHoursAvailable")
    @Expose
    private int isHoursAvailable;
    @SerializedName("specialNotes")
    @Expose
    private String specialNotes;
    @SerializedName("appVersion")
    @Expose
    private String appVersion;
    @SerializedName("unitPrice")
    @Expose
    private double unitPrice;
    @SerializedName("cardType")
    @Expose
    private String cardType;
    @SerializedName("paymentStatus")
    @Expose
    private String paymentStatus;
    @SerializedName("onlineTransactionId")
    @Expose
    private String onlineTransactionId;
    @SerializedName("transactionId")
    @Expose
    private String transactionId;
    @SerializedName("adCashBackPercentage")
    @Expose
    private int adCashBackPercentage;
    @SerializedName("offeredCashBackPercentage")
    @Expose
    private int offeredCashBackPercentage;
    @SerializedName("adDiscountPercentage")
    @Expose
    private int adDiscountPercentage;
    @SerializedName("offeredDiscountPercentage")
    @Expose
    private int offeredDiscountPercentage;
    @SerializedName("postalInformation")
    @Expose
    private String postalInformation;
    @SerializedName("groupBuyId")
    @Expose
    private int groupBuyId;
    @SerializedName("getFree")
    @Expose
    private int getFree;
    private int areaId;
    private String orderType;
    private int thirdPartyLocationId;
    private int unlockCommission;
    private int EventType;
    private String VoucherCode;
    private int VoucherType;
    private int VoucherCodeId;
    private int DiscountInAmount;
    private String dealTitle;
    private String productsSize;
    private int EMIPeriod;

    private String ThirdPartySku;
    private String ThirdPartyImageUrl;
    private int CollectionCharge;
    @SerializedName("ActualPrice")
    private int actualPrice;
    @SerializedName("BogoReferenceId")
    private int bogoReference;
    @SerializedName("SameDayCharge")
    private int sameDayCharge;
    @SerializedName("VSReferenceId")
    private int vsReferenceId;

    private PlaceOrder(Builder builder) {

        this.dealId = builder.dealId;
        this.customerId = builder.customerId;
        this.couponQtn = builder.couponQtn;
        this.couponPrice = builder.couponPrice;
        this.paymentType = builder.paymentType;
        this.customerBillingAddress = builder.customerBillingAddress;
        this.customerMobile = builder.customerMobile;
        this.deliveryDist = builder.deliveryDist;
        this.thanaId = builder.thanaId;
        this.deliveryCharge = builder.deliveryCharge;
        this.sizes = builder.sizes;
        this.colors = builder.colors;
        this.commission = builder.commission;
        this.shopCartId = builder.shopCartId;
        this.orderFrom = builder.orderFrom;
        this.customerAlternateMobile = builder.customerAlternateMobile;
        this.orderSource = builder.orderSource;
        this.merchantId = builder.merchantId;
        this.voucherId = builder.voucherId;
        this.eventId = builder.eventId;
        this.isHoursAvailable = builder.isHoursAvailable;
        this.specialNotes = builder.specialNotes;
        this.appVersion = builder.appVersion;
        this.unitPrice = builder.unitPrice;
        this.cardType = builder.cardType;
        this.paymentStatus = builder.paymentStatus;
        this.onlineTransactionId = builder.onlineTransactionId;
        this.transactionId = builder.transactionId;
        this.adCashBackPercentage = builder.adCashBackPercentage;
        this.offeredCashBackPercentage = builder.offeredCashBackPercentage;
        this.adDiscountPercentage = builder.adDiscountPercentage;
        this.offeredDiscountPercentage = builder.offeredDiscountPercentage;
        this.postalInformation = builder.postalInformation;
        this.groupBuyId = builder.groupBuyId;
        this.getFree = builder.getFree;
        this.areaId = builder.areaId;
        this.orderType = builder.orderType;
        this.thirdPartyLocationId = builder.thirdPartyLocationId;
        this.unlockCommission = builder.unlockCommission;
        this.EventType = builder.EventType;
        this.VoucherCode = builder.VoucherCode;
        this.VoucherType = builder.VoucherType;
        this.VoucherCodeId = builder.VoucherCodeId;
        this.DiscountInAmount = builder.DiscountInAmount;
        this.dealTitle = builder.dealTitle;
        this.productsSize = builder.productsSize;
        this.EMIPeriod = builder.EMIPeriod;
        this.ThirdPartySku = builder.ThirdPartySku;
        this.ThirdPartyImageUrl = builder.ThirdPartyImageUrl;
        this.CollectionCharge = builder.CollectionCharge;
        this.actualPrice = builder.actualPrice;
        this.bogoReference = builder.bogoReference;
        this.sameDayCharge = builder.sameDayCharge;
        this.vsReferenceId = builder.vsReferenceId;
    }

    public static class Builder {

        private int dealId;
        private int customerId;
        private int couponQtn;
        private double couponPrice;
        private String paymentType;
        private String customerBillingAddress;
        private String customerMobile;
        private int deliveryDist;
        private int thanaId;
        private int deliveryCharge;
        private String sizes;
        private String colors;
        private int commission;
        private int shopCartId;
        private String orderFrom;
        private String customerAlternateMobile;
        private String orderSource;
        private int merchantId;
        private int voucherId;
        private int eventId;
        private int isHoursAvailable;
        private String specialNotes;
        private String appVersion;
        private double unitPrice;
        private String cardType;
        private String paymentStatus;
        private String onlineTransactionId;
        private String transactionId;
        private int adCashBackPercentage;
        private int offeredCashBackPercentage;
        private int adDiscountPercentage;
        private int offeredDiscountPercentage;
        private String postalInformation;
        private int groupBuyId;
        private int getFree;
        private int areaId;
        private String orderType;
        private int thirdPartyLocationId;
        private int unlockCommission;
        private int EventType;
        private String VoucherCode;
        private int VoucherType;
        private int VoucherCodeId;
        private int DiscountInAmount;
        private String dealTitle;
        private String productsSize;
        private int EMIPeriod;

        private String ThirdPartySku;
        private String ThirdPartyImageUrl;
        private int CollectionCharge;
        private int actualPrice;
        private int bogoReference;
        private int sameDayCharge;
        private int vsReferenceId;

        private OrderType placeOrderType;

        public Builder(OrderType orderType, int shopCartId, int eventId, int groupBuyId, int eventType) {

            switch (orderType) {

                case SINGLE:

                    this.placeOrderType = orderType;
                    this.shopCartId = 0;
                    this.eventId = eventId;
                    this.groupBuyId = 0;
                    this.getFree = 0;
                    this.EventType = eventType;
                    break;

                case CART:

                    this.placeOrderType = orderType;
                    this.shopCartId = shopCartId;
                    this.eventId = eventId;
                    this.groupBuyId = 0;
                    this.getFree = 0;
                    this.EventType = eventType;
                    break;

                case EVENT_SINGLE:

                    this.placeOrderType = orderType;
                    this.shopCartId = 0;
                    this.eventId = eventId;
                    this.groupBuyId = 0;
                    this.getFree = 0;
                    this.EventType = eventType;
                    break;

                case EVENT_CART:

                    this.placeOrderType = orderType;
                    this.shopCartId = shopCartId;
                    this.eventId = eventId;
                    this.groupBuyId = 0;
                    this.getFree = 0;
                    this.EventType = eventType;
                    break;

                case GROUP_BUY:

                    this.placeOrderType = orderType;
                    this.shopCartId = 0;
                    this.eventId = 0;
                    this.groupBuyId = groupBuyId;
                    this.getFree = 0;
                    this.EventType = 0;
                    break;

                case GET_FREE:

                    this.placeOrderType = orderType;
                    this.shopCartId = 0;
                    this.eventId = 0;
                    this.groupBuyId = groupBuyId;
                    this.getFree = 1;
                    this.EventType = 0;
                    break;

                case BANGLA_MEDS:
                    this.placeOrderType = orderType;
                    this.shopCartId = shopCartId;
                    this.eventId = 0;
                    this.groupBuyId = 0;
                    this.getFree = 0;
                    this.EventType = 0;
                    break;
            }
        }

        public PlaceOrder build() {
            return new PlaceOrder(this);
        }

        public Builder setDealId(int dealId) {
            this.dealId = dealId;
            return this;
        }

        public Builder setCustomerId(int customerId) {
            this.customerId = customerId;
            return this;
        }

        public Builder setCouponQtn(int couponQtn) {
            this.couponQtn = couponQtn;
            return this;
        }

        public Builder setCouponPrice(double couponPrice) {
            this.couponPrice = couponPrice;
            return this;
        }

        public Builder setPaymentType(String paymentType) {
            this.paymentType = paymentType;
            return this;
        }

        public Builder setCustomerBillingAddress(String customerBillingAddress) {
            this.customerBillingAddress = customerBillingAddress;
            return this;
        }

        public Builder setCustomerMobile(String customerMobile) {
            this.customerMobile = customerMobile;
            return this;
        }

        public Builder setDeliveryDist(int deliveryDist) {
            this.deliveryDist = deliveryDist;
            return this;
        }

        public Builder setThanaId(int thanaId) {
            this.thanaId = thanaId;
            return this;
        }

        public Builder setDeliveryCharge(int deliveryCharge) {
            this.deliveryCharge = deliveryCharge;
            return this;
        }

        public Builder setSizes(String sizes) {
            this.sizes = sizes;
            return this;
        }

        public Builder setColors(String colors) {
            this.colors = colors;
            return this;
        }

        public Builder setCommission(int commission) {
            this.commission = commission;
            return this;
        }

        public Builder setOrderFrom(String orderFrom) {
            this.orderFrom = orderFrom;
            return this;
        }

        public Builder setCustomerAlternateMobile(String customerAlternateMobile) {
            this.customerAlternateMobile = customerAlternateMobile;
            return this;
        }

        public Builder setOrderSource(String orderSource) {
            this.orderSource = orderSource;
            return this;
        }

        public Builder setMerchantId(int merchantId) {
            this.merchantId = merchantId;
            return this;
        }

        public Builder setVoucherId(int voucherId) {
            this.voucherId = voucherId;
            return this;
        }

        public Builder setIsHoursAvailable(int isHoursAvailable) {
            this.isHoursAvailable = isHoursAvailable;
            return this;
        }

        public Builder setSpecialNotes(String specialNotes) {
            this.specialNotes = specialNotes;
            return this;
        }

        public Builder setAppVersion(String appVersion) {
            this.appVersion = appVersion;
            return this;
        }

        public Builder setUnitPrice(double unitPrice) {
            this.unitPrice = unitPrice;
            return this;
        }

        public Builder setCardType(String cardType) {
            this.cardType = cardType;
            return this;
        }

        public Builder setPaymentStatus(String paymentStatus) {
            this.paymentStatus = paymentStatus;
            return this;
        }

        public Builder setOnlineTransactionId(String onlineTransactionId) {
            this.onlineTransactionId = onlineTransactionId;
            return this;
        }

        public Builder setTransactionId(String transactionId) {
            this.transactionId = transactionId;
            return this;
        }

        public Builder setAdCashBackPercentage(int adCashBackPercentage) {
            this.adCashBackPercentage = adCashBackPercentage;
            return this;
        }

        public Builder setOfferedCashBackPercentage(int offeredCashBackPercentage) {
            this.offeredCashBackPercentage = offeredCashBackPercentage;
            return this;
        }

        public Builder setAdDiscountPercentage(int adDiscountPercentage) {
            this.adDiscountPercentage = adDiscountPercentage;
            return this;
        }

        public Builder setOfferedDiscountPercentage(int offeredDiscountPercentage) {
            this.offeredDiscountPercentage = offeredDiscountPercentage;
            return this;
        }

        public Builder setPostalInformation(String postalInformation) {
            this.postalInformation = postalInformation;
            return this;
        }

        public Builder setAriaID(int ariaID) {
            this.areaId = ariaID;
            return this;
        }

        public Builder setOrderType(String orderType) {
            this.orderType = orderType;
            return this;
        }

        public Builder setThirdPartyThanaId(int thirdPartyLocationId) {
            this.thirdPartyLocationId = thirdPartyLocationId;
            return this;
        }

        public Builder setUnlockCommission(int unlockCommission) {
            this.unlockCommission = unlockCommission;
            return this;
        }

        public Builder setEventType(int eventType) {
            this.EventType = eventType;
            return this;
        }

        public Builder setSetGroupId(int groupID) {
            this.groupBuyId = groupID;
            return this;
        }

        public Builder setVoucherCode(String voucherCode) {
            this.VoucherCode = voucherCode;
            return this;
        }

        public Builder setVoucherType(int voucherType) {
            this.VoucherType = voucherType;
            return this;
        }

        public Builder setVoucherCodeId(int voucherCodeId) {
            this.VoucherCodeId = voucherCodeId;
            return this;
        }

        public Builder setDiscountInAmount(int discountInAmount) {
            this.DiscountInAmount = discountInAmount;
            return this;
        }

        public Builder setEMIPeriod(int EMIPeriod) {
            this.EMIPeriod = EMIPeriod;
            return this;
        }

        public Builder setSku(String ThirdPartySku) {
            this.ThirdPartySku = ThirdPartySku;
            return this;
        }

        public Builder setPrescriptionImgUrl(String ThirdPartyImageUrl) {
            this.ThirdPartyImageUrl = ThirdPartyImageUrl;
            return this;
        }

        public Builder setCollectionCharge(int collectionCharge) {
            this.CollectionCharge = collectionCharge;
            return this;
        }

        public Builder setActualPrice(int actualPrice) {
            this.actualPrice = actualPrice;
            return this;
        }

        public Builder setBogoReference(int bogoReference) {
            this.bogoReference = bogoReference;
            return this;
        }

        public Builder setSameDayCharge(int sameDayCharge) {
            this.sameDayCharge = sameDayCharge;
            return this;
        }

        public Builder setVSReferenceId(int vsReferenceId) {
            this.vsReferenceId = vsReferenceId;
            return this;
        }

        public void setDealTitle(String dealTitle) {
            this.dealTitle = dealTitle;
        }

        public int getMerchantId() {
            return merchantId;
        }

        public int getDealId() {
            return dealId;
        }

        public int getGroupBuyId() {
            return groupBuyId;
        }

        public String getDealTitle() {
            return dealTitle;
        }

        public String getDealSize() {
            return sizes;
        }

        public String getProductsSize() {
            return productsSize;
        }

        public void setProductsSize(String productsSize) {
            this.productsSize = productsSize;
        }

        public int getEventId() {
            return eventId;
        }

        public int getEventType() {
            return EventType;
        }

        public int getIsHoursAvailable() {
            return isHoursAvailable;
        }
    }

    public int getDealId() {
        return dealId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public int getCouponQtn() {
        return couponQtn;
    }

    public double getCouponPrice() {
        return couponPrice;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public String getCustomerBillingAddress() {
        return customerBillingAddress;
    }

    public String getCustomerMobile() {
        return customerMobile;
    }

    public int getDeliveryDist() {
        return deliveryDist;
    }

    public int getThanaId() {
        return thanaId;
    }

    public int getDeliveryCharge() {
        return deliveryCharge;
    }

    public String getSizes() {
        return sizes;
    }

    public String getColors() {
        return colors;
    }

    public int getCommission() {
        return commission;
    }

    public int getShopCartId() {
        return shopCartId;
    }

    public String getOrderFrom() {
        return orderFrom;
    }

    public String getCustomerAlternateMobile() {
        return customerAlternateMobile;
    }

    public String getOrderSource() {
        return orderSource;
    }

    public int getMerchantId() {
        return merchantId;
    }

    public int getVoucherId() {
        return voucherId;
    }

    public int getEventId() {
        return eventId;
    }

    public int getIsHoursAvailable() {
        return isHoursAvailable;
    }

    public String getSpecialNotes() {
        return specialNotes;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public String getCardType() {
        return cardType;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public String getOnlineTransactionId() {
        return onlineTransactionId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public int getAdCashBackPercentage() {
        return adCashBackPercentage;
    }

    public int getOfferedCashBackPercentage() {
        return offeredCashBackPercentage;
    }

    public int getAdDiscountPercentage() {
        return adDiscountPercentage;
    }

    public int getOfferedDiscountPercentage() {
        return offeredDiscountPercentage;
    }

    public String getPostalInformation() {
        return postalInformation;
    }

    public int getGroupBuyId() {
        return groupBuyId;
    }

    public int getGetFree() {
        return getFree;
    }

    public int getAreaId() {
        return areaId;
    }

    public String getOrderType() {
        return orderType;
    }

    public int getThirdPartyLocationId() {
        return thirdPartyLocationId;
    }

    public int getUnlockCommission() {
        return unlockCommission;
    }

    public int getEventType() {
        return EventType;
    }

    public String getVoucherCode() {
        return VoucherCode;
    }

    public int getVoucherType() {
        return VoucherType;
    }

    public int getDiscountInAmount() {
        return DiscountInAmount;
    }

    public String getDealTitle() {
        return dealTitle;
    }

    public int getVoucherCodeId() {
        return VoucherCodeId;
    }

    public String getProductsSize() {
        return productsSize;
    }

    public int getEMIPeriod() {
        return EMIPeriod;
    }

    public String getThirdPartySku() {
        return ThirdPartySku;
    }

    public String getThirdPartyImageUrl() {
        return ThirdPartyImageUrl;
    }

    public int getCollectionCharge() {
        return CollectionCharge;
    }

    public int getActualPrice() {
        return actualPrice;
    }

    public int getBogoReference() {
        return bogoReference;
    }

    public int getSameDayCharge() {
        return sameDayCharge;
    }

    public int getVsReferenceId() {
        return vsReferenceId;
    }

    @Override
    public String toString() {
        return "PlaceOrder{" +
                "dealId=" + dealId +
                ", customerId=" + customerId +
                ", couponQtn=" + couponQtn +
                ", couponPrice=" + couponPrice +
                ", paymentType='" + paymentType + '\'' +
                ", customerBillingAddress='" + customerBillingAddress + '\'' +
                ", customerMobile='" + customerMobile + '\'' +
                ", deliveryDist=" + deliveryDist +
                ", thanaId=" + thanaId +
                ", deliveryCharge=" + deliveryCharge +
                ", sizes='" + sizes + '\'' +
                ", colors='" + colors + '\'' +
                ", commission=" + commission +
                ", shopCartId=" + shopCartId +
                ", orderFrom='" + orderFrom + '\'' +
                ", customerAlternateMobile='" + customerAlternateMobile + '\'' +
                ", orderSource='" + orderSource + '\'' +
                ", merchantId=" + merchantId +
                ", voucherId=" + voucherId +
                ", eventId=" + eventId +
                ", isHoursAvailable=" + isHoursAvailable +
                ", specialNotes='" + specialNotes + '\'' +
                ", appVersion='" + appVersion + '\'' +
                ", unitPrice=" + unitPrice +
                ", cardType='" + cardType + '\'' +
                ", paymentStatus='" + paymentStatus + '\'' +
                ", onlineTransactionId='" + onlineTransactionId + '\'' +
                ", transactionId='" + transactionId + '\'' +
                ", adCashBackPercentage=" + adCashBackPercentage +
                ", offeredCashBackPercentage=" + offeredCashBackPercentage +
                ", adDiscountPercentage=" + adDiscountPercentage +
                ", offeredDiscountPercentage=" + offeredDiscountPercentage +
                ", postalInformation='" + postalInformation + '\'' +
                ", groupBuyId=" + groupBuyId +
                ", getFree=" + getFree +
                ", areaId=" + areaId +
                ", orderType='" + orderType + '\'' +
                ", thirdPartyLocationId=" + thirdPartyLocationId +
                ", unlockCommission=" + unlockCommission +
                ", EventType=" + EventType +
                ", VoucherCode='" + VoucherCode + '\'' +
                ", VoucherType=" + VoucherType +
                ", VoucherCodeId=" + VoucherCodeId +
                ", DiscountInAmount=" + DiscountInAmount +
                ", dealTitle='" + dealTitle + '\'' +
                ", productsSize='" + productsSize + '\'' +
                ", EMIPeriod=" + EMIPeriod +
                ", ThirdPartySku='" + ThirdPartySku + '\'' +
                ", ThirdPartyImageUrl='" + ThirdPartyImageUrl + '\'' +
                ", CollectionCharge=" + CollectionCharge +
                ", actualPrice=" + actualPrice +
                ", bogoReference=" + bogoReference +
                ", sameDayCharge=" + sameDayCharge +
                ", vsReferenceId=" + vsReferenceId +
                '}';
    }
}
