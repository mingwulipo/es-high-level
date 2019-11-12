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
    /**
     * 索引名称必须全部是小写，所以不能用驼峰命名，只能用下划线了
     */
    public static final String INDEX = "distribute_no";
    public static final String TYPE = "es";

    @JestId
    private String noName;

    /**
     * 因为long值太大时，es取出会四舍五入，数据失真，所以用string
     */
    private String initNo;
    private String currentNo;

}
