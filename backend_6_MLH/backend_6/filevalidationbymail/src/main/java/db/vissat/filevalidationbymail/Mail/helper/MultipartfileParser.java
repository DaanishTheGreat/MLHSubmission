package db.vissat.filevalidationbymail.Mail.helper;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import db.vissat.filevalidationbymail.Data.DataHandlerJ;
import db.vissat.filevalidationbymail.Data.Log;
import db.vissat.filevalidationbymail.VerifyFormat.VerifyFormat;

public class MultipartfileParser {
    
    private MultipartFile MultipartFileObject;
    private String FileTypeAndSubtype;
    private DataHandlerJ DataHandlerJObject;
    private Log LogObject;
    
    public MultipartfileParser(MultipartFile MultiPartFileObject, DataHandlerJ DataHandlerJObject, Log LogObject)
    {
        this.MultipartFileObject = MultiPartFileObject;
        this.DataHandlerJObject = DataHandlerJObject;
        this.LogObject = LogObject;

        FileTypeAndSubtype = GetFileTypeAndSubtype();
    }

    public String GetFileTypeAndSubtype()
    {
        String FileName = MultipartFileObject.getOriginalFilename();
        List<String> SubtypeValues = DataHandlerJObject.GetFileSubtypes();

        for(String ExistingSubtype : SubtypeValues)
        {
            if(FileName.contains(ExistingSubtype))
            {
                return DataHandlerJObject.GetFileTypeFromSubtype(ExistingSubtype);
            }
        }

        return "";
    }

    private String InvalidFormat = "";
    public boolean VerifyFormat()
    {
        VerifyFormat VerifyFormatObject = new VerifyFormat(FileTypeAndSubtype, MultipartFileObject, DataHandlerJObject);
        String VerifiedData = VerifyFormatObject.GetVerifyData();

        boolean Verified = false;

        InvalidFormat = VerifiedData;

        if(VerifiedData.equals(":%"))
        {
            VerifiedData = "";
        }

        if(VerifiedData.equals(""))
        {
            System.out.println("File Verified");
            LogObject.AppendToLog("File Name: " + MultipartFileObject.getOriginalFilename() + " File Results: " + "Valid Format");
            Verified = true;
        }
        else
        {
            System.out.println("File Invalid: " + VerifiedData);
            LogObject.AppendToLog("File Name: " + MultipartFileObject.getOriginalFilename() + " File Results: " + VerifiedData);
            Verified = false;
        }

        return Verified;
    }

    public String GetInvalidFormat()
    {
        return InvalidFormat;
    }

    public String StoreFile()
    {
        if(!FileTypeAndSubtype.equals(""))
        {
            String FilePath = DataHandlerJObject.StoreFile(FileTypeAndSubtype, MultipartFileObject);
            System.out.println("Stored Valid File");
            return FilePath;
        }
        else
        {
            System.out.println("File does not exist, file not being stored");
            return "";
        }
    }

}
