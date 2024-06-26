package io.carbone;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import feign.FeignException;
import feign.Response;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

final class CarboneServices implements ICarboneServices {

    int err;
    private final ICarboneTemplateClient carboneTemplateClient;
    private final ICarboneRenderClient carboneRenderClient;
    private final ICarboneStatusClient carboneStatusClient ;
    String reportName;

    public CarboneServices(ICarboneTemplateClient carboneTemplateClient, ICarboneRenderClient carboneRenderClient, ICarboneStatusClient carboneStatusClient) {
        this.carboneTemplateClient = carboneTemplateClient;
        this.carboneRenderClient = carboneRenderClient;
        this.carboneStatusClient = carboneStatusClient;
    }

    @Override
    public Optional<String> addTemplate(byte[] templateFile) throws CarboneException {
        System.out.println("test");
        CarboneResponse carboneResponse = carboneTemplateClient.addTemplate(templateFile);
        System.out.println(carboneResponse);
        return Optional.of(carboneResponse.getData().getTemplateId());
    }

    @Override
    public void deleteTemplate(String templateId) throws CarboneException { 
        carboneTemplateClient.deleteTemplate(templateId);
    }

    public boolean checkPathIsAbsolute(String path) {
        Path p = Paths.get(path);
        return p.isAbsolute();
    }

    public String generateTemplateId(String path) {
        try {
            File file = new File(path);
            byte[] fileBytes;
            try (FileInputStream fis = new FileInputStream(file)) {
                fileBytes = fis.readAllBytes();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            
            MessageDigest digest;
            try {
                digest = MessageDigest.getInstance("SHA-256");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                return null;
            }
            
            digest.update(fileBytes);
            byte[] hashByte = digest.digest();
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashByte) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append(0);
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public CarboneDocument render(String Json, String fileOrTemplateID) throws CarboneException
    {
        if (fileOrTemplateID == null || fileOrTemplateID.isEmpty()) {
            throw new CarboneException("Carbone SDK render error: argument is missing: file_or_template_id");
        }
        if (Json == null) {
            throw new CarboneException("Carbone SDK render error: argument is missing: json_data");
        }
        CarboneResponse resp = null;
        File file = new File(fileOrTemplateID);
        if (!file.exists()) {
            resp = carboneRenderClient.renderReport(Json, fileOrTemplateID);
        } 
        else {
            try {
                String templateId = generateTemplateId(fileOrTemplateID);
                resp = carboneRenderClient.renderReport(Json, templateId);
            } catch (CarboneException e) {
                if(e.getHttpStatus() == 404) 
                {
                    try {
                        Path filePath = Paths.get(fileOrTemplateID);
                        CarboneResponse respAddTemplate = carboneTemplateClient.addTemplate(Files.readAllBytes(filePath));
                        if (respAddTemplate.isSuccess()) {
                            resp = carboneRenderClient.renderReport(Json, respAddTemplate.getData().getTemplateId());
                        } else {
                            throw new CarboneException("Carbone SDK render error: failed to add template");
                        }
                    } catch (IOException err) {
                        throw new CarboneException("Carbone SDK render error: failed to read template file");
                    }
                }
                else{
                    throw new CarboneException("Carbone SDK render error: failed to generate the template id");
                }
            }
        }
        if (resp == null) {
            throw new CarboneException("Carbone SDK render error: something went wrong");
        }
        if (!resp.isSuccess()) {
            throw new CarboneException("Carbone SDK render error: ");
        }
        if (!resp.isSuccess()) {
            throw new CarboneException("Carbone SDK render error: render_id empty");
        }
        return getReport(resp.getData().getRenderId());
    }


    @Override
    public String renderReport(String renderData, String templateId) throws CarboneException {
        if(templateId instanceof String && checkPathIsAbsolute(templateId))
        {
            CarboneResponse carboneResponse = carboneRenderClient.renderReport(renderData, generateTemplateId(templateId));
            return carboneResponse.getData().getRenderId();
        }

        CarboneResponse carboneResponse = carboneRenderClient.renderReport(renderData, templateId);
        return carboneResponse.getData().getRenderId();
    }

    @Override
    public CarboneDocument getReport(String renderId) throws CarboneException {
        CarboneDocument response = carboneRenderClient.getReport(renderId);
        return response;
    } 

    @Override
    public byte[] getTemplate(String templateId) throws CarboneException 
    {
        CarboneFileResponse response = carboneTemplateClient.getTemplate(templateId);
        return response.getFileContent();
    }

    @Override
    public String getStatus() throws CarboneException 
    {
        Response response = null;
        try {
            response = carboneStatusClient.getStatus();
            InputStream bodyIs = response.body().asInputStream();
            String body = new String(bodyIs.readAllBytes(), StandardCharsets.UTF_8);
            return body;
        } catch (IOException e) {
            throw new CarboneException("Error reading response body");
        } catch (FeignException e) {
            throw new CarboneException("Feign exception occurred");
        } finally {
            if (response != null && response.body() != null) {
                try {
                    response.body().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String takeReportName(Map<String,Collection<String>> headers) {
        Collection<String> contentDisposition = headers.get("content-disposition");
        if (contentDisposition == null || contentDisposition.isEmpty()) {
            return null;
        }
        
        
        String disposition = contentDisposition.iterator().next();
        String[] splitContentDisposition = disposition.split("=");

        if (splitContentDisposition.length != 2) {
            return null;
        }
        
        reportName = splitContentDisposition[1];
        if (reportName.startsWith("\"") && reportName.endsWith("\"")) {
            reportName = reportName.substring(1, reportName.length() - 1);
        }
        
        return reportName;
    }
}
