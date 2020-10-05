package nachos.threads;

import java.util.LinkedList;

import nachos.machine.*;

/**
 * A <i>communicator</i> allows threads to synchronously exchange 32-bit
 * messages. Multiple threads can be waiting to <i>speak</i>,
 * and multiple threads can be waiting to <i>listen</i>. But there should never
 * be a time when both a speaker and a listener are waiting, because the two
 * threads can be paired off at this point.
 */
public class Communicator {
	
	/*
	 * This class should have speakers and listeners. Speakers give words to listeners, and there should only be objects existing in 
	 * one of the categories, or else your listeners aren't working. You want to be sure that as soon as one of the categories has
	 * at least 1 object each, then they should be ready to pair and ship. Else, they're waiting for their partner. There should
	 * only be one speaker at a time, no talking over each other!
	 * */
	
    /**
     * Allocate a new communicator.
     */
    public Communicator() {
    }

    /**
     * Wait for a thread to listen through this communicator, and then transfer
     * <i>word</i> to the listener.
     *
     * <p>
     * Does not return until this thread is paired up with a listening thread.
     * Exactly one listener should receive <i>word</i>.
     *
     * @param	word	the integer to transfer.
     */
    
    public void speak(int word) 
    {
    	Lib.assertTrue(!lck.isHeldByCurrentThread());
    	lck.acquire(); 	// Absolutely need to acquire the lock
    	speech.add(word);
    	if(list == 0)
    	{
    		sleepSpeak.sleep();
    	}
    	listSleep.wake();
    	list--;
    	lck.release();
    }

    /**
     * Wait for a thread to speak through this communicator, and then return
     * the <i>word</i> that thread passed to <tt>speak()</tt>.
     *
     * @return	the integer transferred.
     */    
    public int listen() 
    {
    	Lib.assertTrue(!lck.isHeldByCurrentThread());
    	lck.acquire(); 	// Absolutely need to acquire the lock
    	list++;;
    	if(speech.isEmpty())
    	{
    		listSleep.sleep();
    	}
    	sleepSpeak.wake();
    	lck.release();
    	return speech.removeFirst();
    }
    
    private Lock lck = new Lock();
    private Condition2 sleepSpeak = new Condition2(lck);
    private Condition2 listSleep = new Condition2(lck);
    private LinkedList<Integer> speech = new LinkedList<Integer>();
    private int list = 0; // checks if listener present
    
}
