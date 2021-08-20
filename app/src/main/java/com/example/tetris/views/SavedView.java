package com.example.tetris.views;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.example.tetris.R;

public class SavedView extends View {
    public CustomView mCustomView = new CustomView(getContext());
    private static final int SQUARE_SIZE_DEF = 10;
    private Rect sRectSquare;
    private Paint sPaintSquare;
    private int sSquareColor, sSquareSize;
    Point dest = new Point(3,0);

    public SavedView(Context context) {
        super(context);
        init(null );
    }

    public SavedView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public SavedView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public SavedView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }
    public void init(@Nullable AttributeSet set){
        sRectSquare = new Rect();
        sPaintSquare = new Paint(Paint.ANTI_ALIAS_FLAG);
        sPaintSquare.setColor(sSquareColor);
        if(set==null)
            return;
        TypedArray ta = getContext().obtainStyledAttributes(set, R.styleable.SavedView);
        sSquareSize = ta.getDimensionPixelSize(R.styleable.SavedView_saved_size, SQUARE_SIZE_DEF);
        ta.recycle();
    }
    protected void Draw_Saved() {
        sSquareColor = mCustomView.Color_piece[mCustomView.savedPiece];
        sPaintSquare.setColor(sSquareColor);
    }
    @Override
    protected void onDraw(Canvas canvas){
        postInvalidate();
        if(mCustomView.savedPiece==-1){ //doesn't print a piece until hold has been called at least once
            return;
        }
        Draw_Saved();
        for(Point point: mCustomView.Pieces[mCustomView.savedPiece][0]){
            sRectSquare.left = (dest.x+point.x)*sSquareSize;
            sRectSquare.top = (dest.y+point.y)*sSquareSize;
            sRectSquare.right = sRectSquare.left+sSquareSize;
            sRectSquare.bottom = sRectSquare.top+sSquareSize;
            canvas.drawRect(sRectSquare, sPaintSquare);
        }
    }
}

