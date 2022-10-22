package uet.oop.bomberman.components.entities.players;

import uet.oop.bomberman.components.entities.EntitiesManager;
import uet.oop.bomberman.components.entities.bomb.Bomb;
import uet.oop.bomberman.components.entities.enemies.Enemy;
import uet.oop.bomberman.components.entities.materials.Portal;
import uet.oop.bomberman.components.maps.LevelMap;
import uet.oop.bomberman.config.CharacterStatus;
import uet.oop.bomberman.config.Direction;
import uet.oop.bomberman.config.GameConfig;

import java.util.*;

public class AutoPlay extends Bomber {
    private boolean canMoveR = false;
    private boolean canMoveL = false;
    private boolean canMoveU = false;
    private boolean canMoveD = false;
    private double moveX = 0;
    private double moveY = 0;
    private int iBomber;
    private int jBomber;
    private Random r = new Random();
    private List<Direction> directionList = new ArrayList<>();

    public AutoPlay(double x, double y, int w, int h) {
        super(x, y, w, h);
        initDirectionList();
        iBomber = (int) (y) / GameConfig.TILE_SIZE;
        jBomber = (int) (x) / GameConfig.TILE_SIZE;
        speed = 4;
    }

    @Override
    public void move() {
        iBomber = (int) (y) / GameConfig.TILE_SIZE;
        jBomber = (int) (x) / GameConfig.TILE_SIZE;
        if (jBomber * GameConfig.TILE_SIZE == x && iBomber * GameConfig.TILE_SIZE == y
                && playerStatus != CharacterStatus.DEAD) {
                if (EntitiesManager.getInstance().bombs.size() == 0) {
                    playerStatus = CharacterStatus.MOVING;
                    if(EntitiesManager.getInstance().enemies.size() != 0){
                        direction = findEntity("enemy");
                        avoidEnemy();
                    }else if(!EntitiesManager.getInstance().portal.isAppear()){
                        direction = findEntity("brick");
                    }else if(EntitiesManager.getInstance().coins.size() == 0){
                        direction = findEntity("portal");
                    }else {
                        direction = findEntity("coin");
                    }

                    if (EntitiesManager.getInstance().bombs.size() == 0){
                        moveFind();
                    }

                } else {
                    aVoidBomb();
                    if (playerStatus == CharacterStatus.IDLE) {
                        moveX = 0;
                        moveY = 0;
                    }
                    if (playerStatus == CharacterStatus.MOVING) {
                        moveAvoidBomb();
                    }
                }
        }
        x += moveX;
        y += moveY;
        updateBoxCollider();
    }

    private Direction findEntity(String nameEntity) {
        int i = iBomber;
        int j = jBomber;
        LevelMap levelMap = LevelMap.getInstance();
        boolean[][] checkPass =
                new boolean[levelMap.getMapHash().length][levelMap.getMapHash()[0].length];

        for (int m = 0; m < checkPass.length; m++) {
            for (int n = 0; n < checkPass[0].length; n++) {
                checkPass[m][n] = false;
            }
        }

        canMoveR = checkFindWay(i, j + 1);
        canMoveL = checkFindWay(i, j - 1);
        canMoveU = checkFindWay(i - 1, j);
        canMoveD = checkFindWay(i + 1, j);;

        Queue<Direction> direc = new LinkedList<>();
        Queue<Integer> iTile = new LinkedList<>();
        Queue<Integer> jTile = new LinkedList<>();


        checkPass[i][j] = true;
        if (canMoveR && !checkPass[i][j + 1]) {
            checkPass[i][j + 1] = true;
            direc.add(Direction.RIGHT);
            iTile.add(i);
            jTile.add(j + 1);
        }
        if (canMoveL && !checkPass[i][j - 1]) {
            checkPass[i][j - 1] = true;
            direc.add(Direction.LEFT);
            iTile.add(i);
            jTile.add(j - 1);
        }
        if (canMoveU && !checkPass[i - 1][j]) {
            checkPass[i - 1][j] = true;
            direc.add(Direction.UP);
            iTile.add(i - 1);
            jTile.add(j);
        }
        if (canMoveD && !checkPass[i + 1][j]) {
            checkPass[i + 1][j] = true;
            direc.add(Direction.DOWN);
            iTile.add(i + 1);
            jTile.add(j);
        }

        while (!direc.isEmpty()) {
            Direction direction1 = direc.poll();
            i = iTile.poll();
            j = jTile.poll();

            if (checkEntity(i, j, nameEntity)) {
                return direction1;
            }

            canMoveR = checkFindWay(i, j + 1);
            canMoveL = checkFindWay(i, j - 1);
            canMoveU = checkFindWay(i - 1, j);
            canMoveD = checkFindWay(i + 1, j);

            if (canMoveR && !checkPass[i][j + 1]) {
                checkPass[i][j + 1] = true;
                direc.add(direction1);
                iTile.add(i);
                jTile.add(j + 1);
            }
            if (canMoveL && !checkPass[i][j - 1]) {
                checkPass[i][j - 1] = true;
                direc.add(direction1);
                iTile.add(i);
                jTile.add(j - 1);
            }
            if (canMoveU && !checkPass[i - 1][j]) {
                checkPass[i - 1][j] = true;
                direc.add(direction1);
                iTile.add(i - 1);
                jTile.add(j);
            }
            if (canMoveD && !checkPass[i + 1][j]) {
                checkPass[i + 1][j] = true;
                direc.add(direction1);
                iTile.add(i + 1);
                jTile.add(j);
            }
        }
        return Direction.DOWN;
    }

    protected void initDirectionList() {
        directionList.clear();
        directionList.add(Direction.LEFT);
        directionList.add(Direction.RIGHT);
        directionList.add(Direction.UP);
        directionList.add(Direction.DOWN);
    }

    private boolean checkEntity(int i, int j, String nameEntity) {
        switch (nameEntity){
            case "enemy": {
                List<Enemy> enemyList = EntitiesManager.getInstance().enemies;
                for (int k = 0; k < enemyList.size(); k++) {
                    int iEnemy = (int) enemyList.get(k).getY() / GameConfig.TILE_SIZE;
                    int jEnemy = (int) enemyList.get(k).getX() / GameConfig.TILE_SIZE;
                    if (iEnemy == i && jEnemy == j) {
                        return true;
                    }
                }
                return false;
            }
            case "portal": {
                Portal portal = EntitiesManager.getInstance().portal;
                int iPortal = (int) portal.getY() / GameConfig.TILE_SIZE;
                int jPortal = (int) portal.getX() / GameConfig.TILE_SIZE;
                System.out.println(iPortal + " " + jPortal);
                if (i == iPortal && j == jPortal){
                    System.out.println(true);
                    return true;
                }
                return false;
            }
            case "brick": {
                LevelMap levelMap = LevelMap.getInstance();
                if(levelMap.getHashAt(i, j) == levelMap.getHash("brick")){
                    return true;
                }
                return false;
            }
            case "coin": {
                LevelMap levelMap = LevelMap.getInstance();
                if(levelMap.getHashAt(i, j) == levelMap.getHash("coin")){
                    return true;
                }
                return false;
            }
            default:
                return false;
        }
    }

    private void aVoidBomb() {
        for (Bomb bomb : EntitiesManager.getInstance().bombs) {
            int iBomb = (int) bomb.getY() / GameConfig.TILE_SIZE;
            int jBomb = (int) bomb.getX() / GameConfig.TILE_SIZE;
            playerStatus = CharacterStatus.MOVING;
            if (jBomber == jBomb && iBomber == iBomb) {
                directionList.remove(direction);
                if (directionList.size() != 0) {
                    int ran = r.nextInt(directionList.size());
                    direction = directionList.get(ran);
                } else {
                    playerStatus = CharacterStatus.IDLE;
                }
                break;
            }
            if (iBomber == iBomb && Math.abs(jBomber - jBomb) <= Bomb.getFlameLength()) {
                if (jBomber - jBomb > 0) {
                    directionList.remove(Direction.LEFT);
                    if (directionList.size() != 0) {
                        int ran = r.nextInt(directionList.size());
                        direction = directionList.get(ran);
                    } else {
                        playerStatus = CharacterStatus.IDLE;
                    }
                } else {
                    directionList.remove(Direction.RIGHT);
                    if (directionList.size() != 0) {
                        int ran = r.nextInt(directionList.size());
                        direction = directionList.get(ran);
                    } else {
                        playerStatus = CharacterStatus.IDLE;
                    }
                }
                break;
            }
            if (jBomber == jBomb && Math.abs(iBomber - iBomb) <= Bomb.getFlameLength()) {
                if (iBomber - iBomb > 0) {
                    directionList.remove(Direction.UP);
                    if (directionList.size() != 0) {
                        int ran = r.nextInt(directionList.size());
                        direction = directionList.get(ran);
                    } else {
                        playerStatus = CharacterStatus.IDLE;
                    }
                } else {
                    directionList.remove(Direction.DOWN);
                    if (directionList.size() != 0) {
                        int ran = r.nextInt(directionList.size());
                        direction = directionList.get(ran);
                    } else {
                        playerStatus = CharacterStatus.IDLE;
                    }
                }
                break;
            }
            playerStatus = CharacterStatus.IDLE;
        }
    }

    protected void moveAvoidBomb() {
        moveX = 0;
        moveY = 0;
        LevelMap levelMap = LevelMap.getInstance();
        canMoveR = levelMap.getHashAt(iBomber, jBomber + 1) == levelMap.getHash("grass");
        canMoveL = levelMap.getHashAt(iBomber, jBomber - 1) == levelMap.getHash("grass");
        canMoveU = levelMap.getHashAt(iBomber - 1, jBomber) == levelMap.getHash("grass");
        canMoveD = levelMap.getHashAt(iBomber + 1, jBomber) == levelMap.getHash("grass");
        switch (direction) {
            case UP: {
                if (canMoveU) {
                    moveY = -speed;
                    initDirectionList();
                } else {
                    directionList.remove(direction);
                }
                break;
            }
            case DOWN: {
                if (canMoveD) {
                    moveY = speed;
                    initDirectionList();
                } else {
                    directionList.remove(direction);
                }
                break;
            }
            case LEFT: {
                if (canMoveL) {
                    moveX = -speed;
                    initDirectionList();
                } else {
                    directionList.remove(direction);
                }
                break;
            }
            case RIGHT: {
                if (canMoveR) {
                    moveX = speed;
                    initDirectionList();
                } else {
                    directionList.remove(direction);
                }
                break;
            }
            default:
                break;
        }
    }

    private void moveFind() {
        moveX = 0;
        moveY = 0;
        LevelMap levelMap = LevelMap.getInstance();
        switch (direction) {
            case UP: {
                if (levelMap.getHashAt(iBomber - 1, jBomber) == levelMap.getHash("grass")) {
                    moveY = -speed;
                } else if (levelMap.getHashAt(iBomber - 1, jBomber) == levelMap.getHash("brick")) {
                    placeBomb();
                    initDirectionList();
                } else if (checkEntity(iBomber - 1, jBomber, "portal")
                        && EntitiesManager.getInstance().portal.isCanPass()) {
                    levelMap.prepareNextLevel();
                }
                break;
            }
            case DOWN: {
                if (levelMap.getHashAt(iBomber + 1, jBomber) == levelMap.getHash("grass")) {
                    moveY = speed;
                } else if (levelMap.getHashAt(iBomber + 1, jBomber) == levelMap.getHash("brick")) {
                    placeBomb();
                    initDirectionList();
                } else if (checkEntity(iBomber + 1, jBomber, "portal")
                        && EntitiesManager.getInstance().portal.isCanPass()) {
                    levelMap.prepareNextLevel();
                }
                break;
            }
            case LEFT: {
                if (levelMap.getHashAt(iBomber, jBomber - 1) == levelMap.getHash("grass")) {
                    moveX = -speed;
                } else if (levelMap.getHashAt(iBomber, jBomber - 1) == levelMap.getHash("brick")) {
                    placeBomb();
                    initDirectionList();
                } else if (checkEntity(iBomber, jBomber - 1, "portal")
                        && EntitiesManager.getInstance().portal.isCanPass()) {
                    levelMap.prepareNextLevel();
                }
                break;
            }
            case RIGHT: {
                if (levelMap.getHashAt(iBomber, jBomber + 1) == levelMap.getHash("grass")) {
                    moveX = speed;
                } else if (levelMap.getHashAt(iBomber, jBomber + 1) == levelMap.getHash("brick")) {
                    placeBomb();
                    initDirectionList();
                } else if (checkEntity(iBomber, jBomber + 1, "portal")
                        && EntitiesManager.getInstance().portal.isCanPass()) {
                    levelMap.prepareNextLevel();
                }
                break;
            }
            default:
                break;
        }
    }

    private void avoidEnemy() {
        for (Enemy enemy : EntitiesManager.getInstance().enemies) {
            int iBomb = (int) enemy.getY() / GameConfig.TILE_SIZE;
            int jBomb = (int) enemy.getX() / GameConfig.TILE_SIZE;
            playerStatus = CharacterStatus.MOVING;

            if ((iBomber == iBomb && Math.abs(jBomber - jBomb) <= 2)
                    || (jBomber == jBomb && Math.abs(iBomber - iBomb) <= 2)) {
                if (enemy.getLastDirection() == direction) {
                    placeBomb();
                    initDirectionList();
                } else {
                    playerStatus = CharacterStatus.IDLE;
                }
            }
        }
    }

    protected boolean checkFindWay(int i, int j) {
        LevelMap levelMap = LevelMap.getInstance();
        if(levelMap.getHashAt(i, j) == levelMap.getHash("wall")){
            return false;
        }
        if (levelMap.getHashAt(i, j) == levelMap.getHash("portal")) {
            if (EntitiesManager.getInstance().portal.isAppear()
                    && !EntitiesManager.getInstance().portal.isCanPass()) {
                return false;
            }
        }
        return  true;
    }
}
