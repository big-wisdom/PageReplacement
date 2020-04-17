import java.util.ArrayList;

public class TaskFIFO implements Runnable{
    private int[] sequence;
    private int maxMemoryFrames;
    private int maxPageReference;
    private int[] pageFaults;
    private ArrayList<Integer> memory = new ArrayList<>();

    TaskFIFO(int[] sequence, int maxMemoryFrames, int maxPageReference, int[] pageFaults){
        this.sequence = sequence;
        this.maxMemoryFrames = maxMemoryFrames;
        this.maxPageReference = maxPageReference;
        this.pageFaults = pageFaults;
    }

    @Override
    public void run() {
        int faults = 0;
        // work through sequence
        for(int index=0; index<sequence.length; index++) {
            int pageRequested = sequence[index];

            // if number is in memory go to next
            // else page fault and replace the first in
            if(!memory.contains(pageRequested)){
                // pop off the oldest (first in) if the memory is full
                if(memory.size() >= maxMemoryFrames){
                    memory.remove(0);
                }
                memory.add(pageRequested);
                faults++;
            }
        }
        // return results
        pageFaults[maxMemoryFrames-1] = faults;
    }
}
