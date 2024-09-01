package db.vissat.filevalidationbymail.Frontend.ExternalApiAccess;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.io.File;
import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import db.vissat.filevalidationbymail.FilevalidationbymailApplication;
import db.vissat.filevalidationbymail.Data.DataHandlerJ;
import db.vissat.filevalidationbymail.Data.GroupEmails.GroupEmailsHandler;
import db.vissat.filevalidationbymail.Frontend.Models.FileConfigData;
import db.vissat.filevalidationbymail.Frontend.Models.InputData;

@RestController
@RequestMapping("/external_api")
@CrossOrigin(origins = "http://localhost:3000")
public class ExternalApiAccessController {

    @GetMapping("/get_organizations")
    public List<String> GetOrganizationNames() {
        List<String> organizations = new ArrayList<>();
        for (String organization : FilevalidationbymailApplication.MainObjectsDictionary.keySet()) {
            organizations.add(organization);
        }
        return organizations;
    }

    @GetMapping("/get_groupemailnames")
    public List<String> GetGroupEmailNames()
    {
        List<String> GroupEmailNames = GroupEmailsHandler.GetGroupEmailNames();
        return GroupEmailNames;
    }

    @GetMapping("/get_filetypes")
    public List<String> GetFileTypes()
    {
        List<String> FileTypes = new ArrayList<>();

        for(String Organizations : FilevalidationbymailApplication.MainObjectsDictionary.keySet())
        {
            List<String> FileSubtypes = FilevalidationbymailApplication.MainObjectsDictionary.get(Organizations).GetDataHandlerJ().GetFileSubtypes();
            for(String FileSubtype : FileSubtypes)
            {
                String FileType = FilevalidationbymailApplication.MainObjectsDictionary.get(Organizations).GetDataHandlerJ().GetFileTypeFromSubtype(FileSubtype);
                String FileTypeOnly = FileType.split(":%")[0];
                if(!FileTypes.contains(FileTypeOnly))
                {
                    FileTypes.add(FileTypeOnly);
                }
            }
            
        }

        return FileTypes;
    }

    @GetMapping("/get_filesubtypes")
    public List<String> GetFileSubtypes(String Organization)
    {
        List<String> FileSubtypes = new ArrayList<>();

        DataHandlerJ DataHandlerJObject = FilevalidationbymailApplication.MainObjectsDictionary.get(Organization).GetDataHandlerJ();
        FileSubtypes = DataHandlerJObject.GetFileSubtypes();

        return FileSubtypes;
    }

    @GetMapping("/get_formats")
    public List<String> GetFormats() {
        List<String> AllOrgFormats = new ArrayList<>();
        //DataHandlerJ dataHandlerJObject = FilevalidationbymailApplication.MainObjectsDictionary.

        for(String Organizations : FilevalidationbymailApplication.MainObjectsDictionary.keySet())
        {
            AllOrgFormats.addAll(FilevalidationbymailApplication.MainObjectsDictionary.get(Organizations).GetDataHandlerJ().GetFileFormatLines());
        }

        System.out.println("Get Formats Called");
        
        return AllOrgFormats;
    }

    @PostMapping("/add_format")
    public void AddFormatting(@RequestBody InputData inputDataObject) {
        System.out.println("Organization Name: " + inputDataObject.getOrganization());
        DataHandlerJ dataHandlerJObject = FilevalidationbymailApplication.MainObjectsDictionary.get(inputDataObject.getOrganization()).GetDataHandlerJ();

        List<String> subtypeElements = inputDataObject.getFileFormatElementsList();

        String frequency = inputDataObject.getFrequency();
        String frequencyDetails = inputDataObject.getFrequencyDetails();
        List<String> emails = inputDataObject.getEmails();

        System.out.println("File Name: " + inputDataObject.getFileName() + " File Type: " + inputDataObject.getFileType());
        System.out.println("Frequency: " + frequency);
        System.out.println("Frequency Details: " + frequencyDetails);
        System.out.println("Emails: " + emails);

        dataHandlerJObject.AppendStructureToFileFormat(inputDataObject.getFileType(), inputDataObject.getFileName(), subtypeElements, frequency, frequencyDetails, emails);
    }


    //Json Editor
    @GetMapping("/get_json")
    public ResponseEntity<String> GetJson() throws IOException
    {
        
        try {
            String content = new String(Files.readAllBytes(Paths.get(FilevalidationbymailApplication.ApplicationPropertiesPath_Hyperparameter)));
            //String content = new String(Files.readAllBytes(Paths.get("C:\\Users\\daani\\Documents\\Vissat\\FileValidation_FinalSteps\\backend_6\\filevalidationbymail\\src\\main\\java\\db\\vissat\\filevalidationbymail\\Data\\FileFormats\\FileFormatHonda.json")));
            return new ResponseEntity<>(content, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>("Could not read JSON file", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    //FileStructureEdits

     @GetMapping("/companies")
    public List<String> getCompanies() {
        
        List<String> AllCompanies = new ArrayList<>();
        for(String Organization : FilevalidationbymailApplication.MainObjectsDictionary.keySet())
        {
            String FileFormatPath = FilevalidationbymailApplication.MainObjectsDictionary.get(Organization).GetDataHandlerJ().GetFileFormatPath();
            Path PathObject = Paths.get(FileFormatPath);
            String CompanyName = PathObject.getFileName().toString();
            AllCompanies.add(CompanyName);
        }
        return AllCompanies;
    }

    @GetMapping("/filetypes")
    public List<String> getFileTypes() 
    {
        List<String> FileTypes = new ArrayList<>();
        for(String Organization : FilevalidationbymailApplication.MainObjectsDictionary.keySet())
        {
            List<String> FileFormats = FilevalidationbymailApplication.MainObjectsDictionary.get(Organization).GetDataHandlerJ().GetFileFormatLines();
            for(String FileFormat : FileFormats)
            {
                String FileTypeAndSubtype = FileFormat.split(":%%")[0];
                String FileType = FileTypeAndSubtype.split(":%")[0];
                if(!FileTypes.contains(FileType))
                {
                    FileTypes.add(FileType);
                }
            }
        }
        return FileTypes;
    }

    @GetMapping("/filestructurenames")
    public List<String> getFileStructureNames()
    {
        List<String> FileStructureNames = new ArrayList<>();    
        for(String Organization : FilevalidationbymailApplication.MainObjectsDictionary.keySet())
        {
            List<String> FileFormats = FilevalidationbymailApplication.MainObjectsDictionary.get(Organization).GetDataHandlerJ().GetFileFormatLines();
            for(String FileFormat : FileFormats)
            {
                String FileTypeAndSubtype = FileFormat.split(":%%")[0];
                String FileStructureName = FileTypeAndSubtype.split(":%")[1];
                if(!FileStructureNames.contains(FileStructureName))
                {
                    FileStructureNames.add(FileStructureName);
                }
            }
        }

        return FileStructureNames;
    }

    @GetMapping("/filterdata")
    public List<JsonData> filterData(@RequestParam String company, @RequestParam String fileType, @RequestParam String fileStructureName, @RequestParam String fileFrequency, @RequestParam String mailId)
    {
        List<JsonData> FileFormatData = new ArrayList<>();
        for(String Organization : FilevalidationbymailApplication.MainObjectsDictionary.keySet())
        {
            List<String> FileFormats = FilevalidationbymailApplication.MainObjectsDictionary.get(Organization).GetDataHandlerJ().GetFileFormatLines();
            for(String FileFormat : FileFormats)
            {
                JsonData JsonDataObject = new JsonData();
                JsonDataObject.company = Paths.get(FilevalidationbymailApplication.MainObjectsDictionary.get(Organization).GetDataHandlerJ().GetFileFormatPath()).getFileName().toString();
                JsonDataObject.fileType = FileFormat.split(":%%")[0].split(":%")[0];
                JsonDataObject.fileStructureName = FileFormat.split(":%%")[0].split(":%")[1];
                String FrequencyTypeAndEmailInfo = FilevalidationbymailApplication.MainObjectsDictionary.get(Organization).GetDataHandlerJ().GetUploadFrequencyTypeFromFileTypeAndMailAndSubtype(JsonDataObject.fileType, JsonDataObject.fileStructureName);
                if(!FrequencyTypeAndEmailInfo.equals("None"))
                {
                    JsonDataObject.fileFrequency = FrequencyTypeAndEmailInfo.split(":%")[0];
                    JsonDataObject.mailId = FrequencyTypeAndEmailInfo.split(":%")[1];
                }
                else
                {
                    JsonDataObject.fileFrequency = "None";
                    JsonDataObject.mailId = "None";
                }
                FileFormatData.add(JsonDataObject);
            }
        }

        List<JsonData> FilteredFileFormats = new ArrayList<>(FileFormatData);
        int Index = 0;
        for(JsonData JsonDataObject : FileFormatData)
        {
            if(!company.equals("") && !company.equals(JsonDataObject.company))
            {
                FilteredFileFormats.remove(Index);
                continue;
            }
            if(!fileType.equals("") && !fileType.equals(JsonDataObject.fileType))
            {
                FilteredFileFormats.remove(Index);
                continue;
            }
            if(!fileStructureName.equals("") && !fileStructureName.equals(JsonDataObject.fileStructureName))
            {
                FilteredFileFormats.remove(Index);
                continue;
            }
            if(!fileFrequency.equals("") && !fileFrequency.equals(JsonDataObject.fileFrequency))
            {
                FilteredFileFormats.remove(Index);
                continue;
            }
            if(!mailId.equals("") && !JsonDataObject.mailId.contains(mailId))
            {
                FilteredFileFormats.remove(Index);
                continue;
            }
            Index++;
        }

        return FilteredFileFormats;
    }

    static class JsonData {
        public String company;
        public String fileType;
        public String fileStructureName;
        public String fileFrequency;
        public String mailId;
    }

    @GetMapping("/getlocalfileformatjson")
    public Map<String, Object> getJsonData(@RequestParam String company, @RequestParam String fileType, @RequestParam String fileStructureName, @RequestParam String fileFrequency, @RequestParam String mailId)
    {

        for(String Organization : FilevalidationbymailApplication.MainObjectsDictionary.keySet())
        {
            List<String> FileFormats = FilevalidationbymailApplication.MainObjectsDictionary.get(Organization).GetDataHandlerJ().GetFileFormatLines();
            for(String FileFormat : FileFormats)
            {
                String FileFormatPath = FilevalidationbymailApplication.MainObjectsDictionary.get(Organization).GetDataHandlerJ().GetFileFormatPath();
                
                Path PathObject = Paths.get(FileFormatPath);
                String CompanyName_FileFormatName = PathObject.getFileName().toString();

                String ThisFileType = FileFormat.split(":%%")[0].split(":%")[0];
                String ThisFileStructureName = FileFormat.split(":%%")[0].split(":%")[1];

                if(company.equals(CompanyName_FileFormatName) && ThisFileType.equals(fileType) && ThisFileStructureName.equals(fileStructureName))
                {
                    Map<String, Object> JsonData = new HashMap<>();

                    Map<String, Map<String, String>> FileStructure = new HashMap<>();

                    String[] FileFormatRows = FileFormat.split("%%")[1].split(":%");
                    for(String FileFormatRow : FileFormatRows)
                    {
                        Map<String, String> NewEntry = new HashMap<>();
                        NewEntry.put("Type", FileFormatRow.split(":")[1]);
                        NewEntry.put("Title", FileFormatRow.split(":")[2]);
                        FileStructure.put(FileFormatRow.split(":")[0], NewEntry);
                    }

                    JsonData.put(ThisFileStructureName, FileStructure);
                    return JsonData;
                }
            }
        }
        return null;
    }

    @GetMapping("/getalluploadfrequency")
    public Map<String, String> GetAllUploadFrequency(@RequestParam String company, @RequestParam String fileType, @RequestParam String FileStructureName)
    {
        Map<String, String> AllUploadFrequencyDataList = new HashMap<>();
        for(String Organization : FilevalidationbymailApplication.MainObjectsDictionary.keySet())
        {
            String FileFormatPath = FilevalidationbymailApplication.MainObjectsDictionary.get(Organization).GetDataHandlerJ().GetFileFormatPath();    
            Path PathObject = Paths.get(FileFormatPath);
            String CompanyName_FileFormatName = PathObject.getFileName().toString();


            if(CompanyName_FileFormatName.equals(company))
            {
                String AllUploadFrequencyData = FilevalidationbymailApplication.MainObjectsDictionary.get(Organization).GetDataHandlerJ().GetAllUploadFrequencyDataFromTypeAndSubtype(fileType, FileStructureName);
                if(!AllUploadFrequencyData.equals("None"))
                {
                    AllUploadFrequencyDataList.put("FrequencyType", AllUploadFrequencyData.split(":%")[0]);
                    AllUploadFrequencyDataList.put("FrequencyDetails", AllUploadFrequencyData.split(":%")[1]);
                    AllUploadFrequencyDataList.put("EmailInfo", AllUploadFrequencyData.split(":%")[2]);
                }
                else
                {
                    AllUploadFrequencyDataList.put("FrequencyType", "");
                    AllUploadFrequencyDataList.put("FrequencyDetails", "");
                    AllUploadFrequencyDataList.put("EmailInfo", "");
                }
            }
        }

        return AllUploadFrequencyDataList; //Continue from here add the frontend for frequency data
    }

    @PostMapping("/updatejsondata")
    public void UpdateJsonData(@RequestBody String JsonData)
    {
        for(String Organization : FilevalidationbymailApplication.MainObjectsDictionary.keySet())
        {
            String FileFormatPath = FilevalidationbymailApplication.MainObjectsDictionary.get(Organization).GetDataHandlerJ().GetFileFormatPath();
            Path PathObject = Paths.get(FileFormatPath);
            String CompanyName = PathObject.getFileName().toString();
            List<String> FileFormats = FilevalidationbymailApplication.MainObjectsDictionary.get(Organization).GetDataHandlerJ().GetFileFormatLines();
            try
            {
                ObjectMapper ObjectMapperObject = new ObjectMapper();
                JsonNode RootNode = ObjectMapperObject.readTree(JsonData);
                String JsonData_Company = RootNode.get("company").asText();
                String JsonData_FileType = RootNode.get("fileType").asText();
                String JsonData_fileStructureName = RootNode.get("fileStructureName").asText();
                String JsonData_FrequencyType = RootNode.get("FileData").get("UploadFrequency").get("FrequencyType").asText();
                String JsonData_FrequencyDetails = RootNode.get("FileData").get("UploadFrequency").get("FrequencyDetails").asText();
                String JsonData_EmailInfo = RootNode.get("FileData").get("UploadFrequency").get("EmailInfo").asText();
                JsonNode JsonData_RowData = RootNode.get("FileData");

                for(String FileFormat : FileFormats)
                {
                    String FileType = FileFormat.split(":%%")[0].split(":%")[0];
                    String FileSubtype_FileStructureName = FileFormat.split(":%%")[0].split(":%")[1];

                    if(CompanyName.equals(JsonData_Company) && FileType.equals(JsonData_FileType) && FileSubtype_FileStructureName.equals(JsonData_fileStructureName))
                    {
                        String DateLastUploaded = FilevalidationbymailApplication.MainObjectsDictionary.get(Organization).GetDataHandlerJ().GetAllUploadFrequencyDataFromTypeAndSubtype(JsonData_FileType, FileSubtype_FileStructureName).split(":%")[2];
                        
                        FilevalidationbymailApplication.MainObjectsDictionary.get(Organization).GetDataHandlerJ().UpdateFileSubtype(JsonData_FileType, FileSubtype_FileStructureName, JsonData_RowData, JsonData_FrequencyType, JsonData_FrequencyDetails, DateLastUploaded, JsonData_EmailInfo);
                        System.out.println("FileSubtype/FileStructure Updated: " + JsonData_fileStructureName);
                    }
                }
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    @PostMapping("/deletejsondata")
    public void DeleteJsonData(@RequestBody String DeleteJsonDataValues)
    {
        try
        {
            ObjectMapper ObjectMapperObject = new ObjectMapper();
            JsonNode RootNode = ObjectMapperObject.readTree(DeleteJsonDataValues);

            String CompanyName = RootNode.get("company").asText();
            String FileType = RootNode.get("fileType").asText();
            String FileStructureName = RootNode.get("fileStructureName").asText();

            for(String Organization : FilevalidationbymailApplication.MainObjectsDictionary.keySet())
            {
                String FileFormatPath = FilevalidationbymailApplication.MainObjectsDictionary.get(Organization).GetDataHandlerJ().GetFileFormatPath();
                Path PathObject = Paths.get(FileFormatPath);
                String FileFormatName = PathObject.getFileName().toString();

                if(CompanyName.equals(FileFormatName))
                {
                    FilevalidationbymailApplication.MainObjectsDictionary.get(Organization).GetDataHandlerJ().DeleteFileSubtype(FileType, FileStructureName);
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    //Config Organization Create
    @PostMapping("/organization-configuration")
public ResponseEntity<String> saveOrganizationConfiguration(@RequestBody FileConfigData formData) {
    try {
        ObjectMapper objectMapper = new ObjectMapper();
        // Read the existing JSON file
        File jsonFile = new File(FilevalidationbymailApplication.ApplicationPropertiesPath_Hyperparameter);
        JsonNode rootNode = objectMapper.readTree(jsonFile);

        // Check if the Organization node exists, if not, create it
        String organizationName = formData.getOrganizationName();
        if (organizationName == null || organizationName.isEmpty()) {
            return new ResponseEntity<>("Organization Name is required", HttpStatus.BAD_REQUEST);
        }

        JsonNode organizationNode = rootNode.path(organizationName);
        ObjectNode actualOrganizationNode;

        if (organizationNode.isMissingNode() || !organizationNode.isObject()) {
            actualOrganizationNode = objectMapper.createObjectNode();
            ((ObjectNode) rootNode).set(organizationName, actualOrganizationNode);
        } else {
            actualOrganizationNode = (ObjectNode) organizationNode;
        }

        // Safely access or create the OneDrive node
        JsonNode oneDriveNode = actualOrganizationNode.path("OneDrive");
        ObjectNode actualOneDriveNode;
        if (oneDriveNode.isMissingNode() || !oneDriveNode.isObject()) {
            actualOneDriveNode = objectMapper.createObjectNode();
            actualOrganizationNode.set("OneDrive", actualOneDriveNode);
        } else {
            actualOneDriveNode = (ObjectNode) oneDriveNode;
        }

        actualOneDriveNode.put("onedrive.client-id", formData.getOnedriveClientId());
        actualOneDriveNode.put("onedrive.client-secret", formData.getOnedriveClientSecret());
        actualOneDriveNode.put("onedrive.redirect-uri", formData.getOnedriveRedirectUri());
        actualOneDriveNode.put("onedrive.refresh-token", formData.getOnedriveRefreshToken());
        actualOneDriveNode.put("onedrive.upload-path", formData.getOnedriveUploadPath());

        // Safely access or create the Email node
        JsonNode emailNode = actualOrganizationNode.path("Email");
        ObjectNode actualEmailNode;
        if (emailNode.isMissingNode() || !emailNode.isObject()) {
            actualEmailNode = objectMapper.createObjectNode();
            actualOrganizationNode.set("Email", actualEmailNode);
        } else {
            actualEmailNode = (ObjectNode) emailNode;
        }

        actualEmailNode.put("Host", formData.getEmailHost());
        actualEmailNode.put("Email", formData.getEmail());
        actualEmailNode.put("Password", formData.getEmailPassword());
        actualEmailNode.put("SubjectFilter", formData.getEmailSubjectFilter());
        actualEmailNode.put("ImapProtocol", formData.getEmailImapProtocol());
        actualEmailNode.put("SmtpHost", formData.getEmailSmtpHost());
        actualEmailNode.put("SmtpPort", formData.getEmailSmtpPort());

        // Safely access or create the Directory node
        JsonNode directoryNode = actualOrganizationNode.path("Directory");
        ObjectNode actualDirectoryNode;
        if (directoryNode.isMissingNode() || !directoryNode.isObject()) {
            actualDirectoryNode = objectMapper.createObjectNode();
            actualOrganizationNode.set("Directory", actualDirectoryNode);
        } else {
            actualDirectoryNode = (ObjectNode) directoryNode;
        }

        actualDirectoryNode.put("ParentDirectoryStructure", formData.getDirectoryParentStructure());
        actualDirectoryNode.put("ParentDirectory", formData.getDirectoryParent());
        actualDirectoryNode.put("LogDirectory", formData.getDirectoryLog());
        actualDirectoryNode.put("FileFormatDirectory", formData.getDirectoryFileFormat());

        // Safely access or create the MongoDB node
        JsonNode mongoNode = actualOrganizationNode.path("MongoDB");
        ObjectNode actualMongoNode;
        if (mongoNode.isMissingNode() || !mongoNode.isObject()) {
            actualMongoNode = objectMapper.createObjectNode();
            actualOrganizationNode.set("MongoDB", actualMongoNode);
        } else {
            actualMongoNode = (ObjectNode) mongoNode;
        }

        actualMongoNode.put("OrganizationName", formData.getMongoOrganizationName());
        actualMongoNode.put("ConnectionString", formData.getMongoConnectionString());
        actualMongoNode.put("DatabaseName", formData.getMongoDatabaseName());
        actualMongoNode.put("CollectionName", formData.getMongoCollectionName());

        // Write the updated JSON back to the file
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(jsonFile, rootNode);

        return new ResponseEntity<>("Configuration saved successfully!", HttpStatus.OK);
    } catch (IOException e) {
        e.printStackTrace();
        return new ResponseEntity<>("Failed to save configuration.", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

}
