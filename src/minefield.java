import java.util.concurrent.ThreadLocalRandom; //for random num via "int randomNum = ThreadLocalRandom.current().nextInt(min, max + 1);"



//TEST
public class minefield { //generate 8x8 minefield with 10 mines.
    //TO DO: MAKE MINEFIELD CUSTOMIZABLE.

    boolean[][] boolField = new boolean[8][8];
    int[][] playField = new int[8][8];
    boolean[][] digField = new boolean[8][8]; //check if diggable
    String[][] displayField = new String[8][8];

    int minecount = 10;

    public minefield(){

    }

    public int returnMines(){
        return minecount;
    }


    public void boolFieldGen(){ //makes blank field
        for(int i = 0; i < 8; i++){ //iterate through rows
            for(int x = 0; x < 8; x++){ //iterate through "columns"
                boolField[x][i] = false;
            }
        }
    }
    public void setMines(){ //sets 10 mines

        int[] x_mines = new int[11];
        int[] y_mines = new int[11];

        for(int i = 0; i < minecount; i++){ //10 mines

            int x = ThreadLocalRandom.current().nextInt(0, 8);
            int y = ThreadLocalRandom.current().nextInt(0, 8);

            for(int p = 0; p < x_mines.length; p++){
                if(x_mines[p] == x){
                    x = ThreadLocalRandom.current().nextInt(0, 8);
                }
            }

            for(int o = 0; o < y_mines.length; o++){
                if(y_mines[o] == y){
                    y = ThreadLocalRandom.current().nextInt(0, 8);
                }

                boolField[x][y] = true;

                for(int z = 0; z < x_mines.length; z++) {
                    Integer x_value = Integer.valueOf(x_mines[z]);
                    if(x_value == null){
                        x_mines[z] = x;
                        break;
                    }
                }

                for(int z = 0; z < y_mines.length; z++) {
                    Integer y_value = Integer.valueOf(y_mines[z]);
                    if(y_value == null){
                        x_mines[z] = y;
                        break;
                    }
                }
            }



        }
    }


    public void playFieldGen(){
        for(int i = 0; i < 8; i++){ //iterate through rows
            for(int x = 0; x < 8; x++){ //iterate through "columns"
                if(boolField[x][i] == true){
                    playField[x][i] = 9;
                } else {
                    playField[x][i] = setNumber(x,i);
                }
            }
        }
    }
    public int setNumber(int x, int i){ //detects the amount of surrounding mines
    /*
    X  X  X
    X  O  X
    X  X  X
    (-1,+1) (0, +1) (+1, +1)
    (-1,0) (0, 0) (+1, 0)
    (-1,-1) (0, -1) (+1, -1)
    */
        int number = 0;
        for(int j = -1; j <= 1; j++){
            for(int k = -1; k <= 1; k++){
                try { //edges do not have all sides to detect
                    if (boolField[x+j][i+k] == true)
                        number ++;
                } catch (IndexOutOfBoundsException e) {
                }
            }
        }
        return number;
    }

    public void digFieldGen(){ //makes blank field
        for(int i = 0; i < 8; i++){ //iterate through rows
            for(int x = 0; x < 8; x++){ //iterate through "columns"
                digField[x][i] = true;
            }
        }
    }

    public void displayFieldGen(){
        for(int i = 0; i < 8; i++){ //iterate through rows
            for(int x = 0; x < 8; x++){ //iterate through "columns"
                displayField[x][i] = "-";
            }
        }
    }
    public int getPlayField(int x, int y){
        return this.playField[x][y];
    }
    public boolean getDigField(int x, int y){
        return this.digField[x][y];
    }


    public void refreshDisplay(){
        for(int i = 0; i < 8; i++){ //iterate through rows
            for(int x = 0; x < 8; x++){ //iterate through "columns"
                System.out.print(displayField[x][i]);
                System.out.print(' ');
            }
            System.out.print("\n");
        }
    }



    public void setPlayField(int x, int y, int g){
        this.playField[x][y] = g;
    }
    public void setDigField(int x, int y, boolean b){
        this.digField[x][y] = b;
    }

}







