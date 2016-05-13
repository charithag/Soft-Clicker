package org.softclicker.client.gui.startup;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.softclicker.client.gui.MainWindow;
import org.softclicker.client.transport.TransportManager;

/**
 * Created by Admin on 5/6/2016.
 */
public class AppLoader {
    private final static Logger log = LogManager.getLogger(AppLoader.class);

    public static void main(String[] args) {

    MainWindow app = MainWindow.getInstance();
    TransportManager tm = TransportManager.getInstance();

    app.main(args);
    log.info("SoftClicker UI Finished!");
    }

}
