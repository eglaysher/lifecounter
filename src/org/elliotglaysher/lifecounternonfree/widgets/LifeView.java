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
package org.elliotglaysher.lifecounternonfree.widgets;

import java.util.List;
import java.util.Vector;

import org.elliotglaysher.lifecounternonfree.LifeModel;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class LifeView extends View {
    static final private float CORNER_RADIUS = 8;
    static final private float TEXT_OFFSET = 8;
    static final private int COLUMN_PADDING = 15;
    
    private LifeModel model;
    
    private Paint backgroundPaint;
    private Paint outlinePaint;
    private Paint textPaint;
    
    public LifeView(Context context) {
        super(context);
        model = null;
        buildPaints();
    }
    
    public LifeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        model = null;
        buildPaints();
    }
    
    private void buildPaints() {
        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.LTGRAY);
        backgroundPaint.setAntiAlias(true);
        
        outlinePaint = new Paint();
        outlinePaint.setColor(Color.GRAY);
        outlinePaint.setStyle(Style.STROKE);
        outlinePaint.setStrokeWidth(1.75f);
        outlinePaint.setAntiAlias(true);
        
        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(16);
        textPaint.setTypeface(Typeface.defaultFromStyle(1));
        textPaint.setTextAlign(Align.LEFT);
        textPaint.setAntiAlias(true);
    }
    
    public void setTheme(String theme) {
        // I'd prefer for these to be integers with a switch statement, but it's
        // not to be. There's currently a bug in the SDK where having a 
        // string-array bound to an integer-array causes a NullPtr deref.
        if ("Black".equals(theme)) {
            backgroundPaint.setColor(Color.LTGRAY);
            outlinePaint.setColor(Color.GRAY);            
        } else if ("White".equals(theme)) {
            backgroundPaint.setColor(Color.rgb(255, 255, 204));
            outlinePaint.setColor(Color.rgb(255, 255, 136));
        } else if ("Blue".equals(theme)) {
            backgroundPaint.setColor(Color.rgb(204, 204, 255));
            outlinePaint.setColor(Color.rgb(136, 136, 255));            
        } else if ("Red".equals(theme)) {
            backgroundPaint.setColor(Color.rgb(255, 204, 204));
            outlinePaint.setColor(Color.rgb(255, 136, 136));            
        } else if ("Green".equals(theme)) {
            backgroundPaint.setColor(Color.rgb(204, 255, 204));
            outlinePaint.setColor(Color.rgb(136, 255, 136));
        } else {
            Log.w("LifeView", "Unsupported theme: " + theme);
        }
        
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
}
