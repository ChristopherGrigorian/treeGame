public interface HasActivity {
    void executeActivity(WorldModel worldModel, ImageStore imageStore, EventScheduler eventScheduler);

    void scheduleAction(EventScheduler eventScheduler, WorldModel worldModel, ImageStore imageStore);
}
