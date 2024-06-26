package io.carbone;

import java.util.Optional;

public interface ICarboneServices {

    /**
     * Upload template to Carbone
     * @param templateFile file's content in byte[]
     * @return an {@link Optional} containing the templateId to use for render if template is successfully sent
     * @throws CarboneException contain CarboneResponse format with API error code and error messages
     */
    Optional<String> addTemplate(byte[] templateFile) throws CarboneException;

    /**
     * Delete uploaded template
     * @param templateId id returned by addTemplate() method
     * @throws CarboneException contain CarboneResponse format with API error code and error messages
     */
    void deleteTemplate(String templateId) throws CarboneException;


    /**
     * Download rendered report
     * @param templateId id returned by renderReport()
     * @return report content in byte[]
     * @throws CarboneException contain CarboneResponse format with API error code and error messages
     */
    byte[] getTemplate(String templateId) throws CarboneException;

    /**
     * Render report
     * @param json Json object with data set to replace in template
     * @param templateId id returned by addTemplate() method
     * @return id of rendered report
     * @throws CarboneException contain CarboneResponse format with API error code and error messages
     */
    String renderReport(String json, String templateId) throws CarboneException;

    /**
     * Download rendered report
     * @param renderId id returned by renderReport()
     * @return report content in CarboneDocument
     * @throws CarboneException contain CarboneResponse format with API error code and error messages
     */
    CarboneDocument getReport(String renderId) throws CarboneException;


    /**
     * Give the status
     * @param renderId id returned by renderReport()
     * @return report content in String
     * @throws CarboneException contain CarboneResponse format with API error code and error messages
     */
    String getStatus() throws CarboneException;

    /**
     * render report
     * @param fileOrTemplateID id returned by addTemplate() method or the id of document of studio or a local path
     * @param renderData Json object with data set to replace in template
     * @return report content in CarboneDocument
     * @throws CarboneException contain CarboneResponse format with API error code and error messages
     */
    CarboneDocument render(String Json, String fileOrTemplateID) throws CarboneException;

     /**
     * precalculates the template id 
     * @param fileOrTemplateID local path
     * @return give a precalculates templateId 
     * @throws CarboneException contain CarboneResponse format with API error code and error messages
     */
    String generateTemplateId(String path);
    
}
