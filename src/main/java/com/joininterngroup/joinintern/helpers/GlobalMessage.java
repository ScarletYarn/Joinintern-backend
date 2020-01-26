package com.joininterngroup.joinintern.helpers;

public class GlobalMessage {

    private String type;

    private UserEssential userEssential;

    private Integer onlineCount;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public UserEssential getUserEssential() {
        return userEssential;
    }

    public void setUserEssential(UserEssential userEssential) {
        this.userEssential = userEssential;
    }

    public Integer getOnlineCount() {
        return onlineCount;
    }

    public void setOnlineCount(Integer onlineCount) {
        this.onlineCount = onlineCount;
    }
}
