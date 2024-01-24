import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Random;
import javax.sound.sampled.*;
import javax.swing.*;
public class Game extends JPanel implements ActionListener, KeyListener{

    private class Tile{
        int x;
        int y;

        Tile(int x, int y){
            this.x = x;
            this.y = y;
        }
    }
    int boardWidth;
    int boardHeight;
    int tileSize = 25;

    Tile snakeHead;
    ArrayList<Tile> snakeBody;

    Tile food;
    Random random;

    Timer gameLoop;
    int velocityX;
    int velocityY;

    boolean gameOver = false;



    Game(int boardWidth, int boardHeight){

        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;
        setPreferredSize(new Dimension(this.boardWidth, this.boardHeight));
        setBackground(Color.BLACK);
        addKeyListener(this);
        setFocusable(true);

        snakeHead = new Tile(5,5);
        snakeBody = new ArrayList<Tile>();

        food = new Tile(10,10);
        random = new Random();
        placeFood();

        velocityX = 0;
        velocityY = 1;

        gameLoop = new Timer(100, this);
        gameLoop.start();


    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g){

        g.setColor(Color.red);
        g.fill3DRect(food.x * tileSize, food.y * tileSize, tileSize, tileSize, true);

        g.setColor(Color.green);
        g.fill3DRect(snakeHead.x * tileSize, snakeHead.y * tileSize, tileSize, tileSize, true);

        for(int i = 0; i < snakeBody.size(); i++) {
            Tile snakePart = snakeBody.get(i);
            g.fill3DRect(snakePart.x * tileSize, snakePart.y * tileSize, tileSize, tileSize, true);
        }
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        if(gameOver){
            g.setColor(Color.red);
            g.drawString("Game Over: " + String.valueOf(snakeBody.size()), tileSize-16, tileSize);
        }else{
            g.drawString("Score: " + String.valueOf(snakeBody.size()),tileSize-16, tileSize);
        }
    }

    public void placeFood(){
        do {
            food.x = random.nextInt(boardWidth / tileSize);
            food.y = random.nextInt(boardHeight / tileSize);
        } while (isSnakeAt(food.x, food.y));
    }

    private boolean isSnakeAt(int x, int y) {
        if(collision(snakeHead, new Tile(x, y))){
            return true;
        }

        for(int i=0; i<snakeBody.size(); i++){
            Tile snakePart = snakeBody.get(i);

            if(collision(snakePart, new Tile(x, y))){
                return true;
            }
        }

        return false;
    }


    public boolean collision(Tile tile1, Tile tile2){
        return tile1.x == tile2.x && tile1.y == tile2.y;
    }

    public void playEatSound(){
        try{
            File soundFile = new File("click.wav");
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.start();
        }catch (UnsupportedAudioFileException | IOException | LineUnavailableException e){
            e.printStackTrace();
        }
    }


    public void move(){

        if(collision(snakeHead, food)){
            snakeBody.add(new Tile(food.x, food.y));

            placeFood();

            playEatSound();

        }

        for(int i=snakeBody.size()-1; i>=0; i--){
            Tile snakePart = snakeBody.get(i);
            if(i == 0){
                snakePart.x = snakeHead.x;
                snakePart.y = snakeHead.y;
            }else{
                Tile prevSnakePart = snakeBody.get(i-1);
                snakePart.x = prevSnakePart.x;
                snakePart.y = prevSnakePart.y;
            }
        }

        snakeHead.x += velocityX;
        snakeHead.y += velocityY;

        for(int i=0; i<snakeBody.size(); i++){
            Tile snakePart = snakeBody.get(i);
            if(collision(snakeHead, snakePart)){
                gameOver = true;
            }
        }

        if (snakeHead.x < 0) {
            snakeHead.x = boardWidth / tileSize - 1;
        } else if (snakeHead.x >= boardWidth / tileSize) {
            snakeHead.x = 0;
        }

        if (snakeHead.y < 0) {
            snakeHead.y = boardHeight / tileSize - 1;
        } else if (snakeHead.y >= boardHeight / tileSize) {
            snakeHead.y = 0;
        }

    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        move();
        repaint();

        if(gameOver){
            gameLoop.stop();
        }
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        if(keyEvent.getKeyCode() == KeyEvent.VK_W && velocityY != 1){
            velocityX = 0;
            velocityY = -1;
        }
        else if(keyEvent.getKeyCode() == KeyEvent.VK_S && velocityY != -1){
            velocityX = 0;
            velocityY = 1;
        }
        else if(keyEvent.getKeyCode() == KeyEvent.VK_A && velocityX != 1){
            velocityX = -1;
            velocityY = 0;
        }
        else if(keyEvent.getKeyCode() == KeyEvent.VK_D && velocityX != -1){
            velocityX = 1;
            velocityY = 0;
        }
    }

    @Override
    public void keyTyped(KeyEvent keyEvent) {}
    @Override
    public void keyReleased(KeyEvent keyEvent) {}

}
