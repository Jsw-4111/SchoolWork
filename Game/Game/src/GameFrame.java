// Sir Frank Gameframe, adapted code from Andrew Davison's JackPanel.java

// JackPanel.java
// Andrew Davison, April 2005, ad@fivedots.coe.psu.ac.th

/* The game's drawing surface. Uses active rendering to a JPanel
   with the help of Java 3D's timer.

   Set up the background and sprites, and update and draw
   them every period nanosecs.

   The background is a series of ribbons (wraparound images
   that move), and a bricks ribbon which the JumpingSprite
   (called 'jack') runs and jumps along.

   'Jack' doesn't actually move horizontally, but the movement
   of the background gives the illusion that it is.

   There is a fireball sprite which tries to hit jack. It shoots
   out horizontally from the right hand edge of the panel. After
   MAX_HITS hits, the game is over. Each hit is accompanied 
   by an animated explosion and sound effect.

   The game begins with a simple introductory screen, which
   doubles as a help window during the course of play. When
   the help is shown, the game pauses.

   The game is controlled only from the keyboard, no mouse
   events are caught.
*/

import image.ImagesLoader;
import image.ImagesPlayer;
import image.ImagesPlayerWatcher;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;
import javax.swing.event.MouseInputAdapter;

import framework.sprite;
import sound.ClipsLoader;

public class GameFrame extends JPanel implements Runnable, ImagesPlayerWatcher{
    private static int PWIDTH; // size of panel
    private static int PHEIGHT;

    private static final int NO_DELAYS_PER_YIELD = 16;
    private boolean inTitle = false;
    private boolean gameStart = false;
    private boolean showCreds = false;
    public int mapOffset = 0;
    /*
     * Number of frames with a delay of 0 ms before the animation thread yields to
     * other running threads.
     */
    private static final int MAX_FRAME_SKIPS = 5;
    // no. of frames that can be skipped in any one animation loop
    // i.e the games state is updated but not rendered

    private static final String IMS_INFO = "imsInfo.txt";

    private Thread animator; // the thread that performs the animation
    private volatile boolean running = false; // used to stop the animation thread
    private volatile boolean isPaused = false;

    private long period; // period between drawing in _nanosecs_
    private int waveNum;

    private Frank_Game fGame;
    private FrankSprite frank;
    private sprite projectile;
    private waveMan waveManager;
    private ClipsLoader bgm;
    private ClipsLoader sfx;
    public Image background;
    private long attackStart, slashStart;
    private double attackCD;
    private boolean slash = false;

    // used at game termination
    private volatile boolean gameOver = false;
    private int score = 0;

    // for displaying messages
    private Font msgsFont;
    private FontMetrics metrics;

    // off-screen rendering
    private Graphics dbg;
    private Image dbImage = null;

    // to display the title/help screen
    private boolean showHelp;
    private BufferedImage helpIm;
    private BufferedImage credIm;
    private ImagesLoader imsLoader;

    public GameFrame(Frank_Game game, long period, int width, int height) {
        fGame = game;
        this.period = period;
        PWIDTH = width;
        PHEIGHT = height;

        setDoubleBuffered(false);
        setBackground(Color.white);
        setPreferredSize(new Dimension(PWIDTH, PHEIGHT));

        setFocusable(true);
        requestFocus(); // the JPanel now has focus, so receives key events

        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                processKey(e);
            }

            public void keyReleased(KeyEvent e)
            {
                release(e);
            }
        });

        addMouseListener(new MouseInputAdapter() {
            public void mouseClicked(MouseEvent e) {
                requestFocusInWindow();
                if(inTitle)
                    title(e);
            }
        });

        // initialise the loaders
        imsLoader = new ImagesLoader(IMS_INFO);
        sfx = new ClipsLoader("clipsInfo.txt");
        bgm = new ClipsLoader("clipsInfo.txt");
        bgm.play("Fernweh", true);
        background = imsLoader.getImage("Back_Castle");

        // initialise the game entities
        frank = new FrankSprite((int).07*PWIDTH, (int).12*PHEIGHT, 5, imsLoader, 
                                (int)(period/1000000L), PWIDTH, PHEIGHT);
        frank.dimX = PWIDTH;
        frank.dimY = PHEIGHT;
        waveManager = new waveMan((int).07*PWIDTH, (int).12*PHEIGHT,
                                5, imsLoader, (int)(period/1000000L), PWIDTH, PHEIGHT);
        attackCD = 100; // in milliseconds
        
        // prepare title/help screen
        helpIm = imsLoader.getImage("Title");
        credIm = imsLoader.getImage("Creds");
        inTitle = true;
        isPaused = true;

        // set up message font
        msgsFont = new Font("SansSerif", Font.BOLD, 24);
        metrics = this.getFontMetrics(msgsFont);
    } // end of JackPanel()

    private void title(MouseEvent e)
    {
        // x = [1050, 1240] y = [520, 570] => start
        // x = [1085, 1240] y = [590, 630] => exit
        if(e.getX() >= .82*PWIDTH && e.getX() <= .97*PWIDTH)
        {
            if(e.getY() >= .73*PHEIGHT && e.getY() <= .8*PHEIGHT)
            {
                if(gameStart == false)
                {
                    bgm.stop("Fernweh");
                    bgm.play("And_This_Is_How_It_Ends", true);
                    gameStart = true;
                }
                isPaused = false;
                inTitle = false;
            }
            else if (e.getY() >= .82*PHEIGHT && e.getY() <= .875*PHEIGHT)
            {
                System.exit(0);
            }
        } else if (e.getX() < .133*PWIDTH && e.getY() > .9375*PHEIGHT) {
            inTitle = false;
            showCreds = true;
        }
    }

    private void release(KeyEvent e)
    {
        int keyCode = e.getKeyCode();

        if((keyCode == KeyEvent.VK_RIGHT || keyCode == KeyEvent.VK_LEFT) && !frank.darkness)
        {
            frank.still();
        }
    }

    private void processKey(KeyEvent e)
    // handles termination, help, and game-play keys
    {
        int keyCode = e.getKeyCode();

        // termination keys
        // listen for esc, q, end, ctrl-c on the canvas to
        // allow a convenient exit from the full screen configuration
        if ((keyCode == KeyEvent.VK_ESCAPE) || (keyCode == KeyEvent.VK_Q) || (keyCode == KeyEvent.VK_END)
                || ((keyCode == KeyEvent.VK_C) && e.isControlDown()))
            {
                if (inTitle)
                {
                    System.exit(0);
                }
                showCreds = false;
                isPaused = true;
                inTitle = true;
            }

        // help controls
        if (keyCode == KeyEvent.VK_H) {
            if (showHelp) { // help being shown
                showHelp = false; // switch off
                isPaused = false;
            } else { // help not being shown
                showHelp = true; // show it
                isPaused = true; // isPaused may already be true
            }
        }

        // game-play keys
        if (!isPaused && !gameOver) {
            // move the sprite and ribbons based on the arrow key pressed
            if (keyCode == KeyEvent.VK_LEFT && !frank.darkness) {
                frank.left();
            } else if (keyCode == KeyEvent.VK_RIGHT && !frank.darkness) {
                frank.right();
            } else if (keyCode == KeyEvent.VK_CONTROL) {
                if(!frank.isAttacking && !frank.darkness)
                {
                    attackStart = System.currentTimeMillis();
                    frank.attack(); 
                    bgm.play("Shing!", false);
                    score += waveManager.kill(frank.getXPosn());
                }
                else if(!frank.isAttacking && frank.darkness)
                {
                    slashStart = System.currentTimeMillis();
                    frank.attack();
                    bgm.play("Shing!", false);
                    projectile = new sprite(frank.getXPosn(), frank.getYPosn(), 64, 128, imsLoader, "projectile");
                    projectile.setStep(10, 0);
                    slash = true;
                }
            } else if (keyCode == KeyEvent.VK_SHIFT) {
                frank.overload();
                bgm.play("Thunder!", false);
            } else if (keyCode == KeyEvent.KEY_RELEASED) {
                System.out.println("Key unpress");
                frank.still();
            }
        }
    } // end of processKey()

    public void sequenceEnded(String image)
    // called by ImagesPlayer when the death animation finishes
    {
    } // end of sequenceEnded()

    public void addNotify()
    // wait for the JPanel to be added to the JFrame before starting
    {
        super.addNotify(); // creates the peer
        startGame(); // start the thread
    }

    private void startGame()
    // initialise and start the thread
    {
        if (animator == null || !running) {
            animator = new Thread(this);
            animator.start();
        }
    } // end of startGame()

    // ------------- game life cycle methods ------------
    // called by the JFrame's window listener methods

    public void resumeGame()
    // called when the JFrame is activated / deiconified
    {
        if (!showHelp) // CHANGED
            isPaused = false;
    }

    public void pauseGame()
    // called when the JFrame is deactivated / iconified
    {
        isPaused = true;
    }

    public void stopGame()
    // called when the JFrame is closing
    {
        running = false;
    }

    // ----------------------------------------------

    public void run()
    /* The frames of the animation are drawn inside the while loop. */
    {
        long beforeTime, afterTime, timeDiff, sleepTime;
        long overSleepTime = 0L;
        int noDelays = 0;
        long excess = 0L;

        beforeTime = System.nanoTime();

        running = true;

        while (running) {
            gameUpdate();
            gameRender();
            paintScreen();

            afterTime = System.nanoTime();
            timeDiff = afterTime - beforeTime;
            sleepTime = (period - timeDiff) - overSleepTime;

            if (sleepTime > 0) { // some time left in this cycle
                try {
                    Thread.sleep(sleepTime / 1000000L); // nano -> ms
                } catch (InterruptedException ex) {
                }
                overSleepTime = (System.nanoTime() - afterTime) - sleepTime;
            } else { // sleepTime <= 0; the frame took longer than the period
                excess -= sleepTime; // store excess time value
                overSleepTime = 0L;

                if (++noDelays >= NO_DELAYS_PER_YIELD) {
                    Thread.yield(); // give another thread a chance to run
                    noDelays = 0;
                }
            }

            beforeTime = System.nanoTime();

            /*
             * If frame animation is taking too long, update the game state without
             * rendering it, to get the updates/sec nearer to the required FPS.
             */
            int skips = 0;
            while ((excess > period) && (skips < MAX_FRAME_SKIPS)) {
                excess -= period;
                gameUpdate(); // update state but don't render
                skips++;
            }
        }
        System.exit(0); // so window disappears
    } // end of run()

    private void gameUpdate() {
        if (!isPaused && !gameOver)
        {
            if (!waveManager.gamestart)
                waveManager.gameStart();
            else if (waveManager.waveClear)
            {
                waveManager.nextWave(++waveNum, 0);
            }
            if(frank.getXPosn() > .5*PWIDTH && mapOffset >= -(background.getWidth(null) - PWIDTH/2) && !frank.still && frank.moveRight)
            {
                mapOffset -= frank.moveSize;
                frank.backgroundMove = 1;
            }
            else if (frank.moveLeft && (frank.getXPosn() > .3*PWIDTH && frank.getXPosn() < .5*PWIDTH) && mapOffset < 0 && !frank.still)
            {
                mapOffset += frank.moveSize;
                frank.backgroundMove = 2;
            }
            else
            {
                frank.backgroundMove = 0;
            }
            if(System.currentTimeMillis() > attackStart + attackCD)
            {
                frank.isAttacking = false;
            }
            waveManager.move(frank.getXPosn(), frank.backgroundMove);
            frank.updateSprite();
            waveManager.updateSprite();
            if(projectile != null)
                projectile.updateSprite();
            if(slash)
            {
                score += waveManager.sweep(projectile.getXPosn());
                if(System.currentTimeMillis() > slashStart + 5000)
                {
                    projectile.setActive(false);
                    slash = false;
                }
            }
            gameOver = waveManager.attack(frank.getXPosn(), bgm, frank.getWidth());
            if(frank.isDead)
            {
                gameOver = true;
            }
            
            
        }
    } // end of gameUpdate()

    private void gameRender() {
        if (dbImage == null) {
            dbImage = createImage(PWIDTH, PHEIGHT);
            if (dbImage == null) {
                System.out.println("dbImage is null");
                return;
            } else
                dbg = dbImage.getGraphics();
        }
        if(inTitle)
        {
            dbg.drawImage(helpIm.getScaledInstance(PWIDTH, PHEIGHT, 0), 0, 0, null);
        }
        else if(showCreds)
        {
            dbg.drawImage(credIm.getScaledInstance(PWIDTH, PHEIGHT, 0), 0, 0, null);
        }
        else
        {
            // draw the game elements: order is important
            
            dbg.drawImage(background.getScaledInstance(2*PWIDTH, PHEIGHT, 0), mapOffset, 0, null);
            frank.drawSprite(dbg);
            waveManager.draw(dbg);
            if(projectile != null)
                projectile.drawSprite(dbg);
            if (gameOver)
                gameOverMessage(dbg);

            if (showHelp) // draw the help at the very front (if switched on)
                dbg.drawImage(helpIm, (PWIDTH - helpIm.getWidth()) / 2, (PHEIGHT - helpIm.getHeight()) / 2, null);
        
        }
    } // end of gameRender()

    private void gameOverMessage(Graphics g)
    // Center the game-over message in the panel.
    {
        String msg = "Game Over. Your score: " + score;

        int x = (PWIDTH - metrics.stringWidth(msg)) / 2;
        int y = (PHEIGHT - metrics.getHeight()) / 2;
        g.setColor(Color.black);
        g.setFont(msgsFont);
        g.drawString(msg, x, y);
    } // end of gameOverMessage()

    private void paintScreen()
    // use active rendering to put the buffered image on-screen
    {
        Graphics g;
        try {
            g = this.getGraphics();
            if ((g != null) && (dbImage != null))
                g.drawImage(dbImage, 0, 0, null);
            // Sync the display on some systems.
            // (on Linux, this fixes event queue problems)
            Toolkit.getDefaultToolkit().sync();
            g.dispose();
        } catch (Exception e) {
            System.out.println("Graphics context error: " + e);
        }
    } // end of paintScreen()

} // end of JackPanel class
