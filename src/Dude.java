import processing.core.PImage;

import java.util.List;

public abstract class Dude extends EntityWithAction implements Transform, CanMove {

    private int resourceLimit;

    private int resourceCount;

    public Dude(String id, Point position, List<PImage> images, double actionPeriod, double animationPeriod, int resourceCount, int resourceLimit) {
        super(id, position, images, actionPeriod, animationPeriod);
        this.resourceCount = resourceCount;
        this.resourceLimit = resourceLimit;
    }

    public int getResourceLimit() {
        return resourceLimit;
    }

    public int getResourceCount() {
        return resourceCount;
    }

    public void increaseResourceCount() {
        this.resourceCount++;
    }
}
