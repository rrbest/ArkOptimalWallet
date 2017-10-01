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
public class SubWalletItem {

    private String delegateName;
    private Integer delegateRank;
    private String address;
    private Hyperlink address_link;
    private Integer votes;
    private Double payoutPercentage;
    private Boolean voted;
    private Boolean created;

    private Integer delegateTotalVotes;
    private Integer delegateExcludedVotes;

    public Integer getDelegateTotalVotes() {
        return delegateTotalVotes;
    }

    public void setDelegateTotalVotes(Integer delegateTotalVotes) {
        this.delegateTotalVotes = delegateTotalVotes;
    }

    public Integer getDelegateExcludedVotes() {
        return delegateExcludedVotes;
    }

    public void setDelegateExcludedVotes(Integer delegateExcludedVotes) {
        this.delegateExcludedVotes = delegateExcludedVotes;
    }

    public Boolean getVoted() {
        return voted;
    }

    public void setVoted(Boolean voted) {
        this.voted = voted;
    }

    public Boolean getCreated() {
        return created;
    }

    public void setCreated(Boolean created) {
        this.created = created;
    }
    

    public Double getPayoutPercentage() {
        return payoutPercentage;
    }

    public void setPayoutPercentage(Double payoutPercentage) {
        this.payoutPercentage = payoutPercentage;
    }
    private Boolean checked;

    public Boolean getChecked() {
        return checked;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
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

    public Hyperlink getAddress_link() {
        return address_link;
    }

    public void setAddress_link(Hyperlink address_link) {
        this.address_link = address_link;
    }

    public Integer getVotes() {
        return votes;
    }

    public void setVotes(Integer votes) {
        this.votes = votes;
    }

    public SubWalletItem(String delegateName, Integer delegateRank, String address, Integer votes, Double payoutPercentage) {
        this.delegateName = delegateName;
        this.delegateRank = delegateRank;
        this.address = address;
        this.address_link = new Hyperlink(address);
        this.votes = votes;
        this.payoutPercentage = payoutPercentage;
    }

}
