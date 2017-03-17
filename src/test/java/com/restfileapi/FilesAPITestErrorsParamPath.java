package com.restfileapi;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

/**
 * Created by Tatyana on 17.03.2017.
 */
@RunWith(value = Parameterized.class)
public class FilesAPITestErrorsParamPath {

    private static HttpServer server;
    private WebTarget target;

    String parameterPath;
    int codeErrorPath;

    @BeforeClass
    public static void setUpBeforeClass() {
        server = Main.startServer();
    }

    @Before
    public void setUp() throws Exception {
        javax.ws.rs.client.Client c = ClientBuilder.newBuilder().register(MultiPartFeature.class).build();
        target = c.target(Main.BASE_URI).path("files");
    }
    public FilesAPITestErrorsParamPath(String parameterPath, int codeErrorPath){
        this.parameterPath = parameterPath;
        this.codeErrorPath = codeErrorPath;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        Object[][] data = new Object[][] {
                { "", 400},
                { "{\"path\":}", 400},
                { "{\"path\":\"65e84be33532fb784c48129675f9eff3a682b27168c0ea744b2cf58ee02337c565e84be33532fb784c48129675f9eff3a682b27168c0ea744b2cf58ee02337c565e84be33532fb784c48129675f9eff3a682b27168c0ea744b2cf58ee02337c565e84be33532fb784c48129675f9eff3a682b27168c0ea744b2cf58ee02337c565e84be33532fb784c48129675f9eff3a682b27168c0ea744b2cf58ee02337c5\"}", 409},
                { "{\"path\":\"\\..\\aa.txt\"}", 400},
                { "{\"path\":\"/../aa.txt\"}", 409},
                { "{\"a\":\"a.txt\"{", 400},
                { "{\"path\":\"a.txt\"{", 400}

        };
        return Arrays.asList(data);
    }

    @Test
    public void getMetadata() throws Exception {
        System.out.println("Test GetMetadata");
        Response postResponse =
                target.path("get_metadata").request(MediaType.APPLICATION_JSON)
                        .post(Entity.entity(parameterPath, MediaType.APPLICATION_JSON));
        assertEquals(codeErrorPath, postResponse.getStatus());
    }

    @Test
    public void delete() throws Exception {
        System.out.println("Test Delete");
        Response postResponse =
                target.path("delete").request(MediaType.APPLICATION_JSON)
                        .post(Entity.entity(parameterPath, MediaType.APPLICATION_JSON));
        assertEquals(codeErrorPath, postResponse.getStatus());
    }

    @After
    public void tearDown() throws Exception {

    }

    @AfterClass
    public static void tearDownAfterClass(){
        server.stop();
    }


}