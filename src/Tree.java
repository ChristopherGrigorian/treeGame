import processing.core.PImage;

import java.util.List;
import java.util.Objects;

public class Tree extends EntityWithAction implements Transform, HealthReduction {

    private int health;

    public Tree(String id, Point position, List<PImage> images, double actionPeriod, double animationPeriod, int health) {
        super(id, position, images, actionPeriod, animationPeriod);
        this.health = health;
    }

    public boolean transform(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        if (health <= 0) {
            for (Entity entity : Dude_Not_Full.Last_Interacted) {
                if (this.equals(entity)) {
                    PotOfGold potOfGold = new PotOfGold("potofgold", this.getPosition(), imageStore.getImageList("potofgold"), 5, 0.5);
                    world.removeEntity(scheduler, this);
                    world.addEntity(potOfGold);
                    potOfGold.scheduleAction(scheduler, world, imageStore);
                    Dude_Not_Full.Last_Interacted.remove(entity);
                    return true;
                }
            }
            Stump stump = new Stump(STUMP_KEY + "_" + this.getId(), this.getPosition(), imageStore.getImageList(STUMP_KEY));
            world.removeEntity(scheduler, this);
            world.addEntity(stump);
            return true;
        }
        return false;
    }

    @Override
    public void reduceHealth() {
        this.health--;
    }

    @Override
    public int getHealth() {
        return health;
    }

    @Override
    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        if (!this.transform(world, scheduler, imageStore)) {
            scheduler.scheduleEvent(this, new Activity(this, world, imageStore), this.getActionPeriod());
        }
    }

    @Override
    public void scheduleAction(EventScheduler scheduler, WorldModel world, ImageStore imageStore) {
        scheduler.scheduleEvent(this, new Activity(this, world, imageStore), this.getActionPeriod());
        scheduler.scheduleEvent(this, new Animation(this, 0), this.getAnimationPeriod());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Tree other = (Tree) o;
        return Objects.equals(getPosition(), other.getPosition());
    }

}
