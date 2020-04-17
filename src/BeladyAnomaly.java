public class BeladyAnomaly {
    int previous;
    int current;
    int difference;

    BeladyAnomaly(int previous, int current){
        this.previous = previous;
        this.current = current;
        this.difference = current - previous;
    }

    @Override
    public String toString() {
        return "Detected - Previous "+previous+" : Current "+current+" ("+difference+")";
    }
}
