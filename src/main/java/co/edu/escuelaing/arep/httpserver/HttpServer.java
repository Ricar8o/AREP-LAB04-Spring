package co.edu.escuelaing.arep.httpserver;

import java.net.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.*;

public class HttpServer {
    private int port = 36000;
    private boolean running = false;
    private String[] imagenes = { "jpg", "png", "gif", "svg" };
    private String[] textos = { "html", "js", "css", "txt" };
    private URIProcessor up;

    public HttpServer() {

    }

    public HttpServer(int port) {
        this.port = port;
    }

    public HttpServer(URIProcessor uri) {
        this.up = uri;
    }

    public void start() {
        try {
            ServerSocket serverSocket = null;
            try {
                serverSocket = new ServerSocket(port);
            } catch (IOException e) {
                System.err.println("Could not listen on port: " + port);
                System.exit(1);
            }
            running = true;
            while (running) {
                Socket clientSocket = null;
                try {
                    System.out.println("Listo para recibir ...");
                    clientSocket = serverSocket.accept();
                } catch (IOException e) {
                    System.err.println("Accept failed.");
                    System.exit(1);
                }
                processRequest(clientSocket);

                clientSocket.close();
            }
            serverSocket.close();
        } catch (IOException e) {
            Logger.getLogger(HttpServer.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void processRequest(Socket clientSocket) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        String inputLine;
        Map<String, String> request = new HashMap<String, String>();
        boolean requestReady = false;
        while ((inputLine = in.readLine()) != null) {
            if (!requestReady) {
                request.put("requestLine", inputLine);
                requestReady = true;
            } else {
                String[] linea = inputLine.split(":");
                if (linea.length > 1) {
                    request.put(linea[0], linea[1]);
                }
            }

            if (!in.ready()) {
                break;
            }
        }
        OutputStream out = clientSocket.getOutputStream();
        generateResponse(out, request);
        in.close();
    }

    public void generateResponse(OutputStream out, Map<String, String> request) throws IOException {
        String outputLine;
        outputLine = generateResponseNotFound();
        boolean notFound = true;
        if (request.get("requestLine") != null) {
            String[] requestLine = request.get("requestLine").split(" ");
            System.out.println(requestLine[1].substring(0, 5));
            PrintWriter printWriter = new PrintWriter(out, true);
            if (requestLine[1].contains(".")) {
                byte contenido[] = getResource(requestLine[1]);
                if (contenido != null) {
                    notFound = false;
                    outputLine = "HTTP/1.1 200 OK\r\n" + generateTypeHeader(requestLine[1]);
                    printWriter.println(outputLine);
                    out.write(contenido);

                }
            } else {
                try {
                    URI uri = new URI(requestLine[1]);
                    if (uri.getPath().startsWith("/Apps")){
                        System.out.println("HOLAAA " + uri.getPath().substring(5));
                        getAppResponse(uri.getPath().substring(5), printWriter);
                        notFound = false;
                    }
                } catch (URISyntaxException e) {
                    Logger.getLogger(HttpServer.class.getName()).log(Level.SEVERE, null, e);
                }
                
            }
        }
        if (notFound){
            PrintWriter printWriter = new PrintWriter(out,true);
            printWriter.println(outputLine);
        }
        out.close();
    }

    private String generateResponseNotFound() {
        String out = "HTTP/1.1 404 OK\r\n" 
                    + "Content-Type: text/html\r\n" + "\r\n" 
                    + "<!DOCTYPE html>\n"
                    + "<html>\n" + "<head>\n" + "<meta charset=\"UTF-8\">\n" + "<title>404</title>\n"
                    + "</head>\n" 
                    + "<body>\n" 
                    + "<h1>404 Not Found</h1>\n" 
                    + "</body>\n" 
                    + "</html>\n";
        return out;
    }

    private String generateTypeHeader(String req) {
        
        String header = "";
        String[] lista = req.split("\\.");
        String tipo = lista[lista.length-1];
        if( Arrays.asList(imagenes).contains(tipo) ){
             header += "Content-Type: image/" + tipo +"\r\n";
        }else if (Arrays.asList(textos).contains(tipo)){
             header += "Content-Type: text/" + tipo +"\r\n" ;
        }
        
        return header;
    }

    private byte[] getResource(String path) throws IOException {
        String dir = "src/main/resources" +  path;
        File f = new File(dir);
        byte contenido[] = new byte[(int)f.length()];
        if (f.exists() && f.isFile() ){
            FileInputStream ficheroStream = new FileInputStream(f);
            ficheroStream.read(contenido);
            ficheroStream.close();
        }else{
            contenido = null;
        }
        System.out.println(contenido);
        return contenido;

    }

    private void getAppResponse(String theuri, PrintWriter out) {

        String response = up.executeService(theuri);

        String header = "HTTP/1.1 200 OK\r\n"

                + "Content-Type: text/html\r\n"

                + "\r\n";

        out.println(header + response);

    }
}