package me.gamercoder215.mobchip.ai.behavior;

import net.minecraft.world.entity.ai.behavior.Behavior;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a Behavior Result
 */
public abstract class BehaviorResult {

    /**
     * Constructs an Abstract Behavior Result.
     */
    protected BehaviorResult() {}

    /**
     * Fetches the Status of this BehaviorResult.
     * @return Behavior Status
     */
    @NotNull
    public abstract Status getStatus();

    /**
     * Forces this Behavior to stop.
     */
    public abstract void stop();

    /**
     * Represents a Behavior Status
     */
    public enum Status {
        /**
         * Represents that this Behavior has stopped running.
         */
        STOPPED,
        /**
         * Represents that this Behavior is running.
         */
        RUNNING;

        Status() {}

        /**
         * Converts a MobChip Behavior Status to a NMS Behavior Status.
         * @return NMS Behavior Status
         */
        @NotNull
        public Behavior.Status getHandle() {
            if (this == STOPPED) return Behavior.Status.STOPPED;
            return Behavior.Status.RUNNING;
        }
    }

}