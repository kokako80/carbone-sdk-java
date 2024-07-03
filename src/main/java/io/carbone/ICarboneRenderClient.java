package io.carbone;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import feign.Body;
public interface ICarboneRenderClient {
    @RequestLine("POST /{templateId}")

    @Headers("Content-Type: application/json")
    @Body("{renderData}")
    CarboneResponse renderReport(@Param("renderData") String renderData, @Param("templateId") String templateId) throws CarboneException;

    

    @RequestLine("GET /{renderId}")
    @Headers("Content-Type: application/String")
    CarboneDocument getReport(@Param("renderId") String renderId) throws CarboneException;
}
