package servlets;

import accounts.AccountService;
import accounts.ServiceManager;
import accounts.UserProfile;
import org.example.FileInfo;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

@WebServlet("/file/*")
public class MainServlet extends HttpServlet {
    private AccountService accountService;
    private final String serverPath = "http://localhost:8080/server/file?path=";
    private final String downloadServerPath = "http://localhost:8080/server/download/file?path=";
    @Override
    public void init() {
        accountService = ServiceManager.getAccountService();
    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        PrintWriter writer = resp.getWriter();
        Cookie[] cookies = req.getCookies();

        String sessionId = "none";
        for(Cookie cookie: cookies) {
            if (cookie.getName().equals("sessionId")) {
                sessionId = cookie.getValue();
                break;
            }
        }

        if (sessionId.equals("none")) {
            writer.write("Error: wrong cookie");
            return;
        }

        UserProfile userProfile = accountService.getUserBySessionId(sessionId);

        if (userProfile == null) {
            writer.write("Error: user not found");
            return;
        }

        Map<String, String[]> params = req.getParameterMap();
        File path = new File(params.get("path")[0]);

        if (!path.exists() || !path.toString().contains("C:\\myServerUserFiles\\"+userProfile.getLogin())) {
            writer.write("Error: wrong directory");
            return;
        }

        String prePath = path.getParent();

        String date = new SimpleDateFormat("yyyy.MM.dd 'at' HH:mm:ss z").format(new Date());
        FileInfo[] folders = getFolders(path);
        FileInfo[] files = getFiles(path);

        req.setAttribute("login", userProfile.getLogin());
        req.setAttribute("date", date);
        req.setAttribute("serverPath", serverPath);
        req.setAttribute("downloadServerPath", downloadServerPath);
        req.setAttribute("path", path.toString());
        req.setAttribute("prePath", prePath);
        req.setAttribute("folders", folders);
        req.setAttribute("files", files);

        getServletContext().getRequestDispatcher("/page.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        super.doPost(req, resp);
    }

    public FileInfo[] getFiles(File folder) throws IOException {
        ArrayList<FileInfo> files = new ArrayList<>();
        File[] allFiles = folder.listFiles();

        for(File file : allFiles) {
            if(!file.isDirectory()) {
                BasicFileAttributes attributes = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
                files.add(new FileInfo(file.getName(), new SimpleDateFormat("yyyy.MM.dd 'at' HH:mm:ss z").format(Date.from(attributes.creationTime().toInstant())), attributes.size()));
            }
        }

        return files.toArray(new FileInfo[0]);
    }

    public FileInfo[] getFolders(File folder) throws IOException {
        File[] files = folder.listFiles();
        ArrayList<FileInfo> folders = new ArrayList<>();
        for(File file : files){
            if(file.isDirectory()){
                BasicFileAttributes attributes = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
                folders.add(new FileInfo(file.getName(), new SimpleDateFormat("yyyy.MM.dd 'at' HH:mm:ss z").format(Date.from(attributes.creationTime().toInstant())), attributes.size()));
            }
        }

        return folders.toArray(new FileInfo[0]);
    }
}

