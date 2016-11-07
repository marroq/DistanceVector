/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Stack;

/**
 *
 * @author dfer
 */
public class DV {
    private static Hashtable<String, ArrayList> hash = new Hashtable<String, ArrayList>();
    private static String me = "";
    
    public static void main(String[] args) {
        setMe("A");
        setDV("B","B",7);
        setDV("C","C",11);
        setDV("D","D",5);
        //System.out.println(returnDirty());
        //setDV("B","B",3);
        //setDV("L","L",5);
        //setDV("P","P",5);
        //setDV("Q","Q",5);
        System.out.println(returnDirty());
        setDV("B","C",2);
        System.out.println(returnDirty());
        //System.out.println("Busco mejor ruta: " + getRoute("C"));
    }
    
    //Recibo que router soy
    public static synchronized void setMe(String node) {
        me = node;
    }
    
    public static synchronized void setDV(String column, String row, int coste) {
        //Verifico si ya existo como columna (router)
        if (me.length()>0 && !column.equals(me) && column.length()>0 && row.length()>0 && coste>0) {
            if (!hash.containsKey(column)) {
                ArrayList<Hashtable> list = new ArrayList<Hashtable>();
                Hashtable<String, Integer[]> hashVal = new Hashtable<String, Integer[]>();
                //[0]=costo, [1]=menor, [2]=sucio
                Integer[] flagCoste = new Integer[3];
                flagCoste[0] = coste;
                flagCoste[1] = 1;
                flagCoste[2] = 1;
                hashVal.put(row, flagCoste);
                list.add(hashVal);
                hash.put(column, list);
                findKeys();
            } else {
                //Si la columna(router) existe, verifico existencia de destinos (filas)
                int advance=0;
                int newCoste=0;
                ArrayList list = (ArrayList)hash.get(column);
                for (int i=0;i<list.size();i++) {
                    Hashtable filas = (Hashtable)list.get(i);
                    if (filas.containsKey(row)) {
                        advance=1;
                        
                        //Obtengo costo de la columna (router)
                        for (int j=0;j<list.size();j++) {
                            Hashtable filaVal = (Hashtable)list.get(j);
                            if (filaVal.containsKey(column)) {
                                Integer[] costes = (Integer[])filaVal.get(column);
                                newCoste=costes[0];
                                break;
                            }
                        }
                        
                        //Actualizo el costo del destino (row)
                        Integer[] costes = (Integer[])filas.get(row);
                        costes[0] = newCoste + coste;
                        newCoste=0;
                        
                        //REVISO SI EN EL CAMBIO ALGUIEN ES MENOR (row de cada router(column))
                        //Obtengo las columnas (routers)
                        Stack<String> keycol = new Stack<String>();
                        Enumeration<String> keycolumn = hash.keys();
                        while (keycolumn.hasMoreElements()) {
                            keycol.push(keycolumn.nextElement());
                        }
                        
                        //Obtengo las filas con valor row(destino) de cada columna(router)
                        Object[] values = new Object[keycol.size()];
                        String keyMin=null;
                        Integer[] minCoste = null;
                        for(int j=0;j<keycol.size();j++) {
                            ArrayList vector = (ArrayList)hash.get(keycol.get(j));
                            for (int k=0;k<vector.size();k++) {
                                Hashtable searchFila = (Hashtable)vector.get(k);
                                if (searchFila.containsKey(row)) {
                                    Integer[] cost = (Integer[])searchFila.get(row);
                                    if (cost[1] == 1){
                                        minCoste=cost;
                                        keyMin = keycol.get(j);
                                    }
                                    Hashtable<String, Integer[]> newH = new Hashtable<String, Integer[]>();
                                    newH.put(keycol.get(j),cost);
                                    values[j] = newH;
                                }
                            }
                        }
                        
                        //Si hubiese nuevo mínimo, actualizo tabla
                        for (int j=0; j<keycol.size();j++) {
                            Hashtable temp = (Hashtable)values[j];
                            Integer[] tempCost = (Integer[])temp.get(keycol.get(j));
                            if (tempCost[0] < minCoste[0]) {
                                //ASIGNO EL MENOR Y QUE ESTA SUCIO, AQUI EL HASH ORIGINAL YA ESTÁ ACTUALIZADO
                                tempCost[1] = 1;
                                tempCost[2] = 1;
                                minCoste[1] = 0;
                            }
                        }
                    }
                }
                
                if (advance==0) {
                    Hashtable<String, Integer[]> hashVal = new Hashtable<String, Integer[]>();
                    Integer[] flagCoste = new Integer[3];
                    flagCoste[0] = coste;
                    flagCoste[1] = 0;
                    flagCoste[2] = 0;
                    hashVal.put(row, flagCoste);
                    list.add(hashVal);
                    hash.put(column, list);
                }
            }
        }
    }
    
    //Actualizo los costos a 99 (infinito) para nuevos destinos que sean ingresados
    private static synchronized void findKeys() {
        Stack<String> keycol = new Stack<String>();
        int contiene=0;
        
        Enumeration<String> keycolumn = hash.keys();
        while (keycolumn.hasMoreElements()) {
            keycol.push(keycolumn.nextElement());
        }
        
        if (keycol.size()>1) {
            for (int i=0;i<keycol.size();i++) {
                ArrayList list = (ArrayList)hash.get(keycol.get(i));
                if (list.size() < keycol.size()) {
                    for (int k=0; k<keycol.size();k++) {
                        for (int z=0;z<list.size();z++) {
                            Hashtable temp = (Hashtable)list.get(z);
                            if (!temp.containsKey(keycol.get(k))) {
                                contiene++;
                            } else {
                                contiene=0;
                                break;
                            }
                        }
                        if (contiene>0) {
                            contiene=0;
                            Hashtable<String, Integer[]> newHash = new Hashtable<String, Integer[]>();
                            Integer[] flagCoste = new Integer[3];
                            flagCoste[0] = 99;
                            flagCoste[1] = 0;
                            flagCoste[2] = 0; //debe cambiar a 0, solo es 1 para pruebas
                            newHash.put(keycol.get(k), flagCoste);
                            list.add(newHash);
                        }
                    }
                }
            }
        }
    }
    
    public static synchronized String getRoute(String destiny) {
        Stack<String> keycol = new Stack<String>();
        
        Enumeration<String> keycolumn = hash.keys();
        while (keycolumn.hasMoreElements()) {
            keycol.push(keycolumn.nextElement());
        }
        
        Object[] values = new Object[keycol.size()];
        String keyMin=null;
        Integer[] minCoste = null;
        for(int j=0;j<keycol.size();j++) {
            ArrayList vector = (ArrayList)hash.get(keycol.get(j));
            for (int k=0;k<vector.size();k++) {
                Hashtable searchFila = (Hashtable)vector.get(k);
                if (searchFila.containsKey(destiny)) {
                    Integer[] cost = (Integer[])searchFila.get(destiny);
                    if (cost[1] == 1){
                        keyMin = keycol.get(j);
                        break;
                    }
                }
            }
        }
        
        return keyMin;
    }
    
    public static synchronized String returnDirty() {
        StringBuilder res = new StringBuilder();
        Stack<String> keycol = new Stack<String>();
        Stack<String> keyfila = new Stack<String>();
        int i=0;
        
        Enumeration<String> keycolumn = hash.keys();
        while (keycolumn.hasMoreElements()) {
            keycol.push(keycolumn.nextElement());
        }
        
        for (int j=0;j<keycol.size();j++) {
            ArrayList valcol = (ArrayList)hash.get(keycol.get(j));
            Hashtable filas = (Hashtable)valcol.get(0);
            Enumeration<String> keyrow = filas.keys();
            while (keyrow.hasMoreElements()) {
                keyfila.push(keyrow.nextElement());
            }
        }
        
        for (int j=0; j<keycol.size();j++) {
            ArrayList list = (ArrayList)hash.get(keycol.get(j));
            for (int k=0; k<list.size();k++) {
                Hashtable rows = (Hashtable)list.get(k);
                for (int z=0; z<keyfila.size();z++) {
                    if (rows.get(keyfila.get(z)) != null) {
                        Integer[] costes = (Integer[])rows.get(keyfila.get(z));
                        if (costes[2] == 1) {
                            i++;
                            res.append(keyfila.get(z) + ":" + costes[0] + "\n");
                        }
                        costes[2] = 0;
                        rows.put(keyfila.get(z), costes);
                        list.set(k, rows);
                        hash.put(keycol.get(j), list);
                    }   
                }
            }
        }
        
        if (i>0) {
            res.insert(0, "Len:"+String.valueOf(i)+"\n");
            return res.toString();
        } else {
            return null;
        }
    }
}
