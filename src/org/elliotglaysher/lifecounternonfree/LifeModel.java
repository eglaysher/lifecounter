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
package org.elliotglaysher.lifecounternonfree;

import java.io.Serializable;
import java.util.List;
import java.util.Vector;

/**
 * Model that holds both the current life total and a list of strings that
 * represent the history that brought the player to his current total.
 * 
 * @author glaysher@umich.edu
 */
public class LifeModel implements Serializable {
    static final private int LIFE_START = 20;
    
    private int currentLifeTotal;
    private int committedLifeTotal;
    private Vector<String> lifeHistory;

    private boolean valueCommitted;
    
    public LifeModel() {
        currentLifeTotal = LIFE_START;
        committedLifeTotal = LIFE_START;
        lifeHistory = new Vector<String>();
        lifeHistory.add(String.valueOf(LIFE_START));
        valueCommitted = true;
    }

    public int getLife() {
        return currentLifeTotal;
    }

    public List<String> getHistoryList() {
        return lifeHistory;
    }
    
    public void setLife(int newTotal) {
        if (currentLifeTotal != newTotal) {
            StringBuilder newLifeString = new StringBuilder();
            int difference = newTotal - committedLifeTotal;
            
            newLifeString.append(newTotal);
            newLifeString.append(" (");

            if (difference > 0)
                newLifeString.append('+');
            newLifeString.append(difference);

            newLifeString.append(")");

            currentLifeTotal = newTotal;

            if (valueCommitted) {
                valueCommitted = false;
                lifeHistory.add(newLifeString.toString());
            } else {
                lifeHistory.remove(lifeHistory.size() - 1);
                
                if (difference != 0) {
                    lifeHistory.add(newLifeString.toString());
                } else {
                    // We are back to where we started.
                    valueCommitted = true;
                }
            }
        }
    }
    
    /**
     * Tells the model that new modifications to the life score should be
     * placed on their own line.
     */
    public void commitValue() {
        valueCommitted = true;
        committedLifeTotal = currentLifeTotal;
    }
    
    /**
     *  I don't know what this is, but java wants it.
     */
    private static final long serialVersionUID = -5771572687170326848L;
}
