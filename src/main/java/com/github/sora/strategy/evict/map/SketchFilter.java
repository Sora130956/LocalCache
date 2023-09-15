package com.github.sora.strategy.evict.map;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Count-Min Sketch算法实现的LFU过滤器
 * @author Sora
 */
public class SketchFilter<K,V>{

    private final ExecutorService election;

    private ProbationMap<K,V> probationZone;

    private ProtectedMap<K,V> protectedZone;

    /**
     * 使用CountMin Sketch算法估算元素出现的历史频率
     */
    private final byte[][] sketch;

    private static final short SKETCH_SIZE = 256;

    private static final byte COUNT_OF_HASH = 4;

    private Random random = new Random();

    public SketchFilter(int probationSize,int protectedSize){
        sketch = new byte[COUNT_OF_HASH][SKETCH_SIZE];
        protectedZone = new ProtectedMap<>(protectedSize,this);
        probationZone = new ProbationMap<>(probationSize,this);
        election = Executors.newSingleThreadExecutor();
        // 消费者，淘汰victim或者candidate,未被淘汰的加入probation
        election.execute(() -> {
            while (true){
                try {
                    Entry<K,V> victim = victims.take();
                    Entry<K,V> candidate = candidates.take();
                    // 根据victim和candidate在sketch中记录的历史频率值来淘汰某一方
                    // 策略上优先淘汰probation来的victim,避免新的热点数据难以进入缓存的问题
                    byte victimFreq = frequency(victim.getValue());
                    byte candidateFreq = frequency(candidate.getValue());
                    if (candidateFreq > victimFreq){
                        // 如果candidate的频率更高，直接淘汰victim
                        probationZone.put(candidate.getKey(), candidate.getValue());
                        continue;
                    }
                    if (candidateFreq < 5){
                        // 如果candidate的频率小于5，且victim的频率更高，则直接淘汰candidate
                        probationZone.put(victim.getKey(), victim.getValue());
                        continue;
                    }
                    // 如果candidate和victim的频率都比较高，且victim的频率更高，则随机淘汰candidate或者victim
                    if (random.nextBoolean()){
                        probationZone.put(candidate.getKey(), candidate.getValue());
                    } else {
                        probationZone.put(victim.getKey(), victim.getValue());
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    ProbationMap<K, V> getProbationZone() {
        return probationZone;
    }

    ProtectedMap<K, V> getProtectedZone() {
        return protectedZone;
    }

    /**
     * Count-Min Sketch算法，获得stuff的估算历史频率
     * 具体来说，先获得stuff的哈希值，分别取第[0,7]位、[8,15]位、[16,23]位、[24,31]位的值作为sketch的第一维下标a、b、c、d
     * 然后在sketch[0][a]、sketch[1][b]、sketch[2][c]、sketch[3][d]中找到最小的计数值,我们认为这个值就是该元素的历史频率值
     */
    private byte frequency(V stuff){
        int hashCode = spread(stuff.hashCode());
        int mask = 0x000000ff;
        byte minHash = 0;
        short minPosition = 0;
        byte minFreq = Byte.MAX_VALUE;
        for (byte hash=0; hash<COUNT_OF_HASH; hash++){
            short position = (short)(mask&hashCode);
            byte freq = sketch[hash][position];
            if (freq <= minFreq){
                minFreq = freq;
                minHash = hash;
                minPosition = position;
            }
            hashCode = hashCode>>8;
        }
        return sketch[minHash][minPosition];
    }

    /**
     * Count-Min Sketch算法，在sketch中将stuff的出现次数加1
     * 具体来说，先获得stuff的哈希值，分别取第[0,7]位、[8,15]位、[16,23]位、[24,31]位的值作为sketch的第一维下标a、b、c、d
     * 然后在sketch[0][a]、sketch[1][b]、sketch[2][c]、sketch[3][d]中找到最小的计数值，对这个值加1
     * 为了防止溢出问题、保证出现频率的新鲜度，当计数值达到127后就将这个计数值折半
     */
    void increment(V stuff){
        int hashCode = spread(stuff.hashCode());
        int mask = 0x000000ff;
        byte minHash = 0;
        short minPosition = 0;
        byte minFreq = Byte.MAX_VALUE;
        for (byte hash=0; hash<COUNT_OF_HASH; hash++){
            short position = (short)(mask&hashCode);
            byte freq = sketch[hash][position];
            if (freq <= minFreq){
                minFreq = freq;
                minHash = hash;
                minPosition = position;
            }
            hashCode = hashCode>>8;
        }
        if (++sketch[minHash][minPosition] == Byte.MAX_VALUE){
            sketch[minHash][minPosition] /= 2;
        }
    }

    /** 补充一个散列函数来避免低质量的哈希。 */
    static int spread(int x) {
        x ^= x >>> 17;
        x *= 0xed5ad4bb;
        x ^= x >>> 11;
        x *= 0xac4c1b51;
        x ^= x >>> 15;
        return x;
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
     * 否则，就将当前元素放入candidates,最终让消费者让candidate和victim竞争
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
