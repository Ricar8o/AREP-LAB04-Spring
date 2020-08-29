package co.edu.escuelaing.arep.httpserver;

public interface URIProcessor {
    public void mapService(String command) throws Exception;

     public String executeService(String theURI);
}