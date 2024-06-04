package com.tennaxia.carbone;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import com.fasterxml.jackson.databind.ObjectMapper;

import feign.Body;
import feign.Feign;
import feign.Feign.Builder;
public interface ICarboneRenderClient {
    @RequestLine("POST /{templateId}")

    @Headers("Content-Type: application/json")
    @Body("{renderData}")
    CarboneResponse renderReport(@Param("renderData") String renderData, @Param("templateId") String templateId) throws CarboneException;

    

    @RequestLine("GET /{renderId}")
    CarboneFileResponse getReport(@Param("renderId") String renderId) throws CarboneException;
}
