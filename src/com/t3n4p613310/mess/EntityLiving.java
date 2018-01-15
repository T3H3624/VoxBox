package com.t3n4p613310.mess;

public class EntityLiving extends Entity
{
    String name = "";
    private Item[] inventory = new Item[64];
    private byte health = 100;

    EntityLiving()
    {
        super();
    }
}
