import processing.core.PImage;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

public class PotOfGold extends EntityWithAction implements Transform {

    public PotOfGold(String id, Point position, List<PImage> images,  double actionPeriod, double animationPeriod) {
        super(id, position, images, actionPeriod, animationPeriod);
    }

    @Override
    public void scheduleAction(EventScheduler scheduler, WorldModel world, ImageStore imageStore) {
        scheduler.scheduleEvent(this, new Activity(this, world, imageStore), this.getActionPeriod());
        scheduler.scheduleEvent(this, new Animation(this, 0), this.getAnimationPeriod());
    }

    @Override
    public boolean transform(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        taste_the_rainbow(scheduler, world, imageStore);
        world.removeEntity(scheduler, this);
        scheduler.unscheduleAllEvents(this);
        return true;
    }

    public void taste_the_rainbow(EventScheduler scheduler, WorldModel world, ImageStore imageStore){
        Function<Point, Stream<Point>> clickNeighbors = point ->
                Stream.<Point>builder()
                        .add(new Point(point.x, point.y))
                        .add(new Point(point.x, point.y - 1))
                        .add(new Point(point.x, point.y - 2))
                        .add(new Point(point.x, point.y + 1))
                        .add(new Point(point.x, point.y + 2))
                        .add(new Point(point.x - 1, point.y))
                        .add(new Point(point.x - 2, point.y))
                        .add(new Point(point.x + 1, point.y))
                        .add(new Point(point.x + 2, point.y))
                        .add(new Point(point.x + 2, point.y))
                        .add(new Point(point.x + 1, point.y + 1))
                        .add(new Point(point.x - 1, point.y - 1))
                        .add(new Point(point.x + 1, point.y - 1))
                        .add(new Point(point.x - 1, point.y + 1))

                        .build();

        Point potPosition = this.getPosition();

        clickNeighbors.apply(potPosition).forEach(p -> {
            if (world.withinBounds(p)) {
                Set<Entity> entities = world.getEntitiesAt(p);
                entities.stream()
                        .filter(entity -> entity instanceof Dude_Full || entity instanceof Dude_Not_Full)
                        .forEach(entity -> {
                            Dude_Not_Full dude_not_full_gold = new Dude_Not_Full("dudenotfullgold", entity.getPosition(), imageStore.getImageList("golddude"), 0.285, 0.285, 0, 7);
                            world.removeEntity(scheduler, entity);
                            scheduler.unscheduleAllEvents(entity);
                            world.addEntity(dude_not_full_gold);
                            dude_not_full_gold.scheduleAction(scheduler, world, imageStore);
                        });
                world.setBackgroundCell(p, new Background("bkCh", imageStore.getImageList("rainbowtile")));
            }
        });
    }

    @Override
    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        if (!this.transform(world, scheduler, imageStore)) {
            scheduler.scheduleEvent(this, new Activity(this, world, imageStore), this.getActionPeriod());
        }
    }
}