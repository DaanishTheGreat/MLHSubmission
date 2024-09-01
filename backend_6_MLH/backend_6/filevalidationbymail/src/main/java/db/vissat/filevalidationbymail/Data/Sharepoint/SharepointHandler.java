package db.vissat.filevalidationbymail.Data.Sharepoint;

import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.*;

import java.io.File;
import java.nio.file.Files;


public class SharepointHandler {
    
    private String ClientID;

    private String ClientSecret;

    private String RedirectURI;

    private String RefreshToken;

    private String UploadPath;

    private static final String TOKEN_URL = "https://login.microsoftonline.com/common/oauth2/v2.0/token";
    private static final String UPLOAD_URL = "https://graph.microsoft.com/v1.0/me/drive/root:%s:/content";
    
    private String ZippedLocation;

    public SharepointHandler(String LocalZippedLocation, String ClientID, String ClientSecret, String RedirectURI, String RefreshToken, String UploadPath)
    {
        ZippedLocation = LocalZippedLocation;
        this.ClientID = ClientID;
        this.ClientSecret = ClientSecret;
        this.RedirectURI = RedirectURI;
        this.RefreshToken = RefreshToken;
        this.UploadPath = UploadPath; 
    }


    private OkHttpClient OkHttpClientObject = new OkHttpClient();

    //private OneDriveConfig OneDriveConfigObject;

    private String GetAccessToken() throws IOException
    {
        RequestBody FormBodyObject = new FormBody.Builder()
            .add("client_id", ClientID)
            .add("client_secret", ClientSecret)
            .add("refresh_token", RefreshToken)
            .add("redirect_uri", RedirectURI)
            .add("grant_type", "refresh_token")
            .build();


        Request RequestObject = new Request.Builder().url(TOKEN_URL).post(FormBodyObject).build();

        try(Response ResponseObject = OkHttpClientObject.newCall(RequestObject).execute())
        {
            if(!ResponseObject.isSuccessful())
            {
                throw new IOException("Unexpected code " + ResponseObject);
            }

            ObjectMapper ObjectMapperObject = new ObjectMapper();
            JsonNode JsonNodeObject = ObjectMapperObject.readTree(ResponseObject.body().string());
            return JsonNodeObject.get("access_token").asText();
        }
    }

    public void UploadFile() throws IOException
    {
        String AccessToken = GetAccessToken();
        
        //File FileObject = new File(OneDriveConfigObject.getFilePath());
        File FileObject = new File(ZippedLocation);
        byte[] FileContent = Files.readAllBytes(FileObject.toPath());

        RequestBody RequestBodyObject = RequestBody.create(FileContent);

        Request RequestBody = new Request.Builder()
            .url(String.format(UPLOAD_URL, UploadPath))
            .header("Authorization", "Bearer " + AccessToken)
            .put(RequestBodyObject).build();

        try(Response ResponseObject = OkHttpClientObject.newCall(RequestBody).execute())
        {
            if (!ResponseObject.isSuccessful()) {
                throw new IOException("Unexpected code " + ResponseObject);
            }


            System.out.println("File Uploaded Successfully");
            
        }
    }
}
