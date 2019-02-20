package com.myst.networking;

import com.myst.world.collisions.AABB;
import com.myst.world.view.Transform;

import java.io.Serializable;

public class EntityData implements Serializable {
    public String ownerID;
    public Integer localID;
    public Transform transform;
    public AABB boundingBox;
    public long time;
}
