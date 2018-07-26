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
    
    private JDialog _dDialog;
    private Note _nNote = new Note();
    private JTextArea _taNote = new JTextArea(_nNote.getContents());
    private JScrollPane _spNote = new JScrollPane(_taNote);
    
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
            _nNote.setContents(_taNote.getText());
        }
    };
    
    private NoteWindow(JDialog dialog) {
        this(UUID.randomUUID(), dialog);
    }
    
    private NoteWindow(UUID uuid, JDialog dialog) {
        _uuidUUID = uuid;
        _dDialog = dialog;
    }
    
    public void init() {
        _dDialog.setSize(C_N_SIZE_HOR, C_N_SIZE_VER);
        _dDialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        _taNote.setLineWrap(true);
        _dDialog.getContentPane().add(_spNote, null);
        _dDialog.setVisible(true);
        _taNote.getDocument().addDocumentListener(dlNoteUpdateListener);
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
        return _dDialog;
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
    
    public static final Set<NoteWindow> setnwNoteWindows = new HashSet<NoteWindow>();

    public static NoteWindow newNote() {
        final NoteWindow nw = new NoteWindow(new JDialog(NeverForget.c_fMain, ""));
        nw.init();
        nw._dDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                setnwNoteWindows.remove(nw);
            }
        });
        setnwNoteWindows.add(nw);
        return nw;
    }
}