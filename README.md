# Carbone API Java SDK

The Carbone Java SDK provides a simple interface to communicate with Carbone Cloud API to generate documents.

## Install the Java SDK

```xml
<dependency>
    <groupId>io.carbone</groupId>
    <artifactId>carbone-sdk</artifactId>
    <version>2.0.0</version>
</dependency>
```

## Quickstart with the Java SDK

Try the following code to render a report in 10 seconds. Just insert your API key, the template path you want to render, and the JSON data-set as string. Get your API key on your Carbone account: https://account.carbone.io/.

```java
ICarboneServices carboneServices = CarboneServicesFactory.CARBONE_SERVICES_FACTORY_INSTANCE.create(API_KEY);
String json = "{ \"data\": { \"id\": \"AF128\",\"firstname\": \"John\", \"lastname\": \"wick\"}, \"reportName\": \"invoice-{d.id}\",\"convertTo\": \"pdf\"}";

/** To generate the document */
try{
    CarboneDocument report = carboneServices.render(json ,"/path/to/template.docx");
}
catch(CarboneException e)
{
    // handle error
    System.out.println("Error message : " + e.getMessage() + "Status code : " + e.getHttpStatus());
}

/** To save the generated document */
try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
    outputStream.write(report.getFileContent());
    // Get the name of the document with the `getName()`. For instance the name of the document, based on the JSON, is: "invoice-AF128.pdf"
    System.out.println(report.getName())
} catch (IOException ioe) {
    // handle error
}

```

## Java SDK API

### Table of content

- SDK functions:
    - [CarboneSDK Constructor](#carbone-sdk-constructor)
    - [Generate and Download a Document](#generate-and-download-document)
    - [Generate a Document Only](#generate-document-only)
    - [Download a Document Only](#download-document-only)
    - [Add a Template](#add-template)
    - [Delete a Template](#delete-template)
    - [Get a Template](#get-template)
    - [Set Carbone URL](#set-carbone-url)
    - [Get API status](#get-api-status)
    - [Generate a template ID](#generate-template-Id)
- [Build commands](#build-commands)
- [Test commands](#test-commands)

### Carbone SDK Constructor
**Definition**
```java
def CarboneServicesFactory.CARBONE_SERVICES_FACTORY_INSTANCE.create(String... config);
```

**Example of SDK config for Carbone Cloud**

Constructor to create a new instance of the Carbone SDK.
Get your API key on your Carbone account: https://account.carbone.io/.
```java
// For Carbone Cloud, provide your API Access Token as first argument:
ICarboneServices carboneServices = CarboneServicesFactory.CARBONE_SERVICES_FACTORY_INSTANCE.create("CARBONE_API_TOKEN");
// You can specify which version of Carbone CLoud API you want to request as second argument, by default the version is "4":
ICarboneServices carboneServices = CarboneServicesFactory.CARBONE_SERVICES_FACTORY_INSTANCE.create("CARBONE_API_TOKEN", "4");
// You can define the API Token as Environment Variable under the "CARBONE_API_TOKEN", then you can leave the create function empty:
ICarboneServices carboneServices = CarboneServicesFactory.CARBONE_SERVICES_FACTORY_INSTANCE.create("");
```

**Example of SDK config for Carbone On-premise**
```java
// For Carbone On-premise, define the URL of your Carbone Server:
CarboneServicesFactory.CARBONE_SERVICES_FACTORY_INSTANCE.SetCarboneUrl("NEW_CARBONE_RENDER_API_ACCESS_TOKEN");
// Then get a new instance by providing an empty string to the "create" function:
ICarboneServices carboneServices = CarboneServicesFactory.CARBONE_SERVICES_FACTORY_INSTANCE.create("");
```

### Generate and Download Document

**Prototype**
```java
CarboneDocument render(String jsonData, String pathOrTemplateID) throws CarboneException
```

The render function generates a document using a specified template and data. It takes two parameters:
* jsonData: A stringified JSON containing the data to populate the template.
* pathOrTemplateID: The path to your local file or a template ID.

The render function returns a `CarboneDocument`, it provides two methods:
* **getFileContent()**: Return the document as `byte[]`.
* **getName()**: Return the document name as `String`.

**Function Behavior**

1. Template File Path as Second Argument: 
    - If a template file path is provided, the function first checks if the template has been uploaded to the server.
    - If the template has not been uploaded, it calls the addTemplate function to upload the template and generate a new template ID.
    - The function then calls [renderReport](#generate-document-only) followed by [getReport](#download-document-only) to generate and retrieve the report.
    - If the provided path does not exist, an error is returned.
2. Template ID as Second Argument:
    - If a template ID is provided, the function calls [renderReport](#generate-document-only) to generate the report. It then calls [getReport](#download-document-only) to retrieve the generated report.
    - If the template ID does not exist, an error is returned.

> 🔎 Tip: Providing the Template File Path is the best solution, you won't have to deal with template IDs.

**Example**

```java
try{
    CarboneDocument report = carboneServices.render(json ,"/path/to/template.xlsx");
    // report.getFileContent() returns the generated document as byte[]
    // report.getName() returns the document name as String
}
catch(CarboneException e)
{
    // handle error
    System.out.println("Error message : " + e.getMessage() + "Status code : " + e.getHttpStatus());
}
```

### Generate Document Only

**Definition**
```java
def String renderReport(String renderData, String templateId)
```
The render function takes `templateID` a template ID, `renderData` a stringified JSON.

It return a `renderId`, you can pass this `renderId` at [get_report](#get_report) for download the document.

**Example**

```java
ICarboneServices carboneServices = CarboneServicesFactory.CARBONE_SERVICES_FACTORY_INSTANCE.create(apiKey, version);

    String json = "{ \"data\": { \"id\": \"AF128\",\"firstname\": \"John\", \"lastname\": \"wick\"}, \"reportName\": \"invoice-{d.id}\",\"convertTo\": \"pdf\"}";
try{
    String renderId = carboneServices.renderReport(jsonObj, "Use/your/local/path");
}
catch(CarboneException e)
{
    System.out.println("Error message : " + e.getMessage() + "Status code : " + e.getHttpStatus());
}

System.out.println(renderId);
```

### Download Document Only

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
    System.out.println("Error message : " + e.getMessage() + "Status code : " + e.getHttpStatus());
}

try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
        outputStream.write(renderDocument.getFileContent());
    }
// Get the name of the document with the `getName()`.
System.out.println(renderDocument.getName())
```


### Add Template
**Definition**
```java
def Optional addTemplate(byte[] templateFile)
```
or

```java
def Optional addTemplate(String templatePath)
```
Add the template to the API and returns the response (that contains a `template_id`).

**Example**

```java
ICarboneServices carboneServices = CarboneServicesFactory.CARBONE_SERVICES_FACTORY_INSTANCE.create(apiKey, version);

String templatePath = "Use/your/local/path";
Path filPath = Paths.get(filename);
byte[] templateFile = Files.readAllBytes(filePath);

try{
    String templateId = carboneServices.addTemplate(Files.readAllBytes(templateFile));
}
catch(CarboneException e)
{
    System.out.println("Error message : " + e.getMessage() + "Status code : " + e.getHttpStatus());
}

System.out.println(templateId);
```

or 

```java
ICarboneServices carboneServices = CarboneServicesFactory.CARBONE_SERVICES_FACTORY_INSTANCE.create(apiKey, version);

String templatePath = "Use/your/local/path";
try{
    String templateId = carboneServices.addTemplate(templatePath);
}
catch(CarboneException e)
{
    System.out.println("Error message : " + e.getMessage() + "Status code : " + e.getHttpStatus());
}

System.out.println(templateId);
```

### Delete Template
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
    System.out.println("Error message : " + e.getMessage() + "Status code : " + e.getHttpStatus());
}

System.out.println(bool);
```

### Generate Template Id
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

### Set Carbone Url
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
catch(CarboneException e)
{
    System.out.println("Error message : " + e.getMessage() + "Status code : " + e.getHttpStatus());
}

ICarboneServices carboneServices = CarboneServicesFactory.CARBONE_SERVICES_FACTORY_INSTANCE.create(apiKey, version);


System.out.println(CarboneServicesFactory.CARBONE_SERVICES_FACTORY_INSTANCE.GetCarboneUrl());

```
### Get API Status
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
catch(CarboneException e)
{
    System.out.println("Error message : " + e.getMessage() + "Status code : " + e.getHttpStatus());
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


## Build commands

At the root of the SDK repository run:
```sh
mvn clean && mvn compile && mvn package
```
Then you can create a local build of the SDK:
``` sh
mvn install:install-file -Dfile=/your/local/file.jar  -DgroupId=io.carbone -DartifactId=CarboneSDK -Dversion=x.x.x  -Dpackaging=jar
```

In another Java project, you can load the local build of the SDK, in the pom.xml:
```xml
<dependency>
    <groupId>io.carbone</groupId>
    <artifactId>CarboneSDK</artifactId>
    <version>x.x.x</version>
</dependency>
```
Finally, compile your Java project with the SDK:
```sh
clean compile && mvn exec:java -Dexec.mainClass="local.test.CarboneCloudSdkJava
```

## Test commands

Execute unit tests:
```sh
mvn test
````
Execute unit tests with coverage:
```mvn
mvn clean test jacoco:report  
```
To get the coverage analysis, open the coverage file:
`./target/site/jacoco/index.html`

## Build the Javadoc documentation

```
mvn javadoc:javadoc
```

## 👤 History

The package was originaly made by Benjamin COLOMBE @bcolombe from [Tennaxia](https://www.tennaxia.com/), and open-sourced the code.
The Carbone.io team is now maintaining the SDK and will bring all futur evolutions.

## 🤝 Contributing

Contributions, issues and feature requests are welcome!

Feel free to check [issues page](https://github.com/carboneio/carbone-sdk-java/issues).

## Show your support

Give a ⭐️ if this project helped you!
