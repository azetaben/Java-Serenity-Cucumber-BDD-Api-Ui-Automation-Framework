package saucedemo.com.models;

import saucedemo.com.enums.User;

public record LoginAccount(String username, String password) {
    public static LoginAccount fromAlias(String alias) {
        User user = User.fromAlias(alias);
        return new LoginAccount(user.getStandardUser(), user.getPassword());
    }
}
