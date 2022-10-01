package uet.oop.bomberman.core;

import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import uet.oop.bomberman.config.GameConfig;

import java.net.URISyntaxException;

public class IntroGame {
    private double width;
    private double height;

    private Stage stage;
    private GraphicsContext render;
    private Image logo;
    private Image bar;
    private static double value;
    private double delayValue;
    public static final double MAXVALUE = 444;
    private boolean done;
    private boolean initDone;

    public IntroGame(Stage mainStage) {
        width = GameConfig.WIDTH;
        height = GameConfig.HEIGHT;
        initDone = false;

        Canvas canvas = new Canvas(width, height);
        render = canvas.getGraphicsContext2D();
        Group group = new Group();
        group.getChildren().add(canvas);
        Scene scene = new Scene(group, width, height);

        stage = mainStage;
        stage.setResizable(false);
        stage.setTitle(GameConfig.NAME);
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.setOnCloseRequest(e -> {
            Platform.exit();
            System.exit(0);
        });
        stage.show();

        try {
            logo = new Image(getClass().getResource("/LogoIntro.png").toURI().toString());
            bar = new Image(getClass().getResource("/something/bar3.png").toURI().toString());
            value = 0;
            delayValue = 0;
            done = false;
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        (new Thread(() -> {
//            Bomb.init();
//            Item.init();
//            Balloom.init();
//            Doll.init();
//            Kondoria.init();
//            Minvo.init();
//            Oneal.init();
//            Ovapi.init();
//            Pass.init();
//            MusicPlayer.init();
//            Bomberman.init();
            initDone = true;
        })).start();
    }

    public void draw() {
        render.clearRect(0, 0, GameConfig.WIDTH, GameConfig.HEIGHT);
        render.setFill(Color.web("3c75d8"));
        render.fillRect(0, 0, GameConfig.WIDTH, GameConfig.HEIGHT);
        render.drawImage(logo, 0, 0, GameConfig.WIDTH, 245);
        render.drawImage(bar, 58, 450);
        render.setFill(Color.WHITE);
        render.fillRect(58, 450, value, 20);

        if (value < 330) {
            value += 2;
        } else if (value < MAXVALUE) {
            delayValue ++;
            if (delayValue > 40 && delayValue < 180) {
                if (delayValue % 2 == 0) {
                    value += 1;
                }
            }
            if (delayValue > 280){
                value += 1;
            }
        } else {
            value = MAXVALUE;
            done = true;
        }
    }

    public static void setValue(double v) {
        value = v;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public boolean isInitDone() {
        return initDone;
    }
}
