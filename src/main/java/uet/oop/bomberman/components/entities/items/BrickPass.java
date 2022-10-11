package uet.oop.bomberman.components.entities.items;

import uet.oop.bomberman.components.entities.EntitiesManager;

public class BrickPass extends Item {
    /**
     * Allowed Bomberman to pass through brick.
     */
    public BrickPass(double x, double y){
        super(x, y, brickPass);
    }

    @Override
    public void changePower() {
        EntitiesManager.getInstance().players.get(0).setCanPassBrick(true);
        if(time >= timePowerUp){
            EntitiesManager.getInstance().players.get(0).setCanPassBrick(false);
            eaten = false;
            done = true;
        }
    }
}