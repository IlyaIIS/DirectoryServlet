package servlets;

import accounts.AccountService;
import accounts.ServiceManager;
import accounts.UserProfile;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Map;

public class DownloadServlet extends HttpServlet {
    private AccountService accountService;
    @Override
    public void init() {
        accountService = ServiceManager.getAccountService();
    }
    protected void doGet(HttpServletRequest req,
                         HttpServletResponse resp) throws IOException {
        Cookie[] cookies = req.getCookies();

        String sessionId = "none";
        for(Cookie cookie: cookies) {
            if (cookie.getName().equals("sessionId")) {
                sessionId = cookie.getValue();
                break;
            }
        }

        if (sessionId.equals("none")) {
            resp.setStatus(resp.SC_UNAUTHORIZED);
            return;
        }

        UserProfile userProfile = accountService.getUserBySessionId(sessionId);

        if (userProfile == null) {
            resp.setStatus(resp.SC_UNAUTHORIZED);
            return;
        }

        Map<String, String[]> params = req.getParameterMap();
        String filePath = params.get("path")[0];
        File downloadFile = new File(filePath);

        if (!downloadFile.exists() || !downloadFile.toString().contains("/home/myServerUserFiles/"+userProfile.getLogin())) {
            resp.setStatus(resp.SC_BAD_REQUEST);
            return;
        }

        FileInputStream inStream = new FileInputStream(downloadFile);

        String mimeType = getServletContext().getMimeType(filePath);
        if (mimeType == null) {
            mimeType = "application/octet-stream";
        }
        System.out.println("MIME type: " + mimeType);

        resp.setContentType(mimeType);
        resp.setContentLength((int) downloadFile.length());

        String headerKey = "Content-Disposition";
        String headerValue = String.format("attachment; filename=\"%s\"", downloadFile.getName());
        resp.setHeader(headerKey, headerValue);

        OutputStream outStream = resp.getOutputStream();

        byte[] buffer = new byte[4096];
        int bytesRead = -1;

        while ((bytesRead = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, bytesRead);
        }

        inStream.close();
        outStream.close();
    }
}
