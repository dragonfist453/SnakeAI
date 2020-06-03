package application;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.shape.Line;


public class Snake extends Application {
    //Variables required for control of snake and environment
    private static int speed = 10;
    private static int foodcolor = 0;
    private static int width = 20;
    private static int height = 20;
    private static int foodX = 0;
    private static int score = -1;
    private static int foodY = 0;
    private static int cornersize = 25;
    private static List<Corner> snake = new ArrayList<>();
    private static Dir direction = Dir.left;
    private static boolean gameOver = false;
    private static Random rand = new Random();

    //Enumeration containing direction information
    public enum Dir {
        left, right, up, down
    }

    //Corner class which stores positions of food or snake
    public static class Corner {
        int x;
        int y;

        private Corner(int x, int y) {
            this.x = x;
            this.y = y;
        }

    }

    //Start of game
    public void start(Stage primaryStage) {
        try {
            newFood(); //Make new food every time snake is called (which is when food is eaten)

            VBox root = new VBox(); //Vertical box to contain canvas
            Canvas c = new Canvas(width * cornersize, height * cornersize); //Canvas which has the snake game
            GraphicsContext gc = c.getGraphicsContext2D(); //Getting graphics of canvas to make snake on

            root.getChildren().add(c); //Adding canvas to vertical box

            //Timer handles the snake logic synchronously
            new AnimationTimer() {
                long lastTick = 0;
                public void handle(long now) {
                    //Game start function
                    if (lastTick == 0) {
                        lastTick = now;
                        tick(gc);
                        return;
                    }

                    //Rest of the game. Ticks for every 0.1 s. now is in nano seconds, so it reduces to seconds using the scale
                    if (now - lastTick > 1000000000 / speed) {
                        lastTick = now;
                        tick(gc);
                    }
                }
            }.start();

            //Define scene of game to run in JavaFX
            Scene scene = new Scene(root, width * cornersize, height * cornersize);

            //Control of game according to key presses. Modifies direction as per press of key.
            scene.addEventFilter(KeyEvent.KEY_PRESSED, key -> {
                if (key.getCode() == KeyCode.W || key.getCode() == KeyCode.UP) {
                    direction = Dir.up;
                }
                if (key.getCode() == KeyCode.A || key.getCode() == KeyCode.LEFT) {
                    direction = Dir.left;
                }
                if (key.getCode() == KeyCode.S || key.getCode() == KeyCode.DOWN) {
                    direction = Dir.down;
                }
                if (key.getCode() == KeyCode.D || key.getCode() == KeyCode.RIGHT) {
                    direction = Dir.right;
                }

            });

            //Adds snake starting parts
            snake.add(new Corner(width / 2, height / 2));
            snake.add(new Corner(width / 2, height / 2));
            snake.add(new Corner(width / 2, height / 2));

            primaryStage.setScene(scene);
            primaryStage.setTitle("Snake");
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Logic for each tick in AnimationTimer
    private static void tick(GraphicsContext gc) {
        //Game over check
        if (gameOver) {
            gc.setFill(Color.RED);
            gc.setFont(new Font("", 50));
            gc.fillText("GAME OVER", 100, 250);
            return;
        }

        //Movement of snake in either x or y position for every tick
        for (int i = snake.size() - 1; i >= 1; i--) {
            snake.get(i).x = snake.get(i - 1).x;
            snake.get(i).y = snake.get(i - 1).y;
        }

        //Based on direction by arrow key, arraylist is updated
        switch (direction) {
            case up:
                //Decrementing y axis moves up
                snake.get(0).y--;
                if (snake.get(0).y < 2) {
                    gameOver = true;
                }
                break;
            case down:
                //Incrementing y axis moves down
                snake.get(0).y++;
                if (snake.get(0).y > height) {
                    gameOver = true;
                }
                break;
            case left:
                //Decrementing x axis moves left
                snake.get(0).x--;
                if (snake.get(0).x < 0) {
                    gameOver = true;
                }
                break;
            case right:
                //Incrementing x axis moves right
                snake.get(0).x++;
                if (snake.get(0).x > width) {
                    gameOver = true;
                }
                break;
        }

        //If snake eats food, make more food and increase snake length
        if (foodX == snake.get(0).x && foodY == snake.get(0).y) {
            snake.add(new Corner(-1, -1));
            newFood();
        }

        //If snake eats itself, game is over.
        for (int i = 1; i < snake.size(); i++) {
            if (snake.get(0).x == snake.get(i).x && snake.get(0).y == snake.get(i).y) {
                gameOver = true;
                break;
            }
        }

        //Make background black
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, width * cornersize, height * cornersize);

        //Set borders
        gc.setStroke(Color.RED);
        gc.setLineWidth(2);
        gc.strokeLine(0, 40, 500, 40);
        gc.setLineWidth(5);
        gc.strokeLine(0, 40, 0, 500);
        gc.strokeLine(500, 40, 500, 500);
        gc.strokeLine(0, 500, 500, 500);
        gc.setLineWidth(5);
        gc.setStroke(Color.BLUE);
        gc.strokeLine(0, 0, 0, 37);
        gc.strokeLine(500, 0, 500, 37);
        gc.strokeLine(0, 0, 500, 0);

        //Score is displayed in top left
        gc.setFill(Color.WHITE);
        gc.setFont(new Font("", 30));
        gc.fillText("Score: " + score, 200, 30);

        //Assign food colour according to what is updated in newfood()
        Color cc = Color.WHITE;

        switch (foodcolor) {
            case 0:
                cc = Color.PURPLE;
                break;
            case 1:
                cc = Color.LIGHTBLUE;
                break;
            case 2:
                cc = Color.YELLOW;
                break;
            case 3:
                cc = Color.PINK;
                break;
            case 4:
                cc = Color.ORANGE;
                break;
        }
        //Oval food
        gc.setFill(cc);
        gc.fillOval(foodX * cornersize, foodY * cornersize, cornersize, cornersize);

        //Build snake for every Corner object in snake ArrayList
        for (Corner c : snake) {
            gc.setFill(Color.LIGHTGREEN);
            gc.fillRect(c.x * cornersize, c.y * cornersize, cornersize - 1, cornersize - 1);
            gc.setFill(Color.GREEN);
            gc.fillRect(c.x * cornersize, c.y * cornersize, cornersize - 2, cornersize - 2);
        }
    }

    //Make new food with some error handling out of experience :)
    private static void newFood() {
        start: while (true) {
            foodX = rand.nextInt(width);
            foodY = rand.nextInt(height-2) + 2;

            for (Corner c : snake) {
                if (c.x == foodX && c.y == foodY) {
                    continue start;
                }
            }
            foodcolor = rand.nextInt(5);
            score++;
            break;
        }
    }

    //Main function launches Snake class for execution
    public static void main(String[] args) {
        launch(args);
    }
}