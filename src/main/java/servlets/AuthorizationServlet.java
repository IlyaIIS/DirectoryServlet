package servlets;

import accounts.AccountService;
import accounts.ServiceManager;
import accounts.UserProfile;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

public class AuthorizationServlet extends HttpServlet {
    private AccountService accountService;

    @Override
    public void init() {
        accountService = ServiceManager.getAccountService();
    }

    @Override
    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) throws ServletException, IOException {
        String[] urlParts = request.getRequestURI().split("/");
        String urlLastPart = urlParts[urlParts.length-1];

        if (urlParts.length == 4) {
            if ((urlLastPart.equals("registration") || urlLastPart.equals("login"))) {
                response.setStatus(HttpServletResponse.SC_OK);
                getServletContext().getRequestDispatcher("/" + urlLastPart + ".html").forward(request, response);
            } else if ((urlLastPart.equals("delete"))) {
                logOut(request, response);
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        } else if (urlParts.length == 3) {
            response.setStatus(HttpServletResponse.SC_OK);
            getServletContext().getRequestDispatcher("/login.html").forward(request, response);
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    //log in and registration
    @Override
    public void doPost(HttpServletRequest request,
                       HttpServletResponse response) throws IOException {
        String email = request.getParameter("email");
        String login = request.getParameter("login");
        String pass = request.getParameter("pass");
        boolean fromRegistration = request.getRequestURI().split("/")[3].equals("registration");

        response.setContentType("text/html;charset=utf-8");

        if (login == null || pass == null || (fromRegistration && email == null)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        UserProfile profile = accountService.getUserByLogin(login);
        if (fromRegistration) {
            if (profile == null) {
                accountService.addNewUser(new UserProfile(login, pass, email));
                profile = accountService.getUserByLogin(login);
                File userDir = new File("/home/myServerUserFiles/"+login);
                userDir.mkdir();
            }
            else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
        }

        if (profile == null || !profile.getPass().equals(pass)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        Cookie cookie = new Cookie("sessionId", request.getSession().getId());
        cookie.setMaxAge(-1);
        cookie.setPath(request.getContextPath());
        response.addCookie(cookie);

        accountService.addSession(request.getSession().getId(), profile);

        String a = request.getContextPath();

        response.sendRedirect(request.getContextPath() + "/file?path=/home/myServerUserFiles/"+login);
    }

    private void logOut(HttpServletRequest request,
                         HttpServletResponse response) throws IOException {
        String sessionId = request.getSession().getId();
        UserProfile profile = accountService.getUserBySessionId(sessionId);
        if (profile == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        } else {
            accountService.deleteSession(sessionId);

            Cookie delCookie = null;
            for(Cookie cookie: request.getCookies()) {
                if (cookie.getName().equals("sessionId")) {
                    delCookie = cookie;
                    break;
                }
            }
            if (delCookie != null) {
                delCookie.setMaxAge(0);
                response.addCookie(delCookie);
            }
            response.sendRedirect(request.getContextPath() + "/authorization/login");
        }
    }
}
