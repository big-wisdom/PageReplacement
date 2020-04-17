import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

class PageReplacement {
    public static void main(String[] args) {
        // Initiate thread pool
        int cpuCount = Runtime.getRuntime().availableProcessors();
        ExecutorService threadPool = Executors.newFixedThreadPool(cpuCount);

        // Create results arrays
        int FIFOwins = 0;
        int LRUwins = 0;
        int MRUwins = 0;

        // Arrays for results
        ArrayList<int[]> FIFOresults = new ArrayList<>();
        ArrayList<int[]> LRUresults = new ArrayList<>();
        ArrayList<int[]> MRUresults = new ArrayList<>();

        // Create arrays for beladys anomalies
        ArrayList<BeladyAnomaly> FIFObelady = new ArrayList<>();
        ArrayList<BeladyAnomaly> LRUbelady = new ArrayList<>();
        ArrayList<BeladyAnomaly> MRUbelady = new ArrayList<>();

        // 1000 simulations
        Long start = System.currentTimeMillis();
        for(int simulation = 0; simulation < 1000; simulation++) {
            // create a sequence
            int[] sequence = new int[1000];
            Random random = new Random();
            for(int i=0; i<1000; i++) sequence[i] = random.nextInt(250)+1;

            int[] FIFOresult = new int[101];
            int[] LRUresult = new int[101];
            int[] MRUresult = new int[101];

            // test sequence for 1-100 frames
            for(int frames = 1; frames <= 100; frames++) {
                threadPool.execute(new TaskFIFO(sequence, frames, 250, FIFOresult));
                threadPool.execute(new TaskLRU(sequence, frames, 250, LRUresult));
                threadPool.execute(new TaskMRU(sequence, frames, 250, MRUresult));
            }

            // Add results
            FIFOresults.add(FIFOresult);
            LRUresults.add(LRUresult);
            MRUresults.add(MRUresult);

        }

        threadPool.shutdown();
        try {
            threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
        } catch (Exception e){
        }
        Long end = System.currentTimeMillis();

        System.out.println("Simulation took "+(end-start)+"ms\n");

        // Calculate and summarize results
        for(int simulation=0; simulation<1000; simulation++){
            for(int frames=1; frames<=100; frames++) {
                // Set the min
                int min = 2000;

                int fifo = FIFOresults.get(simulation)[frames];
                int lru = LRUresults.get(simulation)[frames];
                int mru = MRUresults.get(simulation)[frames];

                min = fifo < min ? fifo : min;
                min = lru < min ? lru : min;
                min = mru < min ? mru : min;

                // add wins
                FIFOwins += fifo == min ? 1 : 0;
                LRUwins += lru == min ? 1 : 0;
                MRUwins += mru == min ? 1 : 0;

                // Check for belady anomaly
                int temp;
                if(frames > 1) {
                    if (fifo > (temp = FIFOresults.get(simulation)[frames - 1]))
                        FIFObelady.add(new BeladyAnomaly(temp, fifo));
                    if (lru > (temp = LRUresults.get(simulation)[frames - 1]))
                        LRUbelady.add(new BeladyAnomaly(temp, lru));
                    if (mru > (temp = MRUresults.get(simulation)[frames - 1]))
                        MRUbelady.add(new BeladyAnomaly(temp, mru));
                }
            }
        }


        System.out.println("FIFO min PF: "+FIFOwins);
        System.out.println("LRU min PF: "+LRUwins);
        System.out.println("MRU min PF: "+MRUwins);

        System.out.println();
        System.out.println("Belady's Anomaly Report for FIFO:");
        int max = 0;
        for(BeladyAnomaly ba: FIFObelady) {
            System.out.println(ba);
            if(ba.difference > max) max = ba.difference;
        }
        System.out.println("Anomaly detected "+FIFObelady.size()+" times with a max difference of "+max);

        System.out.println();
        max = 0;
        System.out.println("Belady's Anomaly Report for LRU:");
        for(BeladyAnomaly ba: LRUbelady) {
            System.out.println(ba);
            if(ba.difference > max) max = ba.difference;
        }
        System.out.println("Anomaly detected "+LRUbelady.size()+" times with a max difference of "+max);

        System.out.println();
        max = 0;
        System.out.println("Belady's Anamoly Report for MRU:");
        for(BeladyAnomaly ba: MRUbelady) {
            System.out.println(ba);
            if(ba.difference > max) max = ba.difference;
        }
        System.out.println("Anomaly detected "+MRUbelady.size()+" times with a max difference of "+max);
    }
}