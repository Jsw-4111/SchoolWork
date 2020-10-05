import java.util.Random;

import framework.*;
import image.*;

public class EnemySprite extends sprite 
{
    private static double cycleTime = .5;
    public boolean left;
    public boolean right;
    public boolean still;
    public boolean isDead = false;
    private int animPeriod;
    private int moveSize;
    public int posX = 0;
    public int posY = 0;
    public int dimX;
    public int dimY;
    public int attackDimX, attackDimY = 0;
    public Random rand = new Random();
    public int backgroundMove = 0; // 0 not moving, 1 left, 2 right

    public EnemySprite(int w, int h, int stride, ImagesLoader loader, int p, int x, int y)
    {   super((int) (x*1.5), (int) (y*.76), w, h, loader, "Enemy_L");
        posX = (int)(x*2 + rand.nextInt(500));
        posY = (int)(y*.76);
        dimX = x;
        dimY = y;
        moveSize = stride - 2 + rand.nextInt(3);
        animPeriod = p;
        still = true;
    }

    public void still()
    {
        stopLooping();
        right = false;
        left = false;
        still = true;
    }

    public void moveLeft()
    {
        if(!left)
        {
            setImage("Enemy_L");
            loopImage(animPeriod, cycleTime);
        }
        still = false;
        right = false;
        left = true;
    }

    public void moveRight()
    {
        if(!right)
        {
            setImage("Enemy_R");
            loopImage(animPeriod, cycleTime);
        }
        still = false;
        left = false;
        right = true;
    }

    public void attack()
    {
        if(right)
            setImage("Enemy_1_Attack");
        else
            setImage("Enemy_1_AttackL");
    }

    public void updateSprite()
    {
        if(!still)
        {
            if((left && posX > 0 && backgroundMove == 0 && !isDead) || (backgroundMove == 1))
            {
                posX -= moveSize;
            }
            else if((right && posX < dimX && backgroundMove == 0 && !isDead) || (isDead && backgroundMove == 2))
            {
                posX += moveSize;
            }
            super.locx = posX;
            if (player != null)
                player.updateTick();
        }
        if(isDead)
        {
            setImage("dead");
        }
    }
}