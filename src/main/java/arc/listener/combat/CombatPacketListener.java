package arc.listener.combat;

import arc.Arc;
import arc.check.CheckType;
import arc.check.combat.Criticals;
import arc.check.combat.KillAura;
import arc.check.combat.NoSwing;
import arc.check.combat.Reach;
import arc.data.combat.CombatData;
import arc.data.packet.PacketData;
import arc.listener.AbstractPacketListener;
import com.comphenix.packetwrapper.WrapperPlayClientUseEntity;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

/**
 * Listens for combat related events
 */
public final class CombatPacketListener extends AbstractPacketListener {

    /**
     * Criticals
     */
    private final Criticals criticals;

    /**
     * Reach
     */
    private final Reach reach;

    /**
     * No swing
     */
    private final NoSwing noSwing;

    /**
     * KillAura
     */
    private final KillAura killAura;

    @Override
    public void register(ProtocolManager protocol) {
        listener(protocol, PacketType.Play.Client.USE_ENTITY, this::onUseEntity);
        listener(protocol, PacketType.Play.Client.ARM_ANIMATION, this::onArmSwing);
    }

    public CombatPacketListener() {
        criticals = (Criticals) Arc.arc().checks().getCheck(CheckType.CRITICALS);
        reach = (Reach) Arc.arc().checks().getCheck(CheckType.REACH);
        noSwing = (NoSwing) Arc.arc().checks().getCheck(CheckType.NO_SWING);
        killAura = (KillAura) Arc.arc().checks().getCheck(CheckType.KILL_AURA);
    }

    /**
     * Invoked when the player tries to use an entity.
     *
     * @param event the event
     */
    private void onUseEntity(PacketEvent event) {
        final WrapperPlayClientUseEntity packet = new WrapperPlayClientUseEntity(event.getPacket());
        if (packet.getType() == EnumWrappers.EntityUseAction.ATTACK) {
            // the player attacked an entity, run checks.
            final Player player = event.getPlayer();
            final Entity entity = packet.getTarget(player.getWorld());
            if (entity instanceof LivingEntity) {
                boolean checkKillAura = false, checkCriticals = false, checkReach = false, checkNoSwing = false;
                if (killAura.enabled()) checkKillAura = killAura.check(player, entity);
                if (reach.enabled()) checkReach = reach.check(player, entity);
                if (noSwing.enabled()) checkNoSwing = noSwing.check(player);
                if (criticals.enabled()) checkCriticals = criticals.check(player);

                if (checkKillAura || checkCriticals || checkReach || checkNoSwing) event.setCancelled(true);
            }
        }
    }

    /**
     * Invoked when the player swings their arm
     *
     * @param event the event
     */
    private void onArmSwing(PacketEvent event) {
        final Player player = event.getPlayer();
        final PacketData data = PacketData.get(player);
        data.incrementSwingPacketCount();

        if (data.cancelSwingPackets()) {
            event.setCancelled(true);
            return;
        }

        final CombatData combatData = CombatData.get(player);
        combatData.lastSwingTime(System.currentTimeMillis());
    }

}
