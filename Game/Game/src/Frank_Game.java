import javax.swing.*;

import java.awt.*;
import java.awt.event.*;

public class Frank_Game extends JFrame implements WindowListener
{
    private static int fps = 30;
    private GameFrame gf;
    GraphicsEnvironment gEnv = GraphicsEnvironment.getLocalGraphicsEnvironment();
    GraphicsDevice gDev = gEnv.getDefaultScreenDevice();

    public static void main(String[] args)
    {
        long period = (long) 1000.0/fps;
        new Frank_Game(period*1000000L);
    }

    public Frank_Game(long period)
    {   
        super("Sir Frank");
        if(gDev.isFullScreenSupported())
        {
            setUndecorated(true);
            gDev.setFullScreenWindow(this);
        }
        Container c = getContentPane();
        gf = new GameFrame(this, period, getBounds().width, getBounds().height);
        c.add(gf, "Center");
        addWindowListener(this);
        pack();
        setResizable(false);
        setVisible(true);
    }

    public void windowActivated(WindowEvent e)
    {}

    public void windowDeactivated(WindowEvent e)
    { System.exit(0); }

    public void windowDeiconified(WindowEvent e)
    { gf.pauseGame(); }

    public void windowIconified(WindowEvent e)
    { gf.resumeGame(); }

    public void windowClosing(WindowEvent e)
    {}

    public void windowOpened(WindowEvent e)
    {}

    public void windowClosed(WindowEvent e)
    {}
}