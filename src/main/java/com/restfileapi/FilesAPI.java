package com.restfileapi;

import com.restfileapi.entries.FilePath;
import com.restfileapi.entries.Error;
import com.restfileapi.entries.Metadata;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Tatyana on 14.03.2017.
 */
@Path("/files")
public class FilesAPI {

    @POST
    @Path("/get_metadata")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getMetadata(FilePath filePath) {
        if (!Validator.validate(filePath)) {
            return Response.status(Response.Status.CONFLICT).entity(Error.getError(Error.Type.INVALID_PARAMETER)).build();
        }
        try {
            return Response.ok(FileProvider.getInstance().getMetadata(filePath)).build();
        }catch (FileNotFoundException exc){
            return Response.status(Response.Status.CONFLICT).entity(Error.getError(Error.Type.FILE_NOT_FOUND)).build();
        }catch (IOException exc) {
            return Response.status(Response.Status.CONFLICT).entity(Error.getError(Error.Type.FILE_IO_ERROR)).build();
        }catch (Exception exc){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @POST
    @Path("/download")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response download(FilePath filePath) {
        if (!Validator.validate(filePath)) {
            return Response.status(Response.Status.CONFLICT).entity(Error.getError(Error.Type.INVALID_PARAMETER)).build();
        }
        Metadata res;
        try {
            res = FileProvider.getInstance().getMetadata(filePath);
        }catch (FileNotFoundException exc){
            return Response.status(Response.Status.CONFLICT).entity(Error.getError(Error.Type.FILE_NOT_FOUND)).build();
        }catch (IOException exc) {
            return Response.status(Response.Status.CONFLICT).entity(Error.getError(Error.Type.FILE_IO_ERROR)).build();
        }catch (Exception exc){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        InputStream inputStream;
        try {
            inputStream = FileProvider.getInstance().getFileInputStream(filePath);
        }catch (FileNotFoundException exc){
            return Response.status(Response.Status.CONFLICT).entity(Error.getError(Error.Type.FILE_NOT_FOUND)).build();
        }catch (Exception exc){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        return Response.ok().entity(inputStream).header("File-API-Result", res.toJSON()).build();
    }

    @POST
    @Path("/delete")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response delete(FilePath filePath) {
        if (!Validator.validate(filePath)) {
            return Response.status(Response.Status.CONFLICT).entity(Error.getError(Error.Type.INVALID_PARAMETER)).build();
        }
        try {
            FileProvider.getInstance().delete(filePath);
            return Response.ok(filePath).build();
        }catch (FileNotFoundException exc){
            return Response.status(Response.Status.CONFLICT).entity(Error.getError(Error.Type.FILE_NOT_FOUND)).build();
        }catch (SecurityException exc){
            return Response.status(Response.Status.CONFLICT).entity(Error.getError(Error.Type.FILE_DELETE_ERROR)).build();
        }catch (Exception exc){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @POST
    @Path("/list_folder")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listFolder() {
        try {
            return Response.ok(FileProvider.getInstance().getListFolder().toArray(new Metadata[0])).build();
        } catch (FileNotFoundException exc) {
            return Response.status(Response.Status.CONFLICT).entity(Error.getError(Error.Type.FILE_NOT_FOUND)).build();
        } catch (IOException exc) {
            return Response.status(Response.Status.CONFLICT).entity(Error.getError(Error.Type.FILE_IO_ERROR)).build();
        } catch (Exception exc) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @POST
    @Path("/upload")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response upload(@HeaderParam("File-API-Arg") String arg, @FormDataParam("file") InputStream inputStream) {
        boolean autorename=true;
        String path;
        try {
            JSONObject jsonObject = new JSONObject(arg);
            path = jsonObject.getString("path");
            autorename = jsonObject.getBoolean("autorename");
        }catch (JSONException exc){
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        if (!Validator.validate(new FilePath(path))) {
            return Response.status(Response.Status.CONFLICT).entity(Error.getError(Error.Type.INVALID_PARAMETER)).build();
        }
        Metadata metadata;
        try {
            path = FileProvider.getInstance().upload(inputStream, path, autorename);
            metadata = FileProvider.getInstance().getMetadata(path);
        }catch (IOException exc) {
            return Response.status(Response.Status.CONFLICT).entity(Error.getError(Error.Type.FILE_IO_ERROR)).build();
        } catch (Exception exc) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        return Response.ok(metadata).build();
    }

}
