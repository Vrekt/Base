package arc.check;

/**
 * Tells what type the check is
 */
public enum CheckType {
    /**
     * Checks if the player is taking fall damage
     */
    NOFALL,
    /**
     * Checks if the player is flying
     */
    FLIGHT,
    /**
     * Checks if the player is moving fast
     */
    SPEED,
    /**
     * Checks if the player is using critical hits while impossible.
     */
    CRITICALS

}
