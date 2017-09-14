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
public class SendToMasterItem {
    private String subwalletAddress;
    private Hyperlink subwalletAddress_link;
    private Integer votes;

    public SendToMasterItem(String subwalletAddress, Integer votes) {
        this.subwalletAddress = subwalletAddress;
        this.subwalletAddress_link = new Hyperlink(subwalletAddress);
        this.votes = votes;
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
}
