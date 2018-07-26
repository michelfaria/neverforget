package never;

import java.awt.AWTException;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;

import javax.swing.JOptionPane;

public class NeverForget {

    public static final Image c_trayImage;
    static {
        try {
            final URL urlResource = NeverForget.class.getClassLoader().getResource("NeverForget.gif");
            final File file = new File(urlResource.toURI());
            c_trayImage = IO.readImageFromFile(file);
        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public static PopupMenu c_popupMenu = null;

    public static void main(String[] args) {
        // Check for graphical environment
        if (GraphicsEnvironment.isHeadless()) {
            System.err.println("Sorry, NeverForget needs a graphical environment to run. Exiting.");
            return;
        }

        // Check support for System Tray
        if (!SystemTray.isSupported()) {
            JOptionPane.showMessageDialog(null, "Sorry, NeverForget needs a system tray to work. Exiting.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        System.out.println("NeverForget is now running.");

        // Make system tray icon
        final SystemTray systemTray = SystemTray.getSystemTray();
        final TrayIcon trayIcon = new TrayIcon(c_trayImage);

        c_popupMenu = new PopupMenu();

        // Create Pop-up menu components
        MenuItem aboutItem = new MenuItem("About");
        MenuItem exitItem = exitItem();

        // Add components to Pop-up menu
        c_popupMenu.add(aboutItem);
        c_popupMenu.add(exitItem);

        // Attribute the Pop-up menu to the tray icon
        trayIcon.setPopupMenu(c_popupMenu);

        // Put tray icon in the system tray
        try {
            systemTray.add(trayIcon);
        } catch (AWTException e) {
            JOptionPane.showMessageDialog(null, "NeverForget failed to load. Error: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
    }
    
    public static MenuItem exitItem() {
        final MenuItem menuItem = new MenuItem("Quit");
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        return menuItem;
    }
}
