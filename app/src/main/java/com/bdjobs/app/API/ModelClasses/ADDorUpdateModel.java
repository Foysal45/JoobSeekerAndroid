
package com.bdjobs.app.API.ModelClasses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ADDorUpdateModel {



    @SerializedName("messgae")
    @Expose
    private String messgae;

    @SerializedName("path")
    @Expose
    private String path;

    @SerializedName("Message")
    @Expose
    private String message;
    @SerializedName("messageType")
    @Expose
    private String messageType;
    @SerializedName("isResumeUpdate")
    @Expose
    private String isResumeUpdate;
    @SerializedName("isCvPosted")
    @Expose
    private String isCvPosted;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getIsResumeUpdate() {
        return isResumeUpdate;
    }

    public void setIsResumeUpdate(String isResumeUpdate) {
        this.isResumeUpdate = isResumeUpdate;
    }

    public String getIsCvPosted() {
        return isCvPosted;
    }

    public void setIsCvPosted(String isCvPosted) {
        this.isCvPosted = isCvPosted;
    }

    public String getMessgae() {
        return messgae;
    }

    public void setMessgae(String messgae) {
        this.messgae = messgae;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

}
