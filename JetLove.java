package jetlove;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.imageio.*;
import javax.swing.*;

public class JetLove extends JPanel implements KeyListener {

    JFrame j = new JFrame();
    Graphics g = null;
    Hero hero = new Hero();
    ArrayList<Cloud> clouds = new ArrayList<>();
    ArrayList<Jet> jets = new ArrayList<>();
    ArrayList<Tree> trees = new ArrayList<>();
    ArrayList<Bullet> bullets = new ArrayList<>();
    ArrayList<Explode> exs = new ArrayList<>();
    Random rand = new Random();
    int bulletsCount = 0;
    
    class Explode {
        int x, y;
    }

    class Tree {
        int x, y;
    }
    
    class Bullet {
        int x, y;
    }

    class Hero {
        int x, y;
        int crash, kills;
        ArrayList<Shot> shots = new ArrayList<>();
        
        class Shot {
            int x, y;
        }
        
        void fire() {
            Shot shot = new Shot();
            shot.x = this.x + 110;
            shot.y = this.y + 50;
            shots.add(shot);
        }
        
        void fireTheShot() {
            drawFire();
        }
        
        void drawFire() {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    while(true) {
                        for(int i=0; i<shots.size(); i++) {
                            synchronized(this) {
                                g.setColor(new Color(100, 100, 255));
                                g.drawLine(shots.get(i).x, shots.get(i).y, shots.get(i).x + 10, shots.get(i).y);
                            }
                            shots.get(i).x += 10;
                            synchronized(this) {
                                g.setColor(Color.red);
                                g.drawLine(shots.get(i).x, shots.get(i).y, shots.get(i).x + 10, shots.get(i).y);
                            }
                            for(int k=0; k<jets.size(); k++)
                                if(shots.get(i).x < jets.get(k).x + 100 && shots.get(i).x > jets.get(k).x &&
                                        shots.get(i).y < jets.get(k).y + 100 && shots.get(i).y > jets.get(k).y)
                                {
                                    Explode ex = new Explode();
                                    ex.x = shots.get(i).x;
                                    ex.y = shots.get(i).y;
                                    exs.add(ex);

                                    hero.kills ++;

                                    j.setTitle("crash: " + hero.crash + ", kills: " + hero.kills);

                                    synchronized(this) {
                                        g.setColor(new Color(100, 150, 255));
                                        g.fillRect(jets.get(k).x, jets.get(k).y, 100, 100);
                                    }

                                    jets.remove(jets.get(k));
                                }
                        }
                        try {
                            Thread.sleep(100);
                        } catch(Exception e) {}
                    }
                }
            });
            
            t.start();
        }
    }
    
    class Jet {
        int x, y;
    }

    class Grass {
        int x, y;
        Color color;
    }

    class Cloud {
        int x, y;
    }

    public JetLove() {
        
        super();
        
        setGUI();
        try {
            setHero();
        } catch(Exception e) {
            e.printStackTrace();
        }
        drawScreen();

        hero.fireTheShot();
        
        try {
            explode();
    
            drawBullets();
        } catch(Exception e) {
            e.printStackTrace();
        }
        
        Thread t = new Thread() {
            public void run() {
                while(true) {
                    JetLove.this.repaint();
                    j.setTitle("crash: " + hero.crash + ", kills: " + hero.kills + ", bullets: " + bulletsCount);
                    try {
                        Thread.sleep(1200);
                    } catch(Exception e) {}
                }
            }
        };
        
        t.start();
        
        j.addKeyListener(this);
    }

    void drawBullets() throws Exception {
        Image img = ImageIO.read(getClass().getResourceAsStream("bullet.png"));

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    for(int i=0; i<bullets.size(); i++) {
                        g.drawImage(img, bullets.get(i).x, bullets.get(i).y, 50, 50, null);
                        for(int k=0; k<100; k++)
                            for(int l=0; l<100; l++)
                                try {
                                    if(bullets.get(i).x + 50 > hero.x + k && bullets.get(i).x < hero.x + k &&
                                            bullets.get(i).y + 50 > hero.y + l && bullets.get(i).y < hero.y + l)
                                    {
                                        bulletsCount ++;

                                        bullets.remove(bullets.get(i));
                                    }
                                } catch(Exception e) {}
                    }
                    if(bullets.size() == 0) {
                        for(int i=0; i<20; i++) {
                            Bullet bullet = new Bullet();
                            bullet.x = rand.nextInt(1150);
                            bullet.y = rand.nextInt(550);
                            bullets.add(bullet);
                        }
                    }
                    try {
                        Thread.sleep(150);
                    } catch(Exception e) {}
                }
            }
        });
           
        t.start();
    }
    
    void explode() throws Exception {
        Image img = ImageIO.read(getClass().getResourceAsStream("explode.gif"));

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList ar = new ArrayList();
                while(true) {
                    for(int i=0; i<exs.size(); i++) {
                        g.drawImage(img, exs.get(i).x-50, exs.get(i).y-120, 200, 200, null);
                        ar.add(exs.get(i));
                        exs.remove(exs.get(i));
                    }
                    try {
                        Thread.sleep(150);
                    } catch(Exception e) {}
                    for(int i=0; i<ar.size(); i++) {
                        g.setColor(new Color(100, 150, 255));
                        g.fillRect(((Explode)ar.get(i)).x-50, ((Explode)ar.get(i)).y-120, 200, 200);
                    }
                    ar.clear();
                }
            }
        });
           
        t.start();
    }
    
    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_UP) {
            synchronized(this) {
                g.setColor(new Color(100, 150, 255));
                g.fillRect(hero.x, hero.y, 100, 100);
            }
            if(hero.y > 0)
                hero.y -= 2;
        }
        else if(e.getKeyCode() == KeyEvent.VK_DOWN) {
            synchronized(this) {
                g.setColor(new Color(100, 150, 255));
                g.fillRect(hero.x, hero.y, 100, 100);
            }
            if(hero.y < 501)
                hero.y += 2;
        }
        else if(e.getKeyCode() == KeyEvent.VK_LEFT) {
            synchronized(this) {
                g.setColor(new Color(100, 150, 255));
                g.fillRect(hero.x, hero.y, 100, 100);
            }
            if(hero.x > 0)
                hero.x -= 2;
        }
        else if(e.getKeyCode() == KeyEvent.VK_RIGHT) {
            synchronized(this) {
                g.setColor(new Color(100, 150, 255));
                g.fillRect(hero.x, hero.y, 100, 100);
            }
            if(hero.x < 1200)
                hero.x += 2;
        }
        else if(e.getKeyCode() == KeyEvent.VK_PERIOD) {
            if(bulletsCount > 0) {
                hero.fire();
                bulletsCount --;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    void setHero() throws Exception {
        
        hero.x = 100;
        hero.y = 200;
        
        hero.kills = 0;
        
        hero.crash = 0;
        
        j.setTitle("crash: " + hero.crash + ", kills: " + hero.kills);
        
        Image img = ImageIO.read(getClass().getResourceAsStream("hero.png"));

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    g.drawImage(img, hero.x, hero.y, 100, 100, null);
                    try {
                        Thread.sleep(10);
                    } catch(Exception e) {}
                }
            }
        });
        
        t.start();
        
    }
    
    public void paint(Graphics g) {
        super.paintComponent(g);

        setDoubleBuffered(true);

        g.setColor(new Color(100, 150, 255));
        g.fillRect(0, 0, 1200, 600);

        g.setColor(new Color(50, 150, 0));
        g.fillRect(0, 600, 1200, 200);
    }

    void setGUI() {
        j.setLayout(null);
        
        j.setResizable(false);
        
        j.setMaximumSize(new Dimension(1600, 1000));

        j.setBounds(0, 0, 1600, 1000);

        setBounds(40, 40, 1200, 800);
        
        JPanel pp = new JPanel();
        
        pp.setLayout(null);
        
        pp.setBackground(Color.yellow);

        pp.setBounds(j.getBounds());

        pp.add(this);
        
        j.add(pp);

        j.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        j.setVisible(true);

        setGraphics();
    }
    
    void setGraphics() {
        g = this.getGraphics();
    }
    
    void drawScreen() {
        try {
            drawClouds();
            drawGrass();
            drawJets();
            drawTrees();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    void drawFire() {

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                hero.fireTheShot();
            }
        });
        
        t.start();
    }

    void drawTrees() {

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    if(trees.size() == 0) {
                        for(int i=0; i<10; i++) {
                            Tree cloud = new Tree();
                            cloud.x = 1200 + rand.nextInt(1000);
                            cloud.y = 600;
                            trees.add(cloud);
                        }
                        for(int i=0; i<10; i++) {
                            Tree cloud = new Tree();
                            cloud.x = 1200 + rand.nextInt(1000);
                            cloud.y = 700;
                            trees.add(cloud);
                        }
                    }
                    for(int i=0; i<trees.size(); i++) {
                        synchronized(this) {
                            g.setColor(new Color(50, 150, 0));
                            g.fillRect(trees.get(i).x, trees.get(i).y, 20, 50);
                        }

                        trees.get(i).x -= 10;
                    }

                    for(int i=0; i<trees.size(); i++) {
                        synchronized(this) {
                            g.setColor(new Color(0, 200, 0));
                            g.fillRect(trees.get(i).x, trees.get(i).y, 20, 20);
                            g.setColor(new Color(200,100,50));
                            g.fillRect(trees.get(i).x, trees.get(i).y+20, 20, 30);
                        }
                        
                        if(trees.get(i).x < 0) {
                            trees.remove(trees.get(i));
                        }
                    }
                    
                    try {
                        Thread.sleep(100);
                    } catch(Exception e) {}
                }
            }
        });
        
        t.start();
    }
    
    void drawJets() throws Exception {
        Image img = ImageIO.read(getClass().getResourceAsStream("bomber.png"));

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
//                    if(hero.life == 0)
//                        System.exit(0);
                    if(jets.size() == 0) {
                        for(int i=0; i<10; i++) {
                            Jet cloud = new Jet();
                            cloud.x = 1200 + rand.nextInt(1000);
                            cloud.y = rand.nextInt(499);
                            jets.add(cloud);
                        }
                    }
                    for(int i=0; i<jets.size(); i++) {
                        synchronized(this) {
                            g.setColor(new Color(100, 150, 255));
                            g.fillRect(jets.get(i).x, jets.get(i).y, 100, 100);
                        }

                        jets.get(i).x -= 10;
                    }
                    for(int i=0; i<jets.size(); i++) {
                        g.drawImage(img, jets.get(i).x, jets.get(i).y, 100, 100, null);
                        
                        for(int k=0; k<100; k++)
                            for(int l=0; l<100; l++)
                                try {
                                    if(jets.get(i).x + k < hero.x + 100 && jets.get(i).x + k > hero.x &&
                                            jets.get(i).y + l < hero.y + 100 && jets.get(i).y + l > hero.y)
                                    {
                                        synchronized(this) {
                                            g.setColor(new Color(100, 150, 255));
                                            g.fillRect(jets.get(i).x, jets.get(i).y, 100, 100);
                                        }

                                        jets.remove(jets.get(i));

                                        hero.crash ++;
                                        
                                        bulletsCount --;

                                        j.setTitle("crash: " + hero.crash + ", kills: " + hero.kills);
                                    }
                                } catch(Exception e) 
                                {
                                    //e.printStackTrace();
                                }

                        try {
                            if(jets.get(i).x < -100) {
                                jets.remove(jets.get(i));
                            }
                        } catch(Exception e) {
                        }
                    }
                    try {
                        Thread.sleep(100);
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        t.start();
    }

    void drawClouds() throws Exception {
        Image img = ImageIO.read(getClass().getResourceAsStream("cloud.png"));

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    if(clouds.size() == 0) {
                        for(int i=0; i<10; i++) {
                            Cloud cloud = new Cloud();
                            cloud.x = 1200 + rand.nextInt(1000);
                            cloud.y = rand.nextInt(400);
                            clouds.add(cloud);
                        }
                    }
                    for(int i=0; i<clouds.size(); i++) {
                        synchronized(this) {
                            g.setColor(new Color(100, 150, 255));
                            g.fillRect(clouds.get(i).x, clouds.get(i).y, 100, 100);
                        }

                        clouds.get(i).x -= 10;
                    }
                    for(int i=0; i<clouds.size(); i++) {
                        g.drawImage(img, clouds.get(i).x, clouds.get(i).y, 100, 100, null);
                        
                        if(clouds.get(i).x < -100) {
                            clouds.remove(clouds.get(i));
                        }
                    }
                    try {
                        Thread.sleep(100);
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        t.start();
    }
    
    void drawGrass() throws Exception {
        g.setColor(new Color(50, 150, 0));
        g.fillRect(0, 600, 1200, 200);
    }

    public static void main(String[] args) {
        new JetLove();
    }    
}