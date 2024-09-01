package db.vissat.filevalidationbymail.VerifyFormat.helper;

import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class TypeVerify {
    
    public static String VerifyInt(List<List<String>> Data, int Column)
    {
        String InvalidFormatLocation = "";
        int RowIndex = 0;
        
        for(List<String> Row : Data)
        {
            if(RowIndex != 0)
            {
            try
            {
                Float.parseFloat(Row.get(Column));
            }
            catch(NumberFormatException e)
            {
                if(InvalidFormatLocation != "")
                {
                    InvalidFormatLocation += ":%InvalidInt:" + RowIndex + ":" + Column;
                }
                else
                {
                    InvalidFormatLocation = "InvalidInt:" + RowIndex + ":" + Column;;
                }
            }
            catch(IndexOutOfBoundsException e)
            {
                if(InvalidFormatLocation != "")
                {
                    InvalidFormatLocation += ":%InvalidDate:" + RowIndex + ":" + Column;
                }
                else
                {
                    InvalidFormatLocation += "InvalidDate:" + RowIndex + ":" + Column;
                }
            }
        }
            RowIndex++;
    }
        return InvalidFormatLocation; 
    }

    public static String VerifyDate(List<List<String>> Data, int Column)
    {
        String InvalidFormatLocation = "";
        int RowIndex = 0;
        for(List<String> Row : Data)
        {
            if(RowIndex != 0)
            {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yy");
            try
            {
                LocalDate date = LocalDate.parse(Row.get(Column), formatter);
            }
            catch(DateTimeParseException e)
            {
                if(InvalidFormatLocation != "")
                {
                    InvalidFormatLocation += ":%InvalidDate:" + RowIndex + ":" + Column;
                }
                else
                {
                    InvalidFormatLocation += "InvalidDate:" + RowIndex + ":" + Column;
                }
            }
            catch(IndexOutOfBoundsException e)
            {
                if(InvalidFormatLocation != "")
                {
                InvalidFormatLocation += ":%InvalidDate:" + RowIndex + ":" + Column;
                }
                else
                {
                    InvalidFormatLocation += "InvalidDate:" + RowIndex + ":" + Column;
                }
            }
        }
            RowIndex++;
        }
        return InvalidFormatLocation; 
    }

    public static String VerifyMoneyFloat(List<List<String>> Data, int Column)
    {
        String InvalidFormatLocation = "";
        int RowIndex = 0;
        for(List<String> Row : Data)
        {
            if(RowIndex != 0)
            {
            try
            {
                if(!Row.get(Column).matches(".*[\\p{Sc}].*"))
                {
                    if(InvalidFormatLocation != "")
                    {
                        InvalidFormatLocation += ":%InvalidMoneyFloat:" + RowIndex + ":" + Column;
                    }
                    else
                    {
                        InvalidFormatLocation += "InvalidMoneyFloat:" + RowIndex + ":" + Column;
                    }
                }
                else
                {
                    String CleanedMoneyFloat = Row.get(Column).replaceAll("[\\p{Sc}]", "").trim();
                    CleanedMoneyFloat = CleanedMoneyFloat.replace(",", "");
                    Float.parseFloat(CleanedMoneyFloat);
                }
            }
            catch(Exception e)
            {
                if(InvalidFormatLocation != "")
                    {
                        InvalidFormatLocation += ":%InvalidMoneyFloat:" + RowIndex + ":" + Column;
                    }
                    else
                    {
                        InvalidFormatLocation += "InvalidMoneyFloat:" + RowIndex + ":" + Column;
                    }
            }
        }
            RowIndex++;
        }
        return InvalidFormatLocation; 
    }
}
