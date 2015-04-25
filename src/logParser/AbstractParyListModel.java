/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
    }
    
    public AbstractParyListModel(ArrayList<Attacker> list) {
        arrayList = list;
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
