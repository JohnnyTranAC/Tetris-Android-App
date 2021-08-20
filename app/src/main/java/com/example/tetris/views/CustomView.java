package com.example.tetris.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.example.tetris.R;

import java.util.ArrayList;
import java.util.Collections;

public class CustomView extends View {
    private static final int SQUARE_SIZE_DEF = 30;
    private Rect mRectSquare;
    private Paint mPaintSquare;
    private int mSquareSize;

    private Point pt;
    int currentPiece, rotation, savedPiece=-1;
    ArrayList<Integer> nextPieces = new ArrayList<>();

    public long score;
    public int print_next, line, level, combo, lines_cleared = 0, combo_key=0, B2B=0, time=800;
    public boolean leveling_up;

    private int[][] well = new int[12][24];
    protected final int[] Color_piece = {
            hue.cyan, hue.yellow, hue.purple,
            hue.orange, hue.blue , hue.red, //yellow, purple, red
            hue.green, hue.black, hue.background
    };
   /*initializing the top-left point of the piece to be drawn*/
    final Point[][][] Pieces = {
            {
                    // I-Piece, Cyan
                    { new Point(-1, 1), new Point(0, 1), new Point(1, 1), new Point(2, 1) },
                    { new Point(0, 0), new Point(0, 1), new Point(0, 2), new Point(0, 3) },
                    { new Point(-1, 1), new Point(0, 1), new Point(1, 1), new Point(2, 1) },
                    { new Point(0, 0), new Point(0, 1), new Point(0, 2), new Point(0, 3) }
            },
            {
                    // O-Piece, Yellow
                    { new Point(0, 0), new Point(0, 1), new Point(1, 0), new Point(1, 1) },
                    { new Point(0, 0), new Point(0, 1), new Point(1, 0), new Point(1, 1) },
                    { new Point(0, 0), new Point(0, 1), new Point(1, 0), new Point(1, 1) },
                    { new Point(0, 0), new Point(0, 1), new Point(1, 0), new Point(1, 1) }
            },
            {
                    // T-Piece, Magenta
                    { new Point(-1, 1), new Point(0, 1), new Point(1, 1), new Point(0, 0) },
                    { new Point(0, 0), new Point(0, 1), new Point(0, 2), new Point(-1, 1) },
                    { new Point(-1, 1), new Point(0, 1), new Point(1, 1), new Point(0, 2) },
                    { new Point(0, 0), new Point(0, 1), new Point(0, 2), new Point(1, 1) }
            },
            {
                    // L-Piece, Orange
                    { new Point(-1, 1), new Point(0, 1), new Point(1, 1), new Point(1, 0) },
                    { new Point(0, 0), new Point(0, 1), new Point(0, 2), new Point(-1, 0) },
                    { new Point(-1, 1), new Point(0, 1), new Point(1, 1), new Point(-1, 2) },
                    { new Point(0, 0), new Point(0, 1), new Point(0, 2), new Point(1, 2) }
            },
            {
                    // J-Piece, Blue
                    { new Point(-1, 1), new Point(0, 1), new Point(1, 1), new Point(-1, 0) },
                    { new Point(0, 0), new Point(0, 1), new Point(0, 2), new Point(-1, 2) },
                    { new Point(-1, 1), new Point(0, 1), new Point(1, 1), new Point(1, 2) },
                    { new Point(0, 0), new Point(0, 1), new Point(0, 2), new Point(1, 0) }
            },
            {
                    // Z-Piece, Red
                    { new Point(-1, 0), new Point(0, 0), new Point(0, 1), new Point(1, 1) },
                    { new Point(-1, 1), new Point(-1, 0), new Point(0, 0), new Point(0, -1) },
                    { new Point(-1, 0), new Point(0, 0), new Point(0, 1), new Point(1, 1) },
                    { new Point(-1, 1), new Point(-1, 0), new Point(0, 0), new Point(0, -1) }
            },
            {
                    // S-Piece, Green
                    { new Point(-1, 1), new Point(0, 1), new Point(0, 0), new Point(1, 0) },
                    { new Point(0, 0), new Point(0, 1), new Point(1, 1), new Point(1, 2) },
                    { new Point(-1, 1), new Point(0, 1), new Point(0, 0), new Point(1, 0) },
                    { new Point(0, 0), new Point(0, 1), new Point(1, 1), new Point(1, 2) }
            }
    };

    public CustomView(Context context) {
        super(context);
        init(null);
    }

    public CustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public CustomView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public void init(@Nullable AttributeSet set){
        mRectSquare = new Rect();
        mPaintSquare = new Paint(Paint.ANTI_ALIAS_FLAG);
        if(set==null)
            return;
        TypedArray ta = getContext().obtainStyledAttributes(set, R.styleable.CustomView);
        mSquareSize = ta.getDimensionPixelSize(R.styleable.CustomView_square_size, SQUARE_SIZE_DEF);

        for(int i=0; i<12; i++){
            for (int j = 0; j < 22; j++) {
                if (i == 0 || i == 11 || j==0 || j == 21) {
                    well[i][j] = Color_piece[8];
                } else {
                    well[i][j] = Color_piece[7];
                }
            }
        }
        newPiece();
        ta.recycle();
    }
    /*adds the new piece to be the current piece*/
    public void newPiece(){
        pt = new Point(5,1);
        ArrayList<Integer> mixed = new ArrayList<>();
        rotation = 0;
        if(nextPieces.size()==2){ //ensures that the size will always be > 2 so that the next piece is predictable
            Collections.addAll(mixed, 0,1,2,3,4,5,6,0,1,2,3,4,5,6);
            Collections.shuffle(mixed);
            nextPieces.addAll(mixed);
        }
        else if(nextPieces.isEmpty()) { //will call this for the first 5 pieces
            Collections.addAll(nextPieces, 0, 1, 2, 3, 4, 5, 6);
            Collections.shuffle(nextPieces);
        }
        currentPiece = nextPieces.get(0);// gets the first index as the current piece
        endGame();
        print_next = nextPieces.get(1); //gets the next piece to fall
        nextPieces.remove(0); //removes that index and doing so shifts the other indexes -1.
    }

    private boolean Collide(int x, int y, int rotation) {
        for (Point p : Pieces[currentPiece][rotation]) {
            if (well[p.x + x][p.y + y] != Color.BLACK) { //if at any point the predicted spot is not black then there is a collision
                return true;
            }
        }
        return false; //At no point was there a spot other than black
    }

    public void rotate(int i) {
        int newRotation = (rotation + i) % 4;
        if (newRotation < 0) {
            newRotation = 3;
        }
        if (!Collide(pt.x, pt.y, newRotation)) {
            rotation = newRotation;
        }
        postInvalidate();
    }

    public void move(int i) {
        if (!Collide(pt.x + i, pt.y, rotation)) {
            pt.x += i;
        }
        postInvalidate();
    }

    public void dropDown() {
        if (!Collide(pt.x, pt.y + 1, rotation)) {
            pt.y += 1;
        } else {
            newBottom();
        }
        postInvalidate();
    }

    public void hardDrop(){
        while(true){
            if(!Collide(pt.x, pt.y+1,rotation)){
                score += 2;
                pt.y++;
            }else{
                break;
            }
        }
        newBottom();
        postInvalidate();
    }

    public void newBottom() {
        for (Point p : Pieces[currentPiece][rotation]) {
            well[pt.x + p.x][pt.y + p.y] = Color_piece[currentPiece]; //add that piece to be apart of the well
        }
        rowClear();
        newPiece();
    }

    public void rowDown(int row) { //moves rows above cleared row(s) down
        for (int j = row-1; j > 0; j--) {
            for (int i = 1; i < 11; i++) {
                well[i][j+1] = well[i][j];
            }
        }
    }

    public void rowClear() { //clears a full row and calculates the score
        boolean gap;
        int numClear = 0;
        for (int j = 20; j > 0; j--) {
            gap = false;
            for (int i = 1; i < 11; i++) {
                if (well[i][j] == Color.BLACK) {
                    gap = true;
                    break;
                }
            }
            if (!gap) {
                rowDown(j);
                j += 1;
                numClear += 1;
            }
        }
        if(numClear==0){ //resets the combo
            combo_key = 0;
            combo = 0;
        }
        scoring(numClear);
    }
    public void scoring(int numClear){
        //every 10 lines clear level++
        //level 0 it should drop at 0.8s
        //level 19 it should drop at 0.03s
        //Special Combos are awarded with more points
        lines_cleared = numClear;
        switch (lines_cleared) {
            case 1:
                score += 100*level; //100*level
                line += 1;
                combo_key += 1;
                B2B = 0;
                if(combo_key>1) //knows when there is a consecutive line clear
                    combo += 1;
                break;
            case 2:
                score += 300*level; //300*level
                line += 2;
                combo_key += 1;
                B2B = 0;
                if(combo_key>1)
                    combo += 2;
                break;
            case 3:
                score += 500*level; //500*level
                line += 3;
                combo_key += 1;
                B2B = 0;
                if(combo_key>1)
                    combo += 3;
                break;
            case 4:
                B2B += 1;
                if(B2B>=2) {
                    score += 1200*level; //800*level B2B Tetris is 1200*level
                }else{
                    score += 800*level;
                }
                line += 4;
                combo_key += 1;
                if(combo_key>1)
                    combo += 4;
                break;
            default:
                break;
        }
        if(level!=line/10+1){ //if lines were cleared exceeding the check point it doesn't register
            if(level<20) { //this is max level
                level=line/10+1;
                leveling_up = true;
                time -= 38;
            }
        }else {
            leveling_up = false;
        }
        score += 50*combo*level;
    }
    /*switches out the piece with the hold piece*/
    public void swapPiece(){
        int savedPiece2;
        if(savedPiece!=-1){
            savedPiece2 = savedPiece;
            savedPiece = currentPiece;
            currentPiece = savedPiece2;
            if(Collide(pt.x,pt.y+1,rotation)||Collide(pt.x,pt.y,rotation)){
                savedPiece2 = savedPiece;
                savedPiece = currentPiece;
                currentPiece = savedPiece2;
                return;
            }
        }else{ //when saved_piece is empty
            savedPiece = currentPiece;
            //might have to delete the currentPiece from the screen
            newPiece();
        }
        postInvalidate();
    }
    /*draws the piece on the canvas*/
    private void drawPiece(Canvas canvas){
        mPaintSquare.setColor(Color_piece[currentPiece]);
        for(Point point: Pieces[currentPiece][rotation]){
            mRectSquare.left = (pt.x+point.x)*mSquareSize+3;
            mRectSquare.top = (pt.y+point.y)*mSquareSize+3;
            mRectSquare.right = mRectSquare.left+mSquareSize-6;
            mRectSquare.bottom = mRectSquare.top+mSquareSize-6;

            canvas.drawRect(mRectSquare, mPaintSquare);
        }
    }
    /*draws the whole game board*/
    @Override
    protected void onDraw(Canvas canvas) {
        for (int i = 0; i < 12; i++) {
            for (int j = 0; j < 22; j++) {
                mPaintSquare.setColor(well[i][j]);
                if(i==0 || i==11 || j==0 || j==21) {
                    mRectSquare.left = i * mSquareSize;
                    mRectSquare.top = j * mSquareSize;
                    mRectSquare.right = mRectSquare.left + mSquareSize;
                    mRectSquare.bottom = mRectSquare.top + mSquareSize;
                    canvas.drawRect(mRectSquare, mPaintSquare);
                }else{
                    mRectSquare.left = i * mSquareSize+3;
                    mRectSquare.top = j * mSquareSize+3;
                    mRectSquare.right = mRectSquare.left + mSquareSize-6;
                    mRectSquare.bottom = mRectSquare.top + mSquareSize-6;
                    canvas.drawRect(mRectSquare, mPaintSquare);
                }
            }
        }
        drawPiece(canvas);
    }
    /*this is where it ends the game*/
    protected void endGame(){
        if(Collide(pt.x,pt.y+1,rotation)){
            System.exit(0);
        }
    }
}
