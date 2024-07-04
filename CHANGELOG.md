# Changelog

All notable changes to this project will be documented in this file. This project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## 2.0.0
- Released the 2024/07/04: The package was originaly made by Benjamin COLOMBE from Tennaxia. The Carbone team is now maintaining the SDK. This version bring all missings functions to interact with the Carbone API.
- The package is now available under the artifact `io.carbone`, link to the Central Maven repository: https://central.sonatype.com/artifact/io.carbone/carbone-sdk
- Added function `render`: Provide a template path, the JSON data-set, and the function will execute 3 actions: add the template, generate the document, and download the generated document.
- Added function `GetStatus`: It return the current status and the version of the API as JSON String.
- Added function `getTemplate`: Provide a template ID and it returns the file as `byte[]`.
- Modified for the `create` constructor: it takes two optional arguments: the `Carbone API key` and the `API version`.
- Modified for the `deleteTemplate`: it returns if the request succeed as a Boolean
- Modified: rename `SetCarboneUri` to `SetCarboneUrl`. 
- Modified: rename `GetCarboneUri` to `GetCarboneUrl`. 
- Modified: The `getReport` and `render` function:  return a `CarboneDocument` to get the document as Byte and the document name.

### v1.0.0
- Released the 2023/06/06