package com.t3n4p613310.mess;

import org.lwjgl.BufferUtils;

import java.nio.DoubleBuffer;

public class Entity
{
    private double[] position = new double[3];
    private double[] orientation = new double[3];
    private double[] scale = new double[3];
    long entID=0;
    int modelID=0;
    DoubleBuffer positions;
    DoubleBuffer normals;
    int positionVbo;
    int normalVbo;
    int numVertices;

    Entity(){
        positions = BufferUtils.createDoubleBuffer(9);
        positions.put(0.1).put(0.1).put(0.1);
        positions.put(0.1).put(0.1).put(-0.1);
        positions.put(0.1).put(-0.1).put(0.1);
        normals = BufferUtils.createDoubleBuffer(10);
        positions.put(0.1).put(0.1).put(0.1);
        positions.put(0.1).put(0.1).put(-0.1);
        positions.put(0.1).put(-0.1).put(0.1);
    }
}