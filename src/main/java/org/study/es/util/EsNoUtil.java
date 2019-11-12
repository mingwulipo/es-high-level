package org.study.es.util;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.stereotype.Component;
import org.study.es.config.EsConfig;
import org.study.es.model.EsNoEntity;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.concurrent.locks.Lock;

/**
 * @author lipo
 * @version v1.0
 * @date 2019-11-11 10:27
 */
@Component
@Slf4j
public class EsNoUtil {
    /**
     * 订单编号
     */
    private static final String ORDER_NO = "order_no";
    /**
     * 雪花算法当前生成id为1193737508566913024L，因为时间推移，id越来越大，所以以前id一定比这个小
     * 最高位+1，1->2，这样生成id和以前的数据都不会重复
     */
    private static final long ORDER_NO_INIT = 2193737508566913024L;

    /**
     * 通用编号
     */
    private static final String COMMON_NO = "common_no";
    private static final long COMMON_NO_INIT = 100001L;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    private RedisLockRegistry redisLockRegistry;

    /**
     * 创建索引
     * @author lipo
     * @date 2019-11-11 16:52
     */
    @PostConstruct
    private void init() {

        try {
            //查询索引
            GetIndexRequest request = new GetIndexRequest(EsNoEntity.INDEX);
            boolean exists = restHighLevelClient.indices().exists(request, RequestOptions.DEFAULT);

            //索引存在
            if (exists) {
                log.info("索引{}已经存在", EsNoEntity.INDEX);
                return;
            }

            //索引不存在，创建
            CreateIndexRequest createIndexRequest = new CreateIndexRequest(EsNoEntity.INDEX);
            CreateIndexResponse response = restHighLevelClient.indices().create(createIndexRequest, RequestOptions.DEFAULT);
            if (response != null && response.isAcknowledged()) {
                log.info("索引{}创建成功", EsNoEntity.INDEX);
                return;
            }

            log.error("索引{}创建失败", EsNoEntity.INDEX);

        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 订单编号
     * @author lipo
     * @date 2019-11-11 11:10
     */
    public long nextOrderNo() {
        try {
            return nextNo(ORDER_NO, ORDER_NO_INIT + "");
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 通用编号
     * @author lipo
     * @date 2019-11-11 11:10
     */
    public long nextCommonNo() {
        try {
            return nextNo(COMMON_NO, COMMON_NO_INIT + "");
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 生成编号，分布式锁保证并发安全
     * @author lipo
     * @date 2019-11-11 11:10
     */
    private long nextNo(String noName, String initNo) throws IOException {
        Lock lock = redisLockRegistry.obtain("NO_NAME:" + noName);

        try {
            lock.lock();

            //查询数据
            EsNoEntity esNoEntity = getNo(noName);
            long currentNo;
            if (esNoEntity == null) {
                //不存在数据，插入初始值
                currentNo = Long.parseLong(initNo);
                esNoEntity = new EsNoEntity(noName, initNo, initNo);
            } else {
                //已经存在, +1
                currentNo = Long.parseLong(esNoEntity.getCurrentNo()) + 1;
                esNoEntity.setCurrentNo(currentNo + "");
            }

            //保存
            save(noName, esNoEntity);

            return currentNo;

        } finally {
            lock.unlock();
        }

    }

    /**
     * 添加更新数据，自动创建索引
     * @author lipo
     * @date 2019-11-12 09:55
     */
    private void save(String noName, EsNoEntity esNoEntity) throws IOException {
        IndexRequest request = new IndexRequest(EsNoEntity.INDEX, EsNoEntity.TYPE, noName);
        request.source(JSON.toJSONString(esNoEntity), XContentType.JSON);
        restHighLevelClient.index(request, RequestOptions.DEFAULT);
    }

    /**
     * 查询数据
     * @author lipo
     * @date 2019-11-11 11:37
     */
    private EsNoEntity getNo(String noName) throws IOException {
        GetRequest getRequest = new GetRequest(EsNoEntity.INDEX, EsNoEntity.TYPE, noName);
        GetResponse getResponse = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
        String source = getResponse.getSourceAsString();
        return JSON.parseObject(source, EsNoEntity.class);
    }

}
