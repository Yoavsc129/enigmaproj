package engine.bruteForce.tasks;

public class MyLock {
    private boolean pause = false;

    private int counter = 0;

    public synchronized void paused(){
        pause = true;
        while(pause)
            try{
                wait();
            }catch (InterruptedException e){

            }
    }

    public synchronized void setCounter(int counter) {
        this.counter = counter;
    }

    public synchronized void countdown(){
        counter--;
        if(counter == 0) {
            pause = false;
            notifyAll();
        }
    }

    public synchronized int getCounter() {
        return counter;
    }
}
