package de.mcruben.cloudnetinterface.utility.countdown;
/*
 * Created by Mc_Ruben on 29.10.2018
 */

import lombok.Getter;
import lombok.Setter;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class SimpleCountdown implements Countdown {

    @Getter
    private Plugin owner;
    private BukkitRunnable runnable;
    private long delay;
    private long repeat;
    @Getter
    @Setter
    private long timesRemaining;
    private Runnable finished;
    private Runnable calling;

    public SimpleCountdown(Plugin owner, long delay, long repeat, long times, Runnable runnable, Runnable finished) {
        this.owner = owner;
        this.delay = delay;
        this.repeat = repeat;
        this.timesRemaining = times;
        this.finished = finished;
        this.calling = runnable;

        this.init();
        this.runnable.runTaskTimer(this.owner, delay, repeat);
    }

    public SimpleCountdown(Plugin owner, long delay, long repeat, long times, Runnable runnable) {
        this(owner, delay, repeat, times, runnable, null);
    }

    private void init() {
        this.runnable = new BukkitRunnable() {
            @Override
            public void run() {
                calling.run();
                if (timesRemaining-- <= 0) {
                    cancel();
                    if (finished != null)
                        finished.run();
                }
            }
        };
    }

    @Override
    public void stop() {
        this.runnable.cancel();
        this.timesRemaining = 0;
        if (this.finished != null)
            this.finished.run();
    }

    @Override
    public void pause() {
        this.runnable.cancel();
    }

    @Override
    public void run() {
        try {
            this.runnable.getTaskId();
        } catch (IllegalStateException e) {
            this.init();
        }
        this.runnable.runTaskTimer(this.owner, this.delay, this.repeat);
    }


}
