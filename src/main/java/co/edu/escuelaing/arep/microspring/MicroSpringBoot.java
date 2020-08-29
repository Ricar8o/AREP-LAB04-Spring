package co.edu.escuelaing.arep.microspring;

import java.util.logging.Level;
import java.util.logging.Logger;

import co.edu.escuelaing.arep.httpserver.HttpServer;
import co.edu.escuelaing.arep.httpserver.URIProcessor;

public class MicroSpringBoot {
    public static void main(String[] args){
        try{
            URIProcessor mspring = new MicroSpring();
            mspring.mapService(args[0]);
            HttpServer server = new HttpServer(mspring);
            server.start();
        }catch(Exception ex){
            Logger.getLogger(MicroSpring.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}