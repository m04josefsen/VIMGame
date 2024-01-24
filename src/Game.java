import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class Game extends JFrame {

    /*
    Tanker: må kanskje endre at den drawer et map og ikke i klasse kalt game, fordi når vi går til
    høyre så drawer vi et nytt map

    Og hvert kart lærer du noe nytt:
        -første kart lærer du hjkl
        -andre, WEB, tredje $ og _ kanskje
        -kanskje laget et interface eller abstrakt klasse som en default map design
        -i public game ha en if eller switch for å få player level, så
            laster du inn the mappet, så du tar tiles = osv...

        -wordBlocks, må kunne connecte de på en eller annen måte, kanskje if(block[y][x+1] noe sånt; kanskje lage array i level klassen

        Når jeg skal ha meny, kan den instansierers i public Game(); og da når jeg trykker på button loader jeg Level1 som når man går høyre/venster

        Må gjøre at du unlocker web og $ osv, kan sikkert bli gjort med if og current Map Level
     */
    public static final int TILE_SIZE = 50;
    public static final int MAP_WIDTH = 20;
    public static final int MAP_HEIGHT = 20;

    private int[][] tiles;
    private Map<Integer, Image> tileImages;

    //List that contains all blocks you can stand on / not walk through
    //TODO: gjør til List ikke ArrayList og endre in player class også
    private ArrayList<Integer> hardBlocks; //TODO: bytte til vann f.eks fordi du kan ikke gå på vann
    private ArrayList<Integer> wordBlocks;

    public Game() {
        tiles = Level.level3(tiles);

        loadTileImages();

        setTitle("VIM Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(MAP_WIDTH * TILE_SIZE, MAP_HEIGHT * TILE_SIZE);
        setLocationRelativeTo(null);

        add(new MapPanel());
        setVisible(true);
    }

    private void loadTileImages() {
        tileImages = new HashMap<>();

        tileImages.put(0, new ImageIcon("src/tiles/treeTile.png").getImage());
        tileImages.put(1, new ImageIcon("src/tiles/grassTile.png").getImage());
        tileImages.put(2, new ImageIcon("src/tiles/waterTile.png").getImage());

        hardBlocks = new ArrayList<>();
        hardBlocks.add(2);

        wordBlocks = new ArrayList<>();
        wordBlocks.add(0);

        /*
        tileImages.put(0, new ImageIcon(getClass().getResource("src/tiles/tile_0000.png")).getImage());
        tileImages.put(1, new ImageIcon(getClass().getResource("src/tiles/tile_0004.png")).getImage());
        tileImages.put(2, new ImageIcon(getClass().getResource("src/tiles/tile_0073.png")).getImage());
         */
    }

    private class MapPanel extends JPanel implements KeyListener, ActionListener {
        private Timer timer;
        private Player player;

        public MapPanel() {
            player = new Player(1, 1, 1); //TODO: egt 2, 8
            setFocusable(true);
            addKeyListener(this);

            player.randomTile();

            int delay = 16; // Adjust the delay based on your desired frame rate (e.g., for 60 FPS, use 16)
            timer = new Timer(delay, MapPanel.this);
            timer.start();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            for(int i = 0; i < MAP_WIDTH; i++) {
                for(int j = 0; j < MAP_HEIGHT; j++) {
                    int tileId = tiles[j][i];
                    Image tileImage = tileImages.get(tileId);

                    g.drawImage(tileImage, i * TILE_SIZE, j * TILE_SIZE, TILE_SIZE, TILE_SIZE, null);
                }
            }

            player.draw(g);

            player.drawTimerTile(g);
        }

        public void actionPerformed(ActionEvent e) {
            //update player state from timer
            player.update(tiles, hardBlocks);
            player.playerOnTimer();
            repaint();
            //TODO: her kan jeg bytte på tiles for animasjoner kanskje, potensilet, sånn at den bytter ut mellom to water tiles for animasjoner
        }

        @Override
        public void keyTyped(KeyEvent e) {

        }

        @Override
        public void keyPressed(KeyEvent e) {

            int keyCode = e.getKeyCode();

            switch (keyCode) {
                case KeyEvent.VK_K:
                    player.moveUp(tiles, hardBlocks);
                    break;
                case KeyEvent.VK_J:
                    player.moveDown(tiles, hardBlocks);
                    break;
                case KeyEvent.VK_H:
                    try {
                        player.moveLeft(tiles, hardBlocks);
                        break;
                    }
                    catch(Exception p) {
                        player.setX(18);
                        player.setMapLevel(player.getMapLevel() - 1);

                        tiles = Level.level1(tiles);
                    }
                case KeyEvent.VK_L:
                    try {
                        player.moveRight(tiles, hardBlocks);
                        break;
                    }
                    catch(Exception p) {
                        player.setX(1);
                        player.setY(1);
                        player.setMapLevel(player.getMapLevel() - 1);

                        tiles = Level.level2(tiles);
                        //TODO: kan bruke switch(player.getMapLevel), for å finne mappet
                    }
                case KeyEvent.VK_W:
                    player.moveW(tiles, wordBlocks);
                    break;
                case KeyEvent.VK_E:
                    player.moveE(tiles, wordBlocks);
                    break;
                case KeyEvent.VK_B:
                    player.moveB(tiles, wordBlocks);
                    break;
                case KeyEvent.VK_Z: //TODO: funker ikke å skrive $???
                    player.moveToStartOfLine(tiles, wordBlocks);
                    break;
                case KeyEvent.VK_X: //TODO: funker ikke å skrive _
                    player.moveToEndOfLine(tiles, wordBlocks);
                    break;
                case KeyEvent.VK_C:
                    player.moveToStartOfPage(tiles, wordBlocks);
                    break;
                case KeyEvent.VK_V:
                    player.moveToEndOfPage(tiles, wordBlocks);
                    break;
            }

            repaint();
        }

        @Override
        public void keyReleased(KeyEvent e) {

        }
    }
}
