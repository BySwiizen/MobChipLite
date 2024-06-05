package me.gamercoder215.mobchip.bosses;

import org.bukkit.Bukkit;
import org.bukkit.entity.Mob;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

/**
 * Represents a Boss Handler
 */
public final class BossHandler {

    private final Plugin plugin;
    private final Boss<?> boss;

    BossHandler(Boss<?> boss, Plugin p) {
        this.plugin = p;
        this.boss = boss;
        new BossEvents(this);
    }

    /**
     * Get the Plugin associated with this BossHandler.
     * @return Plugin in this Handler
     */
    public Plugin getPlugin() {
        return this.plugin;
    }

    /**
     * Get the Boss of this Handler.
     * @return Boss Found
     */
    public Boss<?> getBoss() {
        return this.boss;
    }

    private static final class BossEvents implements Listener {

        private final Boss<?> boss;

        private BossEvents(BossHandler handler) {
            this.boss = handler.boss;
            Bukkit.getPluginManager().registerEvents(this, handler.plugin);
        }

        @EventHandler
        public void onDamageDefensive(EntityDamageEvent e) {
            if (!(e.getEntity() instanceof Mob)) return;
            Mob m = (Mob) e.getEntity();

            if (boss.getMob().getUniqueId().equals(m.getUniqueId())) {
                boss.onDamageDefensive(e);
            }
        }

        @EventHandler
        public void onDamageOffensive(EntityDamageByEntityEvent e) {
            if (!(e.getDamager() instanceof Mob)) return;
            Mob m = (Mob) e.getEntity();

            if (boss.getMob().getUniqueId().equals(m.getUniqueId())) {
                boss.onDamageOffensive(e);
            }
        }

        @EventHandler
        public void onDeath(EntityDeathEvent e) {
            if (!(e.getEntity() instanceof Mob)) return;
            Mob m = (Mob) e.getEntity();

            if (boss.getMob().getUniqueId().equals(m.getUniqueId())) {
                boss.onDeath(e);
                for (ItemStack i : boss.getDrops()) m.getWorld().dropItemNaturally(m.getLocation(), i);
                if (boss.getDeathSound() != null) m.getWorld().playSound(m.getLocation(), boss.getDeathSound(), 3F, 1F);
            }
        }

    }

}
