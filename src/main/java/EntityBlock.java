public class EntityBlock extends Entity
{
    int[] indices={0,1,3, 1,2,3,2,3,5,3,4,5,4,5,7,5,6,7,6,7,1,0,3,4,1,2,6,0,4,7,2,5,6};
    float[][] vertices= new float[][] {{1,1,1},{-1,1,1},{-1,1,-1},{1,1,-1},{1,-1,-1},{-1,-1,-1},{-1,-1,1},{1,-1,1}};

    EntityBlock()
    {
        super();
        setModel(vertices,indices);
    }
}
