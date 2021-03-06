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
import java.util.List;

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
    public static PopupMenu c_popmPopupMenu = null;
    public static TrayIcon c_tricTrayIcon = null;

    // Main JFrame
    public static final JFrame c_frmMain = new JFrame("NeverForget");

    public static Thread c_thdSave = null;

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
        c_tricTrayIcon = new TrayIcon(c_imgTrayImage);

        c_popmPopupMenu = new PopupMenu();

        // Create Pop-up menu components
        final MenuItem miNewNote = newNoteMI();
        final MenuItem miSave = saveTrayMI();
        //final MenuItem miLoad = loadTrayMI();
        final MenuItem miAbout = aboutTrayMI();
        final MenuItem miExit = quitTrayMI();

        // Add components to Pop-up menu
        c_popmPopupMenu.add(miNewNote);
        c_popmPopupMenu.add(miSave);
        //c_popmPopupMenu.add(miLoad);
        c_popmPopupMenu.addSeparator();
        c_popmPopupMenu.add(miAbout);
        c_popmPopupMenu.add(miExit);

        // Attribute the Pop-up menu to the tray icon
        c_tricTrayIcon.setPopupMenu(c_popmPopupMenu);

        // Put tray icon in the system tray
        try {
            stSystemTray.add(c_tricTrayIcon);
        } catch (AWTException e) {
            JOptionPane.showMessageDialog(null, "NeverForget failed to load. Error: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        load();
        beginSaveJob();
    }

    private static MenuItem newNoteMI() {
        final MenuItem mi = new MenuItem("New Note");
        mi.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new NoteWindow().init();
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

    public static MenuItem loadTrayMI() {
        final MenuItem mi = new MenuItem("Load");
        mi.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                load();
            }
        });
        return mi;
    }

    public static void save() {
        save(true);
    }
    
    public static void save(boolean bShowSuccessMessage) {
        synchronized (IO.leSaveErrors) {
            NoteSaveStatus nssStatus = IO.saveAllNotes();
            if (nssStatus.equals(NoteSaveStatus.FAIL_NO_SAVE_DIR)) {
                c_tricTrayIcon.displayMessage("Save Fail",
                        "Your notes could not be saved because an error occurred while creating the Notes folder",
                        TrayIcon.MessageType.ERROR);
            } else if (nssStatus.equals(NoteSaveStatus.FAIL_WRITE_ERROR)) {
                c_tricTrayIcon.displayMessage("Save Fail",
                        "Your notes could not be saved because an error occurred while writing them to disk.",
                        TrayIcon.MessageType.ERROR);
                for (Exception e : IO.leSaveErrors) {
                    e.printStackTrace();
                }
            } else {
                assert nssStatus.equals(NoteSaveStatus.SUCCESS);
                if (bShowSuccessMessage) {
                    c_tricTrayIcon.displayMessage("Saved", "Your notes were saved.", TrayIcon.MessageType.INFO);
                }
            }
        }
    }

    public static void load() {
        synchronized (IO.leLoadNotesErrors) {
            List<NoteWindow> lnwNotes = IO.loadSavedNotes();
            if (IO.leLoadNotesErrors.size() > 0) {
                c_tricTrayIcon.displayMessage("Load Fail", "One or more notes failed to load.",
                        TrayIcon.MessageType.ERROR);
            }
            for (NoteWindow nw : lnwNotes) {
                nw.init();
            }
        }
    }

    public static void exit() {
        System.out.println("NeverForget is now exiting. Thank you for using NeverForget!");
        System.exit(0);
    }

    public static boolean beginSaveJob() {
        if (c_thdSave != null) {
            return false;
        }
        c_thdSave = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(10 * 1000);
                    } catch (InterruptedException e) {
                        System.err.println("Autosave thread interrupted");
                    }
                    save(false);
                }
            }
        });
        c_thdSave.start();
        return true;
    }

    private NeverForget() {
    }
}
