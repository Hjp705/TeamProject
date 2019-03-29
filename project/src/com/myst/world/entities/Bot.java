package com.myst.world.entities;

import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;

import com.myst.AI.AStarSearch;
import com.myst.networking.EntityData;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import com.myst.AI.AI;
import com.myst.rendering.Window;
import com.myst.world.World;
import com.myst.world.collisions.AABB;
import com.myst.world.collisions.Collision;
import com.myst.world.collisions.Line;
import com.myst.world.view.Camera;
import com.myst.world.view.Transform;

public class Bot extends Entity {

    private AI intelligence;
    private final float MOVEMENT_SPEED = 1f;
    private ArrayList<Vector3f> path;
    private ArrayList<Line> bullets;
    private Random randInt = null;
    public float health = 50;
    private float maxHealth = 50;
    private long lastSpikeDamage = 0;
    private long timeToReRoute;
    private long lastReRoute;
    private Stack<int[]> route = null;
    private int[] nextLocation;
    public long lastMove = System.currentTimeMillis();
    public long moveTime = 50;
    private AStarSearch search;



    private long spikeDamageDelay = 1000;
    public Bot(Vector2f boundingBoxCoords, ArrayList<Line> bullets, AStarSearch search) {
        super(boundingBoxCoords);
        type = EntityType.PLAYER;
        this.visibleToEnemy = true;
        this.bullets = bullets;
        this.exists = true;
        this.isBot = true;
        this.search = search;

    }


    public void initialiseAI(World world) {
        intelligence = new AI(transform, world);
    }

    public void updateBot(float deltaTime, World world, Entity player) {
        //add enemy detection method here, so constantly checks for enemies as well as randomly turning on flashlight.

        if (player != null) {
            float distToPlayer = this.transform.pos.distance(player.transform.pos);
            timeToReRoute = distPlayerRouteTime(distToPlayer);
            if (route == null) {
                route = search.findRoute(new int[]{(int) this.transform.pos.x, (int) -this.transform.pos.y},
                        new int[]{(int) player.transform.pos.x, (int) -player.transform.pos.y});
                lastReRoute = System.currentTimeMillis();
                if (!route.isEmpty()) {
                    nextLocation = route.pop();
                }
            } else if ((System.currentTimeMillis() - lastReRoute) >= timeToReRoute || route.isEmpty()) {
                route = search.findRoute(new int[]{(int) this.transform.pos.x, (int) -this.transform.pos.y},
                        new int[]{(int) player.transform.pos.x, (int) -player.transform.pos.y});
                lastReRoute = System.currentTimeMillis();
                if (!route.isEmpty()) {
                    nextLocation = route.pop();
                }
            }
        }


        if(System.currentTimeMillis() - lastMove > 1 && nextLocation != null){
            this.transform.pos.x = nextLocation[0];
            this.transform.pos.y = -nextLocation[1];
            lastMove = System.currentTimeMillis();
            if(route.isEmpty()){
                nextLocation = null;
            }else{
                nextLocation = route.pop();
            }

        }

        AABB[] boxes = new AABB[25];
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
//	           30 is the scale and 0.5f is half the width of the box and 2.5 is the scan width
                int x = (int) (transform.pos.x + (0.5f - 2.5 + i));
                int y = (int) (-transform.pos.y + (0.5f - 2.5 + j));
                boxes[i + (j * 5)] = world.getBoundingBox(x, y);
            }
        }



        for (int i = 0; i < boxes.length; i++) {
            if (boxes[i] != null) {
                Collision data = boundingBox.getCollision(boxes[i]);
                if (data.isIntersecting) {
                    boundingBox.correctPosition(boxes[i], data);
                    transform.pos.set(boundingBox.getCentre(), 0);
                    boundingBox.getCentre().set(transform.pos.x, transform.pos.y);
                }
            }
        }
    }

    public static long distPlayerRouteTime(float distance){
        return (long) (1000 * Math.pow(2,distance));
    }

    @Override
    public EntityData getData() {
        EntityData data = super.getData();
        PlayerData playerData = new PlayerData();
        playerData.health = health;
        playerData.maxHealth = maxHealth;
        playerData.lastSpikeDamage = lastSpikeDamage;
        data.typeData = playerData;
        return data;
    }




    @Override
    public void update(float deltaTime, Window window, Camera camera, World world, ConcurrentHashMap<Integer, Entity> items) {


        for(int i=0; i < deltaTime; i++){

        }


        return;
    }
}
