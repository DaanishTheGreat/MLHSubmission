package db.vissat.filevalidationbymail.Data;

import java.util.List;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import static com.mongodb.client.model.Filters.eq;

public class MongoDBHandler {
    private String ApplicationPropertiesPath;
    private String OrganizationName;

    private String ConnectionString;
    private String DatabaseName;
    private String CollectionName;
    
    private String FileFormatPath;

    private String CommonDatabase;
    private String CommonCollection;


    public MongoDBHandler(String ApplicationPropertiesPath, String OrganizationName, String ConnectionString, String DatabaseName, String CollectionName, String FileFormatPath, String CommonDatabase, String CommonCollection)
    {
        this.ApplicationPropertiesPath = ApplicationPropertiesPath;
        this.OrganizationName = OrganizationName;
        this.ConnectionString = ConnectionString;
        this.DatabaseName = DatabaseName;
        this.CollectionName = CollectionName;
        this.FileFormatPath = FileFormatPath;

        this.CommonDatabase = CommonDatabase;
        this.CommonCollection = CommonCollection;
    }

    public static void InitializeMongoDBCommon(String CommonApplicationPropertiesPath, String CommonConnectionString, String CommonDatabase, String CommonCollection)
    {
        LoadApplicationPropertiesFromMongoDB(CommonApplicationPropertiesPath, CommonConnectionString, CommonDatabase, CommonCollection);
    }

    private static void LoadApplicationPropertiesFromMongoDB(String CommonApplicationPropertiesPath, String CommonConnectionString, String CommonDatabase, String CommonCollection)
    {
        try(MongoClient MongoClientObject = MongoClients.create(CommonConnectionString))
        {
            MongoDatabase MongoDatabaseObject = MongoClientObject.getDatabase(CommonDatabase);
            MongoCollection<Document> MongoCollectionObject = MongoDatabaseObject.getCollection(CommonCollection);

            Bson Filter = new Document("_id", "ApplicationProperties");
            Document DocumentObject = MongoCollectionObject.find(Filter).first();

            if(DocumentObject != null)
            {
                ObjectMapper ObjectMapperObject = new ObjectMapper();
                ObjectWriter ObjectWriterObject = ObjectMapperObject.writerWithDefaultPrettyPrinter();
                String JsonString = DocumentObject.toJson();

                ObjectWriterObject.writeValue(new File(CommonApplicationPropertiesPath), ObjectMapperObject.readTree(JsonString));

                System.out.println("Local ApplicationProperties Downloaded Successfully");
            }
            else
            {
                UploadApplicationPropertiesToMongoDB(CommonApplicationPropertiesPath, CommonConnectionString, CommonDatabase, CommonCollection);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    private static void UploadApplicationPropertiesToMongoDB(String CommonApplicationPropertiesPath, String CommonConnectionString, String CommonDatabase, String CommonCollection)
    {   
        try(MongoClient MongoClientObject = MongoClients.create(CommonConnectionString))
        {
            MongoDatabase MongoDatabaseObject = MongoClientObject.getDatabase(CommonDatabase);
            MongoCollection<Document> MongoCollectionObject = MongoDatabaseObject.getCollection(CommonCollection);

            ObjectMapper ObjectMapperObject = new ObjectMapper();
            JsonNode RootNode = ObjectMapperObject.readTree(new File(CommonApplicationPropertiesPath));

            Document DocumentObject = Document.parse(RootNode.toString());

            MongoCollectionObject.insertOne(DocumentObject);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public void UploadApplicationPropertiesScheduled() throws IOException
    {
        ObjectMapper ObjectMapperObject = new ObjectMapper();
        JsonNode RootNode = ObjectMapperObject.readTree(new File(ApplicationPropertiesPath));

        Document NewDocument = Document.parse(ObjectMapperObject.writeValueAsString(RootNode));

        try(MongoClient MongoClientObject = MongoClients.create(ConnectionString))
        {
            MongoDatabase MongoDatabaseObject = MongoClientObject.getDatabase(CommonDatabase);
            MongoCollection<Document> Collection = MongoDatabaseObject.getCollection(CommonCollection);
            Collection.replaceOne(eq("_id", "ApplicationProperties"), NewDocument);
        }
    }

    public void UploadFileFormatScheduled() throws IOException
    {
        ObjectMapper ObjectMapperObject = new ObjectMapper();
        JsonNode RootNode = ObjectMapperObject.readTree(new File(FileFormatPath));
        String FileFormatName = Paths.get(FileFormatPath).getFileName().toString();

        Document NewDocument = Document.parse(ObjectMapperObject.writeValueAsString(RootNode));

        try(MongoClient MongoClientObject = MongoClients.create(ConnectionString))
        {
            MongoDatabase MongoDatabaseObject = MongoClientObject.getDatabase(DatabaseName);
            MongoCollection<Document> Collection = MongoDatabaseObject.getCollection(CollectionName);
            Collection.replaceOne(eq("_id", FileFormatName), NewDocument);
        }
    }

    public void LoadFileFormatsFromMongoDB()
    {
        File FileFormatFile = new File(FileFormatPath);
        String FileName = FileFormatFile.getName();
        
        try(MongoClient MongoClientObject = MongoClients.create(ConnectionString))
        {
            MongoDatabase MongoDatabaseObject = MongoClientObject.getDatabase(DatabaseName);

            MongoCollection<Document> Collections = MongoDatabaseObject.getCollection(CollectionName);

            Bson filter = new Document("_id", FileName);
            Document DocumentObject = Collections.find(filter).first();

            if(DocumentObject != null)
            {
                DocumentObject.remove("_id");

                ObjectMapper ObjectMapperObject = new ObjectMapper();
                ObjectWriter ObjectWriterObject = ObjectMapperObject.writerWithDefaultPrettyPrinter();
                JsonNode RootNode = ObjectMapperObject.readTree(DocumentObject.toJson());

                ObjectWriterObject.writeValue(new File(FileFormatPath), RootNode);
                System.out.println("Downloaded File Format from MongoDB for: " + FileName);
            }
            else
            {
                UploadFileFormatToMongoDB(FileName);
                System.out.println("Uploaded File Format to MongoDB of File Name: " + FileName);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    private void UploadFileFormatToMongoDB(String FileName)
    {
        try(MongoClient MongoClientObject = MongoClients.create(ConnectionString))
        {
            MongoDatabase MongoDatabaseObject = MongoClientObject.getDatabase(DatabaseName);
            MongoCollection<Document> MongoCollectionObject = MongoDatabaseObject.getCollection(CollectionName);

            ObjectMapper ObjectMapperObject = new ObjectMapper();
            JsonNode RootNode = ObjectMapperObject.readTree(new File(FileFormatPath));

            Document DocumentObject = Document.parse(RootNode.toString());
            DocumentObject.append("_id", FileName);

            MongoCollectionObject.insertOne(DocumentObject);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public void UpdateFileUploadDetails(String Path, String FileName, String FileType, String OrgName)
    {
        String status = "Uploaded";
        String createdt = "";
        String createdby = "filevalidation";
        String moved = "na";

        Instant InstantNow = Instant.now();
        DateTimeFormatter DateTimeFormatterObject = DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ss.SSS'Z'");
        ZonedDateTime ESTTime = InstantNow.atZone(ZoneId.of("America/New_York"));
        createdt = DateTimeFormatterObject.format(ESTTime);

        try(MongoClient MongoClientObject = MongoClients.create(ConnectionString))
        {
            MongoDatabase MongoDatabaseObject = MongoClientObject.getDatabase(DatabaseName);
            MongoCollection<Document> Collections = MongoDatabaseObject.getCollection(CollectionName);

            Document DocumentToUpload = new Document("path", Path).append("filename", FileName)
            .append("filetype", FileType).append("orgname", OrgName).append("status", status)
            .append("createdt", createdt).append("createdby", createdby).append("moved", moved);

            Collections.insertOne(DocumentToUpload);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
        
}