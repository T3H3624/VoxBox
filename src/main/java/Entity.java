import java.nio.DoubleBuffer;

public class Entity
{
    long entID = 0;
    int modelID = 0;
    DoubleBuffer positions;
    DoubleBuffer normals;
    int positionVbo;
    int normalVbo;
    int numVertices;
    double[] position = new double[3];
    double[] rotation = new double[3];
    double[] scale = new double[3];

    Entity()
    {

    }
}
