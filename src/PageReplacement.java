import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

class PageReplacement {
    public static void main(String[] args) {
        // Initiate thread pool
        int cpuCount = Runtime.getRuntime().availableProcessors();

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
        // TODO: dont forget to change from 1 to 1000
        for(int simulation = 0; simulation < 1000; simulation++) {
            ExecutorService threadPool = Executors.newFixedThreadPool(cpuCount);
            // create a sequence
            int[] sequence = new int[1000];
            Random random = new Random();
            for(int i=0; i<1000; i++) sequence[i] = random.nextInt(250)+1;

            int[] FIFOresult = new int[100];
            int[] LRUresult = new int[100];
            int[] MRUresult = new int[100];

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

        for(int round=0; round<100; round++){
            // Establish the minimum
            int min = 1001;
            if(FIFOresult[round] < min ) min = FIFOresult[round];
            if(LRUresult[round] < min ) min = LRUresult[round];
            if(MRUresult[round] < min ) min = MRUresult[round];

            // add a win for all winners
            String winner = "";
            if(FIFOresult[round] == min ) {
                FIFOwins++;
                winner += "FIFO";
            }
            if(LRUresult[round] == min ) {
                LRUwins++;
                winner += ", LRU";
            }
            if(MRUresult[round] == min ) {
                MRUwins++;
                winner += ", MRU";
            }
            // System.out.println("Round: "+round+" FIFO: "+FIFOresult[round]+" LRU: "+LRUresult[round]+" MRU: "+MRUresult[round]+" winner: "+winner);

            // Check for anomaly
            if(round > 1 && FIFOresult[round] > FIFOresult[round-1]) FIFObelady.add(new BeladyAnomaly(FIFOresult[round-1], FIFOresult[round]));
            if(round > 1 && LRUresult[round] > LRUresult[round-1]) LRUbelady.add(new BeladyAnomaly(LRUresult[round-1], LRUresult[round]));
            if(round > 1 && MRUresult[round] > MRUresult[round-1]) MRUbelady.add(new BeladyAnomaly(MRUresult[round-1], MRUresult[round]));
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