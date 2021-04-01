package cn.lxw.ss.algorithem;

import org.apache.shardingsphere.api.sharding.hint.HintShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.hint.HintShardingValue;

import java.util.Arrays;
import java.util.Collection;

/**
 * @author xingweilin@clubfactory.com
 * @date 2021/4/1 8:17 下午
 */
public class MyHintDSShardingAlgorithm implements HintShardingAlgorithm<Long> {

    @Override
    public Collection<String> doSharding(Collection<String> collection, HintShardingValue<Long> hintShardingValue) {
        String key = "m" + hintShardingValue.getValues().toArray()[0];
        if(collection.contains(key)){
            return Arrays.asList(key);
        }
        throw new UnsupportedOperationException("route "+ key +" is not supported ,please check your config");
    }
}
