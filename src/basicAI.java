import java.util.ArrayList;

public class basicAI {


    public basicAI(String[][] visible) {
    }

    public static ArrayList<Integer[]> findBest(String[][] currentState) { //use to test functions
        ArrayList<Integer[]> select = new ArrayList<>();
        for (int y = 0; y < 8; y++) { //corner ones
            for (int x = 0; x < 8; x++) {
                if(currentState[x][y].equals("B") || currentState[x][y].equals("F")){
                    continue;
                }
                if(getSpaceCount(x,y, currentState) == Integer.parseInt(currentState[x][y])
                        && getFlagCount(x,y,currentState) == 0
                        && Integer.parseInt(currentState[x][y]) != 0) {  //grid with no mines around and open spaces == number
                    Integer[] temp = new Integer[1];
                    temp[0]= 11;
                    select.add(temp); //show to flag
                    select.add(getSpaceLoc(x,y,currentState).get(0));
                    select.add(getSpaceLoc(x,y,currentState).get(1));

                }
                else if(Integer.parseInt(currentState[x][y]) == getFlagCount(x, y, currentState))
                { //grid with equal flag count means all rest are not mines
                    Integer[] temp = new Integer[1];
                    temp[0]= 10;
                    select.add(temp); //show to dig
                    select.add(getSpaceLoc(x,y,currentState).get(0));
                    select.add(getSpaceLoc(x,y,currentState).get(1));
                }
            }

        }

        return select;
    }


 //search ones

    public static ArrayList<Integer[]> getSpaceLoc(int x, int y, String[][] currentState){ //get the locations of spaces
        ArrayList<Integer[]> coords = new ArrayList<>();

        for (int j = -1; j <= 1; j++) {
            for (int k = -1; k <= 1; k++) {
                try { //edges do not have all sides to detect, and will throw error if not caught.
                    if (currentState[x + j][y + k].equals("B")) {
                        Integer[] temp = new Integer[2];
                        temp[0] = x+j;
                        temp[1] = y+k;
                        coords.add(temp);

                    }
                } catch (IndexOutOfBoundsException e) {
                }
            }
        }


        return coords;

    }


    public static int getSpaceCount(int x, int y, String[][] currentState) { //get the spaces around around each button //WRITE THIS DAMN FUNCTION
        int number = 0;

        if(currentState[x][y].equals("B")){
            return 10;
        } else if (currentState[x][y].equals("F")){
            return 11;
        }

        for (int j = -1; j <= 1; j++) {
            for (int k = -1; k <= 1; k++) {
                try { //edges do not have all sides to detect, and will throw error if not caught.
                    if (currentState[x + j][y + k].equals("B"))
                        number++;
                } catch (IndexOutOfBoundsException e) {
                }
            }
        }
        return number;
    }

    public static int getFlagCount(int x, int y, String[][] currentState){
        int number = 0;
        for (int j = -1; j <= 1; j++) {
            for (int k = -1; k <= 1; k++) {
                try { //edges do not have all sides to detect, and will throw error if not caught.
                    if (currentState[x + j][y + k].equals("F"))
                        number++;
                } catch (IndexOutOfBoundsException e) {
                }
            }
        }
        return number;
    }
}
