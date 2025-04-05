package me.gamercoder215.mobchip.abstraction.v1_21_R4;

import me.gamercoder215.mobchip.ai.behavior.BehaviorResult;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings({"unchecked", "rawtypes"})
final class BehaviorResult1_21_R4 extends BehaviorResult {
    private final BehaviorControl b;
    private final LivingEntity mob;
    private final ServerLevel l;

    public <T extends LivingEntity> BehaviorResult1_21_R4(BehaviorControl<T> b, T mob) {
        this.b = b;
        this.mob = mob;
        this.l = ChipUtil1_21_R4.toNMS(Bukkit.getWorld(mob.getCommandSenderWorld().getWorld().getUID()));

        b.tryStart(l, mob, 0);
    }

    @Override
    public @NotNull Status getStatus() {
        return ChipUtil1_21_R4.fromNMS(b.getStatus());
    }

    @Override
    public void stop() {
        b.doStop(l, mob, 0);
    }
}
