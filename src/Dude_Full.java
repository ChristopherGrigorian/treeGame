import processing.core.PImage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Dude_Full extends Dude {

    public Dude_Full(String id, Point position, List<PImage> images, double actionPeriod, double animationPeriod, int resourceCount, int resourceLimit) {
        super(id, position, images, actionPeriod, animationPeriod, resourceCount, resourceLimit);
    }

    @Override
    public boolean transform(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        Dude_Not_Full dude_not_full = new Dude_Not_Full(this.getId(), this.getPosition(), this.getImages(), this.getActionPeriod(), this.getAnimationPeriod(), this.getResourceCount(), this.getResourceLimit());
        if (this.getId().equals("dudefullgold")) {
            dude_not_full = new Dude_Not_Full("dudenotfullgold", this.getPosition(), imageStore.getImageList("golddude"), 0.285, 0.285, 0, 7);
        }

        world.removeEntity(scheduler, this);

        world.addEntity(dude_not_full);
        dude_not_full.scheduleAction(scheduler, world, imageStore);

        // return true was added to make all methods match
        return true;
    }

    @Override
    public void executeActivity(WorldModel worldModel, ImageStore imageStore, EventScheduler eventScheduler) {
        Optional<Entity> fullTarget = worldModel.findNearest(this.getPosition(), new ArrayList<>(List.of(House.class)));

        if (fullTarget.isPresent() && this.moveTo(worldModel, fullTarget.get(), eventScheduler)) {
            this.transform(worldModel, eventScheduler, imageStore);
        } else {
            eventScheduler.scheduleEvent(this, new Activity(this, worldModel, imageStore), this.getActionPeriod());
        }
    }

    @Override
    public void scheduleAction(EventScheduler scheduler, WorldModel world, ImageStore imageStore) {
        scheduler.scheduleEvent(this, new Activity(this, world, imageStore), this.getActionPeriod());
        scheduler.scheduleEvent(this, new Animation(this, 0), this.getAnimationPeriod());
    }

    @Override
    public boolean moveTo(WorldModel world, Entity target, EventScheduler scheduler) {
        if (Point.adjacent(this.getPosition(), target.getPosition())) {
            return true;
        } else {
            Predicate<Point> canPassThrough = p -> world.withinBounds(p) && (!world.isOccupied(p) || world.isOccupied(p) && world.getOccupancyCell(p) instanceof Stump);
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
            return false;
        }
    }
}
