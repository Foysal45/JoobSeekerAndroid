package com.bdjobs.app.ajkerDeal.api.models.checkout;

import com.google.gson.annotations.SerializedName;

public class ResponseGenericModel<T> {

    @SerializedName("MessageCode")
    private int messageCode;
    @SerializedName("MessageText")
    private String messageText;
    @SerializedName("DatabseTracking")
    private boolean databseTracking;
    @SerializedName("TotalCount")
    private int totalCount;
    @SerializedName("Data")
    private T data;

    public int getMessageCode() {
        return messageCode;
    }

    public void setMessageCode(int messageCode) {
        this.messageCode = messageCode;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public boolean isDatabseTracking() {
        return databseTracking;
    }

    public void setDatabseTracking(boolean databseTracking) {
        this.databseTracking = databseTracking;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
