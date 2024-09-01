package db.vissat.filevalidationbymail.Data;

import db.vissat.filevalidationbymail.Data.Sharepoint.DirectoryCompressionHandler;
import db.vissat.filevalidationbymail.Data.Sharepoint.SharepointHandler;
import db.vissat.filevalidationbymail.Mail.MailHandler;

public class CombinedMainObjects {
    private MailHandler MailHandlerObject;
    private DataHandlerJ DataHandlerJObject;
    private Log LogObject;
    private DirectoryCompressionHandler DirectoryCompressionHandlerObject;
    private SharepointHandler SharepointHandlerObject;
    private MongoDBHandler MongoDBHandlerObject;

    public CombinedMainObjects(MailHandler MailHandlerObject, DataHandlerJ DataHandlerJObject, Log LogObject, DirectoryCompressionHandler DirectoryCompressionHandlerObject, SharepointHandler SharepointHandlerObject, MongoDBHandler MongoDBHandlerObject)
    {
        this.MailHandlerObject = MailHandlerObject;
        this.DataHandlerJObject = DataHandlerJObject;
        this.LogObject = LogObject;
        this.DirectoryCompressionHandlerObject = DirectoryCompressionHandlerObject;
        this.SharepointHandlerObject = SharepointHandlerObject;
        this.MongoDBHandlerObject = MongoDBHandlerObject;
    }

    public MailHandler GetMailHandler()
    {
        return MailHandlerObject;
    }
    
    public DataHandlerJ GetDataHandlerJ()
    {
        return DataHandlerJObject;
    }
    
    public Log GetLogObject()
    {
        return LogObject;
    }

    public DirectoryCompressionHandler GetDirectoryCompressionHandler()
    {
        return DirectoryCompressionHandlerObject;
    }

    public SharepointHandler GetSharepointHandler()
    {
        return SharepointHandlerObject;
    }

    public MongoDBHandler GetMongoDBHandler()
    {
        return MongoDBHandlerObject;
    }
}
