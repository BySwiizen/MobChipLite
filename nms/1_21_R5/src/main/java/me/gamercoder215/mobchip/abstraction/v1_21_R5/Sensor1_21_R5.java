package me.gamercoder215.mobchip.abstraction.v1_21_R5;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;

import java.util.Set;
import java.util.stream.Collectors;

final class Sensor1_21_R5 extends Sensor<LivingEntity> {

    private final me.gamercoder215.mobchip.ai.sensing.Sensor<?> s;

    public Sensor1_21_R5(me.gamercoder215.mobchip.ai.sensing.Sensor<?> s) {
        this.s = s;
    }

    @Override
    protected void doTick(ServerLevel level, LivingEntity en) {
        s.run(ChipUtil1_21_R5.fromNMS(level), ChipUtil1_21_R5.fromNMS(en));
    }

    @Override
    public Set<MemoryModuleType<?>> requires() {
        return s.required().stream().map(ChipUtil1_21_R5::toNMS).collect(Collectors.toSet());
    }

    public me.gamercoder215.mobchip.ai.sensing.Sensor<?> getSensor() {
        return s;
    }

}
