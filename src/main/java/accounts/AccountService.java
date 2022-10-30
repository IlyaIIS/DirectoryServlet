package accounts;

import database.DBInitListener;
import org.hibernate.*;
import org.hibernate.criterion.Restrictions;
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
            Session session = DBInitListener.getNewSession();
            Transaction transaction = session.beginTransaction();
            long id = (Long)session.save(userProfile);
            transaction.commit();
            session.close();
        } catch (HibernateException e) {
            e.printStackTrace();
        }
    }

    public UserProfile getUserByLogin(String login) {
        try {
            Session session = DBInitListener.getNewSession();
            Criteria criteria = session.createCriteria(UserProfile.class);
            UserProfile profile = ((UserProfile) criteria.add(Restrictions.eq("login", login)).uniqueResult());
            if (profile != null) {
                long id = profile.getId();
                UserProfile dataset = (UserProfile) session.get(UserProfile.class, id);
                session.close();
                return dataset;
            } else {
                return null;
            }
        } catch (HibernateException e) {
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