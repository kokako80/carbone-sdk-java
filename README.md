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
    String json = "{ \"data\": { \"id\": \"AF128\",\"firstname\": \"John\", \"lastname\": \"wick\"}, \"reportName\": \"invoice-{d.id}\",\"convertTo\": \"pdf\"}";
    CarboneDocument render = carboneServices.render(json ,"Use/your/local/path")
    try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
            outputStream.write(render.getFileContent());
        }
    // Get the name of the document with the `getName()`. For instance the name of the document, based on the JSON, is: "invoice-AF128.pdf"
    System.out.println(renderDocument.getName())
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

# Carbone if you pass a empty string the fourth is automatically apply
String version = "";
```
Constructor to create a new instance of CarboneSDK.
The access token can be pass as an argument or by the environment variable "CARBONE_TOKEN".
Get your API key on your Carbone account: https://account.carbone.io/.

### render_report
**Definition**
```java
def String renderReport(String renderData, String templateId)
```
The render function takes `templateID` a template ID, `renderData` a stringified JSON.

It return a `renderId`, you can pass this `renderId` at [get_report](#get_report) for download the document.

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
It returns the report as a `bytes` and a unique report name as a `string`. Carbone engine deletes files that have not been used for a while. By using this method, if your file has been deleted, the SDK will automatically upload it again and return you the result.

**Example**

```java
ICarboneServices carboneServices = CarboneServicesFactory.CARBONE_SERVICES_FACTORY_INSTANCE.create(apiKey, version);
try{
    CarboneDocument renderDocument = carboneServices.getReport(renderId);
}
catch(CarboneException e)
{
    e.printStackTrace();
}

try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
        outputStream.write(renderDocument.getFileContent());
    }
// Get the name of the document with the `getName()`.
System.out.println(renderDocument.getName())
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

try{
    String templateId = carboneServices.addTemplate(Files.readAllBytes(testFilePath));
}
catch(CarboneException e)
{
    e.printStackTrace();
}

System.out.println(templateId);
```

### delete_template
**Definition**

```java
def boolean deleteTemplate(String templateId)
```
**Example**
```java

ICarboneServices carboneServices = CarboneServicesFactory.CARBONE_SERVICES_FACTORY_INSTANCE.create(apiKey, version);

try{
    boolean bool = carboneServices.deleteTemplate(templateId.get());
}
catch(CarboneException e)
{
    e.printStackTrace();
}

System.out.println(bool);
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
try{
    String newTemplateId = generateTemplateId(path);
}
catch(Exception e)
{
    e.printStackTrace();
}
catch (NoSuchAlgorithmException e) {
    e.printStackTrace();
}
catch (IOException e) {
    e.printStackTrace();
}

System.out.println(newTemplateId);
```

### set_access_URI
**Definition**
```java
def void SetCarboneUrl(String newCARBONE_URL)
```
It sets the Carbone access token.

**Example**
```java

try{
    CarboneServicesFactory.CARBONE_SERVICES_FACTORY_INSTANCE.SetCarboneUrl("NEW_CARBONE_RENDER_API_ACCESS_TOKEN");
}
catch(Exception e)
{
    e.printStackTrace();
}

ICarboneServices carboneServices = CarboneServicesFactory.CARBONE_SERVICES_FACTORY_INSTANCE.create(apiKey, version);


System.out.println(CarboneServicesFactory.CARBONE_SERVICES_FACTORY_INSTANCE.GetCarboneUrl());

```
### get_status
**Definition**

```java
def getStatus()
```

**Example**
```java

ICarboneServices carboneServices = CarboneServicesFactory.CARBONE_SERVICES_FACTORY_INSTANCE.create(apiKey, version);

try{
    String status = carboneServices.getStatus();
}
catch(Exception e)
{
    e.printStackTrace();
}
catch (IOException e) {
    
    e.printStackTrace();
}

System.out.println(status);

```


### Build the project

- at the root of the project
```maven

mvn clean

mvn compile

mvn package

```

- in other terminal 

```` maven
mvn install:install-file -Dfile=/your/local/project  -DgroupId= io.carbone -DartifactId=CarboneSDK -Dversion= x.x.x  -Dpackaging=jar

```

- in the pom.xml

```
<dependency>
    <groupId>io.carbone</groupId>
    <artifactId>CarboneSDK</artifactId>
    <version>x.x.x</version>
</dependency>

```