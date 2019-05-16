import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.Color;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;


//Test GUI

public class minesweeper{

    //variables
    public static int clickedX;
    public static int clickedY;
    public static Boolean digBool = true;
    public static Boolean flagBool = false;
    public static JFrame f = new JFrame();

    static minefield mf = new minefield();
    public static boolean gameInProgress;

    public static JButton[][] fieldButton = new JButton[10][10];

    public static int totalmines = mf.returnMines();

    //universal label for status report (win/lose)
    static JLabel label = new JLabel();

    public static void main(String[] args){
        initGUI();
        initMF();
    }

    public static void initGUI(){
        int xCoord = 0;
        int yCoord = 0;

        for(int x = 45; x < 755; x+=90){
            final int xCoordFinal = xCoord;
            for(int y = 45; y < 755; y+=90){
                final int yCoordFinal = yCoord;
                String name = (Integer.toString(xCoord) + "," + Integer.toString(yCoord));
                fieldButton[xCoordFinal][yCoordFinal] = new JButton(new AbstractAction("") {//creating instance of JButton
                    @Override
                    public void actionPerformed(ActionEvent event) { //field button clicked
                        if (digBool == true){
                            guiDig(event.getSource(),xCoordFinal,yCoordFinal);
                            this.setEnabled(false); //disable button when clicked, not a debugging function!
                        } else {
                            guiFlag(event.getSource(),xCoordFinal,yCoordFinal);
                        }
                    }
                });

                fieldButton[xCoordFinal][yCoordFinal].setName(name);
                fieldButton[xCoordFinal][yCoordFinal].setBounds(x,y,90,90);
                f.add(fieldButton[xCoordFinal][yCoordFinal]);
                yCoord++;
            }
            yCoord = 0;
            xCoord++;
        }

        JButton actionButton = new JButton("Digging"); //dig
        actionButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent arg0) {

                if (digBool == true && flagBool == false){
                    digBool = false;
                    flagBool = true;
                    actionButton.setText("Flagging");

                    for(int x = 0; x < 8; x++){
                        for(int y = 0; y < 8; y++){
                            if (fieldButton[x][y].getText().equals("Flag")){
                                fieldButton[x][y].setEnabled(true);
                            }
                        }
                    }
                } else if (digBool == false && flagBool == true){
                    digBool = true;
                    flagBool = false;
                    actionButton.setText("Digging");

                    for(int x = 0; x < 8; x++){
                        for(int y = 0; y < 8; y++){
                            if (fieldButton[x][y].getText().equals("Flag")){
                                fieldButton[x][y].setEnabled(false);
                            }
                        }
                    }
                }
            }
        });

        actionButton.setBounds(900,45,90,45);
        f.add(actionButton);

        JButton reset = new JButton("Reset");
        reset.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent arg0){
                reset();
            }
        });

        reset.setBounds(900,100,90,45);
        f.add(reset);


        int delay = 1;
        ActionListener checkWin = new ActionListener(){ //check for win in background
            @Override
            public void actionPerformed(ActionEvent arg0) {
                int flagged = 0;
                boolean mines = true;
                for(int i = 0; i < 8; i++){ //check if all mines flagged
                    for(int x = 0; x < 8; x++){
                        if((mf.displayField[x][i].equals("Flag"))){
                            flagged++;
                        }
                        if(mf.playField[x][i] == 9){
                            if(!(mf.displayField[x][i].equals("Flag"))){
                                mines = false;
                            }
                        }
                    }
                }
                if (mines && flagged == 10){
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
                ArrayList<Integer[]> results = basicAI.findBest(getVisible());
                Integer action = results.get(0)[0];

                if(action == 11){

                } else if (action == 10){

                }

            }
        });
        basicButton.setBounds(900,190,90,50);
        f.add(basicButton);



        f.setSize(1000,1000);
        f.setLayout(null);//using no layout managers
        f.setVisible(true);//making the frame visible
        reset(); //if no reset, starts off in won state.
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }


    public static void initMF(){
        mf.boolFieldGen();
        mf.setMines();
        mf.playFieldGen();
        mf.digFieldGen();
        mf.displayFieldGen();
        gameInProgress = true;
    }

    public static void guiDig(Object buton, int x, int y){
        JButton button = (JButton) buton;
        boolean diggable = mf.getDigField(x,y);
        int value = mf.getPlayField(x,y);

        if (diggable == false){

        } else if (value == 9){
            button.setText("Mine");
            lostgame();
            gameInProgress = false;
        } else if (value != 0){
            button.setText(Integer.toString(value));
            mf.displayField[x][y] = Integer.toString(value);
            mf.setDigField(x,y,false);
            button.setEnabled(false);
        } else if (value == 0){
            mf.displayField[x][y] = Integer.toString(value);
            button.setText(Integer.toString(value));
            mf.setDigField(x,y,false);
            button.setEnabled(false);


            for(int j = -1; j <= 1; j++){ //check for zero, dig zero
                for(int k = -1; k <= 1; k++){
                    try { //edges do not have all sides to detect
                        if (mf.playField[x][y] == 0 && mf.digField[x+j][y+k] == true){
                            guiDig(fieldButton[x+j][y+k],x+j,y+k);
                        }
                    }
                    catch (IndexOutOfBoundsException e) {
                    }
                }
            }
        }
    }

    public static void guiFlag(Object buton, int x, int y){
        JButton button = (JButton) buton;
        button.setEnabled(true);
        if (button.getText().equals("Flag")){
            button.setText("");
            mf.displayField[x][y] = "";
            mf.setDigField(x,y,true);
        } else {
            button.setText("Flag");
            mf.setDigField(x,y,false);
            mf.displayField[x][y] = "Flag";
        }

    }

    public static void reset(){
        f.setVisible(false);
        clickedX = 0;
        clickedY = 0;
        digBool = true;
        flagBool = false;
        mf = new minefield();
        totalmines = mf.returnMines();
        label.setText("");

        gameInProgress = true;

        for(int x = 0; x < 8; x++){
            for(int y = 0; y < 8; y++){
                fieldButton[x][y].setText("");
                fieldButton[x][y].setEnabled(true);
            }
        }
        f.setVisible(true);
        initMF();
    }

    public static void lostgame(){ //set all buttons to disable, display entire field
        label.setText("You hit a mine! Game Over."); //when adding sprites, show flagged mines
        for(int x = 0; x < 8; x++){
            for(int y = 0; y < 8; y++){
                fieldButton[x][y].setText(Integer.toString(mf.getPlayField(x,y)));
                if (mf.getPlayField(x,y) == 9){
                    fieldButton[x][y].setText("Mine");
                    //case for showing flags
                }
                fieldButton[x][y].setEnabled(false);
            }
        }
    }

    public static void wongame(){
        label.setText("All mines flagged, you won!"); //when adding sprites, show flagged mines
        for(int x = 0; x < 8; x++){
            for(int y = 0; y < 8; y++){
                fieldButton[x][y].setText(Integer.toString(mf.getPlayField(x,y)));
                if (mf.getPlayField(x,y) == 9){
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
                if (fieldButton[x][y].getText() == "" || fieldButton[x][y].getText() == null){
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
}
