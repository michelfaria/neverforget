package never;

public class Note {
    private String _contents;

    public Note() {
        _contents = "";
    }
    
    public Note(String contents) {
        _contents = contents;
    }

    public String getContents() {
        return _contents;
    }

    public void setContents(String contents) {
        _contents = contents;
    }
}
