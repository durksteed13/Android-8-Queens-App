package com.example.a8queens;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Button;
import android.widget.TextView;
import android.os.Handler;
import android.widget.LinearLayout;
import static java.lang.Math.abs;

public class MainActivity extends AppCompatActivity {

    //2d array to reference all 64 squares of the chessboard
    private ImageButton[][] buttons = new ImageButton[8][8];

    //2d array to keep track of queen locations
    //0 --> empty square
    //1 --> light queen
    //2 --> dark queen
    private int[][] queenLocations = new int[8][8];

    //messages textview
    TextView messages;

    //variables to keep track of user's most recent clicked square
    int r = 0;
    int c =0;
    int total = 0;
    Button giveUpBtn;

    // getLayoutParams().height = mImageView.getMeasuredWidth();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        messages = findViewById(R.id.messages);
        giveUpBtn = findViewById(R.id.giveup);

        for(int rows=0; rows<8; rows++) {
            for(int columns = 0; columns<8; columns++) {
                String buttonId = "button_" + rows + columns;
                int resId = getResources().getIdentifier(buttonId, "id", getPackageName());
                buttons[rows][columns] = findViewById(resId);
            }
        }
        for (int rows = 0; rows < queenLocations.length; rows++) {
            for (int columns = 0; columns < queenLocations[0].length; columns++) {
                queenLocations[rows][columns] = 0;
            }
        }
    }

    public void lqueen(View v) {
        for(int rows = 0; rows<8; rows++) {
            for(int columns = 0; columns < 8; columns++) {
                if(buttons[rows][columns].getId() == v.getId()) {
                    r=rows;
                    c=columns;
                }
            }
        }
        ImageButton img = findViewById(v.getId());
        if(queenLocations[r][c] != 0) {
            img.setBackgroundResource(R.drawable.light);
            queenLocations[r][c] = 0;
            countTotal();
        }
        else if(checkValidMove("l", r, c)) {
            img.setBackgroundResource(R.drawable.lightqueen);
            queenLocations[r][c] = 2;
            countTotal();
        }
    }

    public void dqueen(View v) {
        for(int rows = 0; rows<8; rows++) {
            for(int columns = 0; columns < 8; columns++) {
                if(buttons[rows][columns].getId() == v.getId()) {
                    r=rows;
                    c=columns;
                }
            }
        }
        ImageButton img = findViewById(v.getId());
        if(queenLocations[r][c] != 0) {
            img.setBackgroundResource(R.drawable.dark);
            queenLocations[r][c] = 0;
            countTotal();
        }
        else if(checkValidMove("d", r, c)) {
            img.setBackgroundResource(R.drawable.darkqueen);
            queenLocations[r][c] = 1;
            countTotal();
        }
    }

    public void resetBoard(View v) {

        //reset chessboard
        for(int rows = 0; rows<8; rows++) {
            for(int columns = 0; columns < 8; columns++) {
                if(queenLocations[rows][columns] == 2) {
                    buttons[rows][columns].setBackgroundResource(R.drawable.light);
                }
                else if (queenLocations[rows][columns] == 1) {
                    buttons[rows][columns].setBackgroundResource(R.drawable.dark);
                }
                else if (queenLocations[rows][columns] == 3) {
                    buttons[rows][columns].performClick();
                }
                else {
                    continue;
                }
            }
        }

        //empty queenLocations 2d array
        for (int rows = 0; rows < queenLocations.length; rows++) {
            for (int columns = 0; columns < queenLocations[0].length; columns++) {
                queenLocations[rows][columns] = 0;
            }
        }
        giveUpBtn.setEnabled(true);
    }

    public void giveUp(View v) {
        if (!findESolution(findStartColumn())) {
            findSolution();
        }
        else {
            findESolution(findStartColumn());
        }
        giveUpBtn.setEnabled(false);
        messages.setText("Solution Found! Find Another Solution");
    }

    //check if the selected square is a valid move to place queen
    public boolean checkValidMove(String s, int r, int c) {
        int valid = 0;

        //check row for other queens
        for(int i = 0; i < 8; i++) {
            if(queenLocations[r][i] !=0) {
                valid++;
            }
        }

        //check column for other queens
        for(int i = 0; i < 8; i++) {
            if(queenLocations[i][c] !=0) {
                valid++;
            }
        }

        for(int rows = 0; rows < 8; rows++) {
            for(int columns = 0; columns < 8; columns++) {
                if(queenLocations[rows][columns] != 0) {
                    int changer = abs(rows - r);
                    int changec = abs(columns - c);
                    if(changer == changec) {
                        valid++;
                    }
                }
            }
        }

        if(valid == 0) {
            return true;
        }
        else if(s.equals("")) {
            return false;
        }
        else {
            invalidMove(s);
            return false;
        }
    }

    public void invalidMove(String s) {
        final String color = s;
        buttons[r][c].setBackgroundResource(R.drawable.redqueen);
        messages.setText("Invalid Move!");
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(color.equals("d")) {
                    buttons[r][c].setBackgroundResource(R.drawable.dark);
                }
                else {
                    buttons[r][c].setBackgroundResource(R.drawable.light);
                }
                messages.setText("Place 8 Queens On The Chessboard");
            }
        }, 350);
    }

    public void countTotal() {
        int t = 0;
        for(int rows = 0; rows < 8; rows++) {
            for(int columns = 0; columns < 8; columns ++) {
                if(queenLocations[rows][columns] != 0) {
                    t ++;
                }
            }
        }
        total = t;
        if(total == 8) {
            messages.setText("Well Done! Find Another Solution");
            giveUpBtn.setEnabled(false);
        }
        else {
            giveUpBtn.setEnabled(true);
            messages.setText("Place 8 Queens On The Chessboard");
        }
    }

    public int findStartColumn() {
        int startCol = 0;
        //find first column without queen
        for (int col = 0; col < 8; col++) {
            boolean hasQueen = false;
            for (int row = 0; row < 8; row++) {
                if (queenLocations[row][col] != 0) {
                    hasQueen = true;
                }
            }
            if (!hasQueen) {
                startCol = col;
                break;
            }
        }
        return startCol;
    }

    public boolean colHasQueen(int col) {
        boolean hasQueen = false;
        for(int row = 0; row < 8; row++) {
            if(queenLocations[row][col] != 0) {
                hasQueen = true;
            }
        }
        return hasQueen;
    }

    public boolean findESolution(int col) {
        if(col >= 8) {
            return true;
        }
        for(int row = 0; row < 8; row++) {
            if(checkValidMove("", row, col)) {
                queenLocations[row][col] = 3;
                buttons[row][col].setBackgroundResource(R.drawable.bluequeen);

                if(findESolution(col+1) == true) {
                    return true;
                }

                queenLocations[row][col] = 0;
                buttons[row][col].performClick();
                buttons[row][col].performClick();
            }
        }
        return false;
    }

    public boolean findPartialSolution(int col) {
        for(int row = 0; row < 8; row++) {
            if(queenLocations[row][col] != 0) {
                buttons[row][col].performClick();
                queenLocations[row][col] = 0;
            }
        }
        if (findESolution(col)) {
            return true;
        }
        else if (findPartialSolution(col -1)){
            return true;
        }
        return false;
    }

    public void findSolution() {
        for(int col = 7; 7 >= 0; col--) {
            if(col == 0) {
                break;
            }
            if(col == findStartColumn()) {
                break;
            }
            else if(colHasQueen(col)) {
                for(int row = 0; row < 8; row++) {
                    if(queenLocations[row][col] != 0) {
                        queenLocations[row][col] = 0;
                        buttons[row][col].performClick();
                        buttons[row][col].performClick();
                    }
                }
            }
        }
        findPartialSolution(findStartColumn());
    }
}
