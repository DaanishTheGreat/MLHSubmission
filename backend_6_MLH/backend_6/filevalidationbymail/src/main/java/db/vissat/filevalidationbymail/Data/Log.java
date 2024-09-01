package db.vissat.filevalidationbymail.Data;

import java.io.FileWriter;
import java.io.IOException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Log {

    public String LogPath;

    public Log(String LogPath)
    {
        this.LogPath = LogPath;
    }

    public void AppendToLog(String LogData)
    {
        LocalDateTime LocalDateTimeObject = LocalDateTime.now();
        DateTimeFormatter DateTimeFormatterObject = DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm");
        String DateTimeString = DateTimeFormatterObject.format(LocalDateTimeObject);
        String FormattedLogData = DateTimeString + " " + LogData + "\n";
        try {
            FileWriter FileWriterObject = new FileWriter(LogPath, true);
            FileWriterObject.write(FormattedLogData);
            FileWriterObject.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
