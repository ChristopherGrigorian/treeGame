import processing.core.PImage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Fairy extends EntityWithAction implements CanMove {
    public Fairy(String id, Point position, List<PImage> images, double actionPeriod, double animationPeriod) {
        super(id, position, images, actionPeriod, animationPeriod);
    }

    @Override
    public boolean moveTo(WorldModel world, Entity target, EventScheduler scheduler) {
        if (Point.adjacent(this.getPosition(), target.getPosition())) {
            world.removeEntity(scheduler, target);
            return true;
        } else {
            Predicate<Point> canPassThrough = p -> world.withinBounds(p) && (!world.isOccupied(p));
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


    @Override
    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> fairyTarget = world.findNearest(this.getPosition(), new ArrayList<>(List.of(Stump.class)));

        if (fairyTarget.isPresent()) {
            Point tgtPos = fairyTarget.get().getPosition();

            if (this.moveTo(world, fairyTarget.get(), scheduler)) {

                Sapling sapling = new Sapling(SAPLING_KEY + "_" + fairyTarget.get().getId(), tgtPos, imageStore.getImageList(SAPLING_KEY));

                world.addEntity(sapling);
                sapling.scheduleAction(scheduler, world, imageStore);
            }
        }

        scheduler.scheduleEvent(this, new Activity(this, world, imageStore), this.getActionPeriod());
    }

    @Override
    public void scheduleAction(EventScheduler scheduler, WorldModel world, ImageStore imageStore) {
        scheduler.scheduleEvent(this, new Activity(this, world, imageStore), this.getActionPeriod());
        scheduler.scheduleEvent(this, new Animation(this, 0), this.getAnimationPeriod());
    }
}
