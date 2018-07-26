package never;

public class Note {
    private String _title;
    private String _contents;

    public Note() {
        _title = "";
        _contents = "";
    }
    
    public Note(String title, String contents) {
        _title = title;
        _contents = contents;
    }

    public String getTitle() {
        return _title;
    }

    public void setTitle(String title) {
        _title = title;
    }

    public String getContents() {
        return _contents;
    }

    public void setContents(String contents) {
        _contents = contents;
    }
}
