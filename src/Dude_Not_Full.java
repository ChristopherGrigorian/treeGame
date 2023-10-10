import processing.core.PImage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Dude_Not_Full extends Dude {

    public static List<Entity> Last_Interacted = new ArrayList<>();

    public Dude_Not_Full(String id, Point position, List<PImage> images, double actionPeriod, double animationPeriod, int resourceCount, int resourceLimit) {
        super(id, position, images, actionPeriod, animationPeriod, resourceCount, resourceLimit);
    }

    @Override
    public boolean transform(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        if (this.getResourceCount() >= this.getResourceLimit()) {

            Dude_Full dude_full = new Dude_Full(this.getId(), this.getPosition(), this.getImages(), this.getActionPeriod(), this.getAnimationPeriod(), this.getResourceCount(), this.getResourceLimit());

            if (this.getId().equals("dudenotfullgold")) {
                dude_full = new Dude_Full("dudefullgold", this.getPosition(), imageStore.getImageList("golddude"), 0.285, 0.285, 0, 7);
            }
            world.removeEntity(scheduler, this);
            scheduler.unscheduleAllEvents(this);

            world.addEntity(dude_full);
            dude_full.scheduleAction(scheduler, world, imageStore);

            return true;
        }
        return false;
    }

    @Override
    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> target = world.findNearest(this.getPosition(), new ArrayList<>(Arrays.asList(Tree.class, Sapling.class)));

        if (target.isEmpty() || !this.moveTo(world, target.get(), scheduler) || !this.transform(world, scheduler, imageStore)) {
            scheduler.scheduleEvent(this, new Activity(this, world, imageStore), this.getActionPeriod());
        }
    }

    @Override
    public void scheduleAction(EventScheduler scheduler, WorldModel world, ImageStore imageStore) {
        scheduler.scheduleEvent(this, new Activity(this, world, imageStore), this.getActionPeriod());
        scheduler.scheduleEvent(this, new Animation(this, 0), this.getAnimationPeriod());
    }

    @Override
    public boolean moveTo(WorldModel world, Entity target, EventScheduler scheduler) {
        if (Point.adjacent(getPosition(), target.getPosition())) {
            increaseResourceCount();
            ((HealthReduction) target).reduceHealth();
            if (((HealthReduction) target).getHealth() == 0 && target instanceof Tree) {
                if (this.getId().equals("dudenotfullgold")) {
                    Dude_Not_Full.Last_Interacted.add(target);
                }
            }
            return true;
        } else {
            Predicate<Point> canPassThrough = p -> world.withinBounds(p) && (!world.isOccupied(p) || world.isOccupied(p) && world.getOccupancyCell(p) instanceof Stump);
            BiPredicate<Point, Point> withinReach = (p1, p2) -> (Math.abs(p2.x - p1.x) + Math.abs(p2.y - p1.y)) == 1;
            Function<Point, Stream<Point>> potentialNeighbors = PathingStrategy.CARDINAL_NEIGHBORS;
            AStarPathingStrategy strategy = new AStarPathingStrategy();
            List<Point> path = strategy.computePath(
                    this.getPosition(),               // start
                    target.getPosition(),             // end
                    canPassThrough,                   // canPassThrough
                    withinReach,                      // withinReach
                    potentialNeighbors                // potentialNeighbors
            );

//            Point nextPos = this.nextPosition(world, target.getPosition());


            if (path != null && !path.isEmpty()) {
                Point nextPos = path.get(0);
                // Check if the next position is already occupied by another entity
                if (!nextPos.equals(this.getPosition())) {
                    world.moveEntity(scheduler, this, nextPos);
                }
            }
            return false;
        }
    }
}
