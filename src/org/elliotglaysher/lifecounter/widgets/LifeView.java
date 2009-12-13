/*
 * Copyright (C) 2009 Elliot Glaysher.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.elliotglaysher.lifecounter.widgets;

import java.util.List;
import java.util.Vector;

import org.elliotglaysher.lifecounter.LifeModel;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.Shader.TileMode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class LifeView extends View {
    static final private float CORNER_RADIUS = 8;
    static final private float TEXT_OFFSET = 8;
    static final private int COLUMN_PADDING = 15;

    // Colors
    static final private int BLACK = Color.LTGRAY;
    static final private int WHITE = Color.rgb(255, 255, 204);
    static final private int BLUE = Color.rgb(204, 204, 255);
    static final private int RED = Color.rgb(255, 204, 204);
    static final private int GREEN = Color.rgb(204, 255, 204);

    static final private int BLACK_BORDER = Color.GRAY;
    static final private int WHITE_BORDER = Color.rgb(255, 255, 136);
    static final private int BLUE_BORDER = Color.rgb(136, 136, 255);
    static final private int RED_BORDER = Color.rgb(255, 136, 136);
    static final private int GREEN_BORDER = Color.rgb(136, 255, 136);

    private LifeModel model;

    private String theme = "Black";
    private Paint backgroundPaint = null;
    private Paint outlinePaint = null;
    private Paint textPaint = null;
    
    public LifeView(Context context) {
        super(context);
        model = null;
    }
    
    public LifeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        model = null;
    }
    
    /**
     * Lazily create our painting structures; we have to do this instead of
     * during the setTheme() method because our views haven't been laid out
     * until after onResume() is called.
     */
    private void buildPaints() {
        backgroundPaint = new Paint();
        backgroundPaint.setAntiAlias(true);

        outlinePaint = new Paint();
        outlinePaint.setStyle(Style.STROKE);
        outlinePaint.setStrokeWidth(1.75f);
        outlinePaint.setAntiAlias(true);

        textPaint = new Paint();
        textPaint.setTextSize(16);
        textPaint.setTypeface(Typeface.defaultFromStyle(1));
        textPaint.setTextAlign(Align.LEFT);
        textPaint.setAntiAlias(true);

        // I'd prefer for these to be integers with a switch statement, but it's
        // not to be. There's currently a bug in the SDK where having a 
        // string-array bound to an integer-array causes a NullPtr deref.
        if ("Black".equals(theme)) {
            backgroundPaint.setColor(BLACK);
            outlinePaint.setColor(BLACK_BORDER);            
        } else if ("White".equals(theme)) {
            backgroundPaint.setColor(WHITE);
            outlinePaint.setColor(WHITE_BORDER);
        } else if ("Blue".equals(theme)) {
            backgroundPaint.setColor(BLUE);
            outlinePaint.setColor(BLUE_BORDER);            
        } else if ("Red".equals(theme)) {
            backgroundPaint.setColor(RED);
            outlinePaint.setColor(RED_BORDER);            
        } else if ("Green".equals(theme)) {
            backgroundPaint.setColor(GREEN);
            outlinePaint.setColor(GREEN_BORDER);
        } else if ("BlueBlack".equals(theme)) {
            backgroundPaint.setShader(buildGradient(BLUE, BLACK));
            outlinePaint.setShader(buildGradient(BLACK_BORDER, BLUE_BORDER));
        } else if ("RedWhite".equals(theme)) {
            backgroundPaint.setShader(buildGradient(RED, WHITE));
            outlinePaint.setShader(buildGradient(WHITE_BORDER, RED_BORDER));
        } else if ("GreenWhite".equals(theme)) {
            backgroundPaint.setShader(buildGradient(GREEN, WHITE));
            outlinePaint.setShader(buildGradient(WHITE_BORDER, GREEN_BORDER));
        } else if ("BlackGreen".equals(theme)) {
            backgroundPaint.setShader(buildGradient(BLACK, GREEN));
            outlinePaint.setShader(buildGradient(GREEN_BORDER, BLACK_BORDER));
        } else if ("WhiteBlack".equals(theme)) {
            backgroundPaint.setShader(buildGradient(WHITE, BLACK));
            outlinePaint.setShader(buildGradient(BLACK_BORDER, WHITE_BORDER));
        } else if ("BlueRed".equals(theme)) {
            backgroundPaint.setShader(buildGradient(BLUE, RED));
            outlinePaint.setShader(buildGradient(RED_BORDER, BLUE_BORDER));
        } else if ("RedGreen".equals(theme)) {
            backgroundPaint.setShader(buildGradient(RED, GREEN));
            outlinePaint.setShader(buildGradient(GREEN_BORDER, RED_BORDER));
        } else if ("WhiteBlue".equals(theme)) {
            backgroundPaint.setShader(buildGradient(WHITE, BLUE));
            outlinePaint.setShader(buildGradient(BLUE_BORDER, WHITE_BORDER));
        } else if ("BlackRed".equals(theme)) {
            backgroundPaint.setShader(buildGradient(BLACK, RED));
            outlinePaint.setShader(buildGradient(RED_BORDER, BLACK_BORDER));
        } else if ("GreenBlue".equals(theme)) {
            backgroundPaint.setShader(buildGradient(GREEN, BLUE));
            outlinePaint.setShader(buildGradient(BLUE_BORDER, GREEN_BORDER));
        } else {
            Log.w("LifeView", "Unsupported theme: " + theme);
        }
    }
    
    public void setTheme(String theme) {
        this.theme = theme;
        backgroundPaint = null;
        invalidate();
    }
    
    public void setModel(LifeModel model) {
        this.model = model;
    }
    
    public void onHistoryChanged() {
        invalidate();
    }

    /**
     * "Struct" used in onDraw to calculate each column's properties.
     */
    private class Column {
        public Column(int firstIndex, int lastIndex, float width) {
            this.firstIndex = firstIndex;
            this.lastIndex = lastIndex;
            this.width = width;
        }
        
        public int firstIndex;
        public int lastIndex;
        public float width;
    };
    
    @Override
    protected void onDraw(Canvas canvas) {
        RectF bounds = new RectF(getPaddingLeft(), getPaddingTop(),
                getWidth() - getPaddingRight(),
                getHeight() - getPaddingBottom());        
        
        if (backgroundPaint == null) {
            buildPaints();
        }
        
        canvas.drawRoundRect(bounds, CORNER_RADIUS, CORNER_RADIUS, backgroundPaint);
        canvas.drawRoundRect(bounds, CORNER_RADIUS, CORNER_RADIUS, outlinePaint);

        if (model != null) {            
            FontMetrics fm = textPaint.getFontMetrics();
            final float textOffset = Math.abs(fm.ascent) + Math.abs(fm.descent) + Math.abs(fm.leading);
            final int entriesInAColumn = (int)(bounds.height() / textOffset);
            final float columnCenteringOffset = ((bounds.height() - (entriesInAColumn * textOffset)) / 2);
            final float startDrawY = bounds.top + Math.abs(fm.ascent) + columnCenteringOffset;
            final List<String> history = model.getHistoryList();
            final List<Column> columns = new Vector<Column>();

            float drawY = startDrawY;
            float drawX = bounds.left + TEXT_OFFSET;
            
            // Step 1: Separate the history list into columns and determine
            // their width.
            int i = 0;
            while (i < history.size()) {
                int columnStart = i;
                float maxWidth = 0;
                
                int endOfCol = Math.min(columnStart + entriesInAColumn, history.size());
                for (int j = i; j < endOfCol; ++j) {
                    maxWidth = Math.max(maxWidth, textPaint.measureText(history.get(j)));
                }
                
                columns.add(new Column(columnStart, endOfCol, maxWidth));
                i = endOfCol;
            }
            
            // Step 2: Figure out how many columns will fit
            int startRenderingCol = columns.size();
            assert(startRenderingCol > 0);
            float displayWidth = 0;
            while (displayWidth < bounds.width() && startRenderingCol > 0) {                
                // Don't add the column padding on the first pass.
                if (startRenderingCol != columns.size()) {
                    displayWidth += COLUMN_PADDING;
                }

                displayWidth += columns.get(startRenderingCol - 1).width;
                if (displayWidth < bounds.width()) {
                    startRenderingCol--;                    
                }
            }
            
            // Step 3: Render!
            for (int columnNum = startRenderingCol; columnNum < columns.size();
                 columnNum++) {
                Column c = columns.get(columnNum);
                
                for (i = c.firstIndex; i < c.lastIndex; ++i) {
                    canvas.drawText(history.get(i), drawX, drawY, textPaint);
                    drawY += textOffset;                    
                }
                
                drawX += c.width + COLUMN_PADDING;
                drawY = startDrawY;
            }
        }
    }
    
    private LinearGradient buildGradient(int leftColor, int rightColor) {
        float firstThird = getWidth() / 3;
        float lastThird = 2 * firstThird;        
        float middle = getHeight() / 2;

        return new LinearGradient(firstThird, middle, lastThird, middle,
                leftColor, rightColor, TileMode.CLAMP);
    }
}
