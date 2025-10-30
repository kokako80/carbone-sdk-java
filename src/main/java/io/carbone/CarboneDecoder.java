package io.carbone;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;

import feign.FeignException;
import feign.Response;
import feign.codec.Decoder;
import feign.gson.GsonDecoder;

class CarboneDecoder implements Decoder {
    @Override
    public Object decode(Response response, Type type) throws IOException, FeignException {
        if (type.equals(CarboneFileResponse.class)) {
            return new CarboneFileResponse(readAllBytes(response));
        }
        if(type.equals(CarboneDocument.class))
        {

            return new CarboneDocument(org.apache.commons.io.IOUtils.toByteArray(response.body().asInputStream()), DecodeContentDisposition(response.headers()));
        }
        return new GsonDecoder().decode(response, type);
    }

    private byte[] readAllBytes(Response response) throws IOException {
        return org.apache.commons.io.IOUtils.toByteArray(response.body().asInputStream()); // This method is fine for JDK 1.8
    }

    // The rest of the class is fine for JDK 1.8
    public String DecodeContentDisposition(Map<String,Collection<String>> headers) {
        Collection<String> contentDisposition = headers.get("content-disposition");
        if (contentDisposition == null || contentDisposition.isEmpty()) {
            return null;
        }
        
        
        String disposition = contentDisposition.iterator().next();
        String[] splitContentDisposition = disposition.split("=");

        if (splitContentDisposition.length != 2) {
            return null;
        }
        
        String reportName = splitContentDisposition[1];
        if (reportName.startsWith("\"") && reportName.endsWith("\"")) {
            reportName = reportName.substring(1, reportName.length() - 1);
        }
        
        return reportName;
    }
}
