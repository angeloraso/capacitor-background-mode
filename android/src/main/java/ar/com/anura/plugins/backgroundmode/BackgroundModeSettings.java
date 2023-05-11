package ar.com.anura.plugins.backgroundmode;

public class BackgroundModeSettings {

    private String title = "App is on background mode";
    private String text = "App will start faster";
    private String subText = "";
    private boolean bigText = false;
    private boolean resume = true;
    private boolean silent = false;
    private boolean hidden = true;
    private String color = "157f1f";
    private String icon = "icon";
    private String channelName = "anuradev-capacitor-background-mode";
    private String channelDescription = "Anuradev Capacitor background mode notification";
    private boolean allowClose = false;
    private String closeIcon = "close-icon";
    private String closeTitle = "Close";
    private boolean showWhen = true;
    private Visibility visibility = Visibility.PUBLIC;
    private boolean disableWebViewOptimization = false;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSubText() {
        return subText;
    }

    public void setSubText(String subText) {
        this.subText = subText;
    }

    public boolean getBigText() {
        return bigText;
    }

    public void setBigText(boolean bigText) {
        this.bigText = bigText;
    }

    public boolean getResume() {
        return resume;
    }

    public void setResume(boolean resume) {
        this.resume = resume;
    }

    public boolean getSilent() {
        return silent;
    }

    public void setSilent(boolean silent) {
        this.silent = silent;
    }

    public boolean getHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getChannelDescription() {
        return channelDescription;
    }

    public void setChannelDescription(String channelDescription) {
        this.channelDescription = channelDescription;
    }

    public boolean getAllowClose() {
        return allowClose;
    }

    public void setAllowClose(boolean allowClose) {
        this.allowClose = allowClose;
    }

    public String getCloseIcon() {
        return closeIcon;
    }

    public void setCloseIcon(String closeIcon) {
        this.closeIcon = closeIcon;
    }

    public String getCloseTitle() {
        return closeTitle;
    }

    public void setCloseTitle(String closeTitle) {
        this.closeTitle = closeTitle;
    }

    public boolean getShowWhen() {
        return showWhen;
    }

    public void setShowWhen(boolean showWhen) {
        this.showWhen = showWhen;
    }

    public Visibility getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        if (visibility != null) {
            this.visibility = Visibility.valueOf(visibility);
        }
    }

    public boolean isDisableWebViewOptimization() {
        return disableWebViewOptimization;
    }

    public void setDisableWebViewOptimization(boolean disableWebViewOptimization) {
        this.disableWebViewOptimization = disableWebViewOptimization;
    }
}
