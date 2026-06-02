package com.group3.dto.response;

import java.math.BigDecimal;

public class RevenuePeriodResponse {
    private String period;
    private String label;
    private BigDecimal revenue;

    public RevenuePeriodResponse() {
    }

    public RevenuePeriodResponse(String period, String label, BigDecimal revenue) {
        this.period = period;
        this.label = label;
        this.revenue = revenue;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public BigDecimal getRevenue() {
        return revenue;
    }

    public void setRevenue(BigDecimal revenue) {
        this.revenue = revenue;
    }
}
