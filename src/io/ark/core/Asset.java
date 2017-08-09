/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.ark.core;

import java.util.List;

/**
 *
 * @author Mastadon
 */
public class Asset {
    private String username;
    private String Signature;
    private List <String> votes;

    Asset() {
    }

    public String getUsername() {
        return username;
    }

    public Asset(String username, String Signature) {
        this.username = username;
        this.Signature = Signature;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Asset(String username, String Signature, List<String> votes) {
        this.username = username;
        this.Signature = Signature;
        this.votes = votes;
    }

    public String getSignature() {
        return Signature;
    }

    public void setSignature(String Signature) {
        this.Signature = Signature;
    }

    public List<String> getVotes() {
        return votes;
    }

    public void setVotes(List<String> votes) {
        this.votes = votes;
    }

    public Asset(String username) {
        this.username = username;
    }
    
    
}
