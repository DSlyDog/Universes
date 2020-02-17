package net.whispwriting.universes.en.events;

public class PortalFired implements Runnable {
    @Override
    public void run() {
        System.out.println("Teleported Via Portal");
    }
}
