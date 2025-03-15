package com.katomegumi.zxpicturebackend.manager.sharding;



import org.apache.shardingsphere.sharding.api.sharding.standard.PreciseShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.RangeShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.StandardShardingAlgorithm;

import java.util.Collection;
import java.util.List;
import java.util.Properties;

/**
 * 自定义 算法类
 */
public class PictureShardingAlgorithm implements StandardShardingAlgorithm<Long> {

    /**
     * @param collection  表示数据库(配置中)中全部的表
     * @param preciseShardingValue (spaceId) 根据分表的字段
     * @return 就是需要查询的表名
     */
    @Override
    public String doSharding(Collection<String> collection, PreciseShardingValue<Long> preciseShardingValue) {
        Long spaceId = preciseShardingValue.getValue();
        String logicTableName = preciseShardingValue.getLogicTableName();
        if (spaceId==null){
            return logicTableName;
        }
        //根据 spaceId查询要路由到那张表
        String realTableName=logicTableName+"_"+spaceId;
        if (collection.contains(realTableName)){
            return realTableName;
        }else {
            return logicTableName;
        }
    }

    @Override
    public Collection<String> doSharding(Collection<String> collection, RangeShardingValue<Long> rangeShardingValue) {
        return List.of();
    }

    @Override
    public Properties getProps() {
        return null;
    }

    @Override
    public void init(Properties properties) {

    }
}
