package utils;

public class Pair <T, V> {
    private T fst;
    private V snd;

    public Pair(T fst, V snd) {
        this.fst = fst;
        this.snd = snd;
    }

    public Pair() {}

    public void setFst(T fst) {
        this.fst = fst;
    }

    public void setSnd(V snd) {
        this.snd = snd;
    }

    public T getFst() {
        return fst;
    }

    public V getSnd() {
        return snd;
    }
}
