// Modiotis Athanasios    AM: 4736
// Bonitsis Pantelis      AM: 4742
// Sidiropoulos Georgios  AM: 4789

import java.io.*;
import java.util.Random;

// minimum priority queue with which it is implemented the search frontier.
public class MinPQ<Item, Key extends Comparable<Key>> {

    private int N;
    private Key  pqK[];
    private Item pqI[];

    public MinPQ(int maxN) {
        pqK = (Key[]) new Comparable[maxN + 1];
        pqI = (Item[]) new Object[maxN + 1];
        N = 0;
    }

    public int size() {
        return N;
    }


	private void exch(int i , int j){
		Key t = pqK[i];
		Item s = pqI[i];
		pqK[i] = pqK[j];
		pqI[i] = pqI[j];
		pqK[j] = t;
		pqI[j] = s;

	}
	private boolean more(int i, int j){
		return (pqK[i].compareTo(pqK[j]) > 0);
	}
	private void fixUp(int k){
		while ( k>1 && more(k/2,k)){
			exch(k,k/2);
			k = k/2;
		}
	}
	private void fixDown(int k){
		int j;
		while (2*k <= size()){
			j = 2*k;
			if (j<size() && more(j,j+1)){
				j++;
			}
			if (!more(k,j)){
				break ;
			}
			exch(k,j);
			k = j ;
		}
	}

    public void insert(Item v, Key k) {
		pqK[++N]=k;
		pqI[N]=v;
		fixUp(N);
    }

    public Item minItem() {
        return pqI[1];
    }

    public Key minKey() {
        return pqK[1];
    }

    public void delMin() {
		Key min = pqK[1];
		exch(1,size());
		pqI[N] = null;
		pqK[N--] = null;
		fixDown(1);
		//printPQ();
    }

    /*public void printPQ() {        //debugging
        System.out.println("");
        for (int i = 1; i <= N; i++) {
            System.out.println("" + i + ": Item = " + pqI[i] + ", key = " + pqK[i]);
        }
        System.out.println("");
    }*/
}
