package com.tennaxia.carbone;

import static java.util.Optional.empty;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Optional;

import feign.Response;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

final class CarboneServices implements ICarboneServices {

    private static final Map<String, Object> USE_LOSSLESS_COMPRESSION = Map.of("UseLosslessCompression", false);

    private final ICarboneTemplateClient carboneTemplateClient;
    private final ICarboneRenderClient carboneRenderClient;
    private final ICarboneStatusClient carboneStatusClient ;

    public CarboneServices(ICarboneTemplateClient carboneTemplateClient, ICarboneRenderClient carboneRenderClient, ICarboneStatusClient carboneStatusClient) {
        this.carboneTemplateClient = carboneTemplateClient;
        this.carboneRenderClient = carboneRenderClient;
        this.carboneStatusClient = carboneStatusClient;
    }

    @Override
    public Optional<String> addTemplate(byte[] templateFile) throws CarboneException {
        CarboneResponse carboneResponse = carboneTemplateClient.addTemplate(templateFile);
        if (carboneResponse.isSuccess()) {
            return Optional.of(carboneResponse.getData().getTemplateId());
        } else {
            return empty();
        }
    }

    @Override
    public void deleteTemplate(String templateId) throws CarboneException {
        carboneTemplateClient.deleteTemplate(templateId);
    }

    @Override
    public String renderReport(Object renderData, String templateId) throws CarboneException {
        if(templateId instanceof String && checkPathIsAbsolute(templateId))
        {
            return renderReport(renderData, generateTemplateId(templateId), USE_LOSSLESS_COMPRESSION);
        }
        return renderReport(renderData, templateId, USE_LOSSLESS_COMPRESSION);
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

    public byte[] render(String fileOrTemplateID, Object Json) throws CarboneException
    {
        if (fileOrTemplateID == null || fileOrTemplateID.isEmpty()) {
            throw new CarboneException("Carbone SDK render error: argument is missing: file_or_template_id");
        }
        if (Json == null) {
            throw new CarboneException("Carbone SDK render error: argument is missing: json_data");
        }
        String resp = null;
        File file = new File(fileOrTemplateID);
        if (!file.exists()) {
            resp = renderReport(Json, fileOrTemplateID);
        } 
        else {
            try {
                String templateId = generateTemplateId(fileOrTemplateID);
                resp = renderReport(Json, templateId);
            } catch (Exception e) {
                throw new CarboneException("Carbone SDK render error: failed to generate the template id");
            }
            if ( resp.contains("404") && checkPathIsAbsolute(fileOrTemplateID)) {
                try {
                    Path filePath = Paths.get(fileOrTemplateID);
                    Optional<String> respAddTemplate = addTemplate(Files.readAllBytes(filePath));
                    if (respAddTemplate.isPresent() && !respAddTemplate.get().equals("success")) {
                        resp = renderReport(Json, respAddTemplate.get());
                    } else {
                        throw new CarboneException("Carbone SDK render error: failed to add template");
                    }
                } catch (IOException e) {
                    throw new CarboneException("Carbone SDK render error: failed to read template file");
                }
            }
        }
        if (resp == null) {
            throw new CarboneException("Carbone SDK render error: something went wrong");
        }
        if (resp.contains("false")) {
            //throw new CarboneException("Carbone SDK render error: " + ((Map) resp).get("error"));
        }
        if (resp.contains("false")) {
            throw new CarboneException("Carbone SDK render error: render_id empty");
        }
        return getReport(resp);
    }
    @Override
    public String renderReport(Object renderData, String templateId, Map<String, Object> additionalOptions) throws CarboneException {
        CarboneResponse carboneResponse = carboneRenderClient
            .renderReport(new CarboneData(renderData, "pdf", additionalOptions), templateId);
        return carboneResponse.getData().getRenderId();
    }

    @Override
    public byte[] getReport(String renderId) throws CarboneException {
        CarboneFileResponse response = carboneRenderClient.getReport(renderId);
        return response.getFileContent();
    }

    @Override
    public byte[] getTemplate(String templateId) throws CarboneException 
    {
        CarboneFileResponse response = carboneRenderClient.getReport(templateId);
        return response.getFileContent();
    }

    @Override
    public String getStatus() throws CarboneException 
    {
        Response response = carboneStatusClient.getStatus();
        return response.body().toString();
    }
    
}
