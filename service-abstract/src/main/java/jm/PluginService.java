package jm;

public interface PluginService<T> {

    void setToken(String code, String login);
    T create(String login);
}
