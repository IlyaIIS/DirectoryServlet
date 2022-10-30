package accounts;

import database.DBInitListener;
import database.QueryExecutor;
import servlets.MainServlet;

import java.util.HashMap;
import java.util.Map;

public class AccountService {
    private final Map<String, UserProfile> sessionIdToProfile;

    public AccountService() {
        sessionIdToProfile = new HashMap<>();
    }

    public void addNewUser(UserProfile userProfile) {
        try {
            DBInitListener.queryExecutor.execUpdate(String.format("INSERT users2(login, pass, email) VALUES ('%s', '%s', '%s');", userProfile.getLogin(), userProfile.getPass(), userProfile.getEmail()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public UserProfile getUserByLogin(String login) {
        try {
            return DBInitListener.queryExecutor.execQuery("SELECT * FROM users2 WHERE login = '" + login + "';", result -> {
                if (result.next()) {
                    return new UserProfile(result.getString(1),
                            result.getString(2),
                            result.getString(3));
                }
                else {
                    return null;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public UserProfile getUserBySessionId(String sessionId) {
        return sessionIdToProfile.get(sessionId);
    }

    public void addSession(String sessionId, UserProfile userProfile) {
        sessionIdToProfile.put(sessionId, userProfile);
    }

    public void deleteSession(String sessionId) {
        sessionIdToProfile.remove(sessionId);
    }
}