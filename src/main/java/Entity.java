import org.lwjgl.system.MemoryUtil;
import static org.lwjgl.opengl.GL30.*;

import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class Entity
{
    long entID = 0;
    int modelID = 0;
    FloatBuffer positionBuffer;
    DoubleBuffer normalBuffer;
    FloatBuffer colourBuffer;
    DoubleBuffer textureBuffer;
    IntBuffer indexBuffer;
    int entityVao;
    int positionVbo;
    int normalVbo;
    int colourVbo;
    int textureVbo;
    int indexVbo;
    int[] indices;
    float[][] vertices;

    double[] position = new double[3];
    double[] rotation = new double[3];
    double[] scale = new double[3];


    Entity()
    {

    }

    public void setModel(float[][] verticesIn,int[] indicesIn)
    {
        vertices = verticesIn;
        indices = indicesIn;
    }

    public void create()
    {
        entityVao = glGenVertexArrays();
        positionVbo = glGenBuffers();
        colourVbo = glGenBuffers();
        indexVbo = glGenBuffers();

        positionBuffer = MemoryUtil.memAllocFloat(vertices.length*3);
        colourBuffer = MemoryUtil.memAllocFloat(vertices.length*3);
        indexBuffer = MemoryUtil.memAllocInt(indices.length);

        float[] positionData = new float[vertices.length*3];
        for(int i=0; i<vertices.length;i++)
        {
            positionData[i*3] = vertices[i][0];
            positionData[i*3+1] = vertices[i][1];
            positionData[i*3+2] = vertices[i][2];
        }

        float[] colorData = new float[vertices.length*3];
        for(int i=0; i<vertices.length;i++)
        {
            colorData[i*3] = (float) Math.random();
            colorData[i*3+1] = (float) Math.random();
            colorData[i*3+2] = (float) Math.random();
        }

        positionBuffer.put(positionData).flip();
        colourBuffer.put(colorData).flip();
        indexBuffer.put(indices).flip();

        glBindVertexArray(entityVao);

        glBindBuffer(GL_ARRAY_BUFFER, positionVbo);
        glBufferData(GL_ARRAY_BUFFER, positionBuffer, GL_STATIC_DRAW);
        glVertexAttribPointer(0,3, GL_FLOAT,false,0,0);
        glBindBuffer(GL_ARRAY_BUFFER,0);

        glBindBuffer(GL_ARRAY_BUFFER, colourVbo);
        glBufferData(GL_ARRAY_BUFFER, colourBuffer, GL_STATIC_DRAW);
        glVertexAttribPointer(0,3, GL_FLOAT,false,0,0);
        glBindBuffer(GL_ARRAY_BUFFER,0);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexVbo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL_STATIC_DRAW);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER,0);
    }

    public void destroy()
    {

    }
}
