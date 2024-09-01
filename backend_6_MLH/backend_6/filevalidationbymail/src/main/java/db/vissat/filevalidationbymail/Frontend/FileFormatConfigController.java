package db.vissat.filevalidationbymail.Frontend;

import java.io.IOException;
import java.util.List;
import java.io.File;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.fasterxml.jackson.databind.ObjectMapper;

import db.vissat.filevalidationbymail.FilevalidationbymailApplication;
import db.vissat.filevalidationbymail.Data.AppHyperparameter;
import db.vissat.filevalidationbymail.Data.DataHandlerJ;
import db.vissat.filevalidationbymail.Frontend.Models.FileFormatModel;
import db.vissat.filevalidationbymail.Frontend.Models.InputData;

@Controller
@RequestMapping("/")
public class FileFormatConfigController {

    /*
    @GetMapping("add")
    public String AddData(Model modelObject) {
        modelObject.addAttribute("inputData", new InputData());
        return "FormatConfig";
    }

    @PostMapping("ShowSubmit")
    public String SubmitData(InputData inputDataObject, Model modelObject)
    {
        DataHandlerJ DataHandlerJObject = FilevalidationbymailApplication.MainObjectsDictionary.get(inputDataObject.getOrganizationName()).GetDataHandlerJ();
        
        String FileType = inputDataObject.getFileName();
        String FileSubtype = inputDataObject.getFileSubtype();
        List<String> SubtypeElements = inputDataObject.GetFileFormatElementsList();

        DataHandlerJObject.AppendStructureToFileFormat(FileType, FileSubtype, SubtypeElements);

        return "ShowSubmit";
    }

    @GetMapping("Show")
    public String ShowData(Model modelObject) {
        AppHyperparameter appHyperparameterObject = new AppHyperparameter();
        String filePath = appHyperparameterObject.GetProperty("FileFormatJsonDirectory");

        ObjectMapper objectMapper = new ObjectMapper();
        FileFormatModel dataModel = null;
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                System.err.println("File not found: " + filePath);
            } else {
                dataModel = objectMapper.readValue(file, FileFormatModel.class);
            }
        } catch (IOException e) {
            System.err.println("Failed to read or parse the file: " + e.getMessage());
            e.printStackTrace();
        }

        if (dataModel != null) {
            modelObject.addAttribute("dataModel", dataModel);
        } else {
            System.err.println("DataModel is null, unable to add attributes to the model.");
        }

        return "Show";
    }
         */
}
