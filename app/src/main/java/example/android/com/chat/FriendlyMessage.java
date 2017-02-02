package example.android.com.chat;

/**
 * Created by Honey on 01-Feb-17.
 */

public class FriendlyMessage {

    private String text;
    private String name;
    private String photoUrl;

    public FriendlyMessage(String text, String name) {
        this.text = text;
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}