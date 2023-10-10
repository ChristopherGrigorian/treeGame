import processing.core.PImage;

import java.util.List;
import java.util.Random;

public class Sapling extends EntityWithAction implements Transform, HealthReduction{

    private int health;
    private int healthLimit;

    public static final double TREE_ANIMATION_MAX = 0.600;
    public static final double TREE_ANIMATION_MIN = 0.050;
    public static final double TREE_ACTION_MAX = 1.400;
    public static final double TREE_ACTION_MIN = 1.000;
    public static final int TREE_HEALTH_MAX = 3;
    public static final int TREE_HEALTH_MIN = 1;

    public static final double SAPLING_ACTION_ANIMATION_PERIOD = 1.000; // have to be in sync since grows and gains health at same time
    public static final int SAPLING_HEALTH_LIMIT = 5;

    public Sapling(String id, Point position, List<PImage> images) {
        super(id, position, images, SAPLING_ACTION_ANIMATION_PERIOD, SAPLING_ACTION_ANIMATION_PERIOD);
        this.health = SAPLING_HEALTH;
        this.healthLimit = SAPLING_HEALTH_LIMIT;

    }


    public boolean transform(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        if (health <= 0) {
            Stump stump = new Stump(STUMP_KEY + "_" + this.getId(), this.getPosition(), imageStore.getImageList(STUMP_KEY));

            world.removeEntity(scheduler, this);

            world.addEntity(stump);

            return true;
        } else if (health >= healthLimit) {
            Tree tree = new Tree(TREE_KEY + "_" + this.getId(), this.getPosition(), imageStore.getImageList(TREE_KEY), getNumFromRange(TREE_ACTION_MAX, TREE_ACTION_MIN), getNumFromRange(TREE_ANIMATION_MAX, TREE_ANIMATION_MIN), getIntFromRange(TREE_HEALTH_MAX, TREE_HEALTH_MIN));

            world.removeEntity(scheduler, this);

            world.addEntity(tree);
            tree.scheduleAction(scheduler, world, imageStore);

            return true;
        }

        return false;
    }

    @Override
    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        this.health++;
        if (!this.transform(world, scheduler, imageStore)) {
            scheduler.scheduleEvent(this, new Activity(this, world, imageStore), this.getActionPeriod());
        }
    }

    @Override
    public void scheduleAction(EventScheduler scheduler, WorldModel world, ImageStore imageStore) {
        scheduler.scheduleEvent(this, new Activity(this, world, imageStore), this.getActionPeriod());
        scheduler.scheduleEvent(this, new Animation(this, 0), this.getAnimationPeriod());
    }

    private static int getIntFromRange(int max, int min) {
        Random rand = new Random();
        return min + rand.nextInt(max-min);
    }

    private static double getNumFromRange(double max, double min) {
        Random rand = new Random();
        return min + rand.nextDouble() * (max - min);
    }

    @Override
    public void reduceHealth() {
        this.health--;
    }

    @Override
    public int getHealth() {
        return health;
    }
}