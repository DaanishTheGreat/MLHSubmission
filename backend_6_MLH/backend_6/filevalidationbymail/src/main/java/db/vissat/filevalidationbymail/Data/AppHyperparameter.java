package db.vissat.filevalidationbymail.Data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AppHyperparameter {

    //private String AppHyperparametersPropertiesFilePath = "C:\\Users\\daani\\Documents\\Vissat\\DirectExpenseDistributionProgramOutlineSteps1_10\\FileUploadValidation\\backend_3\\filevalidationbymail\\src\\main\\java\\db\\vissat\\filevalidationbymail\\Data\\AppHyperparameters.properties";
    private String AppHyperparametersPropertiesFilePath;

    public AppHyperparameter(String AppHyperparametersPropertiesFilePath)
    {
        this.AppHyperparametersPropertiesFilePath = AppHyperparametersPropertiesFilePath;
    }

    public List<String> GetOrganizationNames()
    {
        List<String> OrganizationNames = new ArrayList<>();

        ObjectMapper ObjectMapperObject = new ObjectMapper();

        try {
            JsonNode OrgNode = ObjectMapperObject.readTree(new File(AppHyperparametersPropertiesFilePath));
            Iterator<String> OrgNames = OrgNode.fieldNames();

            while(OrgNames.hasNext())
            {
                String OrgName = OrgNames.next();
                if(!OrgName.equals("_common") && !OrgName.equals("_id"))
                {
                    OrganizationNames.add(OrgName);
                }
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        return OrganizationNames;
    }

    public Map<String, String> GetOrganizationProperties(String OrganizationName)
    {
        Map<String, String> Properties = new HashMap<String, String>();

        ObjectMapper ObjectMapperObject = new ObjectMapper();
        try {
            JsonNode OrgNode = ObjectMapperObject.readTree(new File(AppHyperparametersPropertiesFilePath));
            JsonNode OrganizationNode = OrgNode.path(OrganizationName);

            Iterator<Map.Entry<String, JsonNode>> OrgFields = OrganizationNode.fields();

            while(OrgFields.hasNext())
            {
                Map.Entry<String, JsonNode> Entry = OrgFields.next();
                JsonNode ValueNode = Entry.getValue();

                if(ValueNode.isObject())
                {
                    Iterator<Map.Entry<String, JsonNode>> SubFields = ValueNode.fields();
                    while(SubFields.hasNext())
                    {
                        Map.Entry<String, JsonNode> SubEntry = SubFields.next();
                        JsonNode SubValueNode = SubEntry.getValue();
                        if(SubValueNode.isValueNode())
                        {
                            Properties.put(SubEntry.getKey(), SubValueNode.asText());
                        }
                    }
                }
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

        return Properties;
    }
}
