package jm;

import jm.model.User;

public interface PluginService<T> {

    void setToken(String code, String login);
    T create(String login);
    String buildUrl();
    String getToken(User user);
    String refreshToken(User user);
}
