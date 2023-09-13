package com.github.sora.strategy.evict;

import com.github.sora.strategy.evict.map.Entry;
import com.github.sora.strategy.evict.map.ProbationMap;
import com.github.sora.strategy.evict.map.ProtectedMap;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Count-Min Sketch算法实现的LFU过滤器
 * @author Sora
 */
public class SketchFilter<K,V>{

    private ExecutorService election = null;

    private ProbationMap<K,V> probationZone;

    private ProtectedMap<K,V> protectedZone;

    public SketchFilter(int probationSize,int protectedSize){
        probationZone = new ProbationMap<>(probationSize,this);
        protectedZone = new ProtectedMap<>(protectedSize,this);
        election = Executors.newSingleThreadExecutor();
        // 消费者，淘汰victim或者candidate,未被淘汰的加入probation
        election.execute(() -> {
            while (true){
                try {
                    Entry<K,V> victim = victims.take();
                    Entry<K,V> candidate = candidates.take();

                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    /**
     * Count-Min Sketch算法，获得key的估算历史频率
     */
    private byte frequency(K key){
        // TODO
    }

    /**
     * 保存从Probation区域被淘汰的victims
     */
    LinkedBlockingDeque<Entry<K,V>> victims = new LinkedBlockingDeque<>();

    /**
     * 保存从Window cache或者protected区域被淘汰的candidates
     */
    LinkedBlockingDeque<Entry<K,V>> candidates = new LinkedBlockingDeque<>();

    /**
     * 从window或者protected产生的candidate，试图加入probation区，如果有victim则需要与其竞争
     * 其实这可以看作一个生产者，没有victim时，就试图从probation区中置换出元素，放入victims队列中
     * 否则，就将当前元素放入candidates,最终让消费者让candidate和vicitm竞争
     * @return 直接进入Probation返回true，否则返回false。
     */
    public boolean candidate(K key,V value) throws InterruptedException {
        if (victims.isEmpty()){
            // victims为空，说明不需要跟victim竞争，直接加入Probation，如果Probation满，则产生victim，下一个candidate需要跟victim竞争
            probationZone.put(key, value);
            return true;
        } else {
            // 存在victim，当前candidate放入阻塞队列与之竞争
            candidates.put(new Entry<>(key, value));
            return false;
        }
    }

    public boolean victim(K key,V value) throws InterruptedException {
        // 将probation淘汰的victim加入victims中，等待消费者消费
        victims.put(new Entry<>(key, value));
        return true;
    }

}
