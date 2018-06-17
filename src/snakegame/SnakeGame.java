package snakegame;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import javafx.util.Pair;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.Timer;

public final class SnakeGame extends JPanel
{
    private char direction;
    private int x,y,fx,fy;
    private final Queue<Pair<Integer,Integer>> pos;
    private boolean gameover=false;
    
    private final int sc_x=700,sc_y=800;
    private int speed=50;
    private final int size=25;
    
    private JFrame f;
    private final JLabel score;
    private int sc=0;
   
    private Timer t;
	private Clip clip;
	private long songTime;
    
    public static void main(String[] args)
    {
        new SnakeGame(0);
    }

    public SnakeGame(long sT)
    {   
		newFood();
		x=size*(new Random().nextInt(26-1+1)+1);
        y=size*(new Random().nextInt(28-3+1)+3);
		char tD[]={'U','D','L','R'};
		direction=tD[new Random().nextInt(tD.length)];
		
        pos=new LinkedList<>();
        pos.add(new Pair(x,y));
		songTime=sT;
		
        f=new JFrame();
        f.setTitle("Snake");
        f.setSize(sc_x+6,sc_y-6);
        f.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("icon.png")));
        f.setVisible(true);
        f.setResizable(false);
        f.setLocationRelativeTo(null);
        f.setAlwaysOnTop(true);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
        this.setLayout(null);
        this.setDoubleBuffered(true);
        this.setBackground(Color.WHITE);
        f.add(SnakeGame.this);
        
        Font txt=new Font("Serif", Font.BOLD,25);
        
        score=new JLabel("Score : 000000");
		score.setForeground(Color.WHITE);
        score.setBounds(270,15,300,38);
        score.setFont(txt);
        this.add(score);
        
		JLabel so=new JLabel("Sounds :");
		so.setForeground(Color.WHITE);
        so.setBounds(30,15,120,38);
        so.setFont(txt);
        this.add(so);
		
		JToggleButton sound=new JToggleButton("ON / OFF");
		sound.setFont(new Font("Serif",Font.BOLD,14));
		sound.setBounds(130,15,96,38);
		playSong();
		clip.setMicrosecondPosition(songTime);
		clip.start(); 
		sound.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				if(sound.isSelected())
				{
					songTime=clip.getMicrosecondPosition();
					clip.stop();
				}
				else
				{
					clip.setMicrosecondPosition(songTime);
					clip.start(); 
				}
				f.requestFocus();
			}
		});
		this.add(sound);
		
        JLabel sp=new JLabel("Speed");
		sp.setForeground(Color.WHITE);
        sp.setBounds(539,15,80,38);
        sp.setFont(txt);
        this.add(sp);
        
        JButton i=new JButton("+");
        i.setBounds(612,15,55,38);
        i.setFont(txt);
        i.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
				if(gameover)
					return;
                if(speed>=20)
                    speed-=5;
                t.stop();
                t=new Timer(speed,new TClass());
                t.start();
                f.requestFocus();
            }
        });
        this.add(i);
        
        JButton d=new JButton("-");
        d.setBounds(475,15,55,38);
        d.setFont(txt);
        d.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
				if(gameover)
					return;
                if(speed<=100)
                    speed+=5;
                t.stop();
                t=new Timer(speed,new TClass());
                t.start();
                f.requestFocus();
            }
        });
        this.add(d);
        
        f.addKeyListener(new KClass());
        
        t=new Timer(speed,new TClass());
        t.start();
        f.requestFocus();
	}

    @Override
    protected void paintComponent(Graphics g) 
    {
        super.paintComponent(g);
		
		g.setColor(Color.BLACK);
		g.fillRect(0,0,800,800);
		
		g.setColor(Color.WHITE);
		g.fillRect(25-6,10-6,208+12,49+12);
		g.setColor(Color.BLACK);
		g.fillRect(25,10,208,49);
		
		g.setColor(Color.WHITE);
		g.fillRect(250-6,10-6,201+12,49+12);
		g.setColor(Color.BLACK);
		g.fillRect(250,10,201,49);
		
		g.setColor(Color.WHITE);
		g.fillRect(468-6,10-6,206+12,49+12);
		g.setColor(Color.BLACK);
		g.fillRect(468,10,206,49);
		
		g.setColor(Color.WHITE);
		g.fillRect(size-6,3*size-6,sc_x-2*size+11,sc_y-6*size+11);
        g.setColor(Color.black);
		g.fillRect(size,3*size,sc_x-2*size,sc_y-6*size);
		
        renderSnake(g);
        renderFood(g);
        if(gameover)
        {
            g.setColor(Color.yellow);
            g.drawLine(x,y,x+size,y+size);
            g.drawLine(x,y+size,x+size,y);
            g.setFont(new Font("TimesRoman", Font.PLAIN,55)); 
            g.drawString("Game Over!!!",205,350);
            
            JButton b=new JButton("Play Again");
            b.setBounds(205,400,300,35);
            b.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    f.dispose();
					songTime=clip.getMicrosecondPosition();
					clip.stop();
                    new SnakeGame(songTime);
                }
            });
            this.add(b);
            
            JButton b1=new JButton("Exit Game");
            b1.setBounds(205,450,300,35);
            b1.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    System.exit(0);
                }
            });
            this.add(b1);
        }
    }
    
    private void renderSnake(Graphics g) 
    {
        if(pos!=null)
        for(Pair<Integer,Integer> p:pos)
        {
            g.setColor(Color.BLUE);
            g.fillRect(p.getKey(),p.getValue(),size,size);
            g.setColor(Color.BLACK);
            g.drawRect(p.getKey(),p.getValue(),size,size);
        }
    }
    
    private void renderFood(Graphics g) 
    {
        g.setColor(Color.red);
        g.fillRect(fx,fy,size,size);
        g.setColor(Color.yellow);
        g.drawRect(fx,fy,size,size);
    }
    
    void newFood()
    {
        fx=size*(new Random().nextInt(26-1+1)+1);
        fy=size*(new Random().nextInt(28-3+1)+3);
    }

	private void playSong()
	{
		try
		{
			clip=AudioSystem.getClip();
			AudioInputStream inputStream = AudioSystem.getAudioInputStream(getClass().getResource("song.wav"));
			clip.open(inputStream);
			clip.loop(-1);
		}
		catch(Exception e){}
	}
    
    private class TClass implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent ae) 
        {
            switch(direction)
            {
                case 'U' : y-=size;   break;
                case 'D' : y+=size;   break;
                case 'L' : x-=size;   break;
                case 'R' : x+=size;   break;
            }
            if(x==0)			x=sc_x-2*size;
            if(x==sc_x-size)	x=size;
            if(y==2*size)		y=sc_y-4*size;
            if(y==sc_y-3*size)	y=3*size;
            
            checkDead();
            pos.add(new Pair(x,y));
            
            if(x==fx && y==fy)
            {
                newFood();
                sc+=10;
                score.setText("Score : "+String.format("%06d",sc));
            }
            else
                pos.remove();
            repaint();
        }

        private void checkDead()
        {
            for(Pair<Integer,Integer> p:pos)
                if(x==p.getKey() && y==p.getValue())
                {
                    gameover=true;
                    t.stop();
                    break;
                }
        }
    }
    
    private class KClass implements KeyListener
    {
        @Override
        public void keyPressed(KeyEvent ke)
        {
            int k=ke.getKeyCode();
            switch(k)
            {
                case KeyEvent.VK_UP     :   if(direction=='D')  return;
                                                direction='U';  break;
                case KeyEvent.VK_DOWN   :   if(direction=='U')  return;
                                                direction='D';  break;
                case KeyEvent.VK_LEFT   :   if(direction=='R')  return;
                                                direction='L';  break;
                case KeyEvent.VK_RIGHT  :   if(direction=='L')  return;
                                                direction='R';  break;
            }
            repaint();
        }
        @Override
        public void keyReleased(KeyEvent ke) {}

        @Override
        public void keyTyped(KeyEvent ke) {}
    }       
}
