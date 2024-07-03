package io.carbone;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@AllArgsConstructor
@Data
@Getter
public class CarboneDocument {

    private final byte[] fileContent;

    private final String name;

}
