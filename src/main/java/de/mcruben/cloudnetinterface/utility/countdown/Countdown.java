package de.mcruben.cloudnetinterface.utility.countdown;
/*
 * Created by Mc_Ruben on 29.10.2018
 */

import org.bukkit.plugin.Plugin;

public interface Countdown {

    Plugin getOwner();

    void stop();

    void pause();

    void run();

    long getTimesRemaining();

    void setTimesRemaining(long timesRemaining);

}
