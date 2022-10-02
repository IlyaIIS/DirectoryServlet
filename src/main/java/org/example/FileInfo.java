package org.example;

public class FileInfo {
    public final String Name;
    public final String CreationDate;
    public final long Size;

    public FileInfo(String name, String creationDate, long size) {
        Name = name;
        CreationDate = creationDate;
        Size = size;
    }
}
