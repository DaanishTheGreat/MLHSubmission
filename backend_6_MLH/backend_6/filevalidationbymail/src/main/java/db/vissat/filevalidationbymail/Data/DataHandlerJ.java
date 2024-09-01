package db.vissat.filevalidationbymail.Data;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bson.json.JsonObject;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class DataHandlerJ {
    private String ParentDirectory_Hyperparameter;
    private String FileFormatPath_Hyperparameter;
    private String ParentDirectoryStructure;
    private String OrganizationName;

    public DataHandlerJ(String ParentDir, String FileFormatPath, String ParentDirectoryStructure, String OrganizationName) {
        ParentDirectory_Hyperparameter = ParentDir;
        FileFormatPath_Hyperparameter = FileFormatPath;
        this.ParentDirectoryStructure = ParentDirectoryStructure;
        this.OrganizationName = OrganizationName;
    }

    public String GetFileFormatPath()
    {
        return FileFormatPath_Hyperparameter;
    }

    public void CreateVerifyFileFormatDirectory(String ParentDirectoryStructure, String OrganizationName)
    {
        if(ParentDirectoryStructure.equalsIgnoreCase("Company"))
        {
            CreateVerifyFileFormatDirectory_CompanyStructure(OrganizationName);
        }
        else if(ParentDirectoryStructure.equalsIgnoreCase("Generic"))
        {
            CreateVerifyFileFormatDirectory_GenericStructure();
        }
        else
        {
            throw new java.lang.Error("Invalid ParentDirectoryStructure Parameter");
        }
    }

    private void CreateVerifyFileFormatDirectory_CompanyStructure(String OrganizationName)
    {
        List<String> OptDirectory = GetDirectories(ParentDirectory_Hyperparameter);
        String OptDirectoryPath = ParentDirectory_Hyperparameter + "opt";
        if(!OptDirectory.contains("opt"))
        {
            CreateDirectory(ParentDirectory_Hyperparameter, "opt");
        }
        List<String> AppDirectory = GetDirectories(OptDirectoryPath);
        String AppDirectoryPath = OptDirectoryPath + "\\app"; 
        if(!AppDirectory.contains("app"))
        {
            CreateDirectory(OptDirectoryPath, "app");
        }
        List<String> FintoolDirectory = GetDirectories(AppDirectoryPath);
        String FintoolDirectoryPath = AppDirectoryPath + "\\fintool";
        if(!FintoolDirectory.contains("fintool"))
        {
            CreateDirectory(AppDirectoryPath, "fintool");
        }
        List<String> InboundDirectory = GetDirectories(FintoolDirectoryPath);
        String InboundDirectoryPath = FintoolDirectoryPath + "\\inbound";
        if(!InboundDirectory.contains("inbound"))
        {
            CreateDirectory(FintoolDirectoryPath, "inbound");
        }
        List<String> OrganizationDirectory = GetDirectories(InboundDirectoryPath);
        String OrganizationDirectoryPath = InboundDirectoryPath + "\\" + OrganizationName;
        if(!OrganizationDirectory.contains(OrganizationName))
        {
            CreateDirectory(InboundDirectoryPath, OrganizationName);
        }
        List<String> VerifiedDirectory = GetDirectories(OrganizationDirectoryPath);
        String VerifiedDirectoryPath = OrganizationDirectoryPath + "\\verified";
        if(!VerifiedDirectory.contains("verified"))
        {
            CreateDirectory(OrganizationDirectoryPath, "verified");
        }
        
        ObjectMapper ObjectMapperObject = new ObjectMapper();
        try
        {
            JsonNode RootNode = ObjectMapperObject.readTree(new File(FileFormatPath_Hyperparameter));

            Iterator<Map.Entry<String, JsonNode>> FileTypes = RootNode.fields();

            while(FileTypes.hasNext())
            {
                Map.Entry<String, JsonNode> FileTypeData = FileTypes.next();
                JsonNode FileSubtypesNode = FileTypeData.getValue();

                List<String> SubtypeDirectory = GetDirectories(VerifiedDirectoryPath);

                Iterator<Map.Entry<String, JsonNode>> FileSubtypes = FileSubtypesNode.fields();
                while(FileSubtypes.hasNext())
                {
                    Map.Entry<String, JsonNode> FileSubtypesData = FileSubtypes.next();
                    String FileSubtypeName = FileSubtypesData.getKey();
                    if(!SubtypeDirectory.contains(FileSubtypeName))
                    {
                        CreateDirectory(VerifiedDirectoryPath, FileSubtypeName);
                    }
                }
                
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        
    }

    private void CreateVerifyFileFormatDirectory_GenericStructure() {
        ObjectMapper ObjectMapperObject = new ObjectMapper();

        try {
            JsonNode RootNode = ObjectMapperObject.readTree(new File(FileFormatPath_Hyperparameter));

            Iterator<Map.Entry<String, JsonNode>> FileTypes = RootNode.fields();

            while (FileTypes.hasNext()) {
                Map.Entry<String, JsonNode> FileTypeData = FileTypes.next();
                String FileTypeName = FileTypeData.getKey();
                JsonNode FileSubtypesNode = FileTypeData.getValue();

                List<String> TypeDirectories = GetDirectories(ParentDirectory_Hyperparameter);
                
                if(!TypeDirectories.contains(FileTypeName))
                {
                    CreateDirectory(ParentDirectory_Hyperparameter, FileTypeName);
                }

                
                Iterator<Map.Entry<String, JsonNode>> FileSubtypes = FileSubtypesNode.fields();
                while(FileSubtypes.hasNext())
                {
                    Map.Entry<String, JsonNode> FileSubtypesData = FileSubtypes.next();
                    String FileSubtypeName = FileSubtypesData.getKey();
                    //JsonNode FileElementsNode = FileSubtypesData.getValue();
                    String TypePath = ParentDirectory_Hyperparameter + FileTypeName;
                    List<String> SubtypeDirectories = GetDirectories(TypePath);

                    if(!SubtypeDirectories.contains(FileSubtypeName))
                    {
                        CreateDirectory(TypePath, FileSubtypeName);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<String> GetDirectories(String path) {
        List<String> directories = new ArrayList<>();
        Path dirPath = Paths.get(path);

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dirPath)) {
            for (Path entry : stream) {
                if (Files.isDirectory(entry)) {
                    directories.add(entry.getFileName().toString());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return directories;
    }

    private boolean CreateDirectory(String path, String directoryName) {
        Path dirPath = Paths.get(path, directoryName);

        try {
            Files.createDirectory(dirPath);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<String> GetFileSubtypes()
    {
        List<String> FileSubtypesResult = new ArrayList<>();
        ObjectMapper ObjectMapperObject = new ObjectMapper();

        try {
            JsonNode RootNode = ObjectMapperObject.readTree(new File(FileFormatPath_Hyperparameter));

            Iterator<Map.Entry<String, JsonNode>> FileTypes = RootNode.fields();

            while (FileTypes.hasNext()) {
                Map.Entry<String, JsonNode> FileTypeData = FileTypes.next();
                JsonNode FileSubtypesNode = FileTypeData.getValue();
                
                Iterator<Map.Entry<String, JsonNode>> FileSubtypes = FileSubtypesNode.fields();
                while(FileSubtypes.hasNext())
                {
                    Map.Entry<String, JsonNode> FileSubtypesData = FileSubtypes.next();
                    String FileSubtypeName = FileSubtypesData.getKey();
                    FileSubtypesResult.add(FileSubtypeName);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return FileSubtypesResult;
    }

    public String GetFileTypeFromSubtype(String SubTypeVal)
    {
        ObjectMapper ObjectMapperObject = new ObjectMapper();

        try {
            JsonNode RootNode = ObjectMapperObject.readTree(new File(FileFormatPath_Hyperparameter));

            Iterator<Map.Entry<String, JsonNode>> FileTypes = RootNode.fields();

            while (FileTypes.hasNext()) {
                Map.Entry<String, JsonNode> FileTypeData = FileTypes.next();
                String FileTypeName = FileTypeData.getKey();
                JsonNode FileSubtypesNode = FileTypeData.getValue();
                
                Iterator<Map.Entry<String, JsonNode>> FileSubtypes = FileSubtypesNode.fields();
                while(FileSubtypes.hasNext())
                {
                    Map.Entry<String, JsonNode> FileSubtypesData = FileSubtypes.next();
                    String FileSubtypeName = FileSubtypesData.getKey();
                    
                    if(FileSubtypeName.equals(SubTypeVal))
                    {
                        return FileTypeName + ":%" + FileSubtypeName;
                    }

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String GetUploadFrequencyTypeFromFileTypeAndMailAndSubtype(String FileType, String FileSubtype)
    {
        try
        {
            ObjectMapper ObjectMapperObject = new ObjectMapper();
            JsonNode JsonNodeObject = ObjectMapperObject.readTree(new File(FileFormatPath_Hyperparameter));
            if(JsonNodeObject.has(FileType))
            {
                JsonNode JsonNodeFileType = JsonNodeObject.get(FileType);
                if(JsonNodeFileType.has(FileSubtype))
                {
                    JsonNode JsonNodeFileSubtype = JsonNodeFileType.get(FileSubtype);
                    if(JsonNodeFileSubtype.has("UploadFrequency"))
                    {
                        return JsonNodeFileSubtype.get("UploadFrequency").get("FrequencyType").asText() + ":%" + JsonNodeFileSubtype.get("UploadFrequency").get("EmailInfo").asText();
                    }
                }
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        return "None";
    }

    public String GetAllUploadFrequencyDataFromTypeAndSubtype(String FileType, String FileSubtype)
    {
        try
        {
            ObjectMapper ObjectMapperObject = new ObjectMapper();
            JsonNode JsonNodeObject = ObjectMapperObject.readTree(new File(FileFormatPath_Hyperparameter));
            if(JsonNodeObject.has(FileType))
            {
                JsonNode JsonNodeFileType = JsonNodeObject.get(FileType);
                if(JsonNodeFileType.has(FileSubtype))
                {
                    JsonNode JsonNodeFileSubtype = JsonNodeFileType.get(FileSubtype);
                    if(JsonNodeFileSubtype.has("UploadFrequency"))
                    {
                        String FrequencyType = JsonNodeFileSubtype.get("UploadFrequency").get("FrequencyType").asText();
                        String FrequencyDetails = JsonNodeFileSubtype.get("UploadFrequency").get("FrequencyDetails").asText();
                        String EmailInfo = JsonNodeFileSubtype.get("UploadFrequency").get("EmailInfo").asText();
                        String DateLastUploaded = "";
                        try
                        {
                            DateLastUploaded = JsonNodeFileSubtype.get("UploadFrequency").get("DateLastUploaded").asText();
                        }
                        catch(Exception e)
                        {
                            DateLastUploaded = "None";
                        }
                        return FrequencyType + ":%" + FrequencyDetails + ":%" + EmailInfo + ":%" + DateLastUploaded;
                    }
                }
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

        return "None";
    }

    public void UpdateFileSubtype(String FileType, String FileSubtype, JsonNode RowData, String FrequencyType, String FrequencyDetails, String DateLastUploaded, String EmailInfo)
    {
        try
        {
            File FileFormat = new File(FileFormatPath_Hyperparameter);
            ObjectMapper ObjectMapperObject = new ObjectMapper();
            JsonNode RootNode = ObjectMapperObject.readTree(FileFormat);


            if(RootNode.has(FileType) && RootNode.get(FileType).has(FileSubtype))
            {
                ObjectNode FileSubtypeNode = (ObjectNode) RootNode.get(FileType).get(FileSubtype);
                FileSubtypeNode.removeAll();
                FileSubtypeNode.setAll((ObjectNode) RowData);

                ObjectMapperObject.writerWithDefaultPrettyPrinter().writeValue(FileFormat , RootNode);
            }
            else
            {
                System.out.println("Specified FileType or FileSubtype Not Found - UpdateFileSubtype in DataHandlerJ");
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public void DeleteFileSubtype(String FileType, String FileSubtype)
    {
        try
        {
            File FileFormat = new File(FileFormatPath_Hyperparameter);
            ObjectMapper ObjectMapperObject = new ObjectMapper();
            JsonNode RootNode = ObjectMapperObject.readTree(FileFormat);

            if(RootNode.has(FileType))
            {
                ObjectNode FileTypeNode = (ObjectNode) RootNode.get(FileType);
                if(FileTypeNode.has(FileSubtype))
                {
                    FileTypeNode.remove(FileSubtype);
                    ObjectMapperObject.writerWithDefaultPrettyPrinter().writeValue(FileFormat , RootNode);
                    System.out.println(FileSubtype + " Deleted");
                }
            }
        }
        catch(Exception e)
        {

        }
    }

    public List<String> GetFileFormatLines()
    {
        List<String> FileFormatLines = new ArrayList<>();
        ObjectMapper ObjectMapperObject = new ObjectMapper();

        try {
            JsonNode RootNode = ObjectMapperObject.readTree(new File(FileFormatPath_Hyperparameter));

            Iterator<Map.Entry<String, JsonNode>> FileTypes = RootNode.fields();

            while (FileTypes.hasNext()) {
                Map.Entry<String, JsonNode> FileTypeData = FileTypes.next();
                String FileTypeName = FileTypeData.getKey();
                JsonNode FileSubtypesNode = FileTypeData.getValue();
                
                Iterator<Map.Entry<String, JsonNode>> FileSubtypes = FileSubtypesNode.fields();
                while(FileSubtypes.hasNext())
                {
                    Map.Entry<String, JsonNode> FileSubtypesData = FileSubtypes.next();
                    String FileSubtypeName = FileSubtypesData.getKey();
                    JsonNode FileElementsNode = FileSubtypesData.getValue();

                    String FileFormat = FileTypeName + ":%" + FileSubtypeName;

                    Iterator<Map.Entry<String, JsonNode>> FileElements = FileElementsNode.fields();
                    while(FileElements.hasNext())
                    {
                        Map.Entry<String, JsonNode> FileElementsData = FileElements.next();
                        String Row = FileElementsData.getKey();
                        if(Row.equals("UploadFrequency")) continue;
                        String Type = FileElementsData.getValue().get("Type").asText();
                        String Title = FileElementsData.getValue().get("Title").asText();

                        if(!FileFormat.contains(":%%")) FileFormat += ":%%" + Row + ":" + Type + ":" + Title;
                        else FileFormat += ":%" + Row + ":" + Type + ":" + Title;
                    }

                    FileFormatLines.add(FileFormat);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return FileFormatLines;
    }

    public List<String> GetUploadFrequencyData(boolean GetOnlyExpired)
    {
        List<String> FrequencyData = new ArrayList<>();
        ObjectMapper ObjectMapperObject = new ObjectMapper();

        try {
            JsonNode RootNode = ObjectMapperObject.readTree(new File(FileFormatPath_Hyperparameter));

            Iterator<Map.Entry<String, JsonNode>> FileTypes = RootNode.fields();

            while (FileTypes.hasNext()) {
                Map.Entry<String, JsonNode> FileTypeData = FileTypes.next();
                String FileTypeName = FileTypeData.getKey();
                JsonNode FileSubtypesNode = FileTypeData.getValue();
                
                Iterator<Map.Entry<String, JsonNode>> FileSubtypes = FileSubtypesNode.fields();
                while(FileSubtypes.hasNext())
                {
                    Map.Entry<String, JsonNode> FileSubtypesData = FileSubtypes.next();
                    String FileSubtypeName = FileSubtypesData.getKey();
                    JsonNode FileElementsNode = FileSubtypesData.getValue();

                    String FileFormat = FileTypeName + ":%" + FileSubtypeName;

                    Iterator<Map.Entry<String, JsonNode>> FileElements = FileElementsNode.fields();
                    while(FileElements.hasNext())
                    {
                        Map.Entry<String, JsonNode> FileElementsData = FileElements.next();
                        String Row = FileElementsData.getKey();
                        if(Row.equals("UploadFrequency"))
                        {
                            String FrequencyType = FileElementsData.getValue().get("FrequencyType").asText();
                            String FrequencyDetails = FileElementsData.getValue().get("FrequencyDetails").asText();
                            String DateLastUploaded = FileElementsData.getValue().get("DateLastUploaded").asText();
                            String EmailsInfo = FileElementsData.getValue().get("EmailInfo").asText();

                            FileFormat += ":%%" + FrequencyType + ":%" + DateLastUploaded + ":%" + EmailsInfo;
                            
                            if(GetOnlyExpired == true)
                            {
                                if(CheckUploadFrequencyExpiry(FrequencyType, FrequencyDetails, DateLastUploaded) == true)
                                {
                                    FrequencyData.add(FileFormat);
                                }
                            }
                            else
                            {
                                FrequencyData.add(FileFormat);

                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return FrequencyData;
    }

    private boolean CheckUploadFrequencyExpiry(String FrequencyType, String FrequencyDetails, String DateLastUploaded)
    {
        LocalDate CurrentDate = LocalDate.now();
        DateTimeFormatter DateTimeFormatterObject = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        String CurrentDateFormatted = CurrentDate.format(DateTimeFormatterObject);

        if(FrequencyType.equals("Daily"))
        {
            LocalDate CurrentDate_2 = LocalDate.parse(CurrentDateFormatted, DateTimeFormatterObject);
            LocalDate LastUploadedDate_LocalDateObject = LocalDate.parse(DateLastUploaded, DateTimeFormatterObject);
            Long DaysElapsed = ChronoUnit.DAYS.between(CurrentDate_2, LastUploadedDate_LocalDateObject);
            if(DaysElapsed >= 1)
            {
                return true;
            }
        }
        else if(FrequencyType.equals("Weekly")) // FrequencyData Format: Monday:Tuesday:...
        {
            LocalDate CurrentDate_2 = LocalDate.parse(CurrentDateFormatted, DateTimeFormatterObject);
            LocalDate LastUploadedDate_LocalDateObject = LocalDate.parse(DateLastUploaded, DateTimeFormatterObject);

            String[] DaysOfWeekToNotify = FrequencyDetails.split(":");

            for(String Day : DaysOfWeekToNotify)
            {
                DayOfWeek TargetDayOfWeek = DayOfWeek.valueOf(Day.toUpperCase());
                
                int CurrentDayOfWeekValue = CurrentDate_2.getDayOfWeek().getValue();
                int TargetDayOfWeekValue = TargetDayOfWeek.getValue();
                if((CurrentDayOfWeekValue == TargetDayOfWeekValue) && LastUploadedDate_LocalDateObject.isBefore(CurrentDate_2))
                {
                    return true;
                }
            }
        }
        else if(FrequencyType.equals("Monthly")) //FrequencyDetails Format: 2,23,12,2:...
        {
            LocalDate CurrentDate_2 = LocalDate.parse(CurrentDateFormatted, DateTimeFormatterObject);
            LocalDate LastUploadedDate_LocalDateObject = LocalDate.parse(DateLastUploaded, DateTimeFormatterObject);
            
            String[] NextUploadDays = FrequencyDetails.split(",");
            List<LocalDate> NextUploadDates = new ArrayList<>();

            for(String Day : NextUploadDays)
            {
                int DayOfMonth = Integer.parseInt(Day);
                
                LocalDate NextDate = CurrentDate_2.withDayOfMonth(DayOfMonth);

                NextUploadDates.add(NextDate);
            }

            for(LocalDate NextUploadDate : NextUploadDates)
            {
                if(CurrentDate.isAfter(NextUploadDate) || CurrentDate.isEqual(NextUploadDate))
                {
                    if(LastUploadedDate_LocalDateObject.isBefore(NextUploadDate))
                    {
                        return true;
                    }
                }
            }
        }
        else if(FrequencyType.equals("Quarterly")) // FrequencyDetails: 1, 2, or 3
        {
            LocalDate CurrentDate_2 = LocalDate.parse(CurrentDateFormatted, DateTimeFormatterObject);
            LocalDate LastUploadedDate_LocalDateObject = LocalDate.parse(DateLastUploaded, DateTimeFormatterObject);

            List<LocalDate> NextUploadDates = new ArrayList<>();

            int[][] Quarters = {
                {1, 2, 3},
                {4, 5, 6},
                {7, 8, 9},
                {10, 11, 12}
            };

            for(int[] Quarter : Quarters)
            {
                int QuarterMonthIndex = Quarter[Integer.parseInt(FrequencyDetails) - 1];
                LocalDate TargetDate = LocalDate.of(CurrentDate_2.getYear(), QuarterMonthIndex, 1);

                NextUploadDates.add(TargetDate);
            }

            for(LocalDate TargetDate : NextUploadDates)
            {
                if(CurrentDate.isAfter(TargetDate) || CurrentDate.isEqual(TargetDate))
                {
                    if(LastUploadedDate_LocalDateObject.isBefore(TargetDate))
                    {
                        return true;
                    }
                }
            }
        }
        else if(FrequencyDetails.equals("Semiannually"))
        {
            LocalDate CurrentDate_2 = LocalDate.parse(CurrentDateFormatted, DateTimeFormatterObject);
            LocalDate LastUploadedDate_LocalDateObject = LocalDate.parse(DateLastUploaded, DateTimeFormatterObject);

            LocalDate JanuaryOfThisYear = LocalDate.of(CurrentDate_2.getYear(), 1, 1);
            LocalDate JulyOfThisYear = LocalDate.of(CurrentDate_2.getYear(), 7, 1);

            if((CurrentDate_2.isAfter(JanuaryOfThisYear) || CurrentDate_2.isEqual(JanuaryOfThisYear)) || (CurrentDate_2.isAfter(JulyOfThisYear) || CurrentDate.isEqual(JulyOfThisYear)))
            {
                if(LastUploadedDate_LocalDateObject.isBefore(JanuaryOfThisYear) || LastUploadedDate_LocalDateObject.isBefore(JulyOfThisYear))
                {
                    return true;
                }
            }
        }
        else if(FrequencyType.equals("Yearly"))
        {
            LocalDate CurrentDate_2 = LocalDate.parse(CurrentDateFormatted, DateTimeFormatterObject);
            LocalDate LastUploadedDate_LocalDateObject = LocalDate.parse(DateLastUploaded, DateTimeFormatterObject);

            int CurrentYear = CurrentDate_2.getYear();

            LocalDate FirstYearOfCurrentDate = LocalDate.of(CurrentYear, 1, 1);

            if(LastUploadedDate_LocalDateObject.isBefore(FirstYearOfCurrentDate))
            {
                return true;
            }
        }
        return false;
    }

    public String StoreFile(String FileTypeAndSubtype, MultipartFile MultipartFileObject)
    {
        String TypeValue = FileTypeAndSubtype.split(":%")[0];
        String SubtypeValue = FileTypeAndSubtype.split(":%")[1];
        
        if(ParentDirectoryStructure.equalsIgnoreCase("Generic"))
        {
            String FileLocationPath = ParentDirectory_Hyperparameter + TypeValue + "\\" + SubtypeValue + "\\" + MultipartFileObject.getOriginalFilename();
            File ExcelFile = new File(FileLocationPath);
            try
            {
                MultipartFileObject.transferTo(ExcelFile);
                return FileLocationPath;
            }
            catch(IOException e)
            {
                e.printStackTrace();;
            }
        }
        else if(ParentDirectoryStructure.equalsIgnoreCase("Company"))
        {
            //String FileLocationPath = ParentDirectory_Hyperparameter + "opt\\app\\fintool\\inbound\\" + OrganizationName + "\\verified\\" + SubtypeValue + "\\" + MultipartFileObject.getOriginalFilename();
            String FileLocationPath = ParentDirectory_Hyperparameter + "opt\\app\\fintool\\inbound\\" + OrganizationName + "\\verified\\" + SubtypeValue + "\\" + MultipartFileObject.getOriginalFilename();
            File ExcelFile = new File(FileLocationPath);
            try
            {
                MultipartFileObject.transferTo(ExcelFile);
                return FileLocationPath;
            }
            catch(IOException e)
            {
                e.printStackTrace();;
            }
        }
        else
        {
            System.out.println("Invalid Company Structure Input, Not Storing File");
        }
        return "";
    }

    public void AppendStructureToFileFormat(String FileType, String FileSubtype, List<String> Columns) //Columns Structure: FileType:%FileTitle
    {
        String SubtypeData = "{";
        int Index = 0;
        for(String ColumnVal : Columns)
        {
            SubtypeData += "\""+Index+"\": { \"Type\":\""+ColumnVal.split(":%")[0]+"\", \"Title\":\""+ColumnVal.split(":%")[1]+"\"}";

            if(Columns.size() != Index + 1)
            {
                SubtypeData += ",";
            }

            Index ++;
        }

        SubtypeData += "}";
        System.out.println("Append To JsonFileFormat: " + SubtypeData);

        ObjectMapper ObjectMapperObject = new ObjectMapper();
        File FileFormatJsonFile = new File(FileFormatPath_Hyperparameter);
        
        try {
            JsonNode RootNode = ObjectMapperObject.readTree(FileFormatJsonFile);

            JsonNode FileSubtypeNode = ObjectMapperObject.readTree(SubtypeData);
            
            if(RootNode.has(FileType))
            {
                ((ObjectNode) RootNode.get(FileType)).set(FileSubtype, FileSubtypeNode);
            }
            else
            {
                ObjectNode NewFileTypeNode = ObjectMapperObject.createObjectNode();
                NewFileTypeNode.set(FileSubtype, FileSubtypeNode);
                ((ObjectNode) RootNode).set(FileType, NewFileTypeNode);
            }

            ObjectMapperObject.writerWithDefaultPrettyPrinter().writeValue(FileFormatJsonFile, RootNode);
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    public void AppendStructureToFileFormat(String FileType, String FileSubtype, List<String> Columns, String Frequency, String FrequencyData, List<String> Emails) //Columns Structure: FileType:%FileTitle
    {
        AppendStructureToFileFormat(FileType, FileSubtype, Columns);

        LocalDate CurrentDate = LocalDate.now();
        DateTimeFormatter DateTimeFormatterObject = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        String LastUploaded = CurrentDate.format(DateTimeFormatterObject);

        String EmailsFormatted = "";
        for(String Email : Emails)
        {
            if(EmailsFormatted.equals(""))
            {
                EmailsFormatted = Email;
            }
            else
            {
                EmailsFormatted += ":" + Email;
            }

        }

        String FrequencyDetailsFormatted = "";
        if(Frequency.equals("Daily"))
        {
            FrequencyDetailsFormatted = "NA";
        }
        if(Frequency.equals("Weekly")) //FrequencyDetails Format: Monday:Wednesday:Sunday:...
        {
            FrequencyDetailsFormatted = FrequencyData;
        }
        if(Frequency.equals("Monthly")) //FrequencyDetails Format: 2,22,28,12:...
        {
            FrequencyDetailsFormatted = FrequencyData;
        }
        if(Frequency.equals("Quarterly"))//Format: 1, 2, 3 of month
        {
            FrequencyDetailsFormatted = FrequencyData;
        }
        if(Frequency.equals("Semiannually"))
        {
            FrequencyDetailsFormatted = "NA";
        }
        if(Frequency.equals("Yearly"))//Format: Nothing
        {
            FrequencyDetailsFormatted = FrequencyData;
        }

        String[] TypeSubtypePath = {FileType, FileSubtype};

        try
        {
            ObjectMapper ObjectMapperObject = new ObjectMapper();
            
            JsonNode RootNode = ObjectMapperObject.readTree(new File(FileFormatPath_Hyperparameter));
            
            ObjectNode CurrentNode = (ObjectNode) RootNode;
            for (int i = 0; i < TypeSubtypePath.length; i++) {
                CurrentNode = (ObjectNode) CurrentNode.get(TypeSubtypePath[i]);
            }
            
            ObjectNode UploadFrequencyNode = ObjectMapperObject.createObjectNode();
            UploadFrequencyNode.put("FrequencyType", Frequency);
            UploadFrequencyNode.put("FrequencyDetails", FrequencyDetailsFormatted);
            UploadFrequencyNode.put("DateLastUploaded", LastUploaded);
            UploadFrequencyNode.put("EmailInfo", EmailsFormatted);
            
            CurrentNode.set("UploadFrequency", UploadFrequencyNode);
            
            ObjectMapperObject.writerWithDefaultPrettyPrinter().writeValue(new File(FileFormatPath_Hyperparameter), RootNode);
            
            System.out.println("Updated File Format and Upload Frequency");
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    public void UpdateLastUploadedUploadFrequency(String SenderEmail, String FileNameSubtype)
    {
        List<String> UploadFrequencyDataList = GetUploadFrequencyData(false);
        for(String UploadFrequencyData : UploadFrequencyDataList)
        {
            String UploadFrequencyData_FileSubtype = UploadFrequencyData.split(":%%")[0].split(":%")[1];
            String UploadFrequencyData_EmailInfo = UploadFrequencyData.split(":%%")[1].split(":%")[2];
            String UploadFrequencyData_FileType = UploadFrequencyData.split(":%%")[0].split(":%")[0];

            if(FileNameSubtype.contains(UploadFrequencyData_FileSubtype) && UploadFrequencyData_EmailInfo.contains(SenderEmail))
            {
                ObjectMapper ObjectMapperObject = new ObjectMapper();
                
                try
                {
                    JsonNode RootNode = ObjectMapperObject.readTree(new File(FileFormatPath_Hyperparameter));
                    JsonNode FileType = RootNode.path(UploadFrequencyData_FileType);
                    JsonNode FileSubtype = FileType.path(UploadFrequencyData_FileSubtype);
                    JsonNode UploadFrequency = FileSubtype.path("UploadFrequency");
                    
                    System.out.println("DataHandlerJ FileType and Subtype: " + UploadFrequencyData_FileType + " " + UploadFrequencyData_FileSubtype);
                    if (UploadFrequency.isObject()) {
                        LocalDate CurrentDate = LocalDate.now();
                        DateTimeFormatter DateTimeFormatterObject = DateTimeFormatter.ofPattern("MM/dd/yyyy");
                        String LastUploaded = CurrentDate.format(DateTimeFormatterObject);
                        
                        System.out.println("Updating DateLastUploaded " + LastUploaded);
                        ((ObjectNode) UploadFrequency).put("DateLastUploaded", LastUploaded);

                        ObjectMapperObject.writerWithDefaultPrettyPrinter().writeValue(new File(FileFormatPath_Hyperparameter), RootNode);
                    }
                }
                catch(IOException e)
                {
                    e.printStackTrace();
                }
                     
            }

        }
        
    }

}
