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

public class NextView extends View {
    public CustomView mCustomView = new CustomView(getContext());
    private static final int SQUARE_SIZE_DEF = 10;
    private Rect nRectSquare;
    private Paint nPaintSquare;
    private int nSquareColor, nSquareSize;
    Point dest = new Point(3,0);

    public NextView(Context context) {
        super(context);
        init(null);
    }

    public NextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public NextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public NextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }
    public void init(@Nullable AttributeSet set){
        nRectSquare = new Rect();
        nPaintSquare = new Paint(Paint.ANTI_ALIAS_FLAG);
        nPaintSquare.setColor(nSquareColor);

        if(set==null)
            return;
        TypedArray ta = getContext().obtainStyledAttributes(set, R.styleable.NextView);
        nSquareSize = ta.getDimensionPixelSize(R.styleable.NextView_next_size, SQUARE_SIZE_DEF);
        ta.recycle();
    }

    protected void Draw_Next() {
        nSquareColor = mCustomView.Color_piece[mCustomView.print_next];
        nPaintSquare.setColor(nSquareColor);
    }
    @Override
    protected void onDraw(Canvas canvas){
        postInvalidate();
        Draw_Next();
        for(Point point: mCustomView.Pieces[mCustomView.print_next][0]){
            nRectSquare.left = (dest.x+point.x)*nSquareSize;
            nRectSquare.top = (dest.y+point.y)*nSquareSize;
            nRectSquare.right = nRectSquare.left+nSquareSize;
            nRectSquare.bottom = nRectSquare.top+nSquareSize;
            canvas.drawRect(nRectSquare, nPaintSquare);
        }
    }
}
