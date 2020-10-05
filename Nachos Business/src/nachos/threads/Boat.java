package nachos.threads;
import nachos.ag.BoatGrader;

public class Boat
{
    static BoatGrader bg;
    
    public static void selfTest()
    {
	BoatGrader b = new BoatGrader();
	
	System.out.println("\n ***Testing Boats with only 2 children***");
	begin(10, 10, b);

//	System.out.println("\n ***Testing Boats with 2 children, 1 adult***");
//  	begin(1, 2, b);

//  	System.out.println("\n ***Testing Boats with 3 children, 3 adults***");
//  	begin(3, 3, b);
    }

    public static void begin( int adults, int children, BoatGrader b )
    {
	// Store the externally generated autograder in a class
	// variable to be accessible by children.
	bg = b;

	// Instantiate global variables here
	
	childrenOnOahu = children;
	adultsOnOahu = adults;
	maxChildren = children;
	maxAdults = adults;
	
	
	// Create threads here. See section 3.4 of the Nachos for Java
	// Walkthrough linked from the projects page.

	Runnable r = new Runnable() {
	    public void run() {
                SampleItinerary();
            }
        };
        KThread t = new KThread(r);
        t.setName("Sample Boat Thread");
        t.fork();
        for (int i = 0; i < children; i++)
        {
        	new KThread(new Runnable () { public void run() { ChildItinerary(); } }).setName("C" + i).fork();
        }
        for (int i = 0; i < adults; i++)
        {
        	new KThread(new Runnable () { public void run() { AdultItinerary(); } }).setName("A" + i).fork();
        }
    }

    static void AdultItinerary()
    {
	/* This is where you should put your solutions. Make calls
	   to the BoatGrader to show that it is synchronized. For
	   example:
	       bg.AdultRowToMolokai();
	   indicates that an adult has rowed the boat across to Molokai
	*/
    	// adult goes to island, signals child there to go back and get someone else
		Oahu.acquire();
    	while (boatState == 1 || childrenOnOahu == maxChildren)
    	{
    		if (childrenOnOahu == maxChildren)
    			childOnOahu.wake();
    		adultOnOahu.sleep();
    	}
    	// From here, adult MUST be going to Molokai, then terminated
    	adultsOnOahu--;
    	boatState = 1;
    	Oahu.release();
    	bg.AdultRowToMolokai();
    	Molokai.acquire();
    	adultsOnMolokai++;
    	childOnMolokai.wake();
    	Molokai.release();
    	System.out.println(adultsOnMolokai + " adults have passed.");
    	if (childrenOnMolokai == maxChildren && adultsOnMolokai == maxAdults)
    	{
    		boatState = 2;
    	}
	}

    static void ChildItinerary()
    {
    	// start with 2 children sent
    	// If island not empty on departure, go back
    	// If adult still remaining, drop child and bring the adult
    		// Then bring one child back and grab another child
    	// If only children remaining, take two, bring back one. Rinse repeat
    	// When no more children remaining, done.
    	
    	// child goes to island, signals adult to cross *IF child goes Molokai to Oahu*
    	while (boatState <  2)
    	{
    		if (boatState == 0)
    		{
    			if (!Oahu.isHeldByCurrentThread())
    				Oahu.acquire();
    			if (childrenOnOahu == maxChildren || (adultsOnOahu == 0 && childrenOnOahu != 0) || boatCap == 1)
    			{
    				if (boatCap == 0) 
        			{
        				boatCap = 1;
        				bg.ChildRowToMolokai();
        				//childrenOnOahu--;
        				//childrenOnMolokai++;
        				childOnOahu.wake();
        				Oahu.release();
        				Molokai.acquire();
        				childOnMolokai.sleep();
        				Molokai.release();
        			}
    				else if (boatCap == 1)
        			{
        				//childrenOnOahu--;
        				bg.ChildRideToMolokai();
        				boatState = 1;
        				boatCap = 0;
        				//childrenOnMolokai++;
        				childrenOnOahu -= 2;
        				childrenOnMolokai += 2;
        				Oahu.release();
        			}
    	    		if (boatState == 1 && (adultsOnMolokai != maxAdults || childrenOnMolokai != maxChildren) && childrenOnMolokai != 0)
    	    		{
    	    			Molokai.acquire();
    	    			childrenOnMolokai--;
    	    			bg.ChildRowToOahu();
    	    			boatState = 0;
    	    			Molokai.release();
    	    			Oahu.acquire();
    	    			childrenOnOahu++;
    	    			if (adultsOnOahu > 0)
    	    			{
    	    				adultOnOahu.wake();
    	    				childOnOahu.sleep();
    	    			}
    	    			else if (childrenOnOahu > 1)
    	    			{
    	    				childOnOahu.wake();
    	    				childOnOahu.sleep();
    	    			}
    	    			System.out.println(childrenOnMolokai + " children left on Molokai");
    	    			Oahu.release();
    	    		}
    			}
    			else if (adultsOnOahu != 0 && childrenOnOahu != maxChildren) // Not all adults have left, send them over
    			{
    				adultOnOahu.wake();
    				childOnOahu.sleep();
    				Oahu.release();
    			}
    		}
    		else if (boatState == 1 && (adultsOnMolokai != maxAdults || childrenOnMolokai != maxChildren) && childrenOnMolokai != 0)
    		{
    			Molokai.acquire();
    			childrenOnMolokai--;
    			bg.ChildRowToOahu();
    			boatState = 0;
    			Molokai.release();
    			Oahu.acquire();
    			childrenOnOahu++;
    			if (adultsOnOahu > 0)
    			{
    				adultOnOahu.wake();
    				childOnOahu.sleep();
    			}
    			else if (childrenOnOahu > 1)
    			{
    				childOnOahu.wake();
    				childOnOahu.sleep();
    			}
    			Oahu.release();
    		}

    		if (childrenOnMolokai == maxChildren && adultsOnMolokai == maxAdults)
    		{
    			boatState = 2;
    		}
    	}
    }
 
    static void SampleItinerary()
    {
	// Please note that this isn't a valid solution (you can't fit
	// all of them on the boat). Please also note that you may not
	// have a single thread calculate a solution and then just play
	// it back at the autograder -- you will be caught.
	/*System.out.println("\n ***Everyone piles on the boat and goes to Molokai***");
	bg.AdultRowToMolokai();
	bg.ChildRideToMolokai();
	bg.AdultRideToMolokai();
	bg.ChildRideToMolokai();*/
    }
    static Lock Molokai = new Lock();
    static Lock Oahu = new Lock();
    static Condition childOnOahu = new Condition(Oahu);
    static Condition childOnMolokai = new Condition(Molokai);
    static Condition adultOnMolokai = new Condition(Molokai);
    static Condition adultOnOahu = new Condition(Oahu);
    static int boatState = 0; // 0 = Oahu, 1 = Molokai, 2 = done
    static boolean adultOnM = false;
    static boolean childrenFinished = false;
    static int childrenOnOahu;
    static int childrenOnMolokai = 0;
    static int maxChildren;
    static int adultsOnOahu;
    static int adultsOnMolokai = 0;
    static int maxAdults;
    static int boatCap = 0;
    static int adultsCrossed = 0;
}
