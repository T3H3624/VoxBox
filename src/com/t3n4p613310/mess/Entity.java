package com.t3n4p613310.mess;

import java.nio.DoubleBuffer;

public class Entity
{
    long entID;
    int modelID;
    private double[] position = new double[3];
    private double[] orientation = new double[3];
    DoubleBuffer positions;
    DoubleBuffer normals;
    int positionVbo;
    int normalVbo;
    int numVertices;
}
