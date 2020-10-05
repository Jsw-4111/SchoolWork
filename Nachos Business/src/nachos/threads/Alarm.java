package nachos.threads;

import java.util.HashMap;
import java.util.Map.Entry;

import nachos.machine.*;

/**
 * Uses the hardware timer to provide preemption, and to allow Threadss to sleep
 * until a certain time.
 */
public class Alarm {
    /**
     * Allocate a new Alarm. Set the machine's timer interrupt handler to this
     * alarm's callback.
     *
     * <p><b>Note</b>: Nachos will not function correctly with more than one
     * alarm.
     */
    public Alarm() {
	Machine.timer().setInterruptHandler(new Runnable() {
		public void run() { timerInterrupt(); }
	    });
    }

    /**
     * The timer interrupt handler. This is called by the machine's timer
     * periodically (approximately every 500 clock ticks). Causes the current
     * Threads to yield, forcing a context switch if there is another Threads
     * that should be run.
     */
    public void timerInterrupt() {
	KThread.currentThread().yield();
	if(Threads.size() != 0)
	{
		for (Entry<KThread, Long> hash : Threads.entrySet())
		{
			if (hash.getValue() < Machine.timer().getTime())
			{
				hash.getKey().ready();
				Threads.remove(hash.getKey());
			}
		}
	}
	// if hash map not empty, iterate, get key, check if smaller than time, then set to ready
    }

    /**
     * Put the current Threads to sleep for at least <i>x</i> ticks,
     * waking it up in the timer interrupt handler. The Threads must be
     * woken up (placed in the scheduler ready set) during the first timer
     * interrupt where
     *
     * <p><blockquote>
     * (current time) >= (WaitUntil called time)+(x)
     * </blockquote>
     *
     * @param	x	the minimum number of clock ticks to wait.
     *
     * @see	nachos.machine.Timer#getTime()
     */
    public void waitUntil(long x) {
	// for now, cheat just to get something working (busy waiting is bad)
    Machine.interrupt().disable();
    long wakeTime = Machine.timer().getTime() + x;
	
	while (wakeTime > Machine.timer().getTime())
	{
		KThread.currentThread().yield();   
	}
	
	
	
	
	
	
	
	Threads.put(KThread.currentThread(), wakeTime);
	KThread.currentThread().sleep();
	Machine.interrupt().enable();
    }
    
    private HashMap<KThread, Long> Threads = new HashMap<KThread, Long>();
}