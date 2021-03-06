package arc.check;

import arc.check.combat.Criticals;
import arc.check.combat.KillAura;
import arc.check.combat.NoSwing;
import arc.check.combat.Reach;
import arc.check.moving.*;
import arc.check.network.PayloadFrequency;
import arc.check.network.SwingFrequency;
import arc.check.player.FastUse;
import arc.check.player.Regeneration;
import arc.configuration.ArcConfiguration;
import arc.configuration.Configurable;

import java.io.Closeable;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * A check manager
 */
public final class CheckManager extends Configurable implements Closeable {

    /**
     * All the checks
     */
    private final Set<Check> checks = new HashSet<>();

    /**
     * Populate the check map.
     */
    public void initialize() {
        add(new Criticals());
        add(new MorePackets());
        add(new NoFall());
        add(new PayloadFrequency());
        add(new SwingFrequency());
        add(new Regeneration());
        add(new FastUse());
        add(new Jesus());
        add(new Reach());
        add(new NoSwing());
        add(new KillAura());
        add(new Flight());
        add(new Speed());
    }

    /**
     * Add a check
     *
     * @param check the check
     */
    private void add(Check check) {
        checks.add(check);
    }

    @Override
    public void reload(ArcConfiguration configuration) {
        checks.forEach(check -> check.reload(configuration));
    }

    /**
     * Get a check
     *
     * @param checkType the type
     * @return the check
     */
    public Check getCheck(CheckType checkType) {
        return checks.stream().filter(check -> check.type() == checkType).findAny().orElseThrow(() -> new NoSuchElementException("Could not find check " + checkType.getName()));
    }

    /**
     * @return all checks
     */
    public Set<Check> getAllChecks() {
        return checks;
    }

    @Override
    public void close() {
        checks.forEach(Check::unload);
        checks.clear();
    }
}
