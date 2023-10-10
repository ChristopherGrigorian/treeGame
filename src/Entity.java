import java.util.*;
import processing.core.PImage;
/**
 * An entity that exists in the world. See EntityKind for the
 * different kinds of entities that exist.
 */
public abstract class Entity {

    private String id;

    private Point position;

    private List<PImage> images;
    private int imageIndex;

    public static final int PROPERTY_KEY = 0;
    public static final int PROPERTY_ID = 1;
    public static final int PROPERTY_COL = 2;
    public static final int PROPERTY_ROW = 3;
    public static final int ENTITY_NUM_PROPERTIES = 4;

    public static final String STUMP_KEY = "stump";
    public static final int STUMP_NUM_PROPERTIES = 0;

    public static final String SAPLING_KEY = "sapling";
    public static final int SAPLING_HEALTH = 0;
    public static final int SAPLING_NUM_PROPERTIES = 1;

    public static final String OBSTACLE_KEY = "obstacle";
    public static final int OBSTACLE_ANIMATION_PERIOD = 0;
    public static final int OBSTACLE_NUM_PROPERTIES = 1;

    public static final String DUDE_KEY = "dude";
    public static final int DUDE_ACTION_PERIOD = 0;
    public static final int DUDE_ANIMATION_PERIOD = 1;
    public static final int DUDE_LIMIT = 2;
    public static final int DUDE_NUM_PROPERTIES = 3;

    public static final String HOUSE_KEY = "house";
    public static final int HOUSE_NUM_PROPERTIES = 0;

    public static final String FAIRY_KEY = "fairy";
    public static final int FAIRY_ANIMATION_PERIOD = 0;
    public static final int FAIRY_ACTION_PERIOD = 1;
    public static final int FAIRY_NUM_PROPERTIES = 2;

    public static final String TREE_KEY = "tree";
    public static final int TREE_ANIMATION_PERIOD = 0;
    public static final int TREE_ACTION_PERIOD = 1;
    public static final int TREE_HEALTH = 2;
    public static final int TREE_NUM_PROPERTIES = 3;


    public Entity(String id, Point position, List<PImage> images) {
        this.id = id;
        this.position = position;
        this.images = images;
        this.imageIndex = 0;
    }

    /**
     * Helper method for testing. Preserve this functionality while refactoring.
     */
    public String log(){
        return this.id.isEmpty() ? null :
                String.format("%s %d %d %d", this.id, this.position.x, this.position.y, this.imageIndex);
    }

    public void nextImage() {
        this.imageIndex = this.imageIndex + 1;
    }

    private static void parseSapling(WorldModel world, String[] properties, Point pt, String id, ImageStore imageStore) {
        if (properties.length == SAPLING_NUM_PROPERTIES) {
            Sapling sapling = new Sapling(id, pt, imageStore.getImageList(SAPLING_KEY));
            world.tryAddEntity(sapling);
        }else{
            throw new IllegalArgumentException(String.format("%s requires %d properties when parsing", SAPLING_KEY, SAPLING_NUM_PROPERTIES));
        }
    }

    private static void parseDude(WorldModel world, String[] properties, Point pt, String id, ImageStore imageStore) {
        if (properties.length == DUDE_NUM_PROPERTIES) {
            Dude_Not_Full dude_not_full = new Dude_Not_Full(id, pt, imageStore.getImageList(DUDE_KEY), Double.parseDouble(properties[DUDE_ACTION_PERIOD]), Double.parseDouble(properties[DUDE_ANIMATION_PERIOD]), 0, Integer.parseInt(properties[DUDE_LIMIT]));
            world.tryAddEntity(dude_not_full);
        }else{
            throw new IllegalArgumentException(String.format("%s requires %d properties when parsing", DUDE_KEY, DUDE_NUM_PROPERTIES));
        }
    }



    private static void parseFairy(WorldModel world, String[] properties, Point pt, String id, ImageStore imageStore) {
        if (properties.length == FAIRY_NUM_PROPERTIES) {
            Fairy fairy = new Fairy(id, pt, imageStore.getImageList(FAIRY_KEY), Double.parseDouble(properties[FAIRY_ACTION_PERIOD]), Double.parseDouble(properties[FAIRY_ANIMATION_PERIOD]));
            world.tryAddEntity(fairy);
        }else{
            throw new IllegalArgumentException(String.format("%s requires %d properties when parsing", FAIRY_KEY, FAIRY_NUM_PROPERTIES));
        }
    }

    private static void parseTree(WorldModel world, String[] properties, Point pt, String id, ImageStore imageStore) {
        if (properties.length == TREE_NUM_PROPERTIES) {
            Tree tree = new Tree(id, pt, imageStore.getImageList(TREE_KEY), Double.parseDouble(properties[TREE_ACTION_PERIOD]), Double.parseDouble(properties[TREE_ANIMATION_PERIOD]), Integer.parseInt(properties[TREE_HEALTH]));
            world.tryAddEntity(tree);
        }else{
            throw new IllegalArgumentException(String.format("%s requires %d properties when parsing", TREE_KEY, TREE_NUM_PROPERTIES));
        }
    }

    private static void parseObstacle(WorldModel world, String[] properties, Point pt, String id, ImageStore imageStore) {
        if (properties.length == OBSTACLE_NUM_PROPERTIES) {
            Obstacle obstacle = new Obstacle(id, pt, imageStore.getImageList(OBSTACLE_KEY),  Double.parseDouble(properties[OBSTACLE_ANIMATION_PERIOD]));
            world.tryAddEntity(obstacle);
        }else{
            throw new IllegalArgumentException(String.format("%s requires %d properties when parsing", OBSTACLE_KEY, OBSTACLE_NUM_PROPERTIES));
        }
    }

    private static void parseHouse(WorldModel world, String[] properties, Point pt, String id, ImageStore imageStore) {
        if (properties.length == HOUSE_NUM_PROPERTIES) {
            House house = new House(id, pt, imageStore.getImageList(HOUSE_KEY));
            world.tryAddEntity(house);
        }else{
            throw new IllegalArgumentException(String.format("%s requires %d properties when parsing", HOUSE_KEY, HOUSE_NUM_PROPERTIES));
        }
    }
    private static void parseStump(WorldModel world, String[] properties, Point pt, String id, ImageStore imageStore) {
        if (properties.length == STUMP_NUM_PROPERTIES) {
            Stump stump = new Stump(id, pt, imageStore.getImageList(STUMP_KEY));
            world.tryAddEntity(stump);
        }else{
            throw new IllegalArgumentException(String.format("%s requires %d properties when parsing", STUMP_KEY, STUMP_NUM_PROPERTIES));
        }
    }

    public static void parseEntity(WorldModel world, String line, ImageStore imageStore) {
        String[] properties = line.split(" ", Entity.ENTITY_NUM_PROPERTIES + 1);
        if (properties.length >= Entity.ENTITY_NUM_PROPERTIES) {
            String key = properties[PROPERTY_KEY];
            String id = properties[Entity.PROPERTY_ID];
            Point pt = new Point(Integer.parseInt(properties[Entity.PROPERTY_COL]), Integer.parseInt(properties[Entity.PROPERTY_ROW]));

            properties = properties.length == Entity.ENTITY_NUM_PROPERTIES ?
                    new String[0] : properties[Entity.ENTITY_NUM_PROPERTIES].split(" ");

            switch (key) {
                case Entity.OBSTACLE_KEY -> Entity.parseObstacle(world, properties, pt, id, imageStore);
                case Entity.DUDE_KEY -> Entity.parseDude(world, properties, pt, id, imageStore);
                case Entity.FAIRY_KEY -> Entity.parseFairy(world, properties, pt, id, imageStore);
                case Entity.HOUSE_KEY -> Entity.parseHouse(world, properties, pt, id, imageStore);
                case Entity.TREE_KEY -> Entity.parseTree(world, properties, pt, id, imageStore);
                case Entity.SAPLING_KEY -> Entity.parseSapling(world, properties, pt, id, imageStore);
                case Entity.STUMP_KEY -> Entity.parseStump(world, properties, pt, id, imageStore);
                default -> throw new IllegalArgumentException("Entity key is unknown");
            }
        } else {
            throw new IllegalArgumentException("Entity must be formatted as [key] [id] [x] [y] ...");
        }
    }

    public static PImage getCurrentImage(Object object) {
        if (object instanceof Background background) {
            return background.getImages().get(background.getImageIndex());
        } else if (object instanceof Entity entity) {
            return entity.images.get(entity.imageIndex % entity.images.size());
        } else {
            throw new UnsupportedOperationException(String.format("getCurrentImage not supported for %s", object));
        }
    }

    public String getId() {
        return id;
    }

    public List<PImage> getImages() {
        return images;
    }

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }
}
