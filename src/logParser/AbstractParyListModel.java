/*
 * Copyright (C) 2015 Jakob Dagsland Knutsen (JDK)
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
package logParser;

import java.util.ArrayList;
import javax.swing.AbstractListModel;

/**
 *
 * @author Asmodean
 */
public class AbstractParyListModel extends AbstractListModel {

//    new, because an uninitialized list creates nullpointerexception from JList who calls getSize() right away.
    private ArrayList<Attacker> arrayList = new ArrayList<Attacker>();
    private ArrayList listeners;

    public AbstractParyListModel() {
        this(new ArrayList<Attacker>());
    }
    
    public AbstractParyListModel(ArrayList<Attacker> list) {
        this.arrayList = list;
    }
    public void setList(ArrayList<Attacker> list){
        arrayList = list;
    }
    
    public ArrayList getList(){
        return arrayList;
    }
    
    public int getSize() {
        return arrayList.size();
    }

    public Object getElementAt(int index) {
        return arrayList.get(index);
    }
    
    public void add(Attacker attacker){
        arrayList.add(attacker);
//        Indexes aren't correct and aren't used. I just set something random.
        fireContentsChanged(this, 0, getSize());
    }
    
    public void clear(){
        arrayList.clear();
        fireContentsChanged(this, 0, getSize());
    }
    
    public void remove(Attacker attacker){
        arrayList.remove(attacker);
        fireContentsChanged(this, 0, getSize());
    }
}
