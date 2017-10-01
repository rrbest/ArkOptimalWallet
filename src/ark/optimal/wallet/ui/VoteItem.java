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
public class VoteItem {
    
    private String delegateName;
    private Integer delegateRank;
    private String address;
    private Hyperlink address_link;
    private Integer votes;
    private Double payoutpercentage;
    private Integer excludedVotes;

    public VoteItem(String delegateName, Integer delegateRank, String address, Integer votes, Double payoutpercentage, Integer excludedVotes) {
        this.delegateName = delegateName;
        this.delegateRank = delegateRank;
        this.address = address;
        this.address_link = new Hyperlink(address);
        this.votes = votes;
        this.payoutpercentage = payoutpercentage;
        this.excludedVotes = excludedVotes;
    }

    public Hyperlink getAddress_link() {
        return address_link;
    }

    public void setAddress_link(Hyperlink address_link) {
        this.address_link = address_link;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getVotes() {
        return votes;
    }

    public void setVotes(Integer votes) {
        this.votes = votes;
    }

    public Double getPayoutpercentage() {
        return payoutpercentage;
    }

    public void setPayoutpercentage(Double payoutpercentage) {
        this.payoutpercentage = payoutpercentage;
    }

    public Integer getExcludedVotes() {
        return excludedVotes;
    }

    public void setExcludedVotes(Integer excludedVotese) {
        this.excludedVotes = excludedVotes;
    }

    
}
