import processing.core.PImage;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class Leprechaun extends EntityWithAction implements CanMove, Transform {

    public int resourceLimit;
    public int resourceCount;

    public Leprechaun(String id, Point position, List<PImage> images, double actionPeriod, double animationPeriod, int resourceCount, int resourceLimit) {
        super(id, position, images, actionPeriod, animationPeriod);
        this.resourceCount = resourceCount;
        this.resourceLimit = resourceLimit;
    }
    ///
    @Override
    public boolean moveTo(WorldModel world, Entity target, EventScheduler scheduler) {
        if (Point.adjacent(this.getPosition(), target.getPosition())) {
            this.resourceCount++;
            ((HealthReduction) target).reduceHealth();
            return true;
        } else {
            Predicate<Point> canPassThrough = p -> world.withinBounds(p) && (!world.isOccupied(p));
            BiPredicate<Point, Point> withinReach = (p1, p2) -> (Math.abs(p2.x - p1.x) + Math.abs(p2.y - p1.y)) == 1;
            Function<Point, Stream<Point>> potentialNeighbors = PathingStrategy.CARDINAL_NEIGHBORS;
            AStarPathingStrategy strategy = new AStarPathingStrategy();
            List<Point> path = strategy.computePath(
                    this.getPosition(),
                    target.getPosition(),
                    canPassThrough,
                    withinReach,
                    potentialNeighbors
            );

//            Point nextPos = this.nextPosition(world, target.getPosition());
            if (path != null && !path.isEmpty()) {
                Point nextPos = path.get(0);

                if (!nextPos.equals(this.getPosition())) {
                    world.moveEntity(scheduler, this, nextPos);
                }
            }
        }
        return false;
    }
    @Override
    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> target = world.findNearest(this.getPosition(), new ArrayList<>(List.of(Tree.class)));

        if (target.isEmpty() || this.moveTo(world, target.get(), scheduler) || !this.transform(world, scheduler, imageStore)) {
            scheduler.scheduleEvent(this, new Activity(this, world, imageStore), this.getActionPeriod());
        }
    }

    @Override
    public void scheduleAction(EventScheduler scheduler, WorldModel world, ImageStore imageStore) {
        scheduler.scheduleEvent(this, new Activity(this, world, imageStore), this.getActionPeriod());
        scheduler.scheduleEvent(this, new Animation(this, 0), this.getAnimationPeriod());
    }

    @Override
    public boolean transform(WorldModel worldModel, EventScheduler eventScheduler, ImageStore imageStore) {
        if (resourceCount >= resourceLimit) {
            Leprechaun newLeprechaun = new Leprechaun("lep", this.getPosition(), this.getImages(), this.getActionPeriod(), this.getAnimationPeriod(), 0, 6);

            Function<Point, Stream<Point>> potentialNeighbors = PathingStrategy.CARDINAL_NEIGHBORS;
            Predicate<Point> canPassThrough = p -> worldModel.withinBounds(p) && (!worldModel.isOccupied(p));

            List<Point> spaceOpen = potentialNeighbors.apply(this.getPosition())
                    .filter(canPassThrough)
                    .toList();

            worldModel.removeEntity(eventScheduler, this);
            eventScheduler.unscheduleAllEvents(this);


            worldModel.addEntity(newLeprechaun);
            newLeprechaun.scheduleAction(eventScheduler, worldModel, imageStore);

            if (!spaceOpen.isEmpty() && worldModel.withinBounds(spaceOpen.get(0))) {
                PotOfGold potOfGold = new PotOfGold("potofgold", spaceOpen.get(0), imageStore.getImageList("potofgold"), 5, 0.5);
                worldModel.addEntity(potOfGold);
                potOfGold.scheduleAction(eventScheduler, worldModel, imageStore);
            }


            return true;
        }

        return false;
    }
}

