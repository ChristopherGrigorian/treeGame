public interface HasAnimation {
    double getAnimationPeriod();
    void scheduleAction(EventScheduler eventScheduler, WorldModel worldModel, ImageStore imageStore);
}
