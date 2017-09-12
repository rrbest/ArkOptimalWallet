/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.ark.core;

import ark.optimal.wallet.ui.FXMLCreateAccountController;
import com.google.common.io.BaseEncoding;
import java.security.SecureRandom;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bitcoinj.core.Base58;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.crypto.MnemonicCode;
import org.spongycastle.crypto.digests.RIPEMD160Digest;

/**
 *
 * @author Mastadon
 */
public class Crypto {

    private static int networkVersion = 0x17;
    private static final int WORDS_SIZE = 16;

    public static ECKey.ECDSASignature sign(Transaction t, String passphrase) {
        byte[] txbytes = getBytes(t, true, true);
        return signBytes(txbytes, passphrase);
    }

    public static ECKey.ECDSASignature secondSign(Transaction t, String passphrase) {
        byte[] txbytes = getBytes(t, false, true);
        return signBytes(txbytes, passphrase);
    }

    public static ECKey.ECDSASignature signBytes(byte[] bytes, String passphrase) {
        ECKey keys = getKeys(passphrase);
        return keys.sign(Sha256Hash.of(bytes));
    }

    public static boolean verify(Transaction t) {
        ECKey keys = ECKey.fromPublicOnly(BaseEncoding.base16().lowerCase().decode(t.getSenderPublicKey()));
        byte[] signature = BaseEncoding.base16().lowerCase().decode(t.getSignSignature());
        byte[] bytes = getBytes(t, true, true);
        return verifyBytes(bytes, signature, keys.getPubKey());
    }

    public static boolean secondVerify(Transaction t, String secondPublicKeyHex) {
        ECKey keys = ECKey.fromPublicOnly(BaseEncoding.base16().lowerCase().decode(secondPublicKeyHex));
        byte[] signature = BaseEncoding.base16().lowerCase().decode(t.getSignSignature());
        byte[] bytes = getBytes(t, false, true);
        return verifyBytes(bytes, signature, keys.getPubKey());
    }

    public static boolean verifyBytes(byte[] bytes, byte[] signature, byte[] publicKey) {
        return ECKey.verify(Sha256Hash.hash(bytes), signature, publicKey);
    }

    public static byte[] getBytes(Transaction t, boolean skipSignature, boolean skipSecondSignature) {
        return t.toBytes(skipSignature, skipSecondSignature);
    }

    public static String getId(Transaction t) {
        return BaseEncoding.base16().lowerCase().encode(Sha256Hash.hash(getBytes(t, false, false)));
    }

    public static ECKey getKeys(String passphrase) {
        byte[] sha256 = Sha256Hash.hash(passphrase.getBytes());
        ECKey keys = ECKey.fromPrivate(sha256, true);
        return keys;
    }

    public static String getAddress(ECKey keys) {
        return getAddress(keys.getPubKey());
    }

    public static String getAddress(byte[] publicKey) {
        RIPEMD160Digest digest = new RIPEMD160Digest();
        digest.update(publicKey, 0, publicKey.length);
        byte[] out = new byte[20];
        digest.doFinal(out, 0);
        //VersionedChecksummedBytes np = new VersionedChecksummedBytes(networkVersion, out);
        //Address address = new Address(MainNetParams.get(), networkVersion, out);
        return toBase58(networkVersion, out);
    }

    private static String toBase58(int version, byte[] bytes) {
        // A stringified buffer is:
        //   1 byte version + data bytes + 4 bytes check code (a truncated hash)
        byte[] addressBytes = new byte[1 + bytes.length + 4];
        addressBytes[0] = (byte) version;
        System.arraycopy(bytes, 0, addressBytes, 1, bytes.length);
        byte[] checksum = Sha256Hash.hashTwice(addressBytes, 0, bytes.length + 1);
        System.arraycopy(checksum, 0, addressBytes, bytes.length + 1, 4);
        return Base58.encode(addressBytes);
    }

    public static String generatePassphrase() {
        String passphrase = null;

        try {
            // TODO: Access the person object
            MnemonicCode mnc = new MnemonicCode();
            System.out.println(mnc.toString());
            SecureRandom secureRandom = new SecureRandom();
            byte[] entropy = secureRandom.generateSeed(WORDS_SIZE);
            //secureRandom.nextBytes(entropy);
            List<String> l = mnc.toMnemonic(entropy);
            passphrase = String.join(" ", l);
            
        } catch (Exception ex) {
            Logger.getLogger(FXMLCreateAccountController.class.getName()).log(Level.SEVERE, null, ex);
        }

        return passphrase;
    }

    
}
