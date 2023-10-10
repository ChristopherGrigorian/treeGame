import processing.core.PImage;

import java.util.List;

public abstract class EntityWithAction extends Entity implements HasActivity, HasAnimation {

    private double actionPeriod;

    private double animationPeriod;

    public EntityWithAction(String id, Point position, List<PImage> images, double actionPeriod, double animationPeriod) {
        super(id, position, images);
        this.actionPeriod = actionPeriod;
        this.animationPeriod = animationPeriod;
    }

    public double getActionPeriod() {
        return actionPeriod;
    }

    @Override
    public double getAnimationPeriod() {
        return animationPeriod;
    }
}
