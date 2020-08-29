package co.edu.escuelaing.arep.microspring;

import java.util.logging.Level;
import java.util.logging.Logger;

import co.edu.escuelaing.arep.httpserver.URIProcessor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class MicroSpring implements URIProcessor{

    Map<String, Method> webservices = new HashMap<>();

    @Override
    public void mapService(String componentName) throws Exception {
        int nMethods = 0;

        for (Method m : Class.forName(componentName).getMethods()) {
            System.out.println("Revisando metodo: " + m.getName());
            if (m.isAnnotationPresent(RequestMapping.class)) {
                System.out.println("Tiene anotacion @RequestMapping");
                RequestMapping rm = m.getAnnotation(RequestMapping.class);
                webservices.put(rm.value(), m);
                nMethods++;
            }
        }

        System.out.printf("No. of web services %d %n", nMethods);

    }

    @Override
    public String executeService(String theURI) {
        try {
            return webservices.get(theURI).invoke(null).toString();
        } catch (IllegalAccessException e) {
            Logger.getLogger(MicroSpring.class.getName()).log(Level.SEVERE, null, e);
        } catch (IllegalArgumentException e) {
            Logger.getLogger(MicroSpring.class.getName()).log(Level.SEVERE, null, e);
        } catch (InvocationTargetException e) {
            Logger.getLogger(MicroSpring.class.getName()).log(Level.SEVERE, null, e);
        }
        return "Error";
    }
}