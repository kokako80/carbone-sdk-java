package com.tennaxia.carbone;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

// @Data
// final class CarboneData {
//     private Object data;
//     private CarboneConvertTo convertTo;

//     public CarboneData(Object jsonData) {
//         JSONObject jsonObject = new JSONObject(jsonData);
//         if (jsonObject.has("data")) {
//             this.data = jsonObject.getJSONObject("data");
//             JSONObject convertToJson = jsonObject.getJSONObject("convertTo");
    
//             this.convertTo = new CarboneConvertTo(convertToJson);
//         } else {
//             throw new IllegalArgumentException("Missing 'data' key in JSON object");
//         }
//     }

//     @Data
//     private static final class CarboneConvertTo {
//         private String formatName;
//         private Map<String, Object> formatOptions;

//         public CarboneConvertTo(JSONObject convertToJson) {
//             this.formatName = convertToJson.getString("formatName");
//             this.formatOptions = new HashMap<>();
//             for (String key : convertToJson.keySet()) {
//                 if (!key.equals("formatName")) {
//                     this.formatOptions.put(key, convertToJson.get(key));
//                 }
//             }
//         }
//     }
// }
