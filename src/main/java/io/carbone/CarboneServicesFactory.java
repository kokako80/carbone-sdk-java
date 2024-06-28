package io.carbone;

import feign.form.FormEncoder;
import feign.gson.GsonEncoder;
import feign.slf4j.Slf4jLogger;

public enum CarboneServicesFactory {

    CARBONE_SERVICES_FACTORY_INSTANCE;

    private static String CARBONE_URI = "https://api.carbone.io";

    private CarboneTemplateClient carboneTemplateClient;

    private CarboneRenderClient carboneRenderClient;

    private CarboneStatusClient carboneStatusClient;

    public ICarboneServices create(String token, String version) {
        String apiToken = System.getenv("CARBONE_TOKEN");
        String apiVersion = "4";
        if( token != "")
        {
            apiToken = token;
        }
        if( version != "")
        {
            apiVersion = version;
        }
        carboneTemplateClient = CarboneFeignClientBuilder.createCarboneFeignClient(apiToken, apiVersion)
            .encoder(new FormEncoder())
            .decoder(new CarboneDecoder())
            .logger(new Slf4jLogger(CarboneTemplateClient.class))
            .target(CarboneTemplateClient.class, CARBONE_URI + "/template");

        carboneRenderClient = CarboneFeignClientBuilder.createCarboneFeignClient(apiToken, apiVersion)
            .encoder(new GsonEncoder())
            .decoder(new CarboneDecoder())
            .logger(new Slf4jLogger(CarboneRenderClient.class))
            .target(CarboneRenderClient.class, CARBONE_URI + "/render");

        carboneStatusClient = CarboneFeignClientBuilder.createCarboneFeignClient(apiToken, apiVersion)
            .encoder(new GsonEncoder())
            .decoder(new CarboneDecoder())
            .logger(new Slf4jLogger(CarboneStatusClient.class))
            .target(CarboneStatusClient.class, CARBONE_URI);

        return create(carboneTemplateClient, carboneRenderClient, carboneStatusClient);
    }


    public ICarboneServices create(ICarboneTemplateClient carboneTemplateClient, ICarboneRenderClient carboneRenderClient, ICarboneStatusClient carboneStatusClient ) {
        return new CarboneServices(carboneTemplateClient, carboneRenderClient, carboneStatusClient);
    }

    public void SetCarbonneUri(String newUrl) {CARBONE_URI = newUrl;}

    public String GetCarboneUri() {return CARBONE_URI;}
}
