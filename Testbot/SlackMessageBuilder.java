/*
 * SlackMessageBuilder uses the Builder pattern in java. It reduces the number of parameters required for a constructor
   or method invocation via custom types and parameter objects.
 */

public class SlackMessageBuilder {
    private String channel;
    private String username;
    private String text;
    private String icon_emoji;


    public SlackMessageBuilder() {

    }

    public SlackMessageBuilder(SlackMessage message) {
        this.channel = message.getChannel();
        this.username = message.getUsername();
        this.text = message.getText();
        this.icon_emoji = message.getIcon_emoji();

    }


    public SlackMessageBuilder channel(String channel) {
        this.channel = channel;
        return this;
    }


    public SlackMessageBuilder username(String username) {
        this.username = username;
        return this;
    }

    public SlackMessageBuilder text(String text) {
        this.text = text;
        return this;
    }

    public SlackMessageBuilder icon_emoji(String icon_emoji) {
        this.icon_emoji = icon_emoji;
        return this;
    }

    public SlackMessage build() {
        SlackMessage message = new SlackMessage();
        message.setChannel(channel);
        message.setUsername(username);
        message.setText(text);
        message.setIcon_emoji(icon_emoji);
        return message;
    }


}
