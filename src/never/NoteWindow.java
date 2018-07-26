package never;

import javax.swing.JFrame;

public class NoteWindow {
    private static int uid = 0; // Window uniqueness

    private final int m_uid = uid;
    {
        uid++;
    }
    private JFrame m_jframe;
    private Note m_note = new Note();

    public NoteWindow(JFrame jframe) {
        m_jframe = jframe;
    }

    @Override
    public int hashCode() {
        return uid;
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
        if (m_uid != other.m_uid)
            return false;
        return true;
    }
    
    public JFrame getJFrame() {
        return m_jframe;
    }
    
    public Note getNote() {
        return m_note;
    }
}