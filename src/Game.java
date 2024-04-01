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
        tiles = Level.timerLevel(tiles);

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

        tileImages.put(0, new ImageIcon(getClass().getResource("tiles/treeTile.png")).getImage());
        tileImages.put(1, new ImageIcon(getClass().getResource("tiles/grassTile.png")).getImage());
        tileImages.put(2, new ImageIcon(getClass().getResource("tiles/waterTile.png")).getImage());

        hardBlocks = new ArrayList<>();
        hardBlocks.add(2);

        wordBlocks = new ArrayList<>();
        wordBlocks.add(0);
    }

    private class MapPanel extends JPanel implements KeyListener, ActionListener {
        private Timer timer;
        private Player player;
        private int gCount;

        public MapPanel() {
            player = new Player(1, 1, 3); //TODO: egt 2, 8
            setFocusable(true);
            addKeyListener(this);

            player.randomTile(tiles, hardBlocks);

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
            player.playerOnTimer(tiles, hardBlocks);
            repaint();

            if(player.isFinished()) {
                tiles = Level.level1(tiles); //TODO: HER SKAL DEN GÅ TIL ET MAP SOM SIER HVOR MYE TID DU BRUKTE??
                player.setScore(0);
            }
            //TODO: her kan jeg bytte på tiles for animasjoner kanskje, potensilet, sånn at den bytter ut mellom to water tiles for animasjoner
        }

        @Override
        public void keyTyped(KeyEvent e) {

        }

        @Override
        public void keyPressed(KeyEvent e) {

            char keyCode = e.getKeyChar();
            int key = e.getKeyCode();

            switch (keyCode) {
                case 'k':
                    player.moveUp(tiles, hardBlocks);
                    gCount = 0;
                    break;
                case 'j':
                    player.moveDown(tiles, hardBlocks);
                    gCount = 0;
                    break;
                case 'h':
                    gCount = 0;
                    try {
                        player.moveLeft(tiles, hardBlocks);
                        break;
                    }
                    catch(Exception p) {
                        player.setX(18);

                        tiles = Level.level1(tiles);
                    }
                case 'l':
                    gCount = 0;
                    try {
                        player.moveRight(tiles, hardBlocks);
                        break;
                    }
                    catch(Exception p) {
                        player.setX(1);
                        player.setY(1);

                        tiles = Level.level2(tiles);
                        //TODO: kan bruke switch(player.getMapLevel), for å finne mappet
                    }
                case 'w':
                    player.moveW(tiles, wordBlocks);
                    gCount = 0;
                    break;
                case 'e':
                    player.moveE(tiles, wordBlocks);
                    gCount = 0;
                    break;
                case 'b':
                    player.moveB(tiles, wordBlocks);
                    gCount = 0;
                    break;
                case '$':
                    player.moveToStartOfLine(tiles, wordBlocks);
                    gCount = 0;
                    break;
                case '_':
                    player.moveToEndOfLine(tiles, wordBlocks);
                    gCount = 0;
                    break;
                case 'G':
                    player.moveToEndOfPage(tiles, wordBlocks);
                    gCount = 0;
                    break;
                case 'g':
                    gCount++;
                    //TODO: hvis jeg får flere counts kan jeg kanskje bruke en metode for å resette, men det blir vanskelig også
                    if(gCount == 2) {
                        player.moveToStartOfPage(tiles, wordBlocks);
                        gCount = 0;
                        break;
                    }
            }

            repaint();
        }

        @Override
        public void keyReleased(KeyEvent e) {

        }
    }
}
