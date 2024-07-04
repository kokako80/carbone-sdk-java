# Changelog

All notable changes to this project will be documented in this file. This project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## 2.0.0
- Released on 2024/07/04: The package was originally made by Benjamin COLOMBE from Tennaxia. The Carbone team is now maintaining the SDK. This version brings all missing functions to interact with the Carbone API.
- The package is now available under the artifact `io.carbone`, [link to the Central Maven repository](https://central.sonatype.com/artifact/io.carbone/carbone-sdk).
- Added function `render`: Provide a template path and the JSON data-set, and the function will execute 3 actions: add the template, generate the document, and download the generated document.
- Added function `getStatus`: It return the current status and the version of the API as JSON String.
- Added function `getTemplate`: Provide a template ID and it returns the file as `byte[]`.
- Modified for the `create` constructor: it takes two optional arguments: the `Carbone API key` and the `API version`.
- Modified for the `deleteTemplate`: it returns whether the request succeeded as a Boolean.
- Modified for the `renderReport`: it does not take a Map for render option anymore as argument.
- Renamed `SetCarboneUri` to `SetCarboneUrl`. 
- Renamed `GetCarboneUri` to `GetCarboneUrl`. 
- Modified the `getReport` and `render` functions: They now return a `CarboneDocument` to get the document as Byte[] and the document name.
- Added units tests.

### v1.0.0
- Released on 2023/06/06