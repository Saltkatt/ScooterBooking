/*
 * Serializable is the conversion of the state of an object into a byte stream.
 * SlackMessage initializes SlackMessageBuilder and generates getters and setters for the String parameters.
 */

import java.io.Serializable;

public class SlackMessage implements Serializable {

    private String channel;
    private String username;
    private String text;
    private String icon_emoji;

    //Builder Initialization
    public static SlackMessageBuilder builder() {
        return new SlackMessageBuilder();
    }

    //Getters & Setters
    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getIcon_emoji() {
        return icon_emoji;
    }

    public void setIcon_emoji(String icon_emoji) {
        this.icon_emoji = icon_emoji;
    }


}
