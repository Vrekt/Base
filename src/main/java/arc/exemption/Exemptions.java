package arc.exemption;

import arc.check.CheckType;

import java.util.HashMap;
import java.util.Map;

/**
 * Player exemption data
 */
public final class Exemptions {

    /**
     * Exemption data
     */
    private final Map<CheckType, Long> exemptions = new HashMap<>();

    /**
     * Add an exemption
     *
     * @param check    the check
     * @param duration the duration
     */
    public void addExemption(CheckType check, long duration) {
        exemptions.put(check, duration);
    }

    /**
     * Check if there is an exemption
     *
     * @param check the check
     * @return {@code true} if so
     */
    public boolean isExempt(CheckType check) {
        final var time = exemptions.getOrDefault(check, 0L);
        if (time == 0) return false;

        final var result = (System.currentTimeMillis() - time <= 0);
        if (!result) exemptions.remove(check);
        return result;
    }

    /**
     * Dispose
     */
    public void dispose() {
        exemptions.clear();
    }

}
