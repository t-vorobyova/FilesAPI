package com.restfileapi;


import com.restfileapi.entries.FilePath;
import com.restfileapi.entries.Metadata;
import com.restfileapi.utils.MessageDigestSHA256;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
import org.junit.*;


import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.io.*;
import java.nio.file.Files;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Tatyana on 17.03.2017.
 */
public class FilesAPITest {

    private static HttpServer server;
    private WebTarget target;
    private static File baseDir = FileProvider.getInstance().getBaseDir();
    private static File baseHash = FileProvider.getInstance().getBaseDirHash();
    private static final String filename = "test";
    private static final String filenameDownloaded = "testDownload";
    private static final String filenameUploaded = "testUpload";
    private static final String fileContent = "qwerty";
    private Metadata metadata;
    private FilePath filePath;

    @BeforeClass
    public static void setUpBeforeClass() {
        server = Main.startServer();
    }

    @Before
    public void setUp() throws Exception {
        javax.ws.rs.client.Client c = ClientBuilder.newBuilder().register(MultiPartFeature.class).build();
        target = c.target(Main.BASE_URI).path("files");
        createFile();
    }

    private void createFile(){
        File file = new File(baseDir,filename);
        try (OutputStream outputStream = new FileOutputStream(file)) {
            outputStream.write("qwerty".getBytes());
            outputStream.flush();
            outputStream.close();
            metadata = new Metadata(filename,fileContent.length(),file.lastModified(), MessageDigestSHA256.hash256(file));
            filePath = new FilePath(filename);
        }catch (Exception exc){
            exc.printStackTrace();
            assertTrue(false);
        }
    }

    @Test
    public void getMetadata() throws Exception {
        System.out.println("Test GetMetadata");
        Response postResponse =
                target.path("get_metadata").request(MediaType.APPLICATION_JSON)
                        .post(Entity.entity(filePath.toJSON(), MediaType.APPLICATION_JSON));
        JSONObject object = new JSONObject(postResponse.readEntity(String.class));

        assertEquals(metadata, new Metadata(object.getString("path"),
                object.getLong("size"),
                object.getLong("modified"),
                object.getString("hash")));
        assertEquals(200, postResponse.getStatus());
    }

    @Test
    public void delete() throws Exception {
        System.out.println("Test Delete");
        Response postResponse =
                target.path("delete").request(MediaType.APPLICATION_JSON)
                        .post(Entity.entity(filePath.toJSON(), MediaType.APPLICATION_JSON));
        try {
            JSONObject object = new JSONObject(postResponse.readEntity(String.class));

            assertEquals(filePath, new FilePath(object.getString("path")));
            assertEquals(200, postResponse.getStatus());
        }catch (JSONException exc){
            assertTrue(false);
        }

    }

    @Test
    public void listFolder() throws Exception {
        System.out.println("Test ListFolder");
        Response postResponse =
                target.path("list_folder").request(MediaType.APPLICATION_JSON)
                        .post(Entity.entity("", MediaType.TEXT_PLAIN));
        try {
            JSONArray jsonArray = new JSONArray(postResponse.readEntity(String.class));
            boolean found = false;
            Metadata respMetadata = null;
            for (int i = 0; i < jsonArray.length() && !found; i++) {
                if (filePath.getPath().equals(((JSONObject) jsonArray.get(i)).getString("path"))) {
                    respMetadata = new Metadata(((JSONObject) jsonArray.get(i)).getString("path"),
                            ((JSONObject) jsonArray.get(i)).getLong("size"),
                            ((JSONObject) jsonArray.get(i)).getLong("modified"),
                            ((JSONObject) jsonArray.get(i)).getString("hash"));
                    found = true;
                }

            }
            assertEquals(metadata, respMetadata);
            assertEquals(200, postResponse.getStatus());
        }catch (JSONException exc){
            assertTrue(false);
        }
    }

    @Test
    public void upload() throws Exception {
        System.out.println("Test Upload, Autorename = false, file not exist");
        File file = new File(baseDir,filename);
        FormDataMultiPart multiPart = new FormDataMultiPart();
        if (file != null) {
            FileDataBodyPart fileDataBodyPart = new FileDataBodyPart("file", file,
                    MediaType.APPLICATION_OCTET_STREAM_TYPE);
            multiPart.bodyPart(fileDataBodyPart);
        }
        Response postResponse =
                target.path("upload").request(MediaType.APPLICATION_JSON_TYPE)
                        .header("File-API-Arg","{\"path\":\""+filenameUploaded+"\",\"autorename\":false}")
                        .post(Entity.entity(multiPart,MediaType.MULTIPART_FORM_DATA_TYPE));

        try {
            JSONObject object = new JSONObject(postResponse.readEntity(String.class));
            assertEquals(metadata, new Metadata(filename,
                    object.getLong("size"),
                    metadata.getModified(),
                    object.getString("hash")));
            assertEquals(200, postResponse.getStatus());
        }catch (JSONException exc){
            assertTrue(false);
        }
    }

    @Test
    public void download() throws Exception {
        System.out.println("Test Download");
        Response postResponse =
                target.path("download").request(MediaType.APPLICATION_JSON)
                        .post(Entity.entity(filePath.toJSON(), MediaType.APPLICATION_JSON));

        try {
            JSONObject object = new JSONObject(postResponse.getHeaderString("File-API-Result"));

            assertEquals(metadata, new Metadata(object.getString("path"),
                    object.getLong("size"),
                    object.getLong("modified"),
                    object.getString("hash")));
            InputStream inputStream = postResponse.readEntity(InputStream.class);
            if (inputStream != null) {
                File file = new File(baseDir, filenameDownloaded);
                Files.copy(inputStream, file.toPath());
                assertEquals(MessageDigestSHA256.hash256(file), metadata.getHash());
            } else {
                assertTrue(false);
            }

            assertEquals(200, postResponse.getStatus());
        }catch (JSONException exc){
            assertTrue(false);
        }
    }

    @After
    public void tearDown() throws Exception {
        File file;
        if((file = new File(baseDir,filename)).exists()){
            if(!file.delete())
                System.out.println("Error delete file");
        }
        if((file = new File(baseDir,filenameDownloaded)).exists()){
            if(!file.delete())
                System.out.println("Error delete file downloaded");
        }
    }

    @AfterClass
    public static void tearDownAfterClass(){
        File file;
        if((file = new File(baseDir,filename)).exists()){
            if(!file.delete())
                System.out.println("Error delete file");
        }
        if((file = new File(baseDir,filenameDownloaded)).exists()){
            if(!file.delete())
                System.out.println("Error delete file downloaded");
        }
        if((file = new File(baseDir,filenameUploaded)).exists()){
            if(!file.delete())
                System.out.println("Error delete file uploaded");
        }
        if((file = new File(baseDir,filenameUploaded)).exists()){
            if(!file.delete())
                System.out.println("Error delete file uploaded");
        }
        if((file = new File(baseHash,filenameUploaded)).exists()){
            if(!file.delete())
                System.out.println("Error delete hash file uploaded");
        }
        if((file = new File(baseHash,filename)).exists()){
            if(!file.delete())
                System.out.println("Error delete hash file");
        }
        server.stop();
    }


}