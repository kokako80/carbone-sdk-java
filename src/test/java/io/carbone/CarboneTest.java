package io.carbone;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.Assert.assertArrayEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


import org.junit.Test;

import feign.Request;
import feign.Response;

public class CarboneTest {
    String CARBONE_URI = "https://api.carbone.io";
    ICarboneTemplateClient carboneTemplate = mock(CarboneTemplateClient.class);
    ICarboneRenderClient carboneRender = mock(CarboneRenderClient.class);
    ICarboneStatusClient carboneStatus = mock(CarboneStatusClient.class);
    ICarboneServices carboneServices = CarboneServicesFactory.CARBONE_SERVICES_FACTORY_INSTANCE.create(carboneTemplate, carboneRender, carboneStatus);
    
    String directory = System.getProperty("user.dir");

    @Test
    public void TestAddTemplate() throws CarboneException, IOException, InterruptedException {

        CarboneResponse.CarboneResponseData responseData = CarboneResponse.CarboneResponseData.builder()
                                                                                            .templateId("fb9241ea2218ffd8f974110e539386384620244618c2efbf182b7bd47242987B")
                                                                                            .build();
        CarboneResponse mockedResponse = CarboneResponse.builder()
                                                        .success(true)
                                                        .data(responseData)
                                                        .build();
        String filename = "/src/test/java/io/carbone/template3.odt";
        Path filePath = Paths.get(directory, filename);
        byte[] fileBytes = Files.readAllBytes(filePath);

        when(carboneTemplate.addTemplate(fileBytes)).thenReturn(mockedResponse);
            
        Optional<String> resp =  carboneServices.addTemplate(Files.readAllBytes(filePath));
        assertEquals(resp.get(), mockedResponse.getData().templateId);

    }

    @Test
    public void Test_Add_Template_Error_Path() throws CarboneException, IOException
    {
        String filename = "/src/test/java/io/carbone/template.odt";
        Path filePath = Paths.get(filename);
        try{
            carboneTemplate.addTemplate(Files.readAllBytes(filePath));
        }
        catch(NoSuchFileException e)
        {
            assertEquals(filename, e.getMessage());
        }
    }

    @Test
    public void TestDeleteTemplate() throws CarboneException
    {
        CarboneResponse mockedResponse = CarboneResponse.builder()
                                                    .success(true)
                                                    .error(null)
                                                    .build();
        when(carboneTemplate.deleteTemplate("fb9241ea2218ffd8f974110e539386384620244618c2efbf182b7bd47242987a")).thenReturn(mockedResponse);

        CarboneResponse resp = carboneTemplate.deleteTemplate("fb9241ea2218ffd8f974110e539386384620244618c2efbf182b7bd47242987a");
        
        assertEquals(resp.isSuccess(), mockedResponse.isSuccess());
    }

    @Test 
    public void Test_Delete_Template_Error_Already_Deleted() throws CarboneException
    {
        CarboneResponse mockedResponse = CarboneResponse.builder()
                                                    .success(false)
                                                    .error("Error: Cannot remove template, does it exist ?")
                                                    .build();
        when(carboneTemplate.deleteTemplate("fb9241ea2218ffd8f974110e539386384620244618c2efbf182b7bd47242987a")).thenReturn(mockedResponse);

        CarboneResponse resp = carboneTemplate.deleteTemplate("fb9241ea2218ffd8f974110e539386384620244618c2efbf182b7bd47242987a");
                                                    
        assertEquals(resp.getError(), mockedResponse.getError());
    }

@Test
    public void TestGenerateTemplateId()
    {
        String filename = "/src/test/java/io/carbone/template3.odt";
        Path filePath = Paths.get(directory, filename);

        String templateId = carboneServices.generateTemplateId(filePath.toString());

        assertEquals(templateId, "fb9241ea2218ffd8f974110e539386384620244618c2efbf182b7bd47242987a");
    }

    @Test
    public void TestGetTemplate() throws IOException, CarboneException {
        String filename = "/src/test/java/io/carbone/template3.odt";
        Path filePath = Paths.get(directory, filename);
        byte[] fileBytes = Files.readAllBytes(filePath);
        String templateId = carboneServices.generateTemplateId(filePath.toString());

        when(carboneTemplate.getTemplate(anyString())).thenReturn(new CarboneFileResponse(fileBytes));

        CarboneFileResponse resp = carboneTemplate.getTemplate(templateId);

        assertArrayEquals(fileBytes, resp.getFileContent());
    }

    @Test
    public void TestRenderTemplate() throws CarboneException
    {
        CarboneResponse.CarboneResponseData responseData = CarboneResponse.CarboneResponseData.builder()
                                                                                            .renderId("MTAuMjAuMTEuNDAgICAgCRLu7jNNEe84ubAa82ofaAcmVwb3J0.pdf")
                                                                                            .build();
        CarboneResponse mockedResponse = CarboneResponse.builder()
                                                        .success(true)
                                                        .data(responseData)
                                                        .build();
        String filename = "/src/test/java/io/carbone/template3.odt";
        Path filePath = Paths.get(directory, filename);
        String json = "{ \"data\": { \"invoiceId\": \"FR-2023-781\", \"invoiceDate\": \"2023-01-17\", \"name\": \"Quentin Le Forestier\", \"phone\": \"+33 (0)2 23 12 33 24\", \"email\": \"tim+serverpro@carbone.io\", \"partner\": { \"name\": \"Corp\", \"phone\": \"+33 (0)6 27 89 01 23\", \"email\": \"quentinleforestierleforestier0@gmail.com\", \"vat\": \"FR45899106785\", \"siren\": \"899106785\", \"address\": { \"street\": \"12 Doctor Street\", \"postalcode\": 34920, \"city\": \"Anger\", \"country\": \"France\" } }, \"note\": \"Our team is confident that <b>this cutting-edge technology</b> will enhance your operations and take your business to the next level. Should you have any questions or concerns, please don't hesitate to reach out to us.\", \"subscriptionFromDate\": \"2023-02-02T07:28:05+00:00\", \"subscriptionToDate\": \"2024-04-18T07:28:05+00:00\", \"products\": [ { \"name\": \"<b>Rackmount case</b> (With bays for multiple 3.5-inch drives and room for the motherboard and other components.)\", \"exTaxTotal\": 1000, \"unit\": 1000, \"vat\": 20, \"qty\": 1 }, { \"name\": \"<b>Motherboard</b> (has the necessary number of SATA ports for your storage needs)\", \"exTaxTotal\": 150, \"unit\": 150, \"vat\": 20, \"qty\": 1 }, { \"name\": \"<b>3.5-inch SATA hard drives</b> (20TO)\", \"exTaxTotal\": 1000, \"unit\": 100, \"vat\": 20, \"qty\": 10 }, { \"name\": \"<b>Processor</b> ( A low-power consumption processor)\", \"exTaxTotal\": 100, \"unit\": 100, \"vat\": 20, \"qty\": 1 }, { \"name\": \"<b>4GB DDR5 RAM</b>\", \"exTaxTotal\": 120, \"unit\": 30, \"vat\": 20, \"qty\": 4 } ], \"exTaxTotal\": 1870, \"taxTotal\": 374, \"inTaxTotal\": 2244, \"amountRemaining\": 1044, \"invoicePaymentList\": [ { \"paymentDate\": \"2023-02-03T07:28:05+00:00\", \"typeSelect\": \"Paiement\", \"amount\": 1200 } ], \"payment\": { \"name\": \"SEPA Transfer\", \"typeSelect\": \"SEPAtransfer\", \"condition\": \"Upon receipt of invoice\", \"bankLabel\": \"Revolut\", \"bankBIC\": \"REVOGB2L\", \"bankIBAN\": \"FR2112739000409352423869N75\" } }, \"convertTo\" : { \"formatName\" : \"pdf\" } }";

        when(carboneRender.renderReport(json, filePath.toString())).thenReturn(mockedResponse);

        CarboneResponse resp = carboneRender.renderReport(json, filePath.toString());

        assertEquals(resp.getData().renderId, mockedResponse.getData().renderId);
    }

    @Test
    public void TestGetReport() throws IOException, CarboneException
    {
        String renderId = "MTAuMjAuMTEuNDAgICAgCRLu7jNNEe84ubAa82ofaAcmVwb3J0.pdf";
        String filename = "/src/test/java/io/carbone/template3.odt";
        String name = "test.pdf";
        Path filePath = Paths.get(directory, filename);
        byte[] fileBytes = Files.readAllBytes(filePath);

        when(carboneRender.getReport(anyString())).thenReturn(new CarboneDocument(fileBytes, name));

        CarboneDocument resp = carboneRender.getReport(renderId);

        assertArrayEquals(fileBytes, resp.getFileContent());
        assertEquals(resp.getName(), name);

    }

    @Test
    public void Test_Render_Template_Id() throws CarboneException
    {
        String fakeTemplateId = "FakeTemplateId";

        String json = "{ \"data\": { \"invoiceId\": \"FR-2023-781\", \"invoiceDate\": \"2023-01-17\", \"name\": \"Quentin Le Forestier\", \"phone\": \"+33 (0)2 23 12 33 24\", \"email\": \"tim+serverpro@carbone.io\", \"partner\": { \"name\": \"Corp\", \"phone\": \"+33 (0)6 27 89 01 23\", \"email\": \"quentinleforestierleforestier0@gmail.com\", \"vat\": \"FR45899106785\", \"siren\": \"899106785\", \"address\": { \"street\": \"12 Doctor Street\", \"postalcode\": 34920, \"city\": \"Anger\", \"country\": \"France\" } }, \"note\": \"Our team is confident that <b>this cutting-edge technology</b> will enhance your operations and take your business to the next level. Should you have any questions or concerns, please don't hesitate to reach out to us.\", \"subscriptionFromDate\": \"2023-02-02T07:28:05+00:00\", \"subscriptionToDate\": \"2024-04-18T07:28:05+00:00\", \"products\": [ { \"name\": \"<b>Rackmount case</b> (With bays for multiple 3.5-inch drives and room for the motherboard and other components.)\", \"exTaxTotal\": 1000, \"unit\": 1000, \"vat\": 20, \"qty\": 1 }, { \"name\": \"<b>Motherboard</b> (has the necessary number of SATA ports for your storage needs)\", \"exTaxTotal\": 150, \"unit\": 150, \"vat\": 20, \"qty\": 1 }, { \"name\": \"<b>3.5-inch SATA hard drives</b> (20TO)\", \"exTaxTotal\": 1000, \"unit\": 100, \"vat\": 20, \"qty\": 10 }, { \"name\": \"<b>Processor</b> ( A low-power consumption processor)\", \"exTaxTotal\": 100, \"unit\": 100, \"vat\": 20, \"qty\": 1 }, { \"name\": \"<b>4GB DDR5 RAM</b>\", \"exTaxTotal\": 120, \"unit\": 30, \"vat\": 20, \"qty\": 4 } ], \"exTaxTotal\": 1870, \"taxTotal\": 374, \"inTaxTotal\": 2244, \"amountRemaining\": 1044, \"invoicePaymentList\": [ { \"paymentDate\": \"2023-02-03T07:28:05+00:00\", \"typeSelect\": \"Paiement\", \"amount\": 1200 } ], \"payment\": { \"name\": \"SEPA Transfer\", \"typeSelect\": \"SEPAtransfer\", \"condition\": \"Upon receipt of invoice\", \"bankLabel\": \"Revolut\", \"bankBIC\": \"REVOGB2L\", \"bankIBAN\": \"FR2112739000409352423869N75\" } }, \"convertTo\" : { \"formatName\" : \"pdf\" } }";

        when(carboneRender.renderReport(json, fakeTemplateId)).thenThrow(new CarboneException("Carbone SDK render error: Error while rendering template Error: 404 Not Found")); 
        try {
            carboneServices.render(json, fakeTemplateId);
        } catch (CarboneException e) {
            assertEquals("Carbone SDK render error: Error while rendering template Error: 404 Not Found", e.getMessage());
        }
    }

    @Test
    public void Test_Render_Error_From_Directory() throws CarboneException, NoSuchFileException
    {
        String filename = "/src/test/java/io/carbone/template.odt";
        String json = "{ \"data\": { \"invoiceId\": \"FR-2023-781\", \"invoiceDate\": \"2023-01-17\", \"name\": \"Quentin Le Forestier\", \"phone\": \"+33 (0)2 23 12 33 24\", \"email\": \"tim+serverpro@carbone.io\", \"partner\": { \"name\": \"Corp\", \"phone\": \"+33 (0)6 27 89 01 23\", \"email\": \"quentinleforestierleforestier0@gmail.com\", \"vat\": \"FR45899106785\", \"siren\": \"899106785\", \"address\": { \"street\": \"12 Doctor Street\", \"postalcode\": 34920, \"city\": \"Anger\", \"country\": \"France\" } }, \"note\": \"Our team is confident that <b>this cutting-edge technology</b> will enhance your operations and take your business to the next level. Should you have any questions or concerns, please don't hesitate to reach out to us.\", \"subscriptionFromDate\": \"2023-02-02T07:28:05+00:00\", \"subscriptionToDate\": \"2024-04-18T07:28:05+00:00\", \"products\": [ { \"name\": \"<b>Rackmount case</b> (With bays for multiple 3.5-inch drives and room for the motherboard and other components.)\", \"exTaxTotal\": 1000, \"unit\": 1000, \"vat\": 20, \"qty\": 1 }, { \"name\": \"<b>Motherboard</b> (has the necessary number of SATA ports for your storage needs)\", \"exTaxTotal\": 150, \"unit\": 150, \"vat\": 20, \"qty\": 1 }, { \"name\": \"<b>3.5-inch SATA hard drives</b> (20TO)\", \"exTaxTotal\": 1000, \"unit\": 100, \"vat\": 20, \"qty\": 10 }, { \"name\": \"<b>Processor</b> ( A low-power consumption processor)\", \"exTaxTotal\": 100, \"unit\": 100, \"vat\": 20, \"qty\": 1 }, { \"name\": \"<b>4GB DDR5 RAM</b>\", \"exTaxTotal\": 120, \"unit\": 30, \"vat\": 20, \"qty\": 4 } ], \"exTaxTotal\": 1870, \"taxTotal\": 374, \"inTaxTotal\": 2244, \"amountRemaining\": 1044, \"invoicePaymentList\": [ { \"paymentDate\": \"2023-02-03T07:28:05+00:00\", \"typeSelect\": \"Paiement\", \"amount\": 1200 } ], \"payment\": { \"name\": \"SEPA Transfer\", \"typeSelect\": \"SEPAtransfer\", \"condition\": \"Upon receipt of invoice\", \"bankLabel\": \"Revolut\", \"bankBIC\": \"REVOGB2L\", \"bankIBAN\": \"FR2112739000409352423869N75\" } }, \"convertTo\" : { \"formatName\" : \"pdf\" } }";

        when(carboneRender.renderReport(json, filename)).thenThrow(new CarboneException("Carbone SDK render error: failled to generate the template id")); 
        try {
            carboneServices.render(json, filename);
        } catch (CarboneException e) {
            assertEquals("Carbone SDK render error: failled to generate the template id", e.getMessage());
        }
    
    }

    @Test
    public void Test_Render_A_Report_From_An_Existing_Template_Id() throws CarboneException, IOException {

        final String templateId = "fb9241ea2218ffd8f974110e539386384620244618c2efbf182b7bd47242987B";
        final String json = "{ \"data\": { \"invoiceId\": \"FR-2023-781\", \"invoiceDate\": \"2023-01-17\", \"name\": \"Quentin Le Forestier\", \"phone\": \"+33 (0)2 23 12 33 24\", \"email\": \"tim+serverpro@carbone.io\", \"partner\": { \"name\": \"Corp\", \"phone\": \"+33 (0)6 27 89 01 23\", \"email\": \"quentinleforestierleforestier0@gmail.com\", \"vat\": \"FR45899106785\", \"siren\": \"899106785\", \"address\": { \"street\": \"12 Doctor Street\", \"postalcode\": 34920, \"city\": \"Anger\", \"country\": \"France\" } }, \"note\": \"Our team is confident that <b>this cutting-edge technology</b> will enhance your operations and take your business to the next level. Should you have any questions or concerns, please don't hesitate to reach out to us.\", \"subscriptionFromDate\": \"2023-02-02T07:28:05+00:00\", \"subscriptionToDate\": \"2024-04-18T07:28:05+00:00\", \"products\": [ { \"name\": \"<b>Rackmount case</b> (With bays for multiple 3.5-inch drives and room for the motherboard and other components.)\", \"exTaxTotal\": 1000, \"unit\": 1000, \"vat\": 20, \"qty\": 1 }, { \"name\": \"<b>Motherboard</b> (has the necessary number of SATA ports for your storage needs)\", \"exTaxTotal\": 150, \"unit\": 150, \"vat\": 20, \"qty\": 1 }, { \"name\": \"<b>3.5-inch SATA hard drives</b> (20TO)\", \"exTaxTotal\": 1000, \"unit\": 100, \"vat\": 20, \"qty\": 10 }, { \"name\": \"<b>Processor</b> ( A low-power consumption processor)\", \"exTaxTotal\": 100, \"unit\": 100, \"vat\": 20, \"qty\": 1 }, { \"name\": \"<b>4GB DDR5 RAM</b>\", \"exTaxTotal\": 120, \"unit\": 30, \"vat\": 20, \"qty\": 4 } ], \"exTaxTotal\": 1870, \"taxTotal\": 374, \"inTaxTotal\": 2244, \"amountRemaining\": 1044, \"invoicePaymentList\": [ { \"paymentDate\": \"2023-02-03T07:28:05+00:00\", \"typeSelect\": \"Paiement\", \"amount\": 1200 } ], \"payment\": { \"name\": \"SEPA Transfer\", \"typeSelect\": \"SEPAtransfer\", \"condition\": \"Upon receipt of invoice\", \"bankLabel\": \"Revolut\", \"bankBIC\": \"REVOGB2L\", \"bankIBAN\": \"FR2112739000409352423869N75\" } }, \"convertTo\" : { \"formatName\" : \"pdf\" } }";
        final String name = "test.pdf";
        
        CarboneResponse.CarboneResponseData responseData = CarboneResponse.CarboneResponseData.builder()
                                                                                            .renderId("MTAuMjAuMTEuNDAgICAgCRLu7jNNEe84ubAa82ofaAcmVwb3J0.pdf")
                                                                                            .build();
        CarboneResponse mockedResponse = CarboneResponse.builder()
                                                        .success(true)
                                                        .data(responseData)
                                                        .build();

        String filename = "/src/test/java/io/carbone/template3.odt";
        Path filePath = Paths.get(directory, filename);
        byte[] fileBytes = Files.readAllBytes(filePath);
        when(carboneRender.renderReport(json, templateId)).thenReturn(mockedResponse);
        when(carboneRender.getReport(mockedResponse.getData().renderId)).thenReturn(new CarboneDocument(fileBytes, name));
        
        CarboneDocument resp = carboneServices.render(json, templateId);
            
        
        assertArrayEquals(fileBytes, resp.getFileContent());
        assertEquals(resp.getName(), name);
    }

    @Test
    public void Test_Render_From_A_Template_Already_Upload() throws IOException, CarboneException{
        final String json = "{ \"data\": { \"invoiceId\": \"FR-2023-781\", \"invoiceDate\": \"2023-01-17\", \"name\": \"Quentin Le Forestier\", \"phone\": \"+33 (0)2 23 12 33 24\", \"email\": \"tim+serverpro@carbone.io\", \"partner\": { \"name\": \"Corp\", \"phone\": \"+33 (0)6 27 89 01 23\", \"email\": \"quentinleforestierleforestier0@gmail.com\", \"vat\": \"FR45899106785\", \"siren\": \"899106785\", \"address\": { \"street\": \"12 Doctor Street\", \"postalcode\": 34920, \"city\": \"Anger\", \"country\": \"France\" } }, \"note\": \"Our team is confident that <b>this cutting-edge technology</b> will enhance your operations and take your business to the next level. Should you have any questions or concerns, please don't hesitate to reach out to us.\", \"subscriptionFromDate\": \"2023-02-02T07:28:05+00:00\", \"subscriptionToDate\": \"2024-04-18T07:28:05+00:00\", \"products\": [ { \"name\": \"<b>Rackmount case</b> (With bays for multiple 3.5-inch drives and room for the motherboard and other components.)\", \"exTaxTotal\": 1000, \"unit\": 1000, \"vat\": 20, \"qty\": 1 }, { \"name\": \"<b>Motherboard</b> (has the necessary number of SATA ports for your storage needs)\", \"exTaxTotal\": 150, \"unit\": 150, \"vat\": 20, \"qty\": 1 }, { \"name\": \"<b>3.5-inch SATA hard drives</b> (20TO)\", \"exTaxTotal\": 1000, \"unit\": 100, \"vat\": 20, \"qty\": 10 }, { \"name\": \"<b>Processor</b> ( A low-power consumption processor)\", \"exTaxTotal\": 100, \"unit\": 100, \"vat\": 20, \"qty\": 1 }, { \"name\": \"<b>4GB DDR5 RAM</b>\", \"exTaxTotal\": 120, \"unit\": 30, \"vat\": 20, \"qty\": 4 } ], \"exTaxTotal\": 1870, \"taxTotal\": 374, \"inTaxTotal\": 2244, \"amountRemaining\": 1044, \"invoicePaymentList\": [ { \"paymentDate\": \"2023-02-03T07:28:05+00:00\", \"typeSelect\": \"Paiement\", \"amount\": 1200 } ], \"payment\": { \"name\": \"SEPA Transfer\", \"typeSelect\": \"SEPAtransfer\", \"condition\": \"Upon receipt of invoice\", \"bankLabel\": \"Revolut\", \"bankBIC\": \"REVOGB2L\", \"bankIBAN\": \"FR2112739000409352423869N75\" } }, \"convertTo\" : { \"formatName\" : \"pdf\" } }";
        final String name = "test.pdf";
        final String templateId = "d13876946291b722c3071c8ed2787037bbb3902e2503f955783c6f5912c5b1d4";

        String filename = "/src/test/java/io/carbone/template3.odt";
        Path filePath = Paths.get(directory, filename);
        byte[] fileBytes = Files.readAllBytes(filePath);

        CarboneResponse.CarboneResponseData responseData = CarboneResponse.CarboneResponseData.builder()
                                                                                            .renderId("MTAuMjAuMTEuNDAgICAgCRLu7jNNEe84ubAa82ofaAcmVwb3J0.pdf")
                                                                                            .build();
        CarboneResponse mockedResponse = CarboneResponse.builder()
                                                        .success(true)
                                                        .data(responseData)
                                                        .build();
        
        when(carboneRender.renderReport(json, templateId)).thenReturn(mockedResponse);
        when(carboneRender.getReport(mockedResponse.getData().renderId)).thenReturn(new CarboneDocument(fileBytes, name));

        CarboneDocument resp = carboneServices.render(json, templateId);
            
        
        assertArrayEquals(fileBytes, resp.getFileContent());
        assertEquals(resp.getName(), name);

    }

    @Test
    public void Test_Render_From_A_Generate_Template_Id() throws IOException, CarboneException{

        final String json = "{ \"data\": { \"invoiceId\": \"FR-2023-781\", \"invoiceDate\": \"2023-01-17\", \"name\": \"Quentin Le Forestier\", \"phone\": \"+33 (0)2 23 12 33 24\", \"email\": \"tim+serverpro@carbone.io\", \"partner\": { \"name\": \"Corp\", \"phone\": \"+33 (0)6 27 89 01 23\", \"email\": \"quentinleforestierleforestier0@gmail.com\", \"vat\": \"FR45899106785\", \"siren\": \"899106785\", \"address\": { \"street\": \"12 Doctor Street\", \"postalcode\": 34920, \"city\": \"Anger\", \"country\": \"France\" } }, \"note\": \"Our team is confident that <b>this cutting-edge technology</b> will enhance your operations and take your business to the next level. Should you have any questions or concerns, please don't hesitate to reach out to us.\", \"subscriptionFromDate\": \"2023-02-02T07:28:05+00:00\", \"subscriptionToDate\": \"2024-04-18T07:28:05+00:00\", \"products\": [ { \"name\": \"<b>Rackmount case</b> (With bays for multiple 3.5-inch drives and room for the motherboard and other components.)\", \"exTaxTotal\": 1000, \"unit\": 1000, \"vat\": 20, \"qty\": 1 }, { \"name\": \"<b>Motherboard</b> (has the necessary number of SATA ports for your storage needs)\", \"exTaxTotal\": 150, \"unit\": 150, \"vat\": 20, \"qty\": 1 }, { \"name\": \"<b>3.5-inch SATA hard drives</b> (20TO)\", \"exTaxTotal\": 1000, \"unit\": 100, \"vat\": 20, \"qty\": 10 }, { \"name\": \"<b>Processor</b> ( A low-power consumption processor)\", \"exTaxTotal\": 100, \"unit\": 100, \"vat\": 20, \"qty\": 1 }, { \"name\": \"<b>4GB DDR5 RAM</b>\", \"exTaxTotal\": 120, \"unit\": 30, \"vat\": 20, \"qty\": 4 } ], \"exTaxTotal\": 1870, \"taxTotal\": 374, \"inTaxTotal\": 2244, \"amountRemaining\": 1044, \"invoicePaymentList\": [ { \"paymentDate\": \"2023-02-03T07:28:05+00:00\", \"typeSelect\": \"Paiement\", \"amount\": 1200 } ], \"payment\": { \"name\": \"SEPA Transfer\", \"typeSelect\": \"SEPAtransfer\", \"condition\": \"Upon receipt of invoice\", \"bankLabel\": \"Revolut\", \"bankBIC\": \"REVOGB2L\", \"bankIBAN\": \"FR2112739000409352423869N75\" } }, \"convertTo\" : { \"formatName\" : \"pdf\" } }";
        final String name = "test.pdf";

        String filename = "/src/test/java/io/carbone/Template.html";
        Path filePath = Paths.get(directory, filename);
        byte[] fileBytes = Files.readAllBytes(filePath);

        String templateId = carboneServices.generateTemplateId(filePath.toString());
        
        CarboneResponse.CarboneResponseData responseData = CarboneResponse.CarboneResponseData.builder()
                                                                                            .renderId("MTAuMjAuMTEuNDAgICAgCRLu7jNNEe84ubAa82ofaAcmVwb3J0.pdf")
                                                                                            .build();
        CarboneResponse mockedResponse = CarboneResponse.builder()
                                                        .success(true)
                                                        .data(responseData)
                                                        .build();
        
        when(carboneRender.renderReport(json, templateId)).thenReturn(mockedResponse);
        when(carboneRender.getReport(mockedResponse.getData().renderId)).thenReturn(new CarboneDocument(fileBytes, name));

        CarboneDocument resp = carboneServices.render(json, templateId);
            
        
        assertArrayEquals(fileBytes, resp.getFileContent());
        assertEquals(resp.getName(), name);
        
    }

    @Test
    public void Test_Get_Status() throws CarboneException {

        Map<String, Collection<String>> headers = new HashMap<>();
        headers.put("Content-Type", Collections.singletonList("application/json"));

        Request originalRequest = Request.create(Request.HttpMethod.GET, CARBONE_URI + "/status", headers, null, StandardCharsets.UTF_8, null);

        Response response = Response.builder()
        .status(200)
        .request(originalRequest)
        .headers(Collections.emptyMap())
        .body("{\"success\":true,\"code\":200,\"message\":\"OK\",\"version\":\"4.22.8\"}", StandardCharsets.UTF_8)
        .build();

        when(carboneStatus.getStatus()).thenReturn(response);
        Response result = carboneStatus.getStatus();
        assertEquals(response, result);
    }
}
