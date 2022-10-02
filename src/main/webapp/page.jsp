<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>ServerDirectory.com</title>
</head>
<body>
    <p>Date and time: ${date}</p>
    <b>Path: ${path}</b></p>
    <% if (((String)request.getAttribute("path")).length() != 3) {
        %><a href=${serverPath}${prePath}>Back<a><%}
    %>
    <table style="border: 2px solid black;">
    <tr><td>File</td><td>Size</td><td>Date</td></tr>
    <%
        for (org.example.FileInfo folder: (org.example.FileInfo[])request.getAttribute("folders")) {%>
            <tr><td>D <a href=${serverPath}${path}\<%out.print(folder.Name);%>><%out.print(folder.Name);%></td><td> </td><td><%out.print(folder.CreationDate);%></td></tr>
        <%}
    %>
    <%
        for (org.example.FileInfo folder: (org.example.FileInfo[])request.getAttribute("files")) {%>
            <tr><td>F <a href=${serverPath}${path}\<%out.print(folder.Name);%> download><%out.print(folder.Name);%></td><td><%out.print(folder.Size);%> B</td><td><%out.print(folder.CreationDate);%></td></tr>
        <%}
    %>
    </table>

</body>
</html>