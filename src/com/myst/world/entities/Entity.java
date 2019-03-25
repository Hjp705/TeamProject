package com.myst.world.entities;

import org.joml.Vector2f;
import org.joml.Vector3f;
import com.myst.networking.EntityData;
import com.myst.rendering.Model;
import com.myst.rendering.Shader;
import com.myst.rendering.Texture;
import com.myst.rendering.Window;
import com.myst.world.World;
import com.myst.world.collisions.AABB;
import com.myst.world.view.Camera;
import com.myst.world.view.Transform;

import java.io.Serializable;
<<<<<<< HEAD:src/com/myst/world/entities/Entity.java
import java.util.HashMap;
=======
import java.util.concurrent.ConcurrentHashMap;
>>>>>>> master:project/src/com/myst/world/entities/Entity.java

public abstract class Entity implements Serializable {
    protected Model model;
    public Transform transform;
    protected Texture texture;
    public AABB boundingBox;
    protected EntityType type;
    public String owner;
    public boolean lightSource;
    public float lightDistance;
    public Integer localID;
    private boolean killable;
    protected HashMap<Integer, Entity> entities;
    public boolean visibleToEnemy;
    public boolean exists = true;
    public boolean hidden = false;

    public Entity(float[] vertices, float[] texture, int[] indices, Vector2f boundingBoxCoords){
        model = new Model(vertices, texture, indices);
        this.texture = new Texture("assets/survivor1_hold.png");
        transform = new Transform();
        transform.scale = new Vector3f(1,1,1);
        boundingBox = new AABB(new Vector2f(transform.pos.x, transform.pos.y), boundingBoxCoords);
        this.lightSource = false;
        this.lightDistance = 25f;
    }

    public abstract void update(float deltaTime, Window window, Camera camera, World world, ConcurrentHashMap<Integer,Entity> items);

    public void render(Camera camera, Shader shader){
        if(!exists || hidden) return;

        shader.bind();
        shader.setUniform("sampler", 0);
        shader.setUniform("projection", transform.getProjection(camera.getProjection()));
        texture.bind(0);
        model.render();
    }

    public void setType(EntityType type) {
        this.type = type;
    }

    public EntityType getType() {
        return type;
    }

    //    used in networking to get the entity data
    public EntityData getData(){
        EntityData data = new EntityData();
        data.localID = this.localID;
        data.ownerID = this.owner;
        data.type = this.type;
        data.boundingBox = this.boundingBox;
        data.transform = this.transform;
        data.lightSource = this.lightSource;
        data.lightDistance = this.lightDistance;
        data.exists = this.exists;

        return data;
    }

    public void readInEntityData(EntityData data){
        this.owner = data.ownerID;
        this.localID = data.localID;
        this.transform = data.transform;
        this.boundingBox = data.boundingBox;
        this.lightSource = data.lightSource;
        this.lightDistance = data.lightDistance;
        this.exists = data.exists;
    }
<<<<<<< HEAD:src/com/myst/world/entities/Entity.java
    
    public boolean isKillable() {
		return killable;
	}
	
	public Texture getTexture() {
		return texture;
	}
	
	public Model getModel() {
		return model;
	}
	
	public Enum<EntityTypes> getType() {
		return type;
	}
	
	@Override
	public String toString(){
		return owner + " [" + type.toString() + "]";
	}

=======
>>>>>>> master:project/src/com/myst/world/entities/Entity.java
}
