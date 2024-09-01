package db.vissat.filevalidationbymail.Frontend.Models;

public class FileConfigData {
     // Organization Name
     private String organizationName;

     // OneDrive Configuration
     private String onedriveClientId;
     private String onedriveClientSecret;
     private String onedriveRedirectUri;
     private String onedriveRefreshToken;
     private String onedriveUploadPath;
 
     // Email Configuration
     private String emailHost;
     private String email;
     private String emailPassword;
     private String emailSubjectFilter;
     private String emailImapProtocol;
     private String emailSmtpHost;
     private String emailSmtpPort;
 
     // Directory Configuration
     private String directoryParentStructure;
     private String directoryParent;
     private String directoryLog;
     private String directoryFileFormat;
 
     // MongoDB Configuration
     private String mongoOrganizationName;
     private String mongoConnectionString;
     private String mongoDatabaseName;
     private String mongoCollectionName;
 
     // Getters and Setters
 
     public String getOrganizationName() {
         return organizationName;
     }
 
     public void setOrganizationName(String organizationName) {
         this.organizationName = organizationName;
     }
 
     public String getOnedriveClientId() {
         return onedriveClientId;
     }
 
     public void setOnedriveClientId(String onedriveClientId) {
         this.onedriveClientId = onedriveClientId;
     }
 
     public String getOnedriveClientSecret() {
         return onedriveClientSecret;
     }
 
     public void setOnedriveClientSecret(String onedriveClientSecret) {
         this.onedriveClientSecret = onedriveClientSecret;
     }
 
     public String getOnedriveRedirectUri() {
         return onedriveRedirectUri;
     }
 
     public void setOnedriveRedirectUri(String onedriveRedirectUri) {
         this.onedriveRedirectUri = onedriveRedirectUri;
     }
 
     public String getOnedriveRefreshToken() {
         return onedriveRefreshToken;
     }
 
     public void setOnedriveRefreshToken(String onedriveRefreshToken) {
         this.onedriveRefreshToken = onedriveRefreshToken;
     }
 
     public String getOnedriveUploadPath() {
         return onedriveUploadPath;
     }
 
     public void setOnedriveUploadPath(String onedriveUploadPath) {
         this.onedriveUploadPath = onedriveUploadPath;
     }
 
     public String getEmailHost() {
         return emailHost;
     }
 
     public void setEmailHost(String emailHost) {
         this.emailHost = emailHost;
     }
 
     public String getEmail() {
         return email;
     }
 
     public void setEmail(String email) {
         this.email = email;
     }
 
     public String getEmailPassword() {
         return emailPassword;
     }
 
     public void setEmailPassword(String emailPassword) {
         this.emailPassword = emailPassword;
     }
 
     public String getEmailSubjectFilter() {
         return emailSubjectFilter;
     }
 
     public void setEmailSubjectFilter(String emailSubjectFilter) {
         this.emailSubjectFilter = emailSubjectFilter;
     }
 
     public String getEmailImapProtocol() {
         return emailImapProtocol;
     }
 
     public void setEmailImapProtocol(String emailImapProtocol) {
         this.emailImapProtocol = emailImapProtocol;
     }
 
     public String getEmailSmtpHost() {
         return emailSmtpHost;
     }
 
     public void setEmailSmtpHost(String emailSmtpHost) {
         this.emailSmtpHost = emailSmtpHost;
     }
 
     public String getEmailSmtpPort() {
         return emailSmtpPort;
     }
 
     public void setEmailSmtpPort(String emailSmtpPort) {
         this.emailSmtpPort = emailSmtpPort;
     }
 
     public String getDirectoryParentStructure() {
         return directoryParentStructure;
     }
 
     public void setDirectoryParentStructure(String directoryParentStructure) {
         this.directoryParentStructure = directoryParentStructure;
     }
 
     public String getDirectoryParent() {
         return directoryParent;
     }
 
     public void setDirectoryParent(String directoryParent) {
         this.directoryParent = directoryParent;
     }
 
     public String getDirectoryLog() {
         return directoryLog;
     }
 
     public void setDirectoryLog(String directoryLog) {
         this.directoryLog = directoryLog;
     }
 
     public String getDirectoryFileFormat() {
         return directoryFileFormat;
     }
 
     public void setDirectoryFileFormat(String directoryFileFormat) {
         this.directoryFileFormat = directoryFileFormat;
     }
 
     public String getMongoOrganizationName() {
         return mongoOrganizationName;
     }
 
     public void setMongoOrganizationName(String mongoOrganizationName) {
         this.mongoOrganizationName = mongoOrganizationName;
     }
 
     public String getMongoConnectionString() {
         return mongoConnectionString;
     }
 
     public void setMongoConnectionString(String mongoConnectionString) {
         this.mongoConnectionString = mongoConnectionString;
     }
 
     public String getMongoDatabaseName() {
         return mongoDatabaseName;
     }
 
     public void setMongoDatabaseName(String mongoDatabaseName) {
         this.mongoDatabaseName = mongoDatabaseName;
     }
 
     public String getMongoCollectionName() {
         return mongoCollectionName;
     }
 
     public void setMongoCollectionName(String mongoCollectionName) {
         this.mongoCollectionName = mongoCollectionName;
     }
}
