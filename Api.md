# Carbone API Java SDK

The Carbone Java SDK provides a simple interface to communicate with Carbone Cloud API.

## Install the Java SDK

```xml
<dependency>
    <groupId>io.carbone</groupId>
    <artifactId>CarboneSDK</artifactId>
    <version>1.1.0</version>
</dependency>
```

## Quickstart with the Java SDK

Try the following code to render a report in 10 seconds. Just replace your API key and version, the template you want to render, and the data object. Get your API key on your Carbone account: https://account.carbone.io/.

```java
    ICarboneServices carboneServices = CarboneServicesFactory.CARBONE_SERVICES_FACTORY_INSTANCE.create(apiKey, version);
    String json = "{ \"data\": { \"firstname\": \"John\", \"lastname\": \"wick\"}";
    CarboneDocument documentRender = carboneServices.render("Use/your/local/path", json)
    try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
            outputStream.write(ducumentRender.getFileContent());
        }
```

## Java SDK API

### Functions overview

- [CarboneSDK Constructor](#carbonesdk-constructor)
- [Render function](#render_report)
- [Get a template](#get_report)
- [Add a template](#add_template)
- [Delete a template](#delete_template)
- [Get a template](#get_template)
- [Generate a template ID](#generate_template_Id)
- [Set access token](#set_access_URI)
- [Get API status](#get_status)

### CarboneSDK Constructor
**Definition**
```java

# Carbone access token passed as parameter
String token = "ACCESS-TOKEN";
# Carbone access token passed as environment variable "CARBONE_TOKEN"
String token = "";

# Carbone the last version of the api is put automatically at the last version
String version = "";
```
Constructor to create a new instance of CarboneSDK.
The access token can be pass as an argument or by the environment variable "CARBONE_TOKEN".
Get your API key on your Carbone account: https://account.carbone.io/.

### render_report
**Definition**
```java
def String renderReport(Object renderData, String templateId)
```
The render function takes `templateID` a template ID, `renderData` a stringified JSON.

It returns the report as a `bytes` and a unique report name as a `string`. Carbone engine deletes files that have not been used for a while. By using this method, if your file has been deleted, the SDK will automatically upload it again and return you the result.

When a **template ID** is passed as an argument, the function renders with [render_report](#render_report) then call [get_report](#get_report) to return the report. If the template ID does not exist, an error is returned.


**Example**

```java

ICarboneServices carboneServices = CarboneServicesFactory.CARBONE_SERVICES_FACTORY_INSTANCE.create(apiKey, version);

String json = "{ \"data\": { \"firstname\": \"John\", \"lastname\": \"wick\"}";
String renderId = carboneServices.renderReport(jsonObj, "Use/your/local/path");

System.out.println(renderId)
```

### get_report
**Definition**
```java
def CarboneDocument getReport(String renderId)
```

**Example**

```java
ICarboneServices carboneServices = CarboneServicesFactory.CARBONE_SERVICES_FACTORY_INSTANCE.create(apiKey, version);

CarboneDocument renderDocument = carboneServices.getReport(renderId);

System.out.println(renderDocument.getFileContent())
System.out.println(renderDocument.name())
```


### add_template
**Definition**
```java
def Optional<String> addTemplate(byte[] templateFile)
```
Add the template to the API and returns the response (that contains a `template_id`).

**Example**

```java
ICarboneServices carboneServices = CarboneServicesFactory.CARBONE_SERVICES_FACTORY_INSTANCE.create(apiKey, version);

Optional<String> templateId = carboneServices.addTemplate(Files.readAllBytes(testFilePath));

System.out.println(templateId.get())
```

### delete_template
**Definition**

```java
def void deleteTemplate(String templateId)
```
**Example**
```java

ICarboneServices carboneServices = CarboneServicesFactory.CARBONE_SERVICES_FACTORY_INSTANCE.create(apiKey, version);

carboneServices.deleteTemplate(templateId.get());
```
### generate_template_Id
**Definition**
```java
def String generateTemplateId(String path)
```
The Template ID is predictable and idempotent, pass the template path and it will return the `template_id`.

```java
ICarboneServices carboneServices = CarboneServicesFactory.CARBONE_SERVICES_FACTORY_INSTANCE.create(apiKey, version);

String path = "Use/your/local/path";
String newTemplateId = generateTemplateId(path);

System.out.println(newTemplateId)
```

### set_access_URI
**Definition**
```java
def void SetCARBONEURI(String newCARBONE_URI)
```
It sets the Carbone access token.

**Example**
```java

ICarboneServices carboneServices = CarboneServicesFactory.CARBONE_SERVICES_FACTORY_INSTANCE.create(apiKey, version);

carboneService.SetCARBONEURI("NEW_CARBONE_RENDER_API_ACCESS_TOKEN");
```
### get_status
**Definition**

```java
def getStatus()
```

**Example**
```java

ICarboneServices carboneServices = CarboneServicesFactory.CARBONE_SERVICES_FACTORY_INSTANCE.create(apiKey, version);

String status = carboneServices.getStatus()

System.out.println(status)

```


### Build the project

- at the root of the project
```maven

mvn clean

mvn compile

mvn package

```

- in other terminal 

````
mvn install:install-file -Dfile=/your/local/project  -DgroupId= io.carbone -DartifactId=CarboneSDK -Dversion= x.x.x  -Dpackaging=jar
```