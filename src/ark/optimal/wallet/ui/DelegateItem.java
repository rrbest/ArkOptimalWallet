/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ark.optimal.wallet.ui;

import ark.optimal.wallet.pojo.Delegate;
import javafx.scene.control.Hyperlink;

/**
 *
 * @author Mastadon
 */
public class DelegateItem {
    private String username;
    private Hyperlink address;
    private Integer rate;
    private Double approval;
    private Double productivity;
    private Boolean checked;

    public DelegateItem(String username, Hyperlink address, Integer rate, Double approval, Double productivity, Boolean checked) {
        this.username = username;
        this.address = address;
        this.rate = rate;
        this.approval = approval;
        this.productivity = productivity;
        this.checked = checked;
    }

    public DelegateItem(Delegate delegate) {
        this.username = delegate.getUsername();
        this.address = new Hyperlink(delegate.getAddress());
        this.rate = delegate.getRate();
        this.approval = delegate.getApproval();
        this.productivity = delegate.getProductivity();
        this.checked = delegate.getChecked();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Hyperlink getAddress() {
        return address;
    }

    public void setAddress(Hyperlink address) {
        this.address = address;
    }

    public Integer getRate() {
        return rate;
    }

    public void setRate(Integer rate) {
        this.rate = rate;
    }

    public Double getApproval() {
        return approval;
    }

    public void setApproval(Double approval) {
        this.approval = approval;
    }

    public Double getProductivity() {
        return productivity;
    }

    public void setProductivity(Double productivity) {
        this.productivity = productivity;
    }

    public Boolean getChecked() {
        return checked;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }
    
    
}
