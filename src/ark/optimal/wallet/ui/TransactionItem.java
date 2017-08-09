/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ark.optimal.wallet.ui;

import javafx.scene.control.Hyperlink;
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;

/**
 *
 * @author Mastadon
 */
public class TransactionItem {

    private String id;
    private int confirmations;
    private DateTime date;
    private String type;
    private Double amount;
    private String from;
    private String to;
    private String smartBridge;

    private Hyperlink id_link;
    private Hyperlink from_link;
    private Hyperlink to_link;

    public Hyperlink getId_link() {
        return id_link;
    }

    public void setId_link(Hyperlink id_link) {
        this.id_link = id_link;
    }

    public Hyperlink getFrom_link() {
        return from_link;
    }

    public void setFrom_link(Hyperlink from_link) {
        this.from_link = from_link;
    }

    public TransactionItem(String id, int confirmations, DateTime date, String type, Double amount, String from, String to, String smartBridge) {
        this.id = id;
        this.confirmations = confirmations;
        this.date = date;
        this.type = type;
        this.amount = amount;
        this.from = from;
        this.to = to;
        this.smartBridge = smartBridge;
        this.id_link = new Hyperlink(id);
        //this.id_link.setText(id);
        
        
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getConfirmations() {
        return confirmations;
    }

    public void setConfirmations(int confirmations) {
        this.confirmations = confirmations;
    }

    public DateTime getDate() {
        return date;
    }

    public void setDate(DateTime date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getSmartBridge() {
        return smartBridge;
    }

    public void setSmartBridge(String smartBridge) {
        this.smartBridge = smartBridge;
    }

}
