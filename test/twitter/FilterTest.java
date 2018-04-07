/* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package twitter;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

public class FilterTest {

    /*
     * Test strategy
     * 
     * writtenBy():
     * 
     * length of tweets (1):
     *  no tweets (1.1), single tweet (1.2), multiple tweets (1.3);
     * 
     * number of tweets written by username (2):
     *  none (2.1), single (2.2), multiple (2.3);
     *  
     * inTimeSpan():
     * 
     * number of tweets (1):
     * no tweets (1.1), 
     * single tweet (1.2),
     * multiple tweets (1.3);
     * 
     * number of tweets within time span (2):
     * no tweets (2.1), single tweet (2.2), multiple tweets (2.3);
     * 
     * length of timespan (3):
     * 0 (3.1), > 0 (3.2), max length (3.3);
     * 
     * contains a tweet well below the lower bound of timespan (4.1),
     * else (4.2);
     * 
     * contains a tweet just below the lower bound of timespan (5.1),
     * else (5.2);
     * 
     * contains a tweet on the lower bound (6.1),
     * else (6.2);
     * 
     * contains a tweet just above the lower bound of timespan (7.1),
     * else (7.2);
     * 
     * contains a tweet within the time span (8.1),
     * else (8.2);
     *
     * contains a tweet just below the upper bound (9.1),
     * else (9.2);
     * 
     * contains a tweet on the upper bound  (10.1),
     * else (10.2);
     * 
     * contains a tweet just above the upper bound (11.1),
     * else (11.2);
     * 
     * contains a tweet well above the upper bound (12.1),
     * else (12.2);
     * 
     * containing():
     * 
     * number of tweets (1):
     * 0 (1.1), 1 (1.2), >1 (1.3);
     * 
     * number of matching tweets (2):
     * 0 (2.1), 1 (2.2), >1 (2.3);
     * 
     * number of words (3):
     * 0 (3.1), 1 (3.2), >1 (3.3);
     * 
     * number of matching words (4):
     * 0 (4.1), 1 (4.2), >1 (4.3);
     * 
     * length of the longest run of white space characters (5):
     * 0 (5.1), 1 (5.2), >1 (5.3);
     * 
     * matching words delimited by punctuation (6.1),
     * else (6.2);
     * 
     * matching words at ends of the string (7.1),
     * else (7.2);
     * 
     * 
     * 
     */
    
    private static final Instant d1 = Instant.parse("2016-02-17T10:00:00Z");
    private static final Instant d2 = Instant.parse("2016-02-17T11:00:00Z");
    
    private static final Instant d3 = Instant.parse("3024-02-17T22:00:00Z");
    private static final Instant d4 = Instant.parse("1000-02-17T08:00:00Z");
    
    
    
    private static final Tweet tweet1 = new Tweet(1, "alyssa", "is it reasonable to talk about rivest so much?", d1);
    private static final Tweet tweet2 = new Tweet(2, "bbitdiddle", "rivest talk in 30 minutes #hype", d2);
    
    private static final Tweet tweet3 = new Tweet(3, "john", "A\nB B C", d3);
    private static final Tweet tweet4 = new Tweet(4, "JoHn", "B Bb C\ta", d4);
    private static final Tweet tweet5 = new Tweet(5, "donald", " \rA\f b  ", d2);
    private static final Tweet tweet6 = new Tweet(6, "george", "A;B:c,D?e.", d2);
    
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
    /////////// writtenBy() tests ////////////
    
    //covers writtenBy(): 1.3, 2.1
    @Test
    public void testWrittenByMultipleTweetsNoResults(){
        List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(tweet1, tweet2), "john");
        
        assertTrue("expected empty list but got: " + writtenBy, writtenBy.isEmpty());
    }
    
    //covers writtenBy(): 1.3, 2.2
    @Test
    public void testWrittenByMultipleTweetsSingleResult() {
        List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(tweet1, tweet2), "alyssa");
        
        assertEquals("expected singleton list", 1, writtenBy.size());
        assertTrue("expected list to contain tweet", writtenBy.contains(tweet1));
    }
    
    //covers writtenBy(): 1.3, 2.3
    @Test
    public void testWrittenByMutlipleTweetsMultipleResults(){
        List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(tweet1, tweet3, tweet4), "john");
        List<Tweet> expectedResult = Arrays.asList(tweet3, tweet4);
        
        assertEquals("expected " + expectedResult + " but got " +  writtenBy , writtenBy, expectedResult);
    }
    
    //covers writtenBy(): 1.2, 2.1
    @Test
    public void testWrittenBySingleTweetNoResults(){
        List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(tweet2), "george");
        List<Tweet> expectedResult = Collections.emptyList();
        
        assertEquals("expected " + expectedResult + " but got " +  writtenBy , writtenBy, expectedResult);
    }
    
    //covers writtenBy(): 1.2, 2.2
    @Test 
    public void testWrittenBySingleTweetSingleResults(){
        List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(tweet6), "george");
        List<Tweet> expectedResult = Arrays.asList(tweet6);
        
        assertEquals("expected " + expectedResult + " but got " +  writtenBy , writtenBy, expectedResult); 
    }
    
    //covers writtenBy(): 1.1, 2.1
    @Test
    public void testWrittenByNoTweetNoResults(){
        List<Tweet> writtenBy = Filter.writtenBy(Collections.emptyList(), "john");
        List<Tweet> expectedResult = Collections.emptyList();
        
        assertEquals("expected " + expectedResult + " but got " +  writtenBy , writtenBy, expectedResult);
    };
    
    ////////// inTimeSpan() tests //////////
    
    //covers inTimeSpan(): 1.3, 2.3, 3.2, 8.1
    @Test
    public void testInTimespanMultipleTweetsMultipleResults() {
        Instant testStart = Instant.parse("2016-02-17T09:00:00Z");
        Instant testEnd = Instant.parse("2016-02-17T12:00:00Z");
        
        List<Tweet> inTimespan = Filter.inTimespan(Arrays.asList(tweet1, tweet2), new Timespan(testStart, testEnd));
        
        assertFalse("expected non-empty list", inTimespan.isEmpty());
        assertTrue("expected list to contain tweets", inTimespan.containsAll(Arrays.asList(tweet1, tweet2)));
        assertEquals("expected same order", 0, inTimespan.indexOf(tweet1));
    }
    
    //covers inTimeSpan(): 1.3, 2.2, 3.1, 4.1, 6.1, 10.1, 12.1 
    @Test
    public void testInTimespanMultipleTweetsSingleResult(){
        Instant testStart = d1;
        Instant testEnd = d1;
        
        List<Tweet> inTimespan = Filter.inTimespan(Arrays.asList(tweet4, tweet1, tweet3), new Timespan(testStart, testEnd));
        List<Tweet> expectedResult = Arrays.asList(tweet1);
        
        assertEquals("expected " + expectedResult + " but got " +  inTimespan , inTimespan, expectedResult);
    }
    
    //covers inTimeSpan(): 1.3, 2.1, 3.2, 5.1, 11.1
    @Test
    public void testInTimespanMultipleTweetsJustOutSideBoundary(){
        Instant testStart = d1.plusMillis(1);
        Instant testEnd = d2.minusMillis(1);
        
        List<Tweet> inTimespan = Filter.inTimespan(Arrays.asList(tweet1, tweet2), new Timespan(testStart, testEnd));
        List<Tweet> expectedResult = Collections.emptyList();
        
        assertEquals("expected " + expectedResult + " but got " +  inTimespan , inTimespan, expectedResult);
    }
    
    //covers inTimeSpan(): 1.3, 2.3, 3.2, 7.1, 9.1
    @Test
    public void testInTimespanMultipleTweetsJustWithinBoundary(){
        Instant testStart = d1.minusMillis(1);
        Instant testEnd = d2.plusMillis(1);
        
        List<Tweet> inTimespan = Filter.inTimespan(Arrays.asList(tweet1, tweet2), new Timespan(testStart, testEnd));
        List<Tweet> expectedResult = Arrays.asList(tweet1, tweet2);
        
        assertEquals("expected " + expectedResult + " but got " +  inTimespan , inTimespan, expectedResult);
    }
    
    //covers inTimeSpan(): 1.2, 2.2, 3.3, 8.1
    public void testInTimespanSingleTweetSingleResultsMaxTimeSpan(){
        Instant testStart = Instant.MIN;
        Instant testEnd = Instant.MAX;
        
        List<Tweet> inTimespan = Filter.inTimespan(Arrays.asList(tweet1), new Timespan(testStart, testEnd));
        List<Tweet> expectedResult = Arrays.asList(tweet1);
        
        assertEquals("expected " + expectedResult + " but got " +  inTimespan , inTimespan, expectedResult);
    }
    
    //covers inTimeSpan(): 1.1
    public void testInTimespanNotweets(){
        Instant testStart = d1;
        Instant testEnd = d2;
        
        List<Tweet> inTimespan = Filter.inTimespan(Collections.emptyList(), new Timespan(testStart, testEnd));
        List<Tweet> expectedResult = Collections.emptyList();
        
        assertEquals("expected " + expectedResult + " but got " +  inTimespan , inTimespan, expectedResult);
    }
    
    ////////// containing() tests //////////
    
    //covers containing(): 1.3, 2.3, 3.2, 4.2, 5.2, 6.2, 7.2
    @Test
    public void testContaining() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet1, tweet2), Arrays.asList("talk"));
        
        assertFalse("expected non-empty list", containing.isEmpty());
        assertTrue("expected list to contain tweets", containing.containsAll(Arrays.asList(tweet1, tweet2)));
        assertEquals("expected same order", 0, containing.indexOf(tweet1));
    }
        
    //covers containing(): 1.1
    @Test
    public void testNoTweets(){
        List<Tweet> containing = Filter.containing(Collections.emptyList(), Arrays.asList("talk"));
        
        assertTrue("expected empty list but got: " + containing, containing.isEmpty());
    }
    
    //covers containing(): 1.2, 2.1, 3.3, 4.1, 5.1, 6.1, 7.2
    @Test
    public void testPunctuationDelimitedMatchingWords(){
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet6), Arrays.asList("A","B","C","D","E"));
        
        assertTrue("expected empty list but got: " + containing, containing.isEmpty());
    }
    
    //covers containing(): 1.3, 2.3, 3.3, 4.2, 5.2, 6.2, 7.1
    @Test
    public void testMatchingWordsAtEndsOfTextString(){
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet3, tweet1, tweet4), Arrays.asList("A","D"));
        List<Tweet> expectedResult = Arrays.asList(tweet3,tweet4);
        
        assertEquals(expectedResult, containing);
    }
    
    //covers containing(): 1.2, 2.2, 3.3, 4.3, 5.3, 6.2, 7.2
    @Test
    public void testMatchingWordsSeparatedWithLongWhiteSpaceRuns(){
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet5), Arrays.asList("a","B"));
        List<Tweet> expectedResult = Arrays.asList(tweet5);
        
        assertEquals("expected " + expectedResult + " but got " +  containing , containing, expectedResult);
    }
    
    //covers containing():3.1
    @Test
    public void testNoWords(){
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet1), Collections.emptyList());
        
        assertTrue("expected empty list but got: " + containing, containing.isEmpty());
    }

    /*
     * Warning: all the tests you write here must be runnable against any Filter
     * class that follows the spec. It will be run against several staff
     * implementations of Filter, which will be done by overwriting
     * (temporarily) your version of Filter with the staff's version.
     * DO NOT strengthen the spec of Filter or its methods.
     * 
     * In particular, your test cases must not call helper methods of your own
     * that you have put in Filter, because that means you're testing a stronger
     * spec than Filter says. If you need such helper methods, define them in a
     * different class. If you only need them in this test class, then keep them
     * in this test class.
     */

}
