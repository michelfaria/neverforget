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

    public static final Set<NoteWindow> setnwinNoteWindows = new HashSet<NoteWindow>();
    
    public static final int C_I_SIZE_VER = 200;
    public static final int C_I_SIZE_HOR = 300;
    public static final int C_I_START_POS_X = 200;
    public static final int C_I_START_POS_Y = 200;

    private UUID _uuidUUID;

    private JDialog _dlgDialog;
    private Note _nNote = new Note();
    private JTextArea _txtaNote = new JTextArea(_nNote.getContents());
    private JScrollPane _scrpnlNote = new JScrollPane(_txtaNote);
    private boolean _bContentsUnsaved = true;

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
            _bContentsUnsaved = true;
        }
    };
    
    public NoteWindow() {
        this(new JDialog(NeverForget.c_frmMain, ""));
    }
    
    public NoteWindow(JDialog dialog) {
        this(UUID.randomUUID(), dialog);
    }

    public NoteWindow(UUID uuidUUID, JDialog dlgDialog) {
        _uuidUUID = uuidUUID;
        _dlgDialog = dlgDialog;
        _dlgDialog.setLocation(C_I_START_POS_X, C_I_START_POS_Y);
    }
    
    public void init() {
        _dlgDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                setnwinNoteWindows.remove(NoteWindow.this);
            }
        });
        setnwinNoteWindows.add(this);
        
        _dlgDialog.setSize(C_I_SIZE_HOR, C_I_SIZE_VER);
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
    
    public void setUUID(UUID uuidUUID) {
        _uuidUUID = uuidUUID;
    }

    public int getPosX() {
        return _dlgDialog.getX();
    }

    public void setDlgDialogBounds(int iX, int iY, int iWidth, int iHeight) {
        _dlgDialog.setBounds(iX, iY, iWidth, iHeight);
    }
    
    public void setnNoteContents(String strContent) {
        _nNote.setContents(strContent);
        _txtaNote.setText(strContent);
    }
    
    public int getPosY() {
        return _dlgDialog.getY();
    }

    public int getWidth() {
        return _dlgDialog.getWidth();
    }

    public int getHeight() {
        return _dlgDialog.getHeight();
    }

    public boolean isContentsUnsaved() {
        return _bContentsUnsaved;
    }

    public void setContentsUnsaved(boolean bContentsUnsaved) {
        _bContentsUnsaved = bContentsUnsaved;
    }
    
    
}