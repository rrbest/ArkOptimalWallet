/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ark.optimal.wallet.services.networkservices;

import java.util.List;

/**
 *
 * @author Mastadon
 */
public class Network {
  private String nethash;
  private String name;

    public String getNethash() {
        return nethash;
    }

    public String getName() {
        return name;
    }

    public int getPort() {
        return port;
    }

    public int getPrefix() {
        return prefix;
    }

    public int getBroadcastMax() {
        return broadcastMax;
    }

    public List<String> getPeerseed() {
        return peerseed;
    }
  private int port;
  private int prefix;
  private int broadcastMax = 10;
  private List<String> peerseed;

    public Network(String nethash, String name, int port, int prefix, List<String> peerseed) {
        this.nethash = nethash;
        this.name = name;
        this.port = port;
        this.prefix = prefix;
        this.peerseed = peerseed;
    }
  
  
}
