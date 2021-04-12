package com.whatsaround.geocell.comparator;


import com.whatsaround.geocell.model.Tuple;

public class EntityLocationComparableTuple<T> extends Tuple<T ,Double> implements Comparable<EntityLocationComparableTuple<T>>{

	public EntityLocationComparableTuple(T first, Double second) {
		super(first, second);
	}

	@Override
	public int compareTo(EntityLocationComparableTuple<T> o) {
        if(o == null) {
            return -1;
        }
        int doubleCompare = this.getSecond().compareTo(o.getSecond());
        
            return doubleCompare;
        
	}

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        @SuppressWarnings("unchecked")
		EntityLocationComparableTuple<T> other = (EntityLocationComparableTuple<T>) obj;
        if (getFirst() == null) {
            if (other.getFirst() != null) {
                return false;
            }
        } 
        return true;
    }


}
