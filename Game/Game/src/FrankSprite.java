import framework.sprite;
import image.ImagesLoader;
import image.ImagesPlayer;
import java.awt.*;

public class FrankSprite extends sprite{
    private static double cycleTime = .5;
    public int dimX;
    public int dimY;
    private int animPeriod;
    public int moveSize;
    public boolean still = true;
    public boolean moveLeft = false; 
    public boolean moveRight = false;
    public boolean darkness = false;
    public int backgroundMove = 0; // 0 not moving, 1 left, 2 right
    public boolean isAttacking = false;
    public boolean isDead = false;
    public int charge; // Maximum overcharge = 10, might increase with gameplay
    public int maxCharge = 10;
    public int posX = 0;
    public int posY = 0;
    public int attRange;
    public ImagesLoader imsLoad;
    
    public FrankSprite(int w, int h, int stride, ImagesLoader loader, int p, int x, int y) 
    {
        super((int)(x*.03), (int)(y*.76), w, h, loader, "Frank_Right");
        posX = (int)(x*.03);
        posY = (int)(y*.76);
        imsLoad = loader;
        moveSize = stride;
        animPeriod = p;
        still = true;
        darkness = false;
        setImage("Frank_Right");
        loopImage(animPeriod, cycleTime);
    }

    public void left()
    {
        if(moveLeft == false)
        {
            loopImage(animPeriod, cycleTime);
            setImage("Frank_Left");
            loopImage(animPeriod, cycleTime);
        }
        still = false;
        moveRight = false;
        moveLeft = true;
    }

    public void still()
    {
        stopLooping();
        moveLeft = false;
        moveRight = false;
        still = true;
    }

    public void right()
    {
        if(moveRight == false)
        {
            loopImage(animPeriod, cycleTime);
            setImage("Frank_Right");
            loopImage(animPeriod, cycleTime);
        }
        still = false;
        moveLeft = false;
        moveRight = true;
    }

    public void attack()
    {
        // Play animation for punches/kicks
        if(!isAttacking)
        {
            loopImage(animPeriod, cycleTime);
            isAttacking = true;
            if(darkness)
            {
                // setImage("Dark_Attack");
                // loopImage(animPeriod, cycleTime);
                player = new ImagesPlayer("Dark_Attack", animPeriod, cycleTime, false, imsLoad);
                charge++;
            } else {
                // setImage("Frank_Attack");
                // loopImage(animPeriod, cycleTime);
                player = new ImagesPlayer("Frank_Attack", animPeriod, cycleTime, false, imsLoad);
            }
            if(charge >= 10)
                overloaded();
            moveRight = false;
            moveLeft = false;
        }   
    }

    public void overload()
    {
        // Create animations that make Frank have more and more miasma as he uses
        // abilities. When he's full of it, he gets consumed/dragged down by shadow
        // hands.
        loopImage(animPeriod, cycleTime);
        if (!darkness)
        {
            darkness = true;
            if(charge < 3)
                player = new ImagesPlayer("Dark_Frank", animPeriod, cycleTime, false, imsLoad);
            else if(charge < 6)
                player = new ImagesPlayer("Dark_Frank_2", animPeriod, cycleTime, false, imsLoad);
            else if(charge < 8)
                player = new ImagesPlayer("Dark_Frank_3", animPeriod, cycleTime, false, imsLoad);
        }
        else  
        {
            darkness = false;
            still();
        }
    }

    public void overloaded()
    {
        charge = 10;
        player = new ImagesPlayer("Consumed", animPeriod, cycleTime, false, imsLoad);
    }
    
    @Override
    public void updateSprite()
    {
        if(!still && !darkness)
        {
            if(moveLeft && posX > 0 && backgroundMove == 0)
            {
                posX -= moveSize;
            }
            else if(moveRight && posX < dimX - 128 && backgroundMove == 0)
            {
                posX += moveSize;
            }
            super.locx = posX;
            player.updateTick();
        } 
        if (darkness || isAttacking) {
            player.updateTick();
            if(player.atSequenceEnd())
            {
                if(charge >= 10)
                    isDead = true;
            }
        }
    }
}