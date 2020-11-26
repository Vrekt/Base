package arc.configuration;

import arc.Arc;
import arc.configuration.punishment.ban.BanConfiguration;
import arc.configuration.punishment.kick.KickConfiguration;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * The arc configuration
 */
public final class ArcConfiguration {

    /**
     * Handles ban configuration values
     */
    private BanConfiguration banConfiguration;
    /**
     * Handles kick configuration values
     */
    private KickConfiguration kickConfiguration;

    /**
     * If TPS should be watched
     */
    private boolean watchTps;

    /**
     * The lower limit of when to optimize checks to up the TPS.
     */
    private int tpsLowerLimit;

    /**
     * The violation message
     * The no permissions message
     * The prefix
     */
    private String violationMessage, noPermissionMessage, prefix;

    /**
     * Initializes the configuration and starts reading
     *
     * @param configuration the configuration
     */
    public ArcConfiguration(FileConfiguration configuration) {
        read(configuration);
    }

    /**
     * Read
     *
     * @param configuration the configuration
     */
    private void read(FileConfiguration configuration) {
        banConfiguration = new BanConfiguration(configuration);
        kickConfiguration = new KickConfiguration(configuration);

        watchTps = configuration.getBoolean("tps-helper");
        tpsLowerLimit = configuration.getInt("tps-lower-limit");
        violationMessage = ChatColor.translateAlternateColorCodes('&', configuration.getString("violation-notify-message"));
        noPermissionMessage = ChatColor.translateAlternateColorCodes('&', configuration.getString("arc-command-no-permission-message"));
        prefix = ChatColor.translateAlternateColorCodes('&', configuration.getString("arc-prefix"));
    }

    /**
     * @return the ban configuration
     */
    public BanConfiguration banConfiguration() {
        return banConfiguration;
    }

    /**
     * @return the kick configuration
     */
    public KickConfiguration kickConfiguration() {
        return kickConfiguration;
    }

    /**
     * @return if TPS should be watched
     */
    public boolean watchTps() {
        return watchTps;
    }

    /**
     * @return the tps lower limit
     */
    public int tpsLowerLimit() {
        return tpsLowerLimit;
    }

    /**
     * @return the violation message.
     */
    public String violationMessage() {
        return violationMessage;
    }

    /**
     * @return the no permission message for the /arc command
     */
    public String noPermissionMessage() {
        return noPermissionMessage;
    }

    /**
     * @return the prefix
     */
    public String prefix() {
        return prefix;
    }

    /**
     * Reload configuration
     */
    public void reload() {
        Arc.arc().reloadConfig();
        final FileConfiguration config = Arc.arc().getConfig();

        read(config);
        Arc.arc().checks().reloadConfigurations(config);
    }

}
