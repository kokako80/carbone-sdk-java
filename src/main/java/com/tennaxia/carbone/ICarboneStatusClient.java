package com.tennaxia.carbone;

import feign.RequestLine;
import feign.Response;

public interface ICarboneStatusClient {
    @RequestLine("GET /status")
    Response getStatus() throws CarboneException;
}
