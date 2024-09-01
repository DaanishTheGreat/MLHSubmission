package db.vissat.filevalidationbymail.Data.Sharepoint;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


public class DirectoryCompressionHandler {

    private String ParentDirectory;
    private String ZippedDirectory;
    //private String ZippedDirectory = "C:\\Users\\daani\\Documents\\Vissat\\DirectExpenseDistributionProgramOutlineSteps1_10\\FileUploadValidation\\backend_3\\filevalidationbymail\\src\\main\\java\\db\\vissat\\filevalidationbymail\\Data\\Dir.zip";

    public DirectoryCompressionHandler(String ParentDirectory, String ZippedDirectory)
    {
        this.ParentDirectory = ParentDirectory;
        this.ZippedDirectory = ZippedDirectory;
    }

    public String ZipDirectory() throws IOException
    {
        FileOutputStream FileOutputStreamObject = new FileOutputStream(ZippedDirectory);
        ZipOutputStream ZipOutputStreamObject = new ZipOutputStream(FileOutputStreamObject);
        File DirectoryToZip = new File(ParentDirectory);

        ZipFile(DirectoryToZip, DirectoryToZip.getName(), ZipOutputStreamObject);
        ZipOutputStreamObject.close();
        FileOutputStreamObject.close();

        return ZippedDirectory;
    }

    private void ZipFile(File DirToZip, String FileName, ZipOutputStream ZipOutputStreamObject) throws IOException
    {
        if(DirToZip.isHidden()) return;

        if(DirToZip.isDirectory())
        {
             if (FileName.endsWith("/")) {
                ZipOutputStreamObject.putNextEntry(new ZipEntry(FileName));
                ZipOutputStreamObject.closeEntry();
            } else {
                ZipOutputStreamObject.putNextEntry(new ZipEntry(FileName + "/"));
                ZipOutputStreamObject.closeEntry();
            }
            File[] children = DirToZip.listFiles();
            for (File childFile : children) {
                ZipFile(childFile, FileName + "/" + childFile.getName(), ZipOutputStreamObject);
            }
            return;
        }
        FileInputStream FileInputStreamObject = new FileInputStream(DirToZip);
        ZipEntry ZipEntryObject = new ZipEntry(FileName);
        ZipOutputStreamObject.putNextEntry(ZipEntryObject);
        byte[] bytes = new byte[1024];
        int length;
        while ((length = FileInputStreamObject.read(bytes)) >= 0) {
            ZipOutputStreamObject.write(bytes, 0, length);
        }
        FileInputStreamObject.close();
    }
}
