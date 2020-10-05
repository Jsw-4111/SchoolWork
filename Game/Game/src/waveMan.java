import java.util.ArrayList;
import java.awt.Graphics;

import image.ImagesLoader;
import sound.ClipsLoader;

public class waveMan {
    ArrayList<EnemySprite> enemies;
    public boolean gamestart = false;
    public boolean waveClear = false;
    private int w, h, stride, period, dimX, dimY;
    private ImagesLoader loader;
    public int timer;
    public long nextSpawn;
    public int enemiesLeft = 0;

    public waveMan(int width, int height, int s, ImagesLoader load, int p, int x, int y) {
        w = width;
        h = height;
        stride = s;
        period = p;
        dimX = x;
        dimY = y;
        loader = load;
        enemies = new ArrayList<EnemySprite>();
    }

    public void gameStart() {
        gamestart = true;
        timer = 300;

        for(int i = 0; i < 5; i++)
        {
            enemies.add(new EnemySprite(w, h, stride, loader, period, dimX, dimY));
            enemiesLeft++;
            nextSpawn = System.currentTimeMillis() + timer;
        }
    }

    public void nextWave(int waveNum, int waveCycle)
    {
        waveClear = false;
        enemies.clear();
        enemiesLeft = 0;
            for(int i = 0; i < 5 + 5*waveNum; i++)
            {
                enemies.add(new EnemySprite(w, h, stride, loader, period, dimX, dimY));
                enemiesLeft++;
                nextSpawn = System.currentTimeMillis() + timer;
            } 
    }
    public int kill(int x)
    {
        int scoresum = 0;
        for(EnemySprite i : enemies)
        {
            if (i.getXPosn() - x < .0675 * dimX && !i.isDead)
            {
                i.isDead = true;
                enemiesLeft--;
                scoresum++;
                break;
            }
        }
        if(enemiesLeft <= 0)
            waveClear = true;
        return scoresum;
    }

    public boolean attack(int x, ClipsLoader bgm, int fwidth)
    {
        for(EnemySprite i : enemies)
        {
            if((i.getXPosn() + i.getWidth()*.1 >= x + (int)(.367*fwidth)) && (i.getXPosn() <= x + (int)(.6*fwidth)) && !i.isDead)
            {
                bgm.play("Thwack!", false);
                i.attack();
                return true;
            }
        }
        return false;
    }

    public void draw(Graphics dbg)
    {
        for(EnemySprite i : enemies)
        {
            i.drawSprite(dbg);
        }
    }

    public void move(int x, int backgroundMove)
    {
        for(EnemySprite i:enemies)
        {
            i.backgroundMove = backgroundMove;
            if(i.getXPosn() > x && !i.isDead)
            {
                i.moveLeft();
            }
            else if(!i.isDead)
            {
                i.moveRight();
            }
        }
    }

    public void updateSprite()
    {
        if(enemies.size() > 0)
            for(EnemySprite i : enemies)
            {
                i.updateSprite();
            }
    }

    public int sweep(int x)
    {
        int scoresum = 0;
        for(EnemySprite i : enemies)
        {
            if(i.getXPosn() > x && i.getXPosn() < x + 64)
            {
                if(!i.isDead)
                {
                    System.out.println("Enemy " + i + " died");
                    i.isDead = true;
                    enemiesLeft--;
                    scoresum++;
                }
            }
        }
        if(enemiesLeft <= 0)
        {
            waveClear = true;
        }
        return scoresum;
    }
}