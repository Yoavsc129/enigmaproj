package mainWindow.tasks;

public class MyLock {
    private boolean pause = false;

    public synchronized void paused(){
        pause = true;
        while(pause)
            try{
                wait();
            }catch (InterruptedException e){

            }

    }

    public synchronized void unPause(){
        pause = false;
        notifyAll();
    }
}
