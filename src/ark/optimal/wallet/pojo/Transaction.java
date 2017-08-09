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
public class Transaction {
    private String id; 
    private int timestamp;
    private String recipientId;
    private String senderId;
    private Long amount;
    private Long fee;
    private Integer type;
    private String vendorField;
    private String signature;
    private String signSignature;
    private String senderPublicKey;
    private String requesterPublicKey;
    private int confirmations;

    public String getFrom() {
        return from;
    }

    public Transaction(String id, int timestamp, String recipientId, String senderId, Long amount, Long fee, Integer type, String vendorField, String signature, String signSignature, String senderPublicKey, String requesterPublicKey, int confirmations, String blockid, String from, String to) {
        this.id = id;
        this.timestamp = timestamp;
        this.recipientId = recipientId;
        this.senderId = senderId;
        this.amount = amount;
        this.fee = fee;
        this.type = type;
        this.vendorField = vendorField;
        this.signature = signature;
        this.signSignature = signSignature;
        this.senderPublicKey = senderPublicKey;
        this.requesterPublicKey = requesterPublicKey;
        this.confirmations = confirmations;
        this.blockid = blockid;
        this.from = from;
        this.to = to;
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
    private String blockid;
    private String from;
    private String to;

    public Transaction(String id, int timestamp, String recipientId, String senderId,Long amount, Long fee, Integer type, String vendorField, String signature, String signSignature, String senderPublicKey, String requesterPublicKey, int confirmations, String blockid) {
        this.id = id;
        this.timestamp = timestamp;
        this.recipientId = recipientId;
        this.senderId = senderId;
        this.amount = amount;
        this.fee = fee;
        this.type = type;
        this.vendorField = vendorField;
        this.signature = signature;
        this.signSignature = signSignature;
        this.senderPublicKey = senderPublicKey;
        this.requesterPublicKey = requesterPublicKey;
        this.confirmations = confirmations;
        this.blockid = blockid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBlockid() {
        return blockid;
    }

    public void setBlockid(String blockid) {
        this.blockid = blockid;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public String getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(String recipientId) {
        this.recipientId = recipientId;
    }
    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public Long getFee() {
        return fee;
    }

    public void setFee(Long fee) {
        this.fee = fee;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getVendorField() {
        return vendorField;
    }

    public void setVendorField(String vendorField) {
        this.vendorField = vendorField;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getSignSignature() {
        return signSignature;
    }

    public void setSignSignature(String signSignature) {
        this.signSignature = signSignature;
    }

    public String getSenderPublicKey() {
        return senderPublicKey;
    }

    public void setSenderPublicKey(String senderPublicKey) {
        this.senderPublicKey = senderPublicKey;
    }

    public String getRequesterPublicKey() {
        return requesterPublicKey;
    }

    public void setRequesterPublicKey(String requesterPublicKey) {
        this.requesterPublicKey = requesterPublicKey;
    }

    public int getConfirmations() {
        return confirmations;
    }

    public void setConfirmations(int confirmations) {
        this.confirmations = confirmations;
    }
    
}
