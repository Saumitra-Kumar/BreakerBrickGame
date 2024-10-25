import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class BallBreakGame extends JPanel implements KeyListener, ActionListener {
    // Game constants
    private static final int WIDTH = 700;
    private static final int HEIGHT = 600;
    private static final int BRICK_ROWS = 3;
    private static final int BRICK_COLUMNS = 6;
    private static final int BRICK_WIDTH = 100;
    private static final int BRICK_HEIGHT = 30;
    private static final int PADDLE_WIDTH = 100;
    private static final int PADDLE_HEIGHT = 10;
    private static final int BALL_DIAMETER = 20;

    private boolean play = false;
    private int score = 0;
    private Timer timer;
    private int delay = 8;  // Speed of the game loop

    // Paddle position
    private int paddleX = WIDTH / 2 - PADDLE_WIDTH / 2;

    // Ball position and velocity
    private int ballPosX = WIDTH / 2;
    private int ballPosY = HEIGHT / 2;
    private int ballDirX = -1;
    private int ballDirY = -2;

    // Brick layout
    private BrickGenerator bricks;

    public BallBreakGame() {
        bricks = new BrickGenerator(BRICK_ROWS, BRICK_COLUMNS);
        addKeyListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        timer = new Timer(delay, this);
        timer.start();
    }

    public void paint(Graphics g) {
        // Background
        g.setColor(Color.BLACK);
        g.fillRect(1, 1, WIDTH, HEIGHT);

        // Draw bricks
        bricks.draw((Graphics2D) g);

        // Draw paddle
        g.setColor(Color.GREEN);
        g.fillRect(paddleX, HEIGHT - 50, PADDLE_WIDTH, PADDLE_HEIGHT);

        // Draw ball
        g.setColor(Color.RED);
        g.fillOval(ballPosX, ballPosY, BALL_DIAMETER, BALL_DIAMETER);

        // Draw score
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.drawString("Score: " + score, WIDTH - 150, 30);

        // Game over condition
        if (ballPosY > HEIGHT) {
            play = false;
            ballDirX = 0;
            ballDirY = 0;
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 40));
            g.drawString("Game Over, Score: " + score, 150, HEIGHT / 2);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("Press Enter to Restart", 230, HEIGHT / 2 + 50);
        }

        g.dispose();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        timer.start();

        if (play) {
            // Check for ball-paddle collision
            if (new Rectangle(ballPosX, ballPosY, BALL_DIAMETER, BALL_DIAMETER)
                    .intersects(new Rectangle(paddleX, HEIGHT - 50, PADDLE_WIDTH, PADDLE_HEIGHT))) {
                ballDirY = -ballDirY;
            }

            // Check for ball-brick collision
            A: for (int i = 0; i < bricks.bricks.length; i++) {
                for (int j = 0; j < bricks.bricks[0].length; j++) {
                    if (bricks.bricks[i][j] > 0) {
                        int brickX = j * BRICK_WIDTH + 80;
                        int brickY = i * BRICK_HEIGHT + 50;
                        int brickWidth = BRICK_WIDTH;
                        int brickHeight = BRICK_HEIGHT;

                        Rectangle brickRect = new Rectangle(brickX, brickY, brickWidth, brickHeight);
                        Rectangle ballRect = new Rectangle(ballPosX, ballPosY, BALL_DIAMETER, BALL_DIAMETER);

                        if (ballRect.intersects(brickRect)) {
                            bricks.setBrickValue(0, i, j);
                            score += 5;

                            if (ballPosX + 19 <= brickRect.x || ballPosX + 1 >= brickRect.x + brickRect.width) {
                                ballDirX = -ballDirX;
                            } else {
                                ballDirY = -ballDirY;
                            }
                            break A;
                        }
                    }
                }
            }

            // Update ball position
            ballPosX += ballDirX;
            ballPosY += ballDirY;

            // Ball wall collision (left, right, top)
            if (ballPosX < 0 || ballPosX > WIDTH - BALL_DIAMETER) {
                ballDirX = -ballDirX;
            }
            if (ballPosY < 0) {
                ballDirY = -ballDirY;
            }
        }

        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            if (paddleX >= WIDTH - PADDLE_WIDTH) {
                paddleX = WIDTH - PADDLE_WIDTH;
            } else {
                moveRight();
            }
        }
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            if (paddleX <= 0) {
                paddleX = 0;
            } else {
                moveLeft();
            }
        }
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            if (!play) {
                play = true;
                ballPosX = WIDTH / 2;
                ballPosY = HEIGHT / 2;
                ballDirX = -1;
                ballDirY = -2;
                paddleX = WIDTH / 2 - PADDLE_WIDTH / 2;
                score = 0;
                bricks = new BrickGenerator(BRICK_ROWS, BRICK_COLUMNS);
                repaint();
            }
        }
    }

    public void moveRight() {
        play = true;
        paddleX += 20;
    }

    public void moveLeft() {
        play = true;
        paddleX -= 20;
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {
        JFrame frame = new JFrame("Ball Breaker");
        BallBreakGame game = new BallBreakGame();
        frame.setBounds(10, 10, WIDTH, HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(game);
        frame.setVisible(true);
    }
}

class BrickGenerator {
    public int[][] bricks;
    private int brickWidth;
    private int brickHeight;

    public BrickGenerator(int row, int col) {
        bricks = new int[row][col];
        for (int i = 0; i < bricks.length; i++) {
            for (int j = 0; j < bricks[0].length; j++) {
                bricks[i][j] = 1;  // Indicates the brick is not broken
            }
        }
        brickWidth = 100;
        brickHeight = 30;
    }

    public void draw(Graphics2D g) {
        for (int i = 0; i < bricks.length; i++) {
            for (int j = 0; j < bricks[0].length; j++) {
                if (bricks[i][j] > 0) {
                    g.setColor(Color.WHITE);
                    g.fillRect(j * brickWidth + 80, i * brickHeight + 50, brickWidth, brickHeight);

                    // Adding a border to the bricks
                    g.setStroke(new BasicStroke(3));
                    g.setColor(Color.BLACK);
                    g.drawRect(j * brickWidth + 80, i * brickHeight + 50, brickWidth, brickHeight);
                }
            }
        }
    }

    public void setBrickValue(int value, int row, int col) {
        bricks[row][col] = value;
    }
}

