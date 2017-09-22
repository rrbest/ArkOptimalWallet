/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ark.optimal.wallet.ui.main;

/**
 *
 * @author Mastadon
 */
public class Settings {
    private String walletPath;

    public Settings(String walletPath) {
        this.walletPath = walletPath;
    }

    public String getWalletPath() {
        return walletPath;
    }

    public void setWalletPath(String walletPath) {
        this.walletPath = walletPath;
    }
    
    
}
