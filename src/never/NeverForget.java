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
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import never.IO.NoteSaveStatus;

public final class NeverForget {

    // Program Version
    public static final String C_STR_VERSION = "0.0.0";
    
    // Other constants
    public static final File C_F_SAVE_DIR = new File(System.getProperty("user.home") + "/My Notes");

    // Tray Icon (GIF)
    public static final Image c_imgTrayImage;
    static {
        try {
            final InputStream is = NeverForget.class.getClassLoader().getResourceAsStream("NeverForget.gif");
            c_imgTrayImage = ImageIO.read(is);
        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    // Tray pop-up menu
    public static PopupMenu c_pmPopupMenu = null;
    public static TrayIcon c_tiTrayIcon = null;
    
    // Main JFrame
    public static final JFrame c_fMain = new JFrame("NeverForget");

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
        final SystemTray stSystemTray = SystemTray.getSystemTray();
        c_tiTrayIcon = new TrayIcon(c_imgTrayImage);

        c_pmPopupMenu = new PopupMenu();

        // Create Pop-up menu components
        final MenuItem miNewNote = newNoteMI();
        final MenuItem miSave = saveTrayMI();
        final MenuItem miAbout = aboutTrayMI();
        final MenuItem miExit = quitTrayMI();

        // Add components to Pop-up menu
        c_pmPopupMenu.add(miNewNote);
        c_pmPopupMenu.add(miSave);
        c_pmPopupMenu.addSeparator();
        c_pmPopupMenu.add(miAbout);
        c_pmPopupMenu.add(miExit);

        // Attribute the Pop-up menu to the tray icon
        c_tiTrayIcon.setPopupMenu(c_pmPopupMenu);

        // Put tray icon in the system tray
        try {
            stSystemTray.add(c_tiTrayIcon);
        } catch (AWTException e) {
            JOptionPane.showMessageDialog(null, "NeverForget failed to load. Error: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
    }

    private static MenuItem newNoteMI() {
        final MenuItem mi = new MenuItem("New Note");
        mi.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                NoteWindow.newNote();
            }
        });
        return mi;
    }

    public static MenuItem aboutTrayMI() {
        final MenuItem mi = new MenuItem("About");
        mi.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null,
                        "NeverForget " + C_STR_VERSION + " by Michel (https://github.com/michelfaria)", "About",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });
        return mi;
    }

    public static MenuItem quitTrayMI() {
        final MenuItem mi = new MenuItem("Quit");
        mi.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exit();
            }
        });
        return mi;
    }
    
    public static MenuItem saveTrayMI() {
        final MenuItem mi = new MenuItem("Save");
        mi.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                save();
            }
        });
        return mi;
    }
    
    public static void save() {
        NoteSaveStatus nssStatus = IO.saveAllNotes();
        if (nssStatus.equals(NoteSaveStatus.FAIL_NO_SAVE_DIR)) {
            c_tiTrayIcon.displayMessage("Save Fail", "Your notes could not be saved because an error occurred while creating the Notes folder", TrayIcon.MessageType.ERROR);
        } else if (nssStatus.equals(NoteSaveStatus.FAIL_WRITE_ERROR)) {
            c_tiTrayIcon.displayMessage("Save Fail", "Your notes could not be saved because an error occurred while writing them to disk.", TrayIcon.MessageType.ERROR);
        } else {
            assert nssStatus.equals(NoteSaveStatus.SUCCESS);
            c_tiTrayIcon.displayMessage("Saved", "Your notes were saved.", TrayIcon.MessageType.INFO);
        }
    }
    
    public static void exit() {
        System.out.println("NeverForget is now exiting. Thank you for using NeverForget!");
        System.exit(0);
    }
    
    private NeverForget() {}
}
