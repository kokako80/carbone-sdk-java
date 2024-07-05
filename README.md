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

/** Generate the document */
try{
    CarboneDocument report = carboneServices.render(json ,"/path/to/template.docx");
}
catch(CarboneException e)
{
    // handle error
    System.out.println("Error message : " + e.getMessage() + "Status code : " + e.getHttpStatus());
}

// Get the name of the document with the `getName()`. For instance the name of the document, based on the JSON, is: "invoice-AF128.pdf"
try (FileOutputStream outputStream = new FileOutputStream(report.getName())) {
    /** Save the generated document */
    outputStream.write(report.getFileContent());
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
    - [Set API Version](#set-api-version)
- [Build commands](#build-commands)
- [Test commands](#test-commands)

### Carbone SDK Constructor

**Definition**

```java
public CarboneServicesFactory.CARBONE_SERVICES_FACTORY_INSTANCE.create(String... config);
```

**Example**

Example of a new SDK instance for **Carbone Cloud**:
Get your API key on your Carbone account: https://account.carbone.io/.
```java
// For Carbone Cloud, provide your API Access Token as first argument:
ICarboneServices carboneServices = CarboneServicesFactory.CARBONE_SERVICES_FACTORY_INSTANCE.create("API_TOKEN");
```

Example of a new SDK instance for **Carbone On-premise** or **Carbone On-AWS**:
```java
// Define the URL of your Carbone On-premise Server or AWS EC2 URL:
CarboneServicesFactory.CARBONE_SERVICES_FACTORY_INSTANCE.SetCarboneUrl("ON_PREMISE_URL");
// Then get a new instance by providing an empty string to the "create" function:
ICarboneServices carboneServices = CarboneServicesFactory.CARBONE_SERVICES_FACTORY_INSTANCE.create("");
```

### Generate and Download Document

**Prototype**

```java
public CarboneDocument render(String jsonData, String pathOrTemplateID) throws CarboneException;
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
public String renderReport(String renderData, String templateId) throws CarboneException;
```

The renderReport function takes a template ID as `String`, and the JSON data-set as `String`.
It return a `renderId`, you can pass this `renderId` at [getReport](#download-document-only) for download the document.

**Example**

```java
ICarboneServices carboneServices = CarboneServicesFactory.CARBONE_SERVICES_FACTORY_INSTANCE.create(apiKey);

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
public CarboneDocument getReport(String renderId) throws CarboneException;
```

Download a generated document from a render ID as `String`.

The getReport function returns a `CarboneDocument`, it provides two methods:
* **getFileContent()**: Return the document as `byte[]`.
* **getName()**: Return the document name as `String`.

**Example**

```java
ICarboneServices carboneServices = CarboneServicesFactory.CARBONE_SERVICES_FACTORY_INSTANCE.create(apiKey);
try{
    CarboneDocument render = carboneServices.getReport(renderId);
}
catch(CarboneException e)
{
    System.out.println("Error message : " + e.getMessage() + "Status code : " + e.getHttpStatus());
}

// Get the name of the document with the `getName()`.
try (FileOutputStream outputStream = new FileOutputStream(render.getName())) {
    // Save the file
    outputStream.write(render.getFileContent());
}
```


### Add Template

**Definition**

```java
public String addTemplate(byte[] templateFile) throws CarboneException, IOException;
```
or

```java
public String addTemplate(String templatePath) throws CarboneException, IOException;
```

Add a template as path `String` or as `byte[]` and the function return the template ID as `String`.

**Example**
 
Add a template as file path:
```java
ICarboneServices carboneServices = CarboneServicesFactory.CARBONE_SERVICES_FACTORY_INSTANCE.create(apiKey);

try{
    String templateId = carboneServices.addTemplate("/path/to/template.docx");
}
catch(CarboneException e)
{
    System.out.println("Error message : " + e.getMessage() + "Status code : " + e.getHttpStatus());
}

System.out.println(templateId);
```

Add a template as `byte[]`:
```java
ICarboneServices carboneServices = CarboneServicesFactory.CARBONE_SERVICES_FACTORY_INSTANCE.create(apiKey);

try{
    String templateId = carboneServices.addTemplate(Files.readAllBytes(Paths.get("/path/to/template.docx")));
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
public boolean deleteTemplate(String templateId) throws CarboneException;
```

Delete a template by providing a template ID as `String`, and it returns whether the request succeeded as a `Boolean`.

**Example**

```java
ICarboneServices carboneServices = CarboneServicesFactory.CARBONE_SERVICES_FACTORY_INSTANCE.create(apiKey);

try{
    boolean result = carboneServices.deleteTemplate(templateId.get());
}
catch(CarboneException e)
{
    System.out.println("Error message : " + e.getMessage() + "Status code : " + e.getHttpStatus());
}

System.out.println(result);
```

## Get Template

**Definition**

```java
public byte[] getTemplate(String templateId) throws CarboneException;
```

Provide a template ID as `String` and it returns the file as `byte[]`.

**Example**

```java
// Download the template
try{
    byte[] templateBytes = carboneServices.getTemplate("TEMPLATE_ID");
}
catch(CarboneException e)
{
    System.out.println("Error message : " + e.getMessage() + "Status code : " + e.getHttpStatus());
}

// Save the template file
try (FileOutputStream stream = new FileOutputStream("./template.docx")) {
    stream.write(templateBytes);
} catch (IOException ioe) {
    // handle error
}
```

### Set Carbone Url

**Definition**

```java
public void SetCarboneUrl(String CARBONE_URL);
```

Set the API URL for Carbone On-premise or Carbone On-AWS.

**Example**
```java
CarboneServicesFactory.CARBONE_SERVICES_FACTORY_INSTANCE.SetCarboneUrl("API_URL");
ICarboneServices carboneServices = CarboneServicesFactory.CARBONE_SERVICES_FACTORY_INSTANCE.create(API_TOKEN);
```

### Get API Status

**Definition**

```java
public String getStatus() throws CarboneException;
```

The function requests the Carbone API to get the current status and version as `String`.

**Example**

```java
ICarboneServices carboneServices = CarboneServicesFactory.CARBONE_SERVICES_FACTORY_INSTANCE.create(apiKey);

try{
    String status = carboneServices.getStatus();
}
catch(CarboneException e)
{
    System.out.println("Error message : " + e.getMessage() + "Status code : " + e.getHttpStatus());
}

System.out.println(status);
// Result: "{\"success\":true,\"code\":200,\"message\":\"OK\",\"version\":\"4.22.8\"}"
```

### Set API Version

Specify the version of the Carbone CLoud API you want to request as second argument of the constructor.
By default, all requested are made to the Carbone API version `4`.

```java
ICarboneServices carboneServices = CarboneServicesFactory.CARBONE_SERVICES_FACTORY_INSTANCE.create("CARBONE_API_TOKEN", "3");
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
