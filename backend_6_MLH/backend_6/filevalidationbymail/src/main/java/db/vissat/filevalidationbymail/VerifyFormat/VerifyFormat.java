package db.vissat.filevalidationbymail.VerifyFormat;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import java.io.*;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.web.multipart.MultipartFile;

import db.vissat.filevalidationbymail.Data.DataHandlerJ;
import db.vissat.filevalidationbymail.VerifyFormat.helper.TypeVerify;

public class VerifyFormat {

    private MultipartFile MultipartFileObject;
    private List<List<String>> FileData;
    private String TypeAndSubtype;
    private String VerifiedData;

    public VerifyFormat(String TypeAndSubtype, MultipartFile MultipartFileObject, DataHandlerJ DataHandlerJObject)
    {
        this.MultipartFileObject = MultipartFileObject;
        this.TypeAndSubtype = TypeAndSubtype;

        try {
            if(GetFileFormat().equals("xlsx"))
            {
                FileData = GetXLSXData(MultipartFileObject);
            }
            if(GetFileFormat().equals("csv"))
            {
                FileData = GetCSVData(MultipartFileObject);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        VerifiedData = VerifyDataValues(DataHandlerJObject);
    }

    public String GetVerifyData()
    {
        return VerifiedData;
    }

    private List<List<String>> GetXLSXData(MultipartFile multipartFile) throws IOException 
    {
        try (InputStream inputStream = multipartFile.getInputStream()) {
            Workbook workbook = new XSSFWorkbook(inputStream); // Use XSSFWorkbook for .xlsx format
            Sheet sheet = workbook.getSheetAt(0); // Assuming there's only one sheet
            List<List<String>> rows = new ArrayList<>();

            DataFormatter dataFormatter = new DataFormatter();
            FormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();

            Iterator<Row> rowIterator = sheet.iterator();
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                List<String> cells = new ArrayList<>();

                Iterator<Cell> cellIterator = row.cellIterator();
                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();

                    if (cell.getCellType() == CellType.FORMULA) {
                        CellValue cellValue = formulaEvaluator.evaluate(cell);
                        cells.add(dataFormatter.formatCellValue(cell, formulaEvaluator));
                    } else {
                        cells.add(dataFormatter.formatCellValue(cell));
                    }
                }
                rows.add(cells);
            }

            workbook.close();
            return rows;
        }
    }

    private List<List<String>> GetCSVData(MultipartFile multipartFile)
    {
        List<List<String>> DataOutput = new ArrayList<>();

        try(Reader ReaderObject = new BufferedReader(new InputStreamReader(multipartFile.getInputStream())))
        {
            CSVParser CSVParserObject = new CSVParser(ReaderObject, CSVFormat.DEFAULT);

            for(CSVRecord CSVRecordObject : CSVParserObject)
            {
                List<String> Row = new ArrayList<>();
                for(String Value : CSVRecordObject)
                {
                    Row.add(Value);
                }
                DataOutput.add(Row);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        System.out.println("Test: " + DataOutput.toString());

        return DataOutput;
    }

    private String GetFileFormat()
    {
        int ExtensionIndex = MultipartFileObject.getOriginalFilename().split("\\.").length;
        String FileName = MultipartFileObject.getOriginalFilename();
        if(FileName.split("\\.")[ExtensionIndex - 1].equals("xlsx"))
        {
            return "xlsx"; 
        }
        else if(FileName.split("\\.")[ExtensionIndex - 1].equals("csv"))
        {
            return "csv";
        }
        else return "none";
    }

    private String VerifyDataValues(DataHandlerJ DataHandlerJObject)
    {
        List<String> FormattingLines = DataHandlerJObject.GetFileFormatLines();

        String Result = "";

        for(String FileFormatRow : FormattingLines)
        {
            if(FileFormatRow.contains(TypeAndSubtype))
            {
                String[] FormattingData = FileFormatRow.split(":%%")[1].split(":%");
                for(String Formatting : FormattingData)
                {
                    String Column = Formatting.split(":")[0];
                    String Type = Formatting.split(":")[1];

                    if(Type.equals("Text"))
                    {

                    }
                    else if(Type.equals("StandardDate"))
                    {
                        if(Result != "")
                        {
                            Result += ":%" + TypeVerify.VerifyDate(FileData, Integer.parseInt(Column));
                        }
                        else
                        {
                            Result += TypeVerify.VerifyDate(FileData, Integer.parseInt(Column));
                        }
                    }
                    else if(Type.equals("Int"))
                    {
                        if(Result != "")
                        {
                            Result += ":%" + TypeVerify.VerifyInt(FileData, Integer.parseInt(Column));
                        }
                        else
                        {
                            Result += TypeVerify.VerifyInt(FileData, Integer.parseInt(Column));
                        }
                    }
                    else if(Type.equals("MoneyFloat"))
                    {
                        if(Result != "")
                        {
                            Result += ":%" + TypeVerify.VerifyMoneyFloat(FileData, Integer.parseInt(Column));
                        }
                        else
                        {
                            Result += TypeVerify.VerifyMoneyFloat(FileData, Integer.parseInt(Column));
                        }
                    }
                }
            }
        }
        return Result; 
    }
}
