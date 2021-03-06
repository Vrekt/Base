package arc;

import arc.check.CheckManager;
import arc.command.ArcCommand;
import arc.configuration.ArcConfiguration;
import arc.data.Data;
import arc.exemption.ExemptionManager;
import arc.listener.combat.CombatPacketListener;
import arc.listener.connection.PlayerConnectionListener;
import arc.listener.moving.MovingPacketListener;
import arc.listener.player.PlayerListener;
import arc.punishment.PunishmentManager;
import arc.violation.ViolationManager;
import bridge.Bridge;
import bridge.Version;
import bridge1_15.Bridge115;
import bridge1_16.Bridge116;
import bridge1_8.Bridge18;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

/**
 * The main entry point for Arc.
 */
public final class Arc extends JavaPlugin {

    /**
     * The version of Arc.
     */
    public static final String VERSION_STRING = "2.0.1";

    /**
     * The file configuration
     */
    private static Arc arc;

    /**
     * The version
     */
    private static Version version;

    /**
     * The bridge
     */
    private static Bridge bridge;

    /**
     * The arc configuration
     */
    private final ArcConfiguration arcConfiguration = new ArcConfiguration();

    /**
     * The violation manager.
     */
    private final ViolationManager violationManager = new ViolationManager();

    /**
     * The check manager
     */
    private final CheckManager checkManager = new CheckManager();

    /**
     * The exemption manager
     */
    private final ExemptionManager exemptionManager = new ExemptionManager();

    /**
     * Punishment manager.
     */
    private final PunishmentManager punishmentManager = new PunishmentManager();

    /**
     * The protocol manager.
     */
    private ProtocolManager protocolManager;

    /**
     * If the version is incompatible
     */
    private boolean incompatible;

    @Override
    public void onEnable() {
        arc = this;
        getLogger().info("Checking server version...");
        if (!loadCompatibleVersions()) {
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        getLogger().info("Reading configuration...");
        saveDefaultConfig();
        arcConfiguration.read(getConfig());

        getLogger().info("Registering checks and listeners...");
        loadExternalPlugins();
        checkManager.initialize();
        violationManager.initialize(arcConfiguration);
        punishmentManager.initialize(arcConfiguration);
        registerListeners();

        getLogger().info("Registering base command...");
        verifyCommand();

        getLogger().info("Saving configuration...");
        saveConfig();

        getLogger().info("Ready!");
    }

    @Override
    public void onDisable() {
        if (incompatible) return;

        exemptionManager.close();
        violationManager.close();
        checkManager.close();
        punishmentManager.close();
        unregisterListeners();

        Bukkit.getOnlinePlayers().forEach(Data::removeAll);
        arc = null;
    }

    /**
     * Register all listeners
     */
    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PlayerConnectionListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);

        new MovingPacketListener().register(protocolManager);
        new CombatPacketListener().register(protocolManager);
    }

    /**
     * Unregister listeners
     */
    private void unregisterListeners() {
        protocolManager.removePacketListeners(this);
    }

    /**
     * Load external plugins.
     * TODO: Other plugin support like bans, etc.
     */
    private void loadExternalPlugins() {
        protocolManager = ProtocolLibrary.getProtocolManager();
    }

    /**
     * Verify the command /arc exists.
     */
    private void verifyCommand() {
        final PluginCommand command = getCommand("arc");
        if (command == null) {
            getLogger().log(Level.SEVERE, "/arc command not found! You will not be able to use this command.");
        } else {
            command.setExecutor(new ArcCommand());
        }
    }

    /**
     * Load compatible versions
     */
    private boolean loadCompatibleVersions() {
        if (Bukkit.getVersion().contains("1.8.8")) {
            loadFor1_8();
        } else if (Bukkit.getVersion().contains("1.16.4")) {
            loadFor1_16();
        } else if (Bukkit.getVersion().contains("1.15.2")) {
            loadFor1_15();
        } else {
            getLogger().log(Level.SEVERE, "Arc is not compatible with this version: " + Bukkit.getVersion());
            incompatible = true;
            return false;
        }
        getLogger().info("Initialized Arc for: " + Bukkit.getVersion());
        return true;
    }

    /**
     * Load arc for 1.8.8
     */
    private void loadFor1_8() {
        version = Version.VERSION_1_8;
        bridge = new Bridge18();
    }

    /**
     * Load arc for 1.15
     */
    private void loadFor1_15() {
        version = Version.VERSION_1_15;
        bridge = new Bridge115();
    }

    /**
     * Load arc for 1.16
     */
    private void loadFor1_16() {
        version = Version.VERSION_1_16;
        bridge = new Bridge116();
    }


    /**
     * @return the internal plugin
     */
    public static JavaPlugin plugin() {
        return arc;
    }

    /**
     * @return arc
     */
    public static Arc arc() {
        return arc;
    }

    /**
     * @return the version
     */
    public static Version version() {
        return version;
    }

    /**
     * @return the bridge
     */
    public static Bridge bridge() {
        return bridge;
    }

    /**
     * @return the arc configuration
     */
    public ArcConfiguration configuration() {
        return arcConfiguration;
    }

    /**
     * @return the violation manager
     */
    public ViolationManager violations() {
        return violationManager;
    }

    /**
     * @return the exemptions manager
     */
    public ExemptionManager exemptions() {
        return exemptionManager;
    }

    /**
     * @return the check manager
     */
    public CheckManager checks() {
        return checkManager;
    }

    /**
     * @return the punishment manager
     */
    public PunishmentManager punishment() {
        return punishmentManager;
    }

    /**
     * @return the protocol manager
     */
    public ProtocolManager protocol() {
        return protocolManager;
    }

}
