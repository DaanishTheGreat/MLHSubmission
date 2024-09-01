package db.vissat.filevalidationbymail.Data.GroupEmails;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class GroupEmailsHandler
{
    private static String GroupEmailsPath = "C:\\Users\\daani\\Documents\\Vissat\\DirectExpenseDistributionProgramOutlineSteps1_10\\FileUploadValidation\\backend_5\\filevalidationbymail\\src\\main\\java\\db\\vissat\\filevalidationbymail\\Data\\GroupEmails\\GroupEmails.json";

    public static List<String> GetGroupEmailsData(String GroupEmailName)
    {
        List<String> GroupEmailData = new ArrayList<>();

        ObjectMapper ObjectMapperObject = new ObjectMapper();
        try {
            JsonNode OrgNode = ObjectMapperObject.readTree(new File(GroupEmailsPath));
            JsonNode GroupEmailNode = OrgNode.path(GroupEmailName);

            Iterator<Map.Entry<String, JsonNode>> GroupEmailFields = GroupEmailNode.fields();

            while(GroupEmailFields.hasNext())
            {
                Map.Entry<String, JsonNode> Entry = GroupEmailFields.next();
                JsonNode ValueNode = Entry.getValue();

                GroupEmailData.add(String.valueOf(ValueNode));
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        return GroupEmailData;
    }

    public static List<String> GetGroupEmailNames()
    {
        List<String> GroupEmailNames = new ArrayList<>();

        ObjectMapper ObjectMapperObject = new ObjectMapper();

        try
        {
            JsonNode RootNode = ObjectMapperObject.readTree(new File(GroupEmailsPath));

            Iterator<String> GroupEmailNamesItr = RootNode.fieldNames();

            while(GroupEmailNamesItr.hasNext())
            {
                GroupEmailNames.add(GroupEmailNamesItr.next());
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

        return GroupEmailNames;
    }
}
