package never;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JFrame;

public final class NoteWindows {

    private static final int C_NOTE_SIZE_VER = 200;
    private static final int C_NOTE_SIZE_HOR = 100;

    private static final Set<NoteWindow> aNoteWindows = new HashSet<>();

    public static NoteWindow newNote() {
        final NoteWindow noteWindow = new NoteWindow(new JFrame("Note"));
        aNoteWindows.add(noteWindow);
        
        noteWindow.getJFrame().setSize(C_NOTE_SIZE_HOR, C_NOTE_SIZE_VER);
        noteWindow.getJFrame().setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        noteWindow.getJFrame().addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                aNoteWindows.remove(noteWindow);
            }
        });
        noteWindow.getJFrame().setVisible(true);
        
        return noteWindow;
    }

}
