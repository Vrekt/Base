package arc.check.network;

import arc.Arc;
import arc.check.CheckType;
import arc.check.PacketCheck;
import arc.check.result.CheckResult;
import arc.data.packet.PacketData;
import arc.violation.result.ViolationResult;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Checks if the player is sending too many swing packets.
 */
public final class SwingFrequency extends PacketCheck {

    /**
     * Max packets and packet kick threshold
     */
    private int maxPacketsPerSecond, packetKickThreshold;

    /**
     * Kick if the threshold is reached
     */
    private boolean kickIfThresholdReached;

    public SwingFrequency() {
        super(CheckType.SWING_FREQUENCY);
        enabled(true)
                .cancel(true)
                .cancelLevel(0)
                .notify(true)
                .notifyEvery(1)
                .ban(true)
                .banLevel(10)
                .kick(true)
                .kickLevel(5)
                .build();

        addConfigurationValue("max-packets-per-second", 50);
        addConfigurationValue("kick-if-threshold-reached", false);
        addConfigurationValue("packet-kick-threshold", 100);
        if (enabled()) load();
    }

    /**
     * Check this player
     *
     * @param player the player
     * @param data   the packet data
     */
    private void check(Player player, PacketData data) {
        final CheckResult result = new CheckResult();
        final int count = data.swingPacketCount();

        if (count >= maxPacketsPerSecond) {
            result.setFailed("Too many swing packets per second.");
            result.parameter("packets", data.swingPacketCount());
            result.parameter("max", maxPacketsPerSecond);
            if (count >= packetKickThreshold && kickIfThresholdReached && !Arc.arc().punishment().hasPendingKick(player)) {
                Arc.arc().punishment().kickPlayer(player, this);
            }
        } else {
            data.cancelSwingPackets(false);
        }
        data.swingPacketCount(0);

        final ViolationResult violation = checkViolation(player, result);
        data.cancelSwingPackets(violation.cancel());
    }

    @Override
    public void reloadConfig() {
        load();
    }

    @Override
    public void load() {
        maxPacketsPerSecond = configuration.getInt("max-packets-per-second");
        kickIfThresholdReached = configuration.getBoolean("kick-if-threshold-reached");
        packetKickThreshold = configuration.getInt("packet-kick-threshold");

        schedule(() -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!exempt(player)) check(player, PacketData.get(player));
            }
        }, 20, 20);
    }

    @Override
    public void unload() {
        cancelScheduled();
    }
}
