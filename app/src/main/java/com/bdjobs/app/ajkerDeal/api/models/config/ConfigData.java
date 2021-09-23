package com.bdjobs.app.ajkerDeal.api.models.config;

import androidx.lifecycle.ViewModel;
import java.io.Serializable;
import java.util.List;

public class ConfigData implements Serializable {

    private String SystemTime;
    private int SpinTotalCount;
    private String ImageBaseUrl;
    private List<ViewModel> CustomerAppConfigResponse;

    public ConfigData() {
    }

    public List<ViewModel> getCustomerAppConfigResponse() {
        return CustomerAppConfigResponse;
    }

    public void setCustomerAppConfigResponse(List<ViewModel> customerAppConfigResponse) {
        CustomerAppConfigResponse = customerAppConfigResponse;
    }

    @Override
    public String toString() {
        return "ConfigData{" +
                "SystemTime='" + SystemTime + '\'' +
                ", SpinTotalCount=" + SpinTotalCount +
                ", ImageBaseUrl='" + ImageBaseUrl + '\'' +
                ", CustomerAppConfigResponse=" + CustomerAppConfigResponse +
                '}';
    }
}
