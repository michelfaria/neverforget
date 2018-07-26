package never;

public class Note {
    private String m_title;
    private String m_contents;

    public Note() {
        m_title = "";
        m_contents = "";
    }
    
    public Note(String title, String contents) {
        m_title = title;
        m_contents = contents;
    }

    public String getTitle() {
        return m_title;
    }

    public void setTitle(String title) {
        m_title = title;
    }

    public String getContents() {
        return m_contents;
    }

    public void setContents(String contents) {
        m_contents = contents;
    }
}
