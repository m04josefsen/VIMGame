import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/*
TODO: -tanker
        -MoveW og MoveE bytter linje feil, X aksen må resetes når den når 20 eller 19
        -MoveB går ikke til ny linje

        -Hvis det ikke er flere wordTiles, blir y+1 når du trykker W
        -INdex 20 out of bounds for length 20 når du trykker E på slutten

        -Må jo ha med så man kan gå med HJKL opp og ned
 */

public class Player {
    //Position
    private int x ;
    private int y;
    private int mapLevel;

    public Player(int initialX, int initialY, int mapLevel) {
        this.x = initialX;
        this.y = initialY;
        this.mapLevel = mapLevel;
    }

    //Getters and Setters
    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getMapLevel() {
        return mapLevel;
    }

    public void setMapLevel(int mapLevel) {
        this.mapLevel = mapLevel;
    }

    public void moveUp(int[][] tiles, ArrayList<Integer> hardBlocks) {
        if(!hardBlocks.contains(tiles[y-1][x])) {
            this.y -= 1;
        }
    }

    public void moveDown(int[][] tiles, ArrayList<Integer> hardBlocks) {
        if(!hardBlocks.contains(tiles[y+1][x])) {
            this.y += 1;
        }
    }

    public void moveLeft(int[][] tiles, ArrayList<Integer> hardBlocks) {
        if(!hardBlocks.contains(tiles[y][x-1])) {
            this.x -= 1;
        }
    }

    public void moveRight(int[][] tiles, ArrayList<Integer> hardBlocks) {
        if(!hardBlocks.contains(tiles[y][x+1])) {
            this.x += 1;
        }
    }

    public void moveW(int[][] tiles, ArrayList<Integer> wordBlocks)  {
        //TODO: nå hvis jeg trykker W og det ikke er noe mer går jeg til neste tomme plass
        //If player is on a Word tile
        if(wordBlocks.contains(tiles[y][x])) {
            for(int i = y; i < tiles.length; i++) {
                for(int j = x; j < tiles[i].length; j++) {
                    //If player is not standing on a Word tile, place the player there, and use moveToNextWordBlock method
                    if(!wordBlocks.contains(tiles[i][j])) { //TODO: her er problemet hvis du er på siste ord, move to NextWordBlock gjør ikke node da, så x og y = som er problemet
                        this.y = i;
                        this.x = j;
                        moveToNextWordBlock(tiles, wordBlocks);
                        return;
                    }
                }
            }
        }
        //If player is not on a Word tile
        else {
            moveToNextWordBlock(tiles, wordBlocks);
        }
    }

    public void moveToNextWordBlock(int[][] tiles, ArrayList<Integer> wordBlocks) {
        for(int i = y; i < tiles.length; i++) {
            // Initialize 'j' based on the condition: If 'i' is equal to the starting value 'y', set 'j' to 'x',
            // otherwise, reset 'j' to 0 for the new row in the 2D array.
            for(int j = ((i == y) ? x : 0); j < tiles[i].length; j++) {
                if(wordBlocks.contains(tiles[i][j])) {
                    this.y = i;
                    this.x = j;
                    return;
                }
            }
        }
    }

    public boolean isLastWord(int[][] tiles, ArrayList<Integer> wordBlocks) {
        for(int i = tiles.length - 1; i >= y; i--) {
            // Iterate backward through the inner array of 'tiles' at index 'i',
            // excluding the element at index 'x' if 'i' is equal to 'y'
            for(int j = tiles[i].length - 1; ((i == y) ? (j > x) : (j >= 0)); j--) {
                if(wordBlocks.contains(tiles[i][j])) {
                    return false;
                }
            }
        }
        return true;
    }

    public void moveE(int[][] tiles, ArrayList<Integer> wordBlocks)  {
        //If player is on a Word tile
        if(wordBlocks.contains(tiles[y][x])) {
            //If you are at the end of a word
            if(!wordBlocks.contains(tiles[y][x+1])) {
                moveW(tiles, wordBlocks);
                moveToEndOfWordBlock(tiles, wordBlocks);
            }
            //If you are not at the end of the word
            else {
                for (int i = x; i < tiles[y].length; i++) {
                    //TODO: kan kanskje fjerne try-catch
                    try {
                        if (!wordBlocks.contains(tiles[y][i + 1])) { //TODO: her er problemet fordi jeg bruker i+1, og når jeg er på siste ord så sjekekr den out of bounds, kan bruke ternary if
                            this.x = i;
                            return;
                        }
                    }
                    catch(Exception e) {
                        moveToEndOfWordBlock(tiles, wordBlocks);
                    }
                }
            }
        }
        //If player is not a Word tile
        else {
            moveToEndOfWordBlock(tiles, wordBlocks);
        }
    }
    
    public void moveToEndOfWordBlock(int[][] tiles, ArrayList<Integer> wordBlocks) {
        for(int i = y; i < tiles.length; i++) {
            for(int j = x; j < tiles[i].length; j++) {
                //If there are more than one word left for the player
                try {
                    if(!wordBlocks.contains(tiles[i][j + 1]) && wordBlocks.contains(tiles[i][j])) {
                        this.y = i;
                        this.x = j;
                        return;
                    }
                }
                //If its the last word for the player
                catch(Exception e) {
                    //TODO: gå fra x 19 indeks og gå bakover til du finner en
                    for(int k = 19; k > x; k--) {
                        if(wordBlocks.contains(tiles[y][k])) {
                            this.x = k;
                            return;
                        }
                    }
                }
            }
        }
    }

    public void moveB(int[][] tiles, ArrayList<Integer> wordBlocks)  {
        //If Player is standing on a Word tile
        if(wordBlocks.contains(tiles[y][x])) {
            //If Player is standing on the first letter in a word
            if (!wordBlocks.contains(tiles[y][x - 1])) {
                for(int i = y; i >= 0; i--) {
                    for(int j = ((i == y) ? x-1 : 19); j >= 0; j--) {
                        if(wordBlocks.contains(tiles[i][j])) {
                            this.y = i;
                            this.x = j;
                            moveToStartOfWordBlock(tiles, wordBlocks);
                            return;
                        }
                    }
                }
            }
            //If player is standing on a Word tile that is not the first one
            else {
                moveToStartOfWordBlock(tiles, wordBlocks);
            }
        }
        //If Player is not standing on a Word tile
        else { //TODO: funker kun hvis det er en blokk til venstre
            for(int i = y; i >= 0; i--) {
                for(int j = x; j >= 0; j--) {
                    if(wordBlocks.contains(tiles[i][j])) {
                        moveToStartOfWordBlock(tiles, wordBlocks);
                        return;
                    }
                }
            }
        }
    }

    public void moveToStartOfWordBlock(int[][] tiles, ArrayList<Integer> wordBlocks) {
        for(int i = x; i >= 0; i--) {
            if(!wordBlocks.contains(tiles[y][i-1])) {
                this.x = i;
                return;
            }
        }
    }

    //$, for nå Z
    public void moveToStartOfLine(int[][] tiles, ArrayList<Integer> wordBlocks) {
        for(int i = 0; i < tiles[x].length; i++) {
            if(wordBlocks.contains(tiles[y][i])) {
                this.x = i;
                return;
            }
        }
    }

    //_, for nå X
    public void moveToEndOfLine(int[][] tiles, ArrayList<Integer> wordBlocks) {
        for(int i = tiles[y].length - 1; i >= 0; i--) {
            if(wordBlocks.contains(tiles[y][i])) {
                this.x = i;
                return;
            }
        }
    }

    //gg, for nå C
    public void moveToStartOfPage(int[][] tiles, ArrayList<Integer> wordBlocks) {
        for(int i = 0; i < tiles.length; i++) {
            for(int j = 0; j < tiles[y].length; j++) {
                if(wordBlocks.contains(tiles[i][j])) {
                    this.y = i;
                    this.x = j;
                    return;
                }
            }
        }
    }

    //G, for nå V
    public void moveToEndOfPage(int[][] tiles, ArrayList<Integer> wordBlocks) {
        for(int i = tiles.length - 1; i >= 0; i--) {
            for(int j = tiles[y].length - 1; j >= 0; j--) {
                if(wordBlocks.contains(tiles[i][j])) {
                    this.y = i;
                    this.x = j;
                    return;
                }
            }
        }
    }

    public void draw(Graphics g) {
        g.drawImage(new ImageIcon("src/tiles/pigTile.png").getImage(), x * Game.TILE_SIZE, y * Game.TILE_SIZE, Game.TILE_SIZE, Game.TILE_SIZE, null);
    }

    public void update(int[][] tiles, ArrayList<Integer> hardBlocks) {

    }

}
