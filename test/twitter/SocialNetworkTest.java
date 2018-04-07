/* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package twitter;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

public class SocialNetworkTest {

    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
    /*
     * Test Strategy for guessFollowsGraph():
     * 
     * number of tweets (1):
     * 0 (1.1), 1 (1.2), >1 (1.3);
     * 
     * number of distinct authors (2):
     * 0 (2.1), 1 (2.2), >1 (2.3);
     * 
     * an author of a tweet mentions itself (3.1),
     * else (3.2);
     * 
     * pair of tweets with same author (4.1),
     * else (4.2);
     * 
     * an author of a tweet makes multiple mentions of same username (5.1),
     * else (5.2);
     * 
     * an author makes only a single mention of a any username (6.1),
     * else (6.2);
     */
    
    //covers guessFollowsGraph(): 1.1
    @Test
    public void testGuessFollowsGraphEmpty() {
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(new ArrayList<>());
        
        assertTrue("expected empty graph", followsGraph.isEmpty());
    }
    
    //covers guessFollowsGraph(): 1.2, 2.2, 3.1, 4.2, 5.1, 6.1
    @Test
    public void testGuessFollosGraphSelfMention(){
        final List<Tweet> evidence = Arrays.asList(new Tweet(1,"A", "mentioning my self @A", Instant.EPOCH));
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(evidence);
        
        assertFalse(followsGraph.getOrDefault("A", Collections.emptySet()).contains("A"));
    }
    
    //covers guessFollowsGraph(): 1.3, 2.2, 3.2, 4.1, 5.1, 6.2
    @Test
    public void testGuessFollosGraphAuthorMakesMultipleDistinctMentions(){
        final Tweet t1 = new Tweet(1,"A","mentioning @B in this tweet", Instant.EPOCH);
        final Tweet t2 = new Tweet(2, "a","repeat @b, also @C @c",Instant.MAX);
        final List<Tweet> evidence = Arrays.asList(t1, t2);
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(evidence);
        
        assertFalse(followsGraph.getOrDefault("A", Collections.emptySet()).contains("A"));
        assertFalse(followsGraph.getOrDefault("D", Collections.emptySet()).contains("D"));
        assertTrue(followsGraph.getOrDefault("a", Collections.emptySet()).containsAll(Arrays.asList("B","C")));
    }
    
    //covers guessFollowsGraph(): 1.3, 2.3, 3.2, 4.2, 5.2, 6.1
    @Test
    public void testGuessFollosGraphDistinctAuthorsMentionEachOther(){
        //(A, {B}), (B, {A})
        final Tweet t1 = new Tweet(1,"A","mentioning @b lower case", Instant.EPOCH);
        final Tweet t2 = new Tweet(2, "b","@A and @a",Instant.MAX);
        final List<Tweet> evidence = Arrays.asList(t1, t2);
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(evidence);
        
        assertTrue(followsGraph.getOrDefault("A", Collections.emptySet()).containsAll(Arrays.asList("B")));
        assertTrue(followsGraph.getOrDefault("b", Collections.emptySet()).containsAll(Arrays.asList("A","a")));
    }
    
    /*
     * Test Strategy for influencers():
     * 
     * number of user-names in graph (1):
     * 0 (1.1), 
     * 1 (1.2), 
     * >1 (1.3):
     * 
     *  all user-names have same number of followers (1.1),
     *  else (1.2);
     *  
     *  at least one user-name follows no other user-name (2.1),
     *  else (2.2);
     *  
     *  at least one user-name is followed by no one (3.1),
     *  else (3.2);
     *    
     * largets number of followers (2):
     * 0 (2.1), 1 (2.2), >1 (2.3);

     */
    
    //covers influencers(): 1.1
    @Test
    public void testInfluencersEmpty() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        
        assertTrue("expected empty list", influencers.isEmpty());
    }
    
    //covers influencers(): 1.2, 2.1
    @Test
    public void testInfluencersSingleUserName(){
        Map<String, Set<String>> followsGraph = new HashMap<>();
        followsGraph.put("A", Collections.emptySet());
        
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        
        assertEquals(1, influencers.size());
        assertEquals("a",influencers.get(0).toLowerCase());   
    }
    
    //covers influencers(): 1.3, 1.3.1.1, 1.3.2.2, 1.3.3.2, 2.2
    @Test
    public void testInfluencersAllUserNamesSameNumberOfFollowers(){
        Map<String, Set<String>> followsGraph = new HashMap<>();
        followsGraph.put("A", new HashSet<String>(Arrays.asList("b")));
        followsGraph.put("B", new HashSet<String>(Arrays.asList("a")));
        
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        String firstElement = influencers.get(0).toLowerCase();
        String secondElement = influencers.get(1).toLowerCase();
        
        assertEquals(2, influencers.size());
        assertTrue(firstElement.equals("a") && secondElement.equals("b")
                || firstElement.equals("b") && secondElement.equals("a"));
    }
    
    //covers influencers(): 1.3, 1.3.1.2, 1.3.2.1, 1.3.3.1, 2.3
    @Test
    public void testInfluencersStricklyDecendingList(){
        Map<String, Set<String>> followsGraph = new HashMap<>();
        followsGraph.put("A1", new HashSet<String>(Arrays.asList("A2","A3")));
        followsGraph.put("a2", new HashSet<String>(Arrays.asList("A3")));
        
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        
        assertEquals(3,influencers.size());
        assertEquals("a3", influencers.get(0).toLowerCase());
        assertEquals("a2", influencers.get(1).toLowerCase());
        assertEquals("a1", influencers.get(2).toLowerCase());    
    }

    /*
     * Warning: all the tests you write here must be runnable against any
     * SocialNetwork class that follows the spec. It will be run against several
     * staff implementations of SocialNetwork, which will be done by overwriting
     * (temporarily) your version of SocialNetwork with the staff's version.
     * DO NOT strengthen the spec of SocialNetwork or its methods.
     * 
     * In particular, your test cases must not call helper methods of your own
     * that you have put in SocialNetwork, because that means you're testing a
     * stronger spec than SocialNetwork says. If you need such helper methods,
     * define them in a different class. If you only need them in this test
     * class, then keep them in this test class.
     */

}
