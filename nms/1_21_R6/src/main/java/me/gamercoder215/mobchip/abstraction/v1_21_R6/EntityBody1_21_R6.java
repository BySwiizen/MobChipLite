package me.gamercoder215.mobchip.abstraction.v1_21_R6;

import me.gamercoder215.mobchip.EntityBody;
import me.gamercoder215.mobchip.abstraction.ChipUtil;
import me.gamercoder215.mobchip.ai.animation.EntityAnimation;
import me.gamercoder215.mobchip.util.Position;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAnimatePacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import static net.minecraft.world.InteractionResult.Success.*;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.CombatTracker;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.gameevent.GameEvent;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_21_R6.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

final class EntityBody1_21_R6 implements EntityBody {
    private final net.minecraft.world.entity.Mob nmsMob;
    private final Mob m;

    public EntityBody1_21_R6(Mob m) {
        this.m = m;
        this.nmsMob = ChipUtil1_21_R6.toNMS(m);
    }

    private void update() {
        ClientboundSetEntityDataPacket packet = new ClientboundSetEntityDataPacket(nmsMob.getId(), nmsMob.getEntityData().packDirty());

        for (Player p : m.getWorld().getPlayers())
            ((CraftPlayer) p).getHandle().connection.send(packet);
    }

    /**
     * Whether this Entity is Left Handed.
     *
     * @return true if left-handed, else false
     */
    @Override
    public boolean isLeftHanded() {
        return nmsMob.isLeftHanded();
    }

    /**
     * Sets this Entity to be left-handed.
     *
     * @param leftHanded true if left-handed, else false
     */
    @Override
    public void setLeftHanded(boolean leftHanded) {
        nmsMob.setLeftHanded(leftHanded);
    }

    @Override
    public boolean canBreatheUnderwater() {
        return nmsMob.canBreatheUnderwater();
    }

    @Override
    public boolean shouldDiscardFriction() {
        return nmsMob.shouldDiscardFriction();
    }

    @Override
    public void setDiscardFriction(boolean discard) {
        nmsMob.setDiscardFriction(discard);
    }

    /**
     * Makes this Mob interact with a Player.
     *
     * @param p    Player to interact with
     * @param hand Hand to use
     * @return Result of interaction
     */
    @Override
    public InteractionResult interact(@NotNull Player p, @Nullable InteractionHand hand) {
        final net.minecraft.world.InteractionHand h;

        if (hand == InteractionHand.OFF_HAND) h = net.minecraft.world.InteractionHand.OFF_HAND;
        else h = net.minecraft.world.InteractionHand.MAIN_HAND;
        net.minecraft.world.InteractionResult result = nmsMob.interact(ChipUtil1_21_R6.toNMS(p), h);
        if (result == net.minecraft.world.InteractionResult.FAIL) {
            return InteractionResult.FAIL;
        } else if (result == net.minecraft.world.InteractionResult.CONSUME) {
            return InteractionResult.CONSUME;
        } else if (result == net.minecraft.world.InteractionResult.PASS) {
            return InteractionResult.PASS;
        } else {
            return InteractionResult.SUCCESS;
        }
    }

    @Override
    public boolean isSensitiveToWater() {
        return nmsMob.isSensitiveToWater();
    }

    @Override
    public boolean isAffectedByPotions() {
        return nmsMob.isAffectedByPotions();
    }

    @Override
    public boolean isBlocking() {
        return nmsMob.isBlocking();
    }

    @Override
    public float getArmorCoverPercentage() {
        return nmsMob.getArmorCoverPercentage();
    }

    @Override
    public void useItem(@Nullable InteractionHand hand) {
        if (hand == null) return;

        final net.minecraft.world.InteractionHand h;
        if (hand == InteractionHand.OFF_HAND) h = net.minecraft.world.InteractionHand.OFF_HAND;
        else h = net.minecraft.world.InteractionHand.MAIN_HAND;

        nmsMob.startUsingItem(h);
    }

    @Override
    public boolean isUsingItem() {
        return nmsMob.isUsingItem();
    }

    @Override
    public boolean isFireImmune() {
        return nmsMob.fireImmune();
    }

    @Override
    public boolean isSwinging() {
        return nmsMob.swinging;
    }

    @Override
    public boolean canRideUnderwater() {
        return false; // doesn't exist in 1.19.4+
    }

    @Override
    public boolean isInvisibleTo(@Nullable Player p) {
        return nmsMob.isInvisibleTo(ChipUtil1_21_R6.toNMS(p));
    }

    @Override
    public @NotNull InteractionHand getMainHand() {
        if (nmsMob.getMainArm() == HumanoidArm.LEFT) return InteractionHand.OFF_HAND;
        return InteractionHand.MAIN_HAND;
    }

    @Override
    public List<ItemStack> getDefaultDrops() {
        return nmsMob.drops;
    }

    @Override
    public void setDefaultDrops(@Nullable ItemStack... drops) {
        nmsMob.drops = new ArrayList<>(Arrays.asList(drops));
    }

    @Override
    public boolean isInCombat() {
        try {
            Field inCombatF = CombatTracker.class.getDeclaredField("i");
            inCombatF.setAccessible(true);
            return inCombatF.getBoolean(nmsMob.combatTracker);
        } catch (ReflectiveOperationException e) {
            ChipUtil.printStackTrace(e);
            return false;
        }
    }

    @Override
    public float getFlyingSpeed() {
        return 0; // doesn't exist 1.19.4+
    }

    @Override
    public void setFlyingSpeed(float speed) throws IllegalArgumentException {
        // doesn't exist in 1.19.4+
    }

    @Override
    public boolean isForcingDrops() {
        return nmsMob.forceDrops;
    }

    @Override
    public void setForcingDrops(boolean drop) {
        nmsMob.forceDrops = drop;
    }

    @Override
    public boolean isMoving() {
        double x = nmsMob.getX() - nmsMob.xo;
        double z = nmsMob.getZ() - nmsMob.zo;
        return x * x + z * z > 2.500000277905201E-7D;
    }

    @Override
    public float getBodyRotation() {
        return nmsMob.yBodyRot;
    }

    @Override
    public void setBodyRotation(float rotation) {
        nmsMob.yBodyRot = EntityBody.normalizeRotation(rotation);
    }

    @Override
    public float getHeadRotation() {
        return nmsMob.yHeadRot;
    }

    @Override
    public void setHeadRotation(float rotation) {
        nmsMob.yHeadRot = EntityBody.normalizeRotation(rotation);
    }

    @Override
    public Set<? extends Entity> getCollideExemptions() {
        return nmsMob.collidableExemptions.stream().map(Bukkit::getEntity).filter(Objects::nonNull).collect(Collectors.toSet());
    }

    @Override
    public void addCollideExemption(@NotNull Entity en) throws IllegalArgumentException {
        if (en == null) throw new IllegalArgumentException("Entity cannot be null");
        nmsMob.collidableExemptions.add(en.getUniqueId());
    }

    @Override
    public void removeCollideExemption(@NotNull Entity en) throws IllegalArgumentException {
        if (en == null) throw new IllegalArgumentException("Entity cannot be null");
        nmsMob.collidableExemptions.remove(en.getUniqueId());
    }

    @Override
    public int getDroppedExperience() {
        return nmsMob.expToDrop;
    }

    @Override
    public void setDroppedExperience(int exp) throws IllegalArgumentException {
        if (exp < 0) throw new IllegalArgumentException("Experience cannot be negative");
        nmsMob.expToDrop = exp;
    }

    @Override
    public void playAnimation(@NotNull EntityAnimation anim) {
        switch (anim) {
            case SPAWN -> nmsMob.spawnAnim();
            case DAMAGE -> nmsMob.animateHurt(1.0F);
            case CRITICAL_DAMAGE -> {
                ClientboundAnimatePacket pkt = new ClientboundAnimatePacket(nmsMob, 4);
                for (Player p : ChipUtil1_21_R6.fromNMS(nmsMob).getWorld().getPlayers())
                    ChipUtil1_21_R6.toNMS(p).connection.send(pkt);
            }
            case MAGICAL_CRITICAL_DAMAGE -> {
                ClientboundAnimatePacket pkt = new ClientboundAnimatePacket(nmsMob, 5);
                for (Player p : ChipUtil1_21_R6.fromNMS(nmsMob).getWorld().getPlayers())
                    ChipUtil1_21_R6.toNMS(p).connection.send(pkt);
            }
        }
    }

    @Override
    public float getAnimationSpeed() {
        return 0; // doesn't exist in 1.19.4+
    }

    @Override
    public void setAnimationSpeed(float speed) throws IllegalArgumentException {
        // doesn't exist in 1.19.4+
    }

    @Override
    public boolean hasVerticalCollision() {
        return nmsMob.verticalCollision;
    }

    @Override
    public void setVerticalCollision(boolean collision) {
        nmsMob.verticalCollision = collision;
    }

    @Override
    public boolean hasHorizontalCollision() {
        return nmsMob.horizontalCollision;
    }

    @Override
    public void setHorizontalCollision(boolean collision) {
        nmsMob.horizontalCollision = collision;
    }

    @Override
    public float getWalkDistance() {
        return nmsMob.moveDist;
    }

    @Override
    public float getMoveDistance() {
        return nmsMob.moveDist;
    }

    @Override
    public float getFlyDistance() {
        return nmsMob.flyDist;
    }

    @Override
    public boolean isImmuneToExplosions() {
        return nmsMob.ignoreExplosion(null);
    }

    @Override
    public boolean isPeacefulCompatible() {
        try {
            Method m = net.minecraft.world.entity.Mob.class.getDeclaredMethod("Q");
            m.setAccessible(true);
            return (boolean) m.invoke(nmsMob);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean isInBubbleColumn() {
        return nmsMob.level().getBlockState(nmsMob.blockPosition()).is(Blocks.BUBBLE_COLUMN);
    }

    @Override
    public boolean isInvulnerableTo(EntityDamageEvent.@Nullable DamageCause cause) {
        return nmsMob.isInvulnerableTo(nmsMob.level().getMinecraftWorld(), ChipUtil1_21_R6.toNMS(cause, m));
    }

    @Override
    public int getMaxFallDistance() {
        return nmsMob.getMaxFallDistance();
    }

    @Override
    public boolean isPushableBy(@Nullable Entity entity) {
        return EntitySelector.pushableBy(ChipUtil1_21_R6.toNMS(entity)).test(ChipUtil1_21_R6.toNMS(entity));
    }

    @Override
    public float getYaw() {
        return nmsMob.getYRot();
    }

    @Override
    public void setYaw(float rotation) {
        nmsMob.setYRot(EntityBody.normalizeRotation(rotation));
    }

    @Override
    public float getPitch() {
        return nmsMob.getXRot();
    }

    @Override
    public void setPitch(float rotation) {
        nmsMob.setXRot(EntityBody.normalizeRotation(rotation));
    }

    @Override
    public float getMaxUpStep() {
        return nmsMob.maxUpStep();
    }

    @Override
    public void setMaxUpStep(float maxUpStep) {
        nmsMob.getAttributes().getInstance(Attributes.STEP_HEIGHT).setBaseValue(maxUpStep);
    }

    @Override
    public Position getLastLavaContact() {
        BlockPos p = nmsMob.lastLavaContact;
        if (p == null) return null;
        return new Position(p.getX(), p.getY(), p.getZ());
    }

    @Override
    public void setRiptideTicks(int ticks) {
        if (ticks < 0) throw new IllegalArgumentException("Riptide ticks cannot be negative");
        try {
            Field f = LivingEntity.class.getDeclaredField("bC");
            f.setAccessible(true);
            f.setInt(nmsMob, ticks);

            if (!nmsMob.level().isClientSide()) {
                Method setFlags = LivingEntity.class.getDeclaredMethod("c", int.class, boolean.class);
                setFlags.setAccessible(true);
                setFlags.invoke(nmsMob, 4, true);
            }
        } catch (ReflectiveOperationException e) {
            Bukkit.getLogger().severe(e.getMessage());
            for (StackTraceElement ste : e.getStackTrace()) Bukkit.getLogger().severe(ste.toString());
        }

        update();
    }

    @Override
    public int getRiptideTicks() {
        try {
            Field f = LivingEntity.class.getDeclaredField("bC");
            f.setAccessible(true);
            return f.getInt(nmsMob);
        } catch (ReflectiveOperationException e) {
            return 0;
        }
    }

    @Override
    public @NotNull Mob getEntity() {
        return m;
    }

    @Override
    public boolean shouldRenderFrom(double x, double y, double z) {
        return nmsMob.shouldRender(x, y, z);
    }

    @Override
    public boolean shouldRenderFromSqr(double dist) {
        return nmsMob.shouldRenderAtSqrDistance(dist);
    }

    @Override
    public void sendTo(@NotNull Player p) {
        ChunkMap.TrackedEntity tracked = ((ServerLevel)nmsMob.level()).getChunkSource().chunkMap.entityMap.get(nmsMob.getId());
        Packet<?> packet = nmsMob.getAddEntityPacket(tracked.serverEntity);
        ((CraftPlayer) p).getHandle().connection.send(packet);
    }

    @Override
    public void resetFallDistance() {
        nmsMob.resetFallDistance();
    }

    @Override
    public boolean isInUnloadedChunk() {
        return nmsMob.touchingUnloadedChunk();
    }

    @Override
    public void naturalKnockback(double force, double xForce, double zForce) {
        nmsMob.knockback(force, xForce, zForce);
    }

    @Override
    public void eat(@NotNull ItemStack item) {
        nmsMob.level().playSound(null, nmsMob.getX(), nmsMob.getY(), nmsMob.getZ(), SoundEvents.GENERIC_EAT, nmsMob.getSoundSource(), 1.0F, 1.0F);
        nmsMob.gameEvent(GameEvent.EAT);
    }

    @Override
    public void setRotation(float yaw, float pitch) {
        try {
            if (m instanceof Slime) {
                MoveControl moveControl = nmsMob.getMoveControl();

                Method setRotation = moveControl.getClass().getDeclaredMethod("a", float.class, boolean.class);
                setRotation.setAccessible(true);
                setRotation.invoke(moveControl, yaw, true);
            } else m.setRotation(yaw, pitch);
        } catch (ReflectiveOperationException e) {
            Bukkit.getLogger().severe(e.getMessage());
            for (StackTraceElement ste : e.getStackTrace()) Bukkit.getLogger().severe(ste.toString());
        }
    }

    @Override
    public int getHurtTime() {
        return nmsMob.hurtTime;
    }

    @Override
    public void setHurtTime(int hurtTime) {
        nmsMob.hurtTime = hurtTime;
    }

    @Override
    public int getHurtDuration() {
        return nmsMob.hurtDuration;
    }

    @Override
    public void setHurtDuration(int hurtDuration) {
        nmsMob.hurtDuration = hurtDuration;
    }

    @Override
    public int getDeathTime() {
        return nmsMob.deathTime;
    }

    @Override
    public void setDeathTime(int deathTime) {
        nmsMob.deathTime = deathTime;
    }

    @Override
    public float getForwardSpeed() {
        return nmsMob.zza;
    }

    @Override
    public void setForwardSpeed(float speed) {
        nmsMob.setZza(speed);
    }

    @Override
    public float getSidewaysSpeed() {
        return nmsMob.xxa;
    }

    @Override
    public void setSidewaysSpeed(float speed) {
        nmsMob.setXxa(speed);
    }

    @Override
    public float getUpwardSpeed() {
        return nmsMob.yya;
    }

    @Override
    public void setUpwardSpeed(float speed) {
        nmsMob.setYya(speed);
    }
}
