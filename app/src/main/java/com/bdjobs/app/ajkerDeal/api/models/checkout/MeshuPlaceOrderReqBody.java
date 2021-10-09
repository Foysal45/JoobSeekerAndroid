package com.bdjobs.app.ajkerDeal.api.models.checkout;

import androidx.annotation.Keep;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Keep
public class MeshuPlaceOrderReqBody {
    @SerializedName("dealId")
    @Expose
    private int dealId;

    @SerializedName("resellercustomerid")
    @Expose
    private int customerId;

    @SerializedName("couponQtn")
    @Expose
    private int couponQtn;

    @SerializedName("couponPrice")
    @Expose
    private int couponPrice;

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

    @SerializedName("appVersion")
    @Expose
    private String appVersion;

    @SerializedName("unitPrice")
    @Expose
    private int unitPrice;

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

    private int resellercommission;
    private String customername;

    @SerializedName("postalInformation")
    @Expose
    private String postalInformation;
    private int catalogId;
    private int areaId;

    private String BkasMobile;


    private MeshuPlaceOrderReqBody(Builder builder, OrderType orderType){

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
        this.orderFrom = builder.orderFrom;
        this.customerAlternateMobile = builder.customerAlternateMobile;
        this.orderSource = builder.orderSource;
        this.merchantId = builder.merchantId;

        this.appVersion = builder.appVersion;
        this.unitPrice = builder.unitPrice;
        this.cardType = builder.cardType;
        this.paymentStatus = builder.paymentStatus;
        this.onlineTransactionId = builder.onlineTransactionId;
        this.transactionId = builder.transactionId;

        this.resellercommission = builder.resellercommission;
        this.customername = builder.customername;

        this.postalInformation = builder.postalInformation;
        this.catalogId = builder.catalogId;
        this.areaId = builder.areaId;
        this.BkasMobile = builder.BkasMobile;


    }

    public static class Builder{

        private int dealId;
        private int customerId;
        private int couponQtn;
        private int couponPrice;
        private String paymentType;
        private String customerBillingAddress;
        private String customerMobile;
        private int deliveryDist;
        private int thanaId;
        private int deliveryCharge;
        private String sizes;
        private String colors;
        private int commission;
        private String orderFrom;
        private String customerAlternateMobile;
        private String orderSource;
        private int merchantId;

        private String appVersion;
        private int unitPrice;
        private String cardType;
        private String paymentStatus;
        private String onlineTransactionId;
        private String transactionId;

        private int resellercommission;
        private String customername;
        private String postalInformation;
        private int catalogId;
        private int areaId;
        private String BkasMobile;


        private OrderType orderType;

        public Builder(OrderType orderType) {
        }

        public Builder setResellercommission(int resellercommission) {
            this.resellercommission = resellercommission;
            return this;
        }

        public Builder setCustomername(String customername) {
            this.customername = customername;
            return this;
        }

        public MeshuPlaceOrderReqBody build(){
            return new MeshuPlaceOrderReqBody(this,orderType);
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

        public Builder setCouponPrice(int couponPrice) {
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


        public Builder setAppVersion(String appVersion) {
            this.appVersion = appVersion;
            return this;
        }

        public Builder setUnitPrice(int unitPrice) {
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


        public Builder setPostalInformation(String postalInformation) {
            this.postalInformation = postalInformation;
            return this;
        }

        public Builder setCatalogId(int catalogId) {
            this.catalogId = catalogId;
            return this;
        }

        public Builder setAreaId(int areaId) {
            this.areaId = areaId;
            return this;
        }

        public Builder setBikashMobileNumber(String BkasMobile) {
            this.BkasMobile = BkasMobile;
            return this;
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

    public int getCouponPrice() {
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

    public String getAppVersion() {
        return appVersion;
    }

    public int getUnitPrice() {
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

    public String getPostalInformation() {
        return postalInformation;
    }

    public int getCatalogId() {
        return catalogId;
    }

    public int getResellercommission() {
        return resellercommission;
    }

    public String getCustomername() {
        return customername;
    }

    public int getAreaId() {
        return areaId;
    }

    public String getBikashMobileNumber() {
        return BkasMobile;
    }

    @Override
    public String toString() {
        return "MeshuPlaceOrderReqBody{" +
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
                ", orderFrom='" + orderFrom + '\'' +
                ", customerAlternateMobile='" + customerAlternateMobile + '\'' +
                ", orderSource='" + orderSource + '\'' +
                ", merchantId=" + merchantId +
                ", appVersion='" + appVersion + '\'' +
                ", unitPrice=" + unitPrice +
                ", cardType='" + cardType + '\'' +
                ", paymentStatus='" + paymentStatus + '\'' +
                ", onlineTransactionId='" + onlineTransactionId + '\'' +
                ", transactionId='" + transactionId + '\'' +
                ", resellercommission=" + resellercommission +
                ", customername='" + customername + '\'' +
                ", postalInformation='" + postalInformation + '\'' +
                ", catalogId=" + catalogId +
                ", areaId=" + areaId +
                ", bikashMobileNumber='" + BkasMobile + '\'' +
                '}';
    }
}
