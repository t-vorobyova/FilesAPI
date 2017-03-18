# HTTP File API

Files API allows to work with files in one folder.
Syntax is similar to Dropbox API.

## Supported requests:

* get_metadata
* upload
* download
* list_folder
* delete

## Request formats:
HTTP POST requests with data supplied in JSON format.
JSON data is passed in request body or header depending on endpoint.

## Response format:
Response data is in result code, header and body.

## Errors
Errors are returned using standard HTTP error code syntax.

```
Code                            Description
400 BAD_REQUEST              Bad input parameter.
409 CONFLICT                 Specific error. Additional info is in JSON format in response body.
500 INTERNAL_SERVER_ERROR    Server-side error.
```

Specific error has following format (JSON): 
```
{
 "id": "",
 "summary": ""
}
```

# Endpoints

## get_metadata
returns file metadata including file name, size, last modified time and SHA256 hash

### Arguments
```
{
 "path": "filename"
}
```
JSON should be passed in request body

### Result
```
{
 "path": "filename"
 "size": "size in bytes"
 "modified": "last modified UNIX time"
 "hash": "SHA256 hash"
}
```
JSON is returned in response body

## delete
deletes file

### Arguments
```
{
 "path": "filename"
}
```
JSON should be passed in request body

### Result
```
{
 "path": "filename of deleted file"
}
```
JSON is returned in response body

## list_folder
returns list of files

### Arguments
no arguments

### Result
JSON array of the following JSON values:
```
{
 "path": "filename"
 "size": "size in bytes"
 "modified": "last modified UNIX time"
 "hash": "SHA256 hash"
}
```
JSON is returned in response body

## download
downloads file

### Arguments
```
{
 "path": "filename"
}
```
JSON should be passed in request body

### Result
Response body contains file content

Response header `File-API-Result` contains following JSON:
```
{
 "path": "filename"
 "size": "size in bytes"
 "modified": "last modified UNIX time"
 "hash": "SHA256 hash"
}
```

## upload
uploads file

### Arguments
Request should contain header `File-API-Arg` of the following format:
```
{
 "path": "filename"
 "autorename": "boolean value - automatically rename file in case of name conflict or rewrite otherwise"
}
```
File content is supplied as `multipart/form-data` format

### Result
Response body contains the following JSON:
```
{
 "path": "filename"
 "size": "size in bytes"
 "modified": "last modified UNIX time"
 "hash": "SHA256 hash"
}
```

# Running
To start File API Server run:
```
mvn exec:java
```
To start unit tests run:
```
mvn clean test
```