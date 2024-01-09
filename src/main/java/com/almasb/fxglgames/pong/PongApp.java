/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2017 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.almasb.fxglgames.pong;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.audio.Audio;
import com.almasb.fxgl.audio.Music;
import com.almasb.fxgl.audio.impl.DesktopMusic;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.net.*;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.physics.CollisionResult;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.ui.UI;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
//import javafx.scene.media.Media;
//aimport javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.almasb.fxglgames.pong.NetworkMessages.*;

//how to add in a timer for 3 minutes java
//Java 11 three-minute timer without static declarations inside an inner class
//import java.util.concurrent.Executors;
//import java.util.concurrent.ScheduledExecutorService;
//import java.util.concurrent.TimeUnit;

import java.util.Timer;
import java.util.TimerTask;

/**
 * A simple clone of Pong.
 * Sounds from https://freesound.org/people/NoiseCollector/sounds/4391/ under CC BY 3.0.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class PongApp extends GameApplication implements MessageHandler<String> {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("Pong");
        settings.setVersion("1.0");
        settings.setFontUI("pong.ttf");
        settings.setApplicationMode(ApplicationMode.DEBUG);
    }

    //adding players
    private Entity player1;
    private Entity player2;
    private Entity player3;

    private Entity spikes;

    private Entity endPoint;

    //adding fruits
    private Entity strawberry;

    private Entity cherries;

    private Entity pineapple;


    boolean p1 = false;
    boolean p2 = false;
    boolean p3 = false;

    boolean p1Score = false;
    boolean p2Score = false;
    boolean p3Score = false;

    // private Entity ball;

    //
    private Entity levelData;
    //
    private BatComponent player1Bat;
    private BatComponent player2Bat;
    private BatComponent player3Bat;



    private Server<String> server;

    @Override
    protected void initInput() {
        //player 1 controls
        //up - player 1
        getInput().addAction(new UserAction("Up1") {
            @Override
            protected void onAction() {
                if(!player1Bat.physics.isMovingY()) {
                    server.broadcast("PLAY_SOUND," + "jump");
                }
                player1Bat.up();



            }
            @Override
            protected void onActionEnd() {
                player1Bat.stop();
            }
        }, KeyCode.W);

        //down - player 1
        getInput().addAction(new UserAction("Down1") {
            @Override
            protected void onAction() {
                player1Bat.down();
            }

            @Override
            protected void onActionEnd() {
                player1Bat.stop();
            }
        }, KeyCode.S);

        //left - player 1
        getInput().addAction(new UserAction("Left1") {
            @Override
            protected void onAction() {
                player1Bat.left();
            }

            @Override
            protected void onActionEnd() {
                player1Bat.stop();
            }
        }, KeyCode.A);

        //right - player 1
        getInput().addAction(new UserAction("Right1") {
            @Override
            protected void onAction() {
                player1Bat.right();
            }

            @Override
            protected void onActionEnd() {
                player1Bat.stop();
            }
        }, KeyCode.D);

        //player 2 controls
        //up - player 2
        getInput().addAction(new UserAction("Up2") {
            @Override
            protected void onAction() {
                if(!player2Bat.physics.isMovingY()) {
                    server.broadcast("PLAY_SOUND," + "jump");
                }
                player2Bat.up();
            }

            @Override
            protected void onActionEnd() {
                player2Bat.stop();
            }
        }, KeyCode.I);

        //down - player 2
        getInput().addAction(new UserAction("Down2") {
            @Override
            protected void onAction() {
                player2Bat.down();
            }

            @Override
            protected void onActionEnd() {
                player2Bat.stop();
            }
        }, KeyCode.K);

        //left - player 2
        getInput().addAction(new UserAction("Left2") {
            @Override
            protected void onAction() {
                player2Bat.left();
            }

            @Override
            protected void onActionEnd() {
                player2Bat.stop();
            }
        }, KeyCode.J);

        //right - player 2
        getInput().addAction(new UserAction("Right2") {
            @Override
            protected void onAction() {
                player2Bat.right();
            }

            @Override
            protected void onActionEnd() {
                player2Bat.stop();
            }
        }, KeyCode.L);

        //player 3 controls
        //up - player 3
        getInput().addAction(new UserAction("Up3") {
            @Override
            protected void onAction() {
                if(!player3Bat.physics.isMovingY()) {
                    server.broadcast("PLAY_SOUND," + "jump");
                }
                player3Bat.up();
            }

            @Override
            protected void onActionEnd() {
                player3Bat.stop();
            }
        }, KeyCode.T);

        //down - player 3
        getInput().addAction(new UserAction("Down3") {
            @Override
            protected void onAction() {
                player3Bat.down();
            }

            @Override
            protected void onActionEnd() {
                player3Bat.stop();
            }
        }, KeyCode.G);

        //left - player 3
        getInput().addAction(new UserAction("Left3") {
            @Override
            protected void onAction() {
                player3Bat.left();
            }

            @Override
            protected void onActionEnd() {
                player3Bat.stop();
            }
        }, KeyCode.F);

        //right - player 3
        getInput().addAction(new UserAction("Right3") {
            @Override
            protected void onAction() {
                player3Bat.right();
            }

            @Override
            protected void onActionEnd() {
                player3Bat.stop();
            }
        }, KeyCode.H);
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("player1score", 0);
        vars.put("player2score", 0);
        vars.put("player3score", 0);
    }
    boolean Player1Connected=false;
    boolean Player2Connected=false;
    boolean Player3Connected=false;
    @Override
    protected void initGame() {
        Writers.INSTANCE.addTCPWriter(String.class, outputStream -> new MessageWriterS(outputStream));
        Readers.INSTANCE.addTCPReader(String.class, in -> new MessageReaderS(in));

        server = getNetService().newTCPServer(55555, new ServerConfig<>(String.class));

        server.setOnConnected(connection -> {
            //assigns client and id
            int ID=-1; //if stays -1 all players have been assigned
            if (!Player1Connected){
                ID=0;
                Player1Connected=true;
            }else if (!Player2Connected){
                ID=1;
                Player2Connected=true;
            }else if (!Player3Connected){
                ID=2;
                Player3Connected=true;
            }
            connection.getLocalSessionData().setValue("PlayerID",ID);

            connection.addMessageHandlerFX(this);
        });

        getGameWorld().addEntityFactory(new PongFactory());
        getGameScene().setBackgroundColor(Color.rgb(0, 0, 5));

        initScreenBounds();
        initGameObjects();

        var t = new Thread(server.startTask()::run);
        t.setDaemon(true);
        t.start();
    }

    @Override
    protected void initPhysics() {
        getPhysicsWorld().setGravity(0, 400);

        // collisions between player and spikes

        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.PLAYER_BAT, EntityType.SPIKES) {
            @Override
            protected void onCollisionBegin(Entity player1, Entity spikes) {

                resetPlayer();

            }

        });
        //player 2 and 3 are enemy bat
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.ENEMY_BAT, EntityType.SPIKES) {
            @Override
            protected void onCollisionBegin(Entity player2, Entity spikes) {

                resetPlayer();

            }

        });
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.ENEMY_BAT, EntityType.SPIKES) {
            @Override
            protected void onCollisionBegin(Entity player3, Entity spikes) {

                resetPlayer();


            }

        });

        //collisions between players and fruits
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.PLAYER_BAT, EntityType.STRAWBERRY) {
            @Override
            protected void onCollisionBegin(Entity player1, Entity strawberry) {
                //increasing player score when collide with fruit
                inc("player1score", +1);
                var scores = "SCORES," + geti("player1score") + "," + geti("player2score")+ "," + geti("player3score");
                server.broadcast(scores);

                server.broadcast("PLAY_SOUND,fruitCollision");

                //remove collected fruit from screen
                getGameWorld().removeEntity(strawberry);

                //if player has collected all fruits this will == true (1 fruit on screen)
                //this means they can access end point
                if (geti("player1score") > 1 ){
                    p1Score = true;
                }

            }

        });
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.ENEMY_BAT, EntityType.CHERRIES) {
            @Override
            protected void onCollisionBegin(Entity player2, Entity cherries) {

                //increasing player score when collide with fruit
                inc("player2score", +1);
                var scores = "SCORES," + geti("player1score") + "," + geti("player2score")+ "," + geti("player3score");
                server.broadcast(scores);
                server.broadcast("PLAY_SOUND," + "fruitCollision");

                //remove collected fruit from screen
                getGameWorld().removeEntity(cherries);
                if (geti("player2score") > 1 ){
                    p2Score = true;
                }
            }

        });
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.ENEMY_BAT, EntityType.PINEAPPLE) {
            @Override
            protected void onCollisionBegin(Entity player3, Entity pineapple) {

                //increasing player score when collide with fruit
                inc("player3score", +1);
                var scores = "SCORES," + geti("player1score") + "," + geti("player2score")+ "," + geti("player3score");
                server.broadcast(scores);
                server.broadcast("PLAY_SOUND," + "fruitCollision");

                //remove collected fruit from screen
                getGameWorld().removeEntity(pineapple);
                if (geti("player3score") > 1 ){
                    p3Score = true;
                }
            }

        });

//end game collision

        if (p1Score && p2Score && p3Score) {


            getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.PLAYER_BAT, EntityType.END) {
                @Override
                protected void onCollisionBegin(Entity player1, Entity endPoint) {
                    boolean p1 = true;
                    server.broadcast("PLAY_SOUND,"+ "playerEnd");

                }


            });
            getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.ENEMY_BAT, EntityType.END) {
                @Override
                protected void onCollisionBegin(Entity player2, Entity endPoint) {
                    boolean p2 = true;
                    server.broadcast("PLAY_SOUND," + "playerEnd");
                }

            });
            getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.ENEMY_BAT, EntityType.END) {
                @Override
                protected void onCollisionBegin(Entity player3, Entity endPoint) {
                    boolean p3 = true;
                    server.broadcast("PLAY_SOUND," + "playerEnd");
                }

            });

        }

        if (p1 && p2 &&  p3){

            getGameWorld().removeEntity(endPoint);
            getGameWorld().addEntity(endPoint);

            server.broadcast("PLAY_SOUND,win");
            //end game
            getGameController().exit();

        }

    }

    @Override
    protected void initUI() {
        MainUIController controller = new MainUIController();
        UI ui = getAssetLoader().loadUI("main.fxml", controller);

        controller.getLabelScorePlayer().textProperty().bind(getip("player1score").asString());
        controller.getLabelScoreEnemy().textProperty().bind(getip("player2score").asString());
        controller.getLabelScoreEnemy().textProperty().bind(getip("player3score").asString());

        getGameScene().addUI(ui);
    }


    boolean dead=false;
    protected void resetPlayer(){
        if(!dead){
            dead=true;
        }

    }

    @Override
    protected void onUpdate(double tpf) {
        if(dead){

            player1Bat.physics.overwritePosition(new Point2D(400, 500));
            player2Bat.physics.overwritePosition(new Point2D(350, 500));
            player3Bat.physics.overwritePosition(new Point2D(300, 500));
            dead=false;

            server.broadcast("PLAY_SOUND," + "spikeCollision");
        }

        if (!server.getConnections().isEmpty()) {
            var message = "GAME_DATA," + player1.getY() + "," + player2.getY() + "," + player3.getY() + "," + player1.getX() + "," + player2.getX() + "," + player3.getX() ;

            server.broadcast(message);


        }
    }

    private void initScreenBounds() {
        Entity walls = entityBuilder()
                .type(EntityType.WALL)
                .collidable()
                .buildScreenBounds(150);

        getGameWorld().addEntity(walls);
    }

    private void initGameObjects() {
        //ball = spawn("ball", getAppWidth() / 2 - 5, getAppHeight() / 2 - 5);
        player1 = spawn("bat", new SpawnData(getAppWidth() / 4 + 200, getAppHeight() / 2 + 200).put("isPlayer", true));
        player2 = spawn("bat", new SpawnData(getAppWidth() / 4 + 50, getAppHeight() / 2 + 200).put("isPlayer", false));
        player3 = spawn("bat", new SpawnData(getAppWidth() / 4 - 70, getAppHeight() / 2 + 200).put("isPlayer", false));

        spikes = spawn("spikes");
        strawberry = spawn("strawberry");
        cherries = spawn("cherries");
        pineapple = spawn("pineapple");
        endPoint = spawn("end");

        //block = spawn("blocks", new SpawnData(getAppWidth() / 4, getAppHeight() / 2 - 30).put("isBlock", true));
        int[][] leveldata =

                {{ 0, 0,	0,	0,	0,	0,	0,	0,	0,	0,	0,	1,	0,	0,	0,	0,	0,	0,	0,	0,	0,	0,	0,	0,	0},
                        {0,	0,	0,	0,	0,	0,	0,	0,	0,	0,	0,	1,	0,	0,	0,	0,	0,	0,	0,	0,	0,	0,	6,	0,	0},
                        {0,	0,	0,	0,	0,	0,	0,	0,	0,	4,	1,	1,	0,	0,	0,	0,	0,	0,	0,	0,	0,	1,	1,	1,	0},
                        {0,	1,	1,	0,	0,	0,	0,	0,	0,	1,	0,	1,	1,	1,	1,	0,	0,	0,	0,	0,	0,	0,	0,	0,	0},
                        {0,	0,	0,	1,	0,	0,	0,	3,	1,	0,	0,	1,	0,	0,	0,	0,	0,	0,	0,	0,	0,	0,	0,	0,	0},
                        {0,	0,	0,	0,	0,	0,	0,	1,	0,	0,	0,	1,	0,	0,	0,	0,	0,	0,	0,	1,	1,	1,	0,	0,	0},
                        {0,	0,	0,	0,	0,	0,	1,	0,	0,	0,	0,	1,	0,	0,	0,	0,	0,	0,	0,	0,	0,	0,	0,	0,	0},
                        {0,	0,	0,	1,	0,	1,	1,	0,	0,	0,	0,	1,	0,	0,	0,	0,	1,	0,	0,	0,	0,	0,	0,	0,	0},
                        {1,	0,	0,	0,	0,	0,	0,	0,	0,	0,	0,	1,	1,	2,	0,	0,	0,	0,	0,	0,	0,	0,	0,	0,	0},
                        {0,	0,	0,	0,	0,	0,	0,	0,	0,	0,	0,	1,	0,	1,	0,	0,	0,	0,	0,	0,	0,	0,	0,	0,	0},
                        {0,	0,	1,	0,	0,	0,	0,	0,	0,	0,	2,	1,	0,	0,	1,	1,	0,	0,	0,	0,	0,	0,	0,	0,	0},
                        {1,	1,	0,	0,	0,	0,	1,	1,	1,	1,	1,	1,	0,	0,	0,	0,	0,	0,	1,	0,	0,	0,	0,	0,	0},
                        {0,	0,	0,	0,	0,	1,	0,	0,	0,	0,	0,	1,	0,	0,	0,	0,	0,	0,	0,	0,	1,	1,	0,	0,	0},
                        {0,	0,	0,	0,	0,	0,	0,	0,	0,	0,	0,	1,	0,	0,	0,	5,	0,	0,	0,	0,	0,	0,	0,	0,	0},
                        {1,	1,	1,	0,	0,	0,	0,	0,	0,	0,	0,	1,	0,	1,	1,	1,	1,	1,	0,	0,	0,	0,	0,	0,	0},
                        {0,	0,	0,	1,	0,	0,	0,	0,	0,	0,	0,	1,	0,	0,	0,	0,	0,	0,	0,	0,	0,	0,	0,	0,	0},
                        {0,	0,	0,	0,	1,	1,	1,	0,	0,	0,	0,	1,	0,	0,	3,	0,	0,	0,	0,	0,	0,	0,	0,	0,	0},
                        {0,	0,	0,	0,	0,	0,	0,	0,	0,	1,	1,	1,	0,	0,	1,	0,	0,	1,	1,	1,	0,	0,	0,	0,	0},
                        {0,	0,	0,	0,	0,	0,	0,	0,	0,	0,	0,	0,	0,	0,	0,	0,	0,	0,	1,	0,	0,	0,	0,	1,	0},
                        {0,	0,	0,	0,	0,	0,	0,	0,	0,	0,	0,	0,	0,	0,	0,	0,	0,	2,	1,	2,	2,	2,	2,	1,	0},
                        {2,	2,	2,	1,	1,	1,	1,	1,	1,	1,	1,	1,	1,	1,	1,	1,	1,	1,	1,	1,	1,	1,	1,	1,	1},
                        {1,	1,	1,	1,	1,	0,	1,	1,	1,	0,	0,	1,	0,	0,	1,	1,	1,	0,	0,	0,	0,	0,	0,	0,	0}};

        for (int y = 0; y < 25; y++) {
            for (int x = 0; x < 22; x++) {
                int Tile = leveldata[x][y];
                if (Tile == 1) {
                    spawn("box",new SpawnData(y*28,x*28));
                }
                if (Tile == 2) {
                    spawn("spikes",new SpawnData(y*28,x*28));
                }
                if (Tile == 3) {
                    spawn("strawberry",new SpawnData(y*28,x*28));
                }
                if (Tile == 4) {
                    spawn("cherries",new SpawnData(y*28,x*28));
                }
                if (Tile == 5) {
                    spawn("pineapple",new SpawnData(y*28,x*28));
                }
                if (Tile == 6) {
                    spawn("end",new SpawnData(y*28,x*28));
                }

            } ;

        };

        player1Bat = player1.getComponent(BatComponent.class);
        player2Bat = player2.getComponent(BatComponent.class);
        player3Bat = player3.getComponent(BatComponent.class);
    }

    private void playHitAnimation(Entity bat) {
        animationBuilder()
                .autoReverse(true)
                .duration(Duration.seconds(0.5))
                .interpolator(Interpolators.BOUNCE.EASE_OUT())
                .rotate(bat)
                .from(FXGLMath.random(-25, 25))
                .to(0);
    }

    // array of keys
    String[] UpKeys={"W","I","T"};
    String[] DownKeys={"S","K","G"};
    String[] LeftKeys={"A","J","F"};
    String[] RightKeys={"D","L","H"};
    @Override
    public void onReceive(Connection<String> connection, String message) {

        int ID=connection.getLocalSessionData().getInt("PlayerID");

        //Player ID not assigned
        if(ID==-1){
            return;
        }

        var tokens = message.split(",");

        Arrays.stream(tokens).skip(1).forEach(key -> {

            String adjustedKey="";
            switch (key.substring(0, 1)){

                //changes key based on players ID
                case "W":
                    adjustedKey = UpKeys[ID];
                    break;
                case "S":
                    adjustedKey = DownKeys[ID];
                    break;
                case "A":
                    adjustedKey = LeftKeys[ID];
                    break;
                case "D":
                    adjustedKey = RightKeys[ID];
                    break;



            }
            if (key.endsWith("_DOWN")) {
                getInput().mockKeyPress(KeyCode.valueOf(adjustedKey));
            } else if (key.endsWith("_UP")) {
                getInput().mockKeyRelease(KeyCode.valueOf(adjustedKey));
            }
        });
    }

    static class MessageWriterS implements TCPMessageWriter<String> {

        private OutputStream os;
        private PrintWriter out;

        MessageWriterS(OutputStream os) {
            this.os = os;
            out = new PrintWriter(os, true);
        }

        @Override
        public void write(String s) throws Exception {
            out.print(s.toCharArray());
            out.flush();
        }
    }

    static class MessageReaderS implements TCPMessageReader<String> {

        private BlockingQueue<String> messages = new ArrayBlockingQueue<>(50);

        private InputStreamReader in;

        MessageReaderS(InputStream is) {
            in =  new InputStreamReader(is);

            var t = new Thread(() -> {
                try {

                    char[] buf = new char[36];

                    int len;

                    while ((len = in.read(buf)) > 0) {
                        var message = new String(Arrays.copyOf(buf, len));

                        System.out.println("Recv message: " + message);

                        messages.put(message);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            t.setDaemon(true);
            t.start();
        }

        @Override
        public String read() throws Exception {
            return messages.take();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
