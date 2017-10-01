/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ark.optimal.wallet.pojo;

/**
 *
 * @author Mastadon
 */
public class Delegate {
    private String username;
    private String address;
    private String publicKey;
    private Integer vote;
    private Integer producedblocks;
    private Integer missedblocks;
    private Integer rate;
    private Double approval;
    private Double productivity;
    
    //private Double poolPercentage;
    //private Double payoutFrequency;
    //private Double minPayout;
    private Double payoutPercentage;
    private Integer excludedVotes;

    private final Double PAYOUTPERCENTAGE = 90.0;
    private final Double PAYOUTFREQUENCY = 0.25;
    private final Double POOLPERCENTAGE = 90.0;
    private final Double MINPAYOUT = 100.0;
    private final Integer EXCLUDEDVOTES = 0;
 
    
    private Boolean checked;

    
    public Boolean getChecked() {
        return checked;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }

    public void setVote(Integer vote) {
        this.vote = vote;
    }

//    public Double getPoolPercentage() {
//        return poolPercentage;
//    }
//
//    public void setPoolPercentage(Double poolPercentage) {
//        this.poolPercentage = poolPercentage;
//    }
//
//    public Double getPayoutFrequency() {
//        return payoutFrequency;
//    }
//
//    public void setPayoutFrequency(Double payoutFrequency) {
//        this.payoutFrequency = payoutFrequency;
//    }
//
//    public Double getMinPayout() {
//        return minPayout;
//    }
//
//    public void setMinPayout(Double minPayout) {
//        this.minPayout = minPayout;
//    }
    public void setRate(Integer rate) {
        this.rate = rate;
    }

    public Double getPayoutPercentage() {
        return payoutPercentage;
    }

    public void setPayoutPercentage(Double payoutPercentage) {
        this.payoutPercentage = payoutPercentage;
    }
    

    public String getUsername() {
        return username;
    }

    public String getAddress() {
        return address;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public Integer getVote() {
        return vote;
    }

    public Integer getProducedblocks() {
        return producedblocks;
    }

    public Integer getMissedblocks() {
        return missedblocks;
    }

    public Integer getRate() {
        return rate;
    }

    public Double getApproval() {
        return approval;
    }

    public Double getProductivity() {
        return productivity;
    }

    public Integer getExcludedVotes() {
        return excludedVotes;
    }

    public void setExcludedVotes(Integer excludedVotes) {
        this.excludedVotes = excludedVotes;
    }
    

    public Delegate(String username, String address, String publicKey, Integer vote, Integer producedblocks, Integer missedblocks, Integer rate, Double approval, Double productivity) {
        this.username = username;
        this.address = address;
        this.publicKey = publicKey;
        this.vote = vote;
        this.producedblocks = producedblocks;
        this.missedblocks = missedblocks;
        this.rate = rate;
        this.approval = approval;
        this.productivity = productivity;
        this.checked = false;
//        this.payoutFrequency = PAYOUTFREQUENCY;
//        this.poolPercentage = POOLPERCENTAGE;
//        this.minPayout = MINPAYOUT;
        this.payoutPercentage = PAYOUTPERCENTAGE;
        this.excludedVotes = EXCLUDEDVOTES;
    }
    
    
    
}
