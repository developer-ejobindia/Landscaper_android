package com.seazoned.landscaper.model;

/**
 * Created by root on 3/2/18.
 */

public class User {
    private String userId;
    private String userToken;
    private String userFastName;
    private String userLastName;
    private String userLoginType;

    public User(String userId, String userToken, String userFastName, String userLastName, String userLoginType) {
        this.userId = userId;
        this.userToken = userToken;
        this.userFastName = userFastName;
        this.userLastName = userLastName;
        this.userLoginType = userLoginType;
    }

    public String getUserLoginType() {
        return userLoginType;
    }

    public void setUserLogimType(String userLoginType) {
        this.userLoginType = userLoginType;
    }

    public String getUserFastName() {
        return userFastName;
    }

    public void setUserFastName(String userFastName) {
        this.userFastName = userFastName;
    }

    public String getUserLastName() {
        return userLastName;
    }

    public void setUserLastName(String userLastName) {
        this.userLastName = userLastName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }
}
