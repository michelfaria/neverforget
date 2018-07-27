package never;

public class Note {

    private String _strContents;

    public Note() {
        _strContents = "";
    }
    
    public Note(String contents) {
        _strContents = contents;
    }

    public String getContents() {
        return _strContents;
    }

    public void setContents(String contents) {
        _strContents = contents;
    }
}
