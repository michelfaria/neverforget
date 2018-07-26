package never;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class NoteWindow {
    
    private static final int C_NOTE_SIZE_VER = 200;
    private static final int C_NOTE_SIZE_HOR = 100;
    
    private static int c_uid = 0; // Window uniqueness

    private final int _uid = c_uid;
    {
        c_uid++;
    }
    private JDialog _dialog;
    private Note _note = new Note();
    private JTextArea _textAreaNote = new JTextArea(_note.getContents());
    private JScrollPane _scrollNote = new JScrollPane(_textAreaNote);

    private NoteWindow(JDialog dialog) {
        _dialog = dialog;
    }
    
    public void setDefaults() {
        _dialog.setSize(C_NOTE_SIZE_HOR, C_NOTE_SIZE_VER);
        _dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        _textAreaNote.setLineWrap(true);
        _dialog.getContentPane().add(_scrollNote, null);
        _dialog.setVisible(true);
    }

    @Override
    public int hashCode() {
        return c_uid;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        NoteWindow other = (NoteWindow) obj;
        if (_uid != other._uid)
            return false;
        return true;
    }
    
    public JDialog getDialog() {
        return _dialog;
    }
    
    public Note getNote() {
        return _note;
    }
    
    /*
     * NoteWindow Management
     */
    
    private static final Set<NoteWindow> aNoteWindows = new HashSet<>();

    public static NoteWindow newNote() {
        final NoteWindow noteWindow = new NoteWindow(new JDialog(NeverForget.c_frameMain, ""));
        noteWindow.setDefaults();
        noteWindow._dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                aNoteWindows.remove(noteWindow);
            }
        });
        aNoteWindows.add(noteWindow);
        return noteWindow;
    }
}