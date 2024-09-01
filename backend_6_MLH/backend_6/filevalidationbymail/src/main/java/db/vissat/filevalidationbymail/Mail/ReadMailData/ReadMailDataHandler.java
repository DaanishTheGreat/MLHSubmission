package db.vissat.filevalidationbymail.Mail.ReadMailData;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class ReadMailDataHandler {
    
    private static String ReadMailDataPath = ".\\filevalidationbymail\\src\\main\\java\\db\\vissat\\filevalidationbymail\\Mail\\ReadMailData\\ReadMailData.txt";

    // Returns true if MessageID exists in ReadMailData.txt else returns false and appends MessageID to the txt file
    public static boolean CheckMessageIDExistsToProcess(String MessageID)
    {
        boolean MessageIDExists = false;
        try(Scanner ScannerObject = new Scanner(new File(ReadMailDataPath)))
        {
            while(ScannerObject.hasNextLine())
            {
                String Row = ScannerObject.nextLine();

                if(Row.contains(MessageID))
                {
                    MessageIDExists = true;
                }
            }
        }
        catch(FileNotFoundException e)
        {
            e.printStackTrace();
        }

        if(MessageIDExists == false)
        {
            AppendToReadMailData(MessageID);
        }

        return MessageIDExists;
    }

    private static void AppendToReadMailData(String MessageID)
    {
        LocalDateTime LocalDateTimeObject = LocalDateTime.now();
        DateTimeFormatter DateTimeFormatterObject = DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm");
        String DateTimeString = DateTimeFormatterObject.format(LocalDateTimeObject);
        String FormattedReadMailData = DateTimeString + " " + MessageID + "\n";
        try {
            FileWriter FileWriterObject = new FileWriter(ReadMailDataPath, true);
            FileWriterObject.write(FormattedReadMailData);
            FileWriterObject.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
