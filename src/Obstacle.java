import processing.core.PImage;

import java.util.List;

public class Obstacle extends Entity implements HasAnimation {
    private double animationPeriod;

    public Obstacle(String id, Point position, List<PImage> images, double animationPeriod) {
        super(id, position, images);
        this.animationPeriod = animationPeriod;
    }

    @Override
    public double getAnimationPeriod() {
        return animationPeriod;
    }
    @Override
    public void scheduleAction(EventScheduler scheduler, WorldModel worldModel, ImageStore imageStore) {
        scheduler.scheduleEvent(this, new Animation(this, 0), this.getAnimationPeriod());
    }
}
