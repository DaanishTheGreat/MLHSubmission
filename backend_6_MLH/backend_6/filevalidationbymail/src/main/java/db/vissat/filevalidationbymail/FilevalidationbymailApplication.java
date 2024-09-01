package db.vissat.filevalidationbymail;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import db.vissat.filevalidationbymail.Data.AppHyperparameter;
import db.vissat.filevalidationbymail.Data.CombinedMainObjects;
import db.vissat.filevalidationbymail.Data.DataHandlerJ;
import db.vissat.filevalidationbymail.Data.Log;
import db.vissat.filevalidationbymail.Data.MongoDBHandler;
import db.vissat.filevalidationbymail.Data.Sharepoint.DirectoryCompressionHandler;
import db.vissat.filevalidationbymail.Data.Sharepoint.SharepointHandler;
import db.vissat.filevalidationbymail.Mail.MailHandler;

// Current: Complete UpdateLastUploadedUploadFrequency in DataHandlerJ.java, Complete Yearly and Semiannually Frequencies backend --> reference personal teams notes for this


// comment out project
// Potential Problems: ReadMailData may not read new emails from same sender

/*
	Front end: 
		Add frequency of file upload input
			Daily: Notify user to upload every 24 hours if time expires
			Weekly: Admin specify day of week for file upload, Notify user to upload at that day of week if time expires
			Monthly: Admin specify day of month, Notify user to upload at that day of month if time expires
			Quarter/Custom: Admin manually sets specific days, Notify user to upload at that day of the month if time expires
		Organization Input: Specify which organization this serves based on static values for now
		Individual Email Inputs: Specify one or multiple valid emails
		Group Emails: Fetch group email data from backend, user specify organization by dropdown
		Remove File Pattern and File Format
		Field Type: Fetch field types from backend
	
	Back end:
		Create Get Apis for: Organizations, Group Emails, and Field Types
		Done: Restructure applications.properties into a .json with each organization having the same properties as application.properties

 */
// the largest to smallest file format, make config and json usable, make tiles in server
@SpringBootApplication
@EnableScheduling
@Component
public class FilevalidationbymailApplication {

	public static void main(String[] args) {
		SpringApplication.run(FilevalidationbymailApplication.class, args);
	}

	AppHyperparameter AppHyperparameterObject;
	public static Map<String, CombinedMainObjects> MainObjectsDictionary;

	public static String ApplicationPropertiesPath_Hyperparameter = ".\\filevalidationbymail\\src\\main\\resources\\ApplicationProperties.json";
 
	@EventListener(ApplicationReadyEvent.class)
	public void StartUp()
	{	
		AppHyperparameterObject = new AppHyperparameter(ApplicationPropertiesPath_Hyperparameter);

		// Development Email Password found in path
		//String EmailPassword = System.getenv("FILEVALID_PWD");
		String EmailPassword = "yfpc aduc plft uoaa";

		MainObjectsDictionary = new HashMap<>();

		List<String> OrganizationNames = AppHyperparameterObject.GetOrganizationNames();

		Map<String, String> CommonProperties = AppHyperparameterObject.GetOrganizationProperties("_common");
		
		for(String OrganizationName : OrganizationNames)
		{
			System.out.println("OrgNames: " + OrganizationName);
			Map<String, String> HyperparameterProperties = AppHyperparameterObject.GetOrganizationProperties(OrganizationName);
			
			MailHandler MailHandlerObject = new MailHandler(HyperparameterProperties.get("Host"), HyperparameterProperties.get("Email"),
			EmailPassword, HyperparameterProperties.get("SubjectFilter"), HyperparameterProperties.get("ImapProtocol"),
			HyperparameterProperties.get("SmtpHost"), HyperparameterProperties.get("SmtpPort"));

			DataHandlerJ DataHandlerJObject = new DataHandlerJ(HyperparameterProperties.get("ParentDirectory"), HyperparameterProperties.get("FileFormatDirectory"), HyperparameterProperties.get("ParentDirectoryStructure"), OrganizationName);
			DataHandlerJObject.CreateVerifyFileFormatDirectory(HyperparameterProperties.get("ParentDirectoryStructure"), OrganizationName);

			Log LogObject = new Log(HyperparameterProperties.get("LogDirectory"));

			DirectoryCompressionHandler DirectoryCompressionHandlerObject = new DirectoryCompressionHandler(HyperparameterProperties.get("ParentDirectory"), HyperparameterProperties.get("ZippedDirectory"));

			SharepointHandler SharepointHandlerObject = new SharepointHandler(HyperparameterProperties.get("ZippedDirectory"), HyperparameterProperties.get("onedrive.client-id")
			, HyperparameterProperties.get("onedrive.client-secret"), HyperparameterProperties.get("onedrive.redirect-uri"), HyperparameterProperties.get("onedrive.refresh-token"), HyperparameterProperties.get("onedrive.upload-path"));

			MongoDBHandler MongoDBHandlerObject = new MongoDBHandler(ApplicationPropertiesPath_Hyperparameter, HyperparameterProperties.get("OrganizationName"), HyperparameterProperties.get("ConnectionString"),
			 HyperparameterProperties.get("DatabaseName"), HyperparameterProperties.get("CollectionName"), HyperparameterProperties.get("FileFormatDirectory"), CommonProperties.get("DatabaseName"), CommonProperties.get("CollectionName"));

			CombinedMainObjects CombinedMainObjectsObject = new CombinedMainObjects(MailHandlerObject, DataHandlerJObject, LogObject, DirectoryCompressionHandlerObject, SharepointHandlerObject, MongoDBHandlerObject);
			MainObjectsDictionary.put(OrganizationName, CombinedMainObjectsObject);

			System.out.println("Loaded Email Data and Created or Verified FileFormat Directory Successfully For Organization: " + OrganizationName);
		}

		//InitializeMongoDBData(ApplicationPropertiesPath_Hyperparameter, CommonProperties.get("ConnectionString"), CommonProperties.get("DatabaseName"), CommonProperties.get("CollectionName"));
	}

	private void InitializeMongoDBData(String CommonApplicationPropertiesPath, String CommonConnectionString, String CommonDatabase, String CommonCollection)
	{
		MongoDBHandler.InitializeMongoDBCommon(CommonApplicationPropertiesPath, CommonConnectionString, CommonDatabase, CommonCollection);

		for(String MainObjectsDictionaryKey : MainObjectsDictionary.keySet())
		{
			MongoDBHandler MongoDBHandlerObject = MainObjectsDictionary.get(MainObjectsDictionaryKey).GetMongoDBHandler();
			MongoDBHandlerObject.LoadFileFormatsFromMongoDB();
		}
	}

	//@Scheduled(fixedRate = 50000)
	private void UpdateMongoDB()
	{
		if(MainObjectsDictionary != null)
		{
			for(String Organization : MainObjectsDictionary.keySet())
			{
				MongoDBHandler MongoDBHandlerObject = MainObjectsDictionary.get(Organization).GetMongoDBHandler();
				try
				{
					MongoDBHandlerObject.UploadApplicationPropertiesScheduled();
					MongoDBHandlerObject.UploadFileFormatScheduled();
				}
				catch(IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	//@Scheduled(fixedRate = 5000)
	public void VerifyAndCaptureEmailsAndNotifyContactsForExpFileUpl()
	{
		if(MainObjectsDictionary != null)
		{
			for(String MainObjectsDictionaryKey : MainObjectsDictionary.keySet())
			{
				MailHandler MailHandlerObject = MainObjectsDictionary.get(MainObjectsDictionaryKey).GetMailHandler();
				DataHandlerJ DataHandlerJObject = MainObjectsDictionary.get(MainObjectsDictionaryKey).GetDataHandlerJ();
				Log LogObject = MainObjectsDictionary.get(MainObjectsDictionaryKey).GetLogObject();
				MongoDBHandler MongoDBHandlerObject = MainObjectsDictionary.get(MainObjectsDictionaryKey).GetMongoDBHandler();

				MailHandlerObject.GetEmailData(DataHandlerJObject, LogObject, MongoDBHandlerObject, MainObjectsDictionaryKey);
				

				//MailHandlerObject.NotifyContactsForExpiredFileUpload(DataHandlerJObject);

			}
		}
		else
		{
			System.out.println("Waiting for email data to load");
		}
	}
	
	//@Scheduled(fixedRate = 5000)
	public void SyncDirectory()
	{	
		if(MainObjectsDictionary != null)
		{
			for(String MainObjectsDictionaryKey : MainObjectsDictionary.keySet())
			{
				DirectoryCompressionHandler DirectoryCompressionHandlerObject = MainObjectsDictionary.get(MainObjectsDictionaryKey).GetDirectoryCompressionHandler();
				try
				{
					DirectoryCompressionHandlerObject.ZipDirectory();
					SharepointHandler SharepointHandlerObject = MainObjectsDictionary.get(MainObjectsDictionaryKey).GetSharepointHandler();
					SharepointHandlerObject.UploadFile();
				}
				catch(IOException e)
				{
					e.printStackTrace();
				}
			}
		}
		else
		{
			System.out.println("Waiting for Onedrive data to load");
		}
	}

	//@Scheduled(fixedRate = 3000)
	public void MongoDBUpdate()
	{
		for(String MainObjectsDictionaryKey : MainObjectsDictionary.keySet())
		{
			MongoDBHandler MongoDBHandlerObject = MainObjectsDictionary.get(MainObjectsDictionaryKey).GetMongoDBHandler();
			//MongoDBHandlerObject.UpdateApplicationProperties();
			MongoDBHandlerObject.UpdateFileUploadDetails("/opt/fintool/inbound/etc", "Expense2", "Expense", "Honda");
		}
	}
		 

	//@Scheduled(fixedRate = 2000)
	public void ForTesting()
	{
		
		for(String MainObjectsDictionaryKey : MainObjectsDictionary.keySet())
		{
			DataHandlerJ DataHandlerJObject = MainObjectsDictionary.get(MainObjectsDictionaryKey).GetDataHandlerJ();
			System.out.println(DataHandlerJObject.GetUploadFrequencyData(true));
		}
		
	}
}
