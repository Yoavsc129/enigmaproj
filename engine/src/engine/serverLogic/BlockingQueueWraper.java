package engine.serverLogic;

import engine.bruteForce.tasks.DecodeTask;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class BlockingQueueWraper {
    BlockingQueue<DecodeTask> blockingQueue;
    Object lock;

    public BlockingQueueWraper() {
        blockingQueue = new ArrayBlockingQueue<>(1000);
        lock = new Object();
    }

    public synchronized List<DecodeTask> addTasks(List<DecodeTask> tasks){
        List<DecodeTask> leftovers = new ArrayList<>();
        boolean check;
        for(DecodeTask task: tasks){
            check = blockingQueue.offer(task);
            if(!check)
                leftovers.add(task);
        }
        return leftovers;
    }

    public synchronized List<DecodeTask> getTasks(int count){
        List<DecodeTask> res = new ArrayList<>();
        DecodeTask temp;
        for (int i = 0; i < count; i++) {
            temp = blockingQueue.poll();
            if(temp != null)
                res.add(temp);
            else return res;
        }
        return res;
    }
}
