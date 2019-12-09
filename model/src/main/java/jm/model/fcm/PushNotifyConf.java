package jm.model.fcm;

public class PushNotifyConf {
    private String title;
    private String body;
    private String icon;
    private String click_action;
    private String ttlInSeconds;

    public PushNotifyConf(String title, String body, String icon, String click_action, String ttlInSeconds) {
        this.title = title;
        this.body = body;
        this.icon = icon;
        this.click_action = click_action;
        this.ttlInSeconds = ttlInSeconds;
    }

    public PushNotifyConf() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getClick_action() {
        return click_action;
    }

    public void setClick_action(String click_action) {
        this.click_action = click_action;
    }

    public String getTtlInSeconds() {
        return ttlInSeconds;
    }

    public void setTtlInSeconds(String ttlInSeconds) {
        this.ttlInSeconds = ttlInSeconds;
    }
}
