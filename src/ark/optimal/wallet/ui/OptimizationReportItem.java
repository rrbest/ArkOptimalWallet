/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ark.optimal.wallet.ui;

import javafx.scene.control.Hyperlink;

/**
 *
 * @author Mastadon
 */
public class OptimizationReportItem {
    private String delegateName;
    private Integer delegateRank;
    private String subwalletAddress;
    private Hyperlink subwalletAddress_link;
    private Integer votes;
    private Double payout;

    public OptimizationReportItem(String delegateName, Integer delegateRank, String subwalletAddress, Integer votes, Double payout) {
        this.delegateName = delegateName;
        this.delegateRank = delegateRank;
        this.subwalletAddress = subwalletAddress;
        this.subwalletAddress_link = new Hyperlink(subwalletAddress);
        this.votes = votes;
        this.payout = payout;
    }

    public String getDelegateName() {
        return delegateName;
    }

    public void setDelegateName(String delegateName) {
        this.delegateName = delegateName;
    }

    public Integer getDelegateRank() {
        return delegateRank;
    }

    public void setDelegateRank(Integer delegateRank) {
        this.delegateRank = delegateRank;
    }

    public String getSubwalletAddress() {
        return subwalletAddress;
    }

    public void setSubwalletAddress(String subwalletAddress) {
        this.subwalletAddress = subwalletAddress;
    }

    public Hyperlink getSubwalletAddress_link() {
        return subwalletAddress_link;
    }

    public void setSubwalletAddress_link(Hyperlink subwalletAddress_link) {
        this.subwalletAddress_link = subwalletAddress_link;
    }

    public Integer getVotes() {
        return votes;
    }

    public void setVotes(Integer votes) {
        this.votes = votes;
    }

    public Double getPayout() {
        return payout;
    }

    public void setPayout(Double payout) {
        this.payout = payout;
    }
    
    
}
