package io.carbone;

import feign.form.FormEncoder;
import feign.gson.GsonEncoder;
import feign.slf4j.Slf4jLogger;

public enum CarboneServicesFactory {

    CARBONE_SERVICES_FACTORY_INSTANCE;

    private static String CARBONE_URL = "https://api.carbone.io";

    private CarboneTemplateClient carboneTemplateClient;

    private CarboneRenderClient carboneRenderClient;

    private CarboneStatusClient carboneStatusClient;

    public ICarboneServices create(String ... apiAcess) {
        String apiToken = System.getenv("CARBONE_TOKEN");
        String apiVersion = "4";
        if (apiAcess.length > 0 && !apiAcess[0].isEmpty()) {
            apiToken = apiAcess[0];
        }
        
        if (apiAcess.length > 1 && !apiAcess[1].isEmpty()) {
            apiVersion = apiAcess[1];
        }
        carboneTemplateClient = CarboneFeignClientBuilder.createCarboneFeignClient(apiToken, apiVersion)
            .encoder(new FormEncoder())
            .decoder(new CarboneDecoder())
            .logger(new Slf4jLogger(CarboneTemplateClient.class))
            .target(CarboneTemplateClient.class, CARBONE_URL + "/template");

        carboneRenderClient = CarboneFeignClientBuilder.createCarboneFeignClient(apiToken, apiVersion)
            .encoder(new GsonEncoder())
            .decoder(new CarboneDecoder())
            .logger(new Slf4jLogger(CarboneRenderClient.class))
            .target(CarboneRenderClient.class, CARBONE_URL + "/render");

        carboneStatusClient = CarboneFeignClientBuilder.createCarboneFeignClient(apiToken, apiVersion)
            .encoder(new GsonEncoder())
            .decoder(new CarboneDecoder())
            .logger(new Slf4jLogger(CarboneStatusClient.class))
            .target(CarboneStatusClient.class, CARBONE_URL);

        return create(carboneTemplateClient, carboneRenderClient, carboneStatusClient);
    }


    public ICarboneServices create(ICarboneTemplateClient carboneTemplateClient, ICarboneRenderClient carboneRenderClient, ICarboneStatusClient carboneStatusClient ) {
        return new CarboneServices(carboneTemplateClient, carboneRenderClient, carboneStatusClient);
    }

    public void SetCarboneUrl(String newUrl) throws CarboneException {CARBONE_URL = newUrl;}

    public String GetCarboneUrl() {return CARBONE_URL;}
}
