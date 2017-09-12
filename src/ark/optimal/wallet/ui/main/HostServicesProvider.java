/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ark.optimal.wallet.ui.main;

import javafx.application.HostServices;

/**
 *
 * @author Mastadon
 */
public class HostServicesProvider {

    private static HostServices hostServices;
    private static HostServicesProvider Instance;
    
    public static HostServicesProvider getInstance(){
        if (Instance == null){
            Instance = new HostServicesProvider();
        }
        return Instance;
    }
    public void init(HostServices hostServices) {
        if (this.hostServices != null) {
            throw new IllegalStateException("Host services already initialized");
        }
        this.hostServices = hostServices;
    }

    public static HostServices getHostServices() {
        return hostServices;
    }
    
}
