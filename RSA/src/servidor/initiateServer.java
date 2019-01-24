/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servidor;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Sam
 */
public class initiateServer extends Thread{  
    
    private Servidor server;
    
    public initiateServer(){
        
        try {
            Thread.sleep(2000);
            
            this.setName("Server");
            server = new Servidor(this);
        } catch (InterruptedException ex) {
            Logger.getLogger(initiateServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
    
    public Servidor getServer(){
        return this.server;
    }
    
}
