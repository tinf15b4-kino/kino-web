package de.tinf15b4.kino.cucumber;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.Manifest;

public class InProcJarRunner implements Runnable {
    public InProcJarRunner(URL jarfile, String[] params) {
        this.jarfile = jarfile;
        this.params = params;
    }

    private URL jarfile;
    private String[] params;

    @Override
    public void run() {
        // create a class loader for that
        URLClassLoader loader = new URLClassLoader(new URL[] { jarfile }, ClassLoader.getSystemClassLoader().getParent());

        // get the manifest
        Manifest manifest;
        try (InputStream s = loader.findResource("META-INF/MANIFEST.MF").openStream()) {
            manifest = new Manifest(s);
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse manifest", e);
        }

        // load the main class
        String mainClass = manifest.getMainAttributes().getValue("Main-Class");

        Class<?> clazz;
        try {
            clazz = loader.loadClass(mainClass);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to load main class", e);
        }

        // find and execute the main method
        try {
            clazz.getMethod("main", String[].class).invoke(null, (Object)params);
        } catch (NoSuchMethodException|IllegalAccessException|InvocationTargetException e) {
            throw new RuntimeException("Failed to start main()", e);
        }
    }
}
