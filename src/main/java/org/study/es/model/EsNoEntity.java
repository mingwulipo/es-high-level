package org.study.es.model;

import io.searchbox.annotations.JestId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lipo
 * @version v1.0
 * @date 2019-11-11 10:27
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EsNoEntity {
    public static final String INDEX = "distribute_no";
    public static final String TYPE = "es";

    @JestId
    private String noName;
    private String initNo;
    private String currentNo;

}
