package never;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class NoteWindow {
    
    private static final int C_N_SIZE_VER = 200;
    private static final int C_N_SIZE_HOR = 100;

    private UUID _uuidUUID;
    
    private JDialog _dlgDialog;
    private Note _nNote = new Note();
    private JTextArea _txtaNote = new JTextArea(_nNote.getContents());
    private JScrollPane _scrpnlNote = new JScrollPane(_txtaNote);
    
    private DocumentListener dlNoteUpdateListener = new DocumentListener() {
        @Override
        public void changedUpdate(DocumentEvent e) {
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            updateContents();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            updateContents();
        }
        
        void updateContents() {
            _nNote.setContents(_txtaNote.getText());
        }
    };
    
    private NoteWindow(JDialog dialog) {
        this(UUID.randomUUID(), dialog);
    }
    
    private NoteWindow(UUID uuidUUID, JDialog dlgDialog) {
        _uuidUUID = uuidUUID;
        _dlgDialog = dlgDialog;
    }
    
    public void init() {
        _dlgDialog.setSize(C_N_SIZE_HOR, C_N_SIZE_VER);
        _dlgDialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        _txtaNote.setLineWrap(true);
        _dlgDialog.getContentPane().add(_scrpnlNote, null);
        _dlgDialog.setVisible(true);
        _txtaNote.getDocument().addDocumentListener(dlNoteUpdateListener);
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((_uuidUUID == null) ? 0 : _uuidUUID.hashCode());
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
        if (_uuidUUID == null) {
            if (other._uuidUUID != null)
                return false;
        } else if (!_uuidUUID.equals(other._uuidUUID))
            return false;
        return true;
    }

    public JDialog getDialog() {
        return _dlgDialog;
    }
    
    public Note getNote() {
        return _nNote;
    }
    
    public UUID getUUID() {
        return _uuidUUID;
    }
    
    /*
     * NoteWindow Management
     */
    
    public static final Set<NoteWindow> setnwinNoteWindows = new HashSet<NoteWindow>();

    public static NoteWindow newNote() {
        final NoteWindow nwin = new NoteWindow(new JDialog(NeverForget.c_frmMain, ""));
        nwin.init();
        nwin._dlgDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                setnwinNoteWindows.remove(nwin);
            }
        });
        setnwinNoteWindows.add(nwin);
        return nwin;
    }
}