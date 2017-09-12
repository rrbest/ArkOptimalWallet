/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.ark.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.BaseEncoding;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Map;
import org.bitcoinj.core.Base58;

/**
 *
 * @author Mastadon
 */
public class Transaction {

    private String id;
    private int timestamp;
    private String senderId;

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }
    private String recipientId;
    private Long amount;
    private Long fee;
    private byte type;
    private String vendorField;
    private String signature;
    private String signSignature;
    private String senderPublicKey;
    private String requesterPublicKey;
    private Asset asset;

    public void setAsset(Asset asset) {
        this.asset = asset;
    }

    public Transaction(int timestamp, String recipientId, Long amount, Long fee, byte type, String vendorField, String signature, String signSignature, String senderPublicKey, String requesterPublicKey) {
        this.timestamp = timestamp;
        this.recipientId = recipientId;
        this.amount = amount;
        this.fee = fee;
        this.type = type;
        this.vendorField = vendorField;
        this.signature = signature;
        this.signSignature = signSignature;
        this.senderPublicKey = senderPublicKey;
        this.requesterPublicKey = requesterPublicKey;
    }

    public Transaction(String recipientId, Long amount, Long fee, byte type, String vendorField) {
        this.recipientId = recipientId;
        this.amount = amount;
        this.fee = fee;
        this.type = type;
        this.vendorField = vendorField;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public byte[] toBytes(boolean skipSignature, boolean skipSecondSignature) {
        ByteBuffer buffer = ByteBuffer.allocate(1000);
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        buffer.put(type);
        buffer.putInt(timestamp);
        buffer.put(BaseEncoding.base16().lowerCase().decode(senderPublicKey));

        if (requesterPublicKey != null) {
            buffer.put(BaseEncoding.base16().lowerCase().decode(requesterPublicKey));
        }

        if (recipientId != null) {
            buffer.put(Base58.decodeChecked(recipientId));
        } else {
            buffer.put(new byte[21]);
        }

        if (vendorField != null) {
            byte[] vbytes = vendorField.getBytes();
            if (vbytes.length < 65) {
                buffer.put(vbytes);
                buffer.put(new byte[64 - vbytes.length]);
            }
        } else {
            buffer.put(new byte[64]);
        }

        buffer.putLong(amount);
        buffer.putLong(fee);

        if (type == 1) {
            buffer.put(BaseEncoding.base16().lowerCase().decode((CharSequence) asset.getSignature()));
        } else if (type == 2) {
            buffer.put(asset.getUsername().getBytes());
        } else if (type == 3) {
            buffer.put(String.join(",", asset.getVotes()).getBytes());
        }
        
        
        // TODO: multisignature
        // else if(type==4){
        //   buffer.put BaseEncoding.base16().lowerCase().decode(asset.signature)
        // }

        if (!skipSignature && signature != null) {
            buffer.put(BaseEncoding.base16().lowerCase().decode(signature));
        }
        if (!skipSecondSignature && signSignature != null) {
            buffer.put(BaseEncoding.base16().lowerCase().decode(signSignature));
        }

        byte[] outBuffer = new byte[buffer.position()];
        buffer.rewind();
        buffer.get(outBuffer);
        return outBuffer;
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

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
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

    public void sign(String passphrase) {
        this.senderPublicKey = BaseEncoding.base16().lowerCase().encode(Crypto.getKeys(passphrase).getPubKey());
        this.signature = BaseEncoding.base16().lowerCase().encode(Crypto.sign(this, passphrase).encodeToDER());
    }

    public Map toObject() {
        ObjectMapper oMapper = new ObjectMapper();
        Map<String, Object> map = oMapper.convertValue(this, Map.class);
        return map;
    }

}
