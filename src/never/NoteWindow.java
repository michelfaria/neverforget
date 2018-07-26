package never;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class NoteWindow {
    
    private static final int C_NOTE_SIZE_VER = 200;
    private static final int C_NOTE_SIZE_HOR = 100;

    private UUID _uuid;
    
    private JDialog _dialog;
    private Note _note = new Note();
    private JTextArea _textAreaNote = new JTextArea(_note.getContents());
    private JScrollPane _scrollNote = new JScrollPane(_textAreaNote);
    
    private DocumentListener onTextAreaNoteUpdate = new DocumentListener() {
        @Override
        public void removeUpdate(DocumentEvent e) {
        }
        
        @Override
        public void insertUpdate(DocumentEvent e) {
        }
        
        @Override
        public void changedUpdate(DocumentEvent e) {
        }
    };
    
    private NoteWindow(JDialog dialog) {
        this(UUID.randomUUID(), dialog);
    }
    
    private NoteWindow(UUID uuid, JDialog dialog) {
        _uuid = uuid;
        _dialog = dialog;
    }
    
    public void init() {
        _dialog.setSize(C_NOTE_SIZE_HOR, C_NOTE_SIZE_VER);
        _dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        _textAreaNote.setLineWrap(true);
        _dialog.getContentPane().add(_scrollNote, null);
        _dialog.setVisible(true);
        _textAreaNote.getDocument().addDocumentListener(onTextAreaNoteUpdate);
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((_uuid == null) ? 0 : _uuid.hashCode());
        return result;
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
        if (_uuid == null) {
            if (other._uuid != null)
                return false;
        } else if (!_uuid.equals(other._uuid))
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
        noteWindow.init();
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