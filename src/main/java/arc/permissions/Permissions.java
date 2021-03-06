package arc.permissions;

import arc.check.CheckCategory;
import arc.check.CheckType;
import org.bukkit.entity.Player;

/**
 * Arc permissions
 */
public final class Permissions {

    /**
     * The violations permission
     */
    public static final String ARC_VIOLATIONS = "arc.violations";

    /**
     * The bypass permission
     */
    public static final String ARC_BYPASS = "arc.bypass";

    /**
     * The administrator permission
     */
    public static final String ARC_ADMINISTRATOR = "arc.administrator";

    /**
     * The base command permission.
     */
    public static final String ARC_COMMANDS_BASE = "arc.commands.base";

    /**
     * The permission to toggle violations
     */
    public static final String ARC_COMMANDS_TOGGLE_VIOLATIONS = "arc.commands.toggleviolations";

    /**
     * The permission to cancel player bans
     */
    public static final String ARC_COMMANDS_CANCEL_BAN = "arc.commands.cancelban";

    /**
     * The permission to reload the config
     */
    public static final String ARC_COMMANDS_RELOAD_CONFIG = "arc.commands.reloadconfig";

    /**
     * The permission to execute all arc commands
     */
    public static final String ARC_COMMANDS_ALL = "arc.commands.all";

    /**
     * The permission to view player summaries.
     */
    public static final String ARC_COMMANDS_SUMMARY = "arc.commands.summary";

    /**
     * The permission to view timings
     */
    public static final String ARC_COMMANDS_TIMINGS = "arc.commands.timings";

    /**
     * Check if the player can view violations
     *
     * @param player the player
     * @return {@code true} if so.
     */
    public static boolean canViewViolations(Player player) {
        return player.hasPermission(ARC_VIOLATIONS);
    }

    /**
     * Check if the player can bypass checks
     *
     * @param player the player
     * @return {@code true} if so.
     */
    public static boolean canBypassAllChecks(Player player) {
        return player.hasPermission(ARC_BYPASS);
    }

    /**
     * Check if a player can bypass a category all together
     *
     * @param player   the player
     * @param category the category
     * @return {@code true} if so
     */
    public static boolean canBypassCategory(Player player, CheckCategory category) {
        return player.hasPermission(ARC_BYPASS + "." + category.name().toLowerCase());
    }

    /**
     * Check if the player can bypass the provided checks.
     *
     * @param player the player
     * @param checks checks
     * @return {@code true} if so.
     */
    public static boolean canBypassChecks(Player player, CheckType... checks) {
        if (canBypassAllChecks(player)) return true;

        for (CheckType check : checks) {
            if (canBypassCategory(player, check.category())) return true;
            if (player.hasPermission(ARC_BYPASS + "." + check.category().name().toLowerCase() + "." + check.getName().toLowerCase()))
                return true;

        }
        return false;
    }
}
