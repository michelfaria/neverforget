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

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

public final class NeverForget {

    // Program Version
    public static final String c_strVersion = "0.0.0";

    // Tray Icon (GIF)
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

    // Tray pop-up menu
    public static PopupMenu c_popupMenu = null;
    
    // Main JFrame
    public static final JFrame c_frameMain = new JFrame("NeverForget");

    public static void main(String[] args) {
        // Check for graphical environment
        if (GraphicsEnvironment.isHeadless()) {
            System.err.println("Sorry, NeverForget needs a graphical environment to run. Exiting.");
            return;
        }

        // Make the Swing UI look good
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Could not change look and feel, resorting to default look: " + e);
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
        final MenuItem newNoteItem = newNoteMenuItem();
        final MenuItem aboutItem = aboutTrayMenuItem();
        final MenuItem exitItem = quitTrayMenuItem();

        // Add components to Pop-up menu
        c_popupMenu.add(newNoteItem);
        c_popupMenu.addSeparator();
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

    private static MenuItem newNoteMenuItem() {
        final MenuItem menuItem = new MenuItem("New Note");
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                NoteWindow.newNote();
            }
        });
        return menuItem;
    }

    public static MenuItem aboutTrayMenuItem() {
        final MenuItem menuItem = new MenuItem("About");
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null,
                        "NeverForget " + c_strVersion + " by Michel (https://github.com/michelfaria)", "About",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });
        return menuItem;
    }

    public static MenuItem quitTrayMenuItem() {
        final MenuItem menuItem = new MenuItem("Quit");
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exit();
            }
        });
        return menuItem;
    }
    
    public static void exit() {
        System.out.println("NeverForget is now exiting. Thank you for using NeverForget!");
        System.exit(0);
    }
    
    private NeverForget() {}
}
