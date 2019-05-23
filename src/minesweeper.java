import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;


//Test GUI

public class minesweeper {

    //variables
    public static int clickedX;
    public static int clickedY;
    public static Boolean digBool = true;
    public static Boolean flagBool = false;
    public static JFrame f = new JFrame();

    static minefield mf = new minefield();
    public static boolean gameInProgress;

    public static JButton[][] fieldButton = new JButton[10][10];
    private static JButton actionButton;


    public static int totalmines = mf.returnMines();

    //universal label for status report (win/lose)
    static JLabel label = new JLabel();

    public static void main(String[] args) {
        initGUI();
        initMF();
        JOptionPane.showMessageDialog(null, "Press the Digging/Flagging button to toggle between dig and flag\n " +
                "Note: Press basic button to engage AI for one rep. AI may glitch out on flags from duplicates, fix WIP",  "Instructions/Warning", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void initGUI() {
        int xCoord = 0;
        int yCoord = 0;

        for (int x = 45; x < 755; x += 90) {
            final int xCoordFinal = xCoord;
            for (int y = 45; y < 755; y += 90) {
                final int yCoordFinal = yCoord;
                String name = (Integer.toString(xCoord) + "," + Integer.toString(yCoord));
                fieldButton[xCoordFinal][yCoordFinal] = new JButton(new AbstractAction("") {//creating instance of JButton
                    @Override
                    public void actionPerformed(ActionEvent event) { //field button clicked
                        if (digBool == true) {
                            guiDig(event.getSource(), xCoordFinal, yCoordFinal);
                            this.setEnabled(false); //disable button when clicked, not a debugging function!
                        } else {
                            guiFlag(event.getSource(), xCoordFinal, yCoordFinal);
                        }
                    }
                });

                fieldButton[xCoordFinal][yCoordFinal].setName(name);
                fieldButton[xCoordFinal][yCoordFinal].setBounds(x, y, 90, 90);
                f.add(fieldButton[xCoordFinal][yCoordFinal]);
                yCoord++;
            }
            yCoord = 0;
            xCoord++;
        }

        actionButton = new JButton("Digging"); //dig
        actionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {

                if (digBool == true && flagBool == false) {
                    digBool = false;
                    flagBool = true;
                    actionButton.setText("Flagging");

                    for (int x = 0; x < 8; x++) {
                        for (int y = 0; y < 8; y++) {
                            if (fieldButton[x][y].getText().equals("Flag")) {
                                fieldButton[x][y].setEnabled(true);
                            }
                        }
                    }
                } else if (digBool == false && flagBool == true) {
                    digBool = true;
                    flagBool = false;
                    actionButton.setText("Digging");

                    for (int x = 0; x < 8; x++) {
                        for (int y = 0; y < 8; y++) {
                            if (fieldButton[x][y].getText().equals("Flag")) {
                                fieldButton[x][y].setEnabled(false);
                            }
                        }
                    }
                }
            }
        });

        actionButton.setBounds(900, 45, 90, 45);
        f.add(actionButton);

        JButton reset = new JButton("Reset");
        reset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                reset();
            }
        });

        reset.setBounds(900, 100, 90, 45);
        f.add(reset);


        int delay = 5;
        ActionListener checkWin = new ActionListener() { //check for win in background, somehow broke?
            @Override
            public void actionPerformed(ActionEvent arg0) {
                boolean finish = true;
                for (int i = 0; i < 8; i++) { //check if all mines flagged
                    for (int x = 0; x < 8; x++) {
                       if(fieldButton[x][i].isEnabled()){
                           finish = false;
                       }

                    }
                }

                if(gameInProgress == true && finish == true){
                    wongame();
                }
            }
        };

        new Timer(delay, checkWin).start();


        //label
        label.setBounds(800, 135, 300, 45);
        f.add(label);

        //algorithm test buttons
        JButton basicButton = new JButton("Basic");
        basicButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                findBest(getVisible());
                if(flagBool){
                    actionButton.doClick();
                }
            }


        });
        basicButton.setBounds(900, 190, 90, 50);
        f.add(basicButton);


        f.setSize(1000, 1000);
        f.setLayout(null);//using no layout managers
        f.setVisible(true);//making the frame visible
        reset(); //if no reset, starts off in won state.
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }


    public static void initMF() {
        mf.boolFieldGen();
        mf.setMines();
        mf.playFieldGen();
        mf.digFieldGen();
        mf.displayFieldGen();
        gameInProgress = true;
    }

    public static void guiDig(Object buton, int x, int y) {
        JButton button = (JButton) buton;
        boolean diggable = mf.getDigField(x, y);
        int value = mf.getPlayField(x, y);

        if (diggable == false) {

        } else if (value == 9) {
            button.setText("Mine");
            gameInProgress = false;
            lostgame();
        } else if (value != 0) {
            button.setText(Integer.toString(value));
            mf.displayField[x][y] = Integer.toString(value);
            mf.setDigField(x, y, false);
            button.setEnabled(false);
        } else if (value == 0) {
            mf.displayField[x][y] = Integer.toString(value);
            button.setText(Integer.toString(value));
            mf.setDigField(x, y, false);
            button.setEnabled(false);


            for (int j = -1; j <= 1; j++) { //check for zero, dig zero
                for (int k = -1; k <= 1; k++) {
                    try { //edges do not have all sides to detect
                        if (mf.playField[x][y] == 0 && mf.digField[x + j][y + k] == true) {
                            guiDig(fieldButton[x + j][y + k], x + j, y + k);
                        }
                    } catch (IndexOutOfBoundsException e) {
                        System.out.println("Guidig bound checker on edge");
                    }
                }
            }
        }
    }

    public static void guiFlag(Object buton, int x, int y) {
        JButton button = (JButton) buton;
        button.setEnabled(true);
        if (button.getText().equals("Flag")) {
            button.setText("");
            mf.displayField[x][y] = "";
            mf.setDigField(x, y, true);
        } else {
            button.setText("Flag");
            mf.setDigField(x, y, false);
            mf.displayField[x][y] = "Flag";
        }

    }

    public static void reset() {
        f.setVisible(false);
        clickedX = 0;
        clickedY = 0;
        digBool = true;
        flagBool = false;
        mf = new minefield();
        totalmines = mf.returnMines();
        label.setText("");

        gameInProgress = true;

        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                fieldButton[x][y].setText("");
                fieldButton[x][y].setEnabled(true);
            }
        }
        f.setVisible(true);
        initMF();
    }

    public static void lostgame() { //set all buttons to disable, display entire field
        label.setText("You hit a mine! Game Over."); //when adding sprites, show flagged mines
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                fieldButton[x][y].setText(Integer.toString(mf.getPlayField(x, y)));
                if (mf.getPlayField(x, y) == 9) {
                    fieldButton[x][y].setText("Mine");
                    //case for showing flags
                }
                fieldButton[x][y].setEnabled(false);
            }
        }
    }

    public static void wongame() {
        label.setText("All mines flagged, you won!"); //when adding sprites, show flagged mines
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                fieldButton[x][y].setText(Integer.toString(mf.getPlayField(x, y)));
                if (mf.getPlayField(x, y) == 9) {
                    fieldButton[x][y].setText("Mine");
                    //case for showing flags
                }
                fieldButton[x][y].setEnabled(false);
            }
        }

    }

    //algorithm code
    public static String[][] getVisible() {
        String[][] visField = new String[8][8];
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                if (fieldButton[x][y].getText() == "" || fieldButton[x][y].getText() == null) {
                    visField[x][y] = "B";
                } else if (fieldButton[x][y].getText() == "Flag") {
                    visField[x][y] = "F";
                } else {
                    visField[x][y] = fieldButton[x][y].getText();
                }
            }
        }
        return visField;
    }


    /*
    BASIC ALGORITHM! BRUTE FORCE CHECKS A GIVEN SET OF RULES

     */
    public static void findBest(String[][] currentState) { //use to test functions

        ArrayList<Integer[]> clicked = new ArrayList<>();
        ArrayList<Integer[]> select = new ArrayList<>();
        yloop:
        for (int y = 0; y < 8; y++) { //corner ones
            for (int x = 0; x < 8; x++) {
                if (currentState[x][y].equals("B") || currentState[x][y].equals("F")) {
                    continue;
                } else if (getSpaceCount(x, y, currentState) == Integer.parseInt(currentState[x][y])
                        && (getFlagCount(x, y, currentState) == 0)
                        && Integer.parseInt(currentState[x][y]) != 0) {  //grid with no mines around and open spaces == number
                    select = removeDups(getSpaceLoc(x, y, currentState));


                    for (Integer[] i : select) {
                        if (digBool) {
                            actionButton.doClick();
                        }
                        fieldButton[i[0]][i[1]].doClick();
                        break yloop;
                    }

                } else if (getFlagCount(x, y, currentState) == Integer.parseInt(currentState[x][y]) //all mines around grid marked
                ) {
                    select = removeDups(getSpaceLoc(x, y, currentState));


                    for (Integer[] i : select) {
                        if (flagBool) {
                            actionButton.doClick();
                        }
                        fieldButton[i[0]][i[1]].doClick();
                        break yloop;
                    }
                } else if (
                        Integer.parseInt(currentState[x][y]) == getFlagCount(x, y, currentState) + getSpaceCount(x, y, currentState)
                ) {
                    select = removeDups(getSpaceLoc(x, y, currentState));


                    for (Integer[] i : select) {
                        if (digBool) {
                            actionButton.doClick();
                        }
                        fieldButton[i[0]][i[1]].doClick();
                        break yloop;
                    }
                }
            }

        }
    }

    public static ArrayList<Integer[]> getSpaceLoc(int x, int y, String[][] currentState) { //get the locations of spaces
        ArrayList<Integer[]> coords = new ArrayList<>();

        for (int j = -1; j <= 1; j++) {
            for (int k = -1; k <= 1; k++) {
                try { //edges do not have all sides to detect, and will throw error if not caught.
                    if (currentState[x + j][y + k].equals("B")) {
                        Integer[] temp = new Integer[2];
                        temp[0] = x + j;
                        temp[1] = y + k;
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

        if (currentState[x][y].equals("B")) {
            return 10;
        } else if (currentState[x][y].equals("F")) {
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

    public static int getFlagCount(int x, int y, String[][] currentState) {
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
    public static ArrayList<Integer[]> removeDups(ArrayList<Integer[]> f){

        ArrayList<Integer[]> noDup = new ArrayList<Integer[]>();
        try {
            noDup.add(f.get(0));
        } catch(IndexOutOfBoundsException e){
            System.out.println("removeDups out of bounds!");
        }

        Boolean dup = false;
        for(Integer[] i : f ){
            dup = false;
            for(Integer[] g : noDup){
                if(i.equals(g)){
                    dup = true;
                }
            }
            if(!dup){
                noDup.add(i);
            }
        }
        return noDup;
    }


}

