package optjava.ch13.agent;

/**
 *
 * @author kittylyst
 */
public class RuntimeCostAccounter {
    private static final ThreadLocal<Long> allocationCost = new ThreadLocal<Long>() {
        @Override
        protected Long initialValue() {
            return 0L;
        }
    };

    public static void recordAllocation(final String typeName) {
        // 사실, 이것보다 정교한 코드가 필요하다.
        // 가령, 타입에 맞는 대략적인 크기를 캐시하는...
        checkAllocationCost(1);
    }

    public static void recordArrayAllocation(final int length, final int multiplier) {
        checkAllocationCost(length * multiplier);
    }

    private static void checkAllocationCost(final long additional) {
        final long newValue = additional + allocationCost.get();
        allocationCost.set(newValue);
        // 액션을 취해야 하나? 어떤 한계치를 초과할 경우 실패하는...?
    }

    // 이 메서드는 (JMX 계수기를 통해) 표출할 수 있다
    public static long getAllocationCost() {
        return allocationCost.get();
    }

    public static void resetCounters() {
        allocationCost.set(0L);
    }
}
