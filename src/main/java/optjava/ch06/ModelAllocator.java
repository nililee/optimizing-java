package optjava.ch06;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 *
 * @author ben
 */
public class ModelAllocator implements Runnable {
    private volatile boolean shutdown = false;

    private double chanceOfLongLived = 0.02;
    private int multiplierForLongLived = 20;
    private int x = 1024;
    private int y = 1024;
    private int mbPerSec = 50;
    private int shortLivedMs = 100;
    private int nThreads = 8;
    private Executor exec = Executors.newFixedThreadPool(nThreads);    

    public static void main(String[] args) {
        ModelAllocator ma = new ModelAllocator();
        ma.run();
    }

    public void run() {
        final int mainSleep = (int) (1000.0 / mbPerSec);

        while (!shutdown) {
            for (int i = 0; i < mbPerSec; i++) {
                ModelObjectAllocation to = new ModelObjectAllocation(x, y, lifetime());
                exec.execute(to);
                try {
                    Thread.sleep(mainSleep);
                } catch (InterruptedException ex) {
                    shutdown = true;
                }
            }
        }
    }

    // 약한 세대별 가설을 간단히 모형화한 함수
    // 객체의 기대 수명을 반환함.
    // 보통 객체 수명은 아주 짧지만, 드물게 "장수하는" 객체도 있음.
    public int lifetime() {
        if (Math.random() < chanceOfLongLived) {
            return multiplierForLongLived * shortLivedMs;
        }

        return shortLivedMs;
    }
}
