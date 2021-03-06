package arc.exemption;

import arc.check.CheckType;
import arc.exemption.type.ExemptionType;

import java.util.*;

/**
 * Player exemption data
 */
public final class Exemptions {

    /**
     * Exemption data
     */
    private final Map<CheckType, Long> exemptions = new HashMap<>();

    /**
     * Set of exemption types
     */
    private final List<ExemptionType> exemptionTypes = new ArrayList<>();

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
     * Add an exemption type
     *
     * @param type the type
     */
    public void addExemption(ExemptionType type) {
        exemptionTypes.add(type);
    }

    /**
     * Remove an exemption type
     *
     * @param type the type
     */
    public void removeExemption(ExemptionType type) {
        exemptionTypes.remove(type);
    }

    /**
     * Check if there is an exemption
     *
     * @param check the check
     * @return {@code true} if so
     */
    public boolean isExempt(CheckType check) {
        final long time = exemptions.getOrDefault(check, -1L);
        if (time == -1) return false;

        final boolean result = time == 0 || (System.currentTimeMillis() - time <= 0);
        if (result) exemptions.remove(check);
        return result;
    }

    /**
     * Check if there is an exemption
     *
     * @param type the type
     * @return {@code true} if so
     */
    public boolean isExempt(ExemptionType type) {
        return exemptionTypes.contains(type);
    }

    /**
     * Clear these exemptions
     */
    public void clear() {
        exemptions.clear();
        exemptionTypes.clear();
    }

}
