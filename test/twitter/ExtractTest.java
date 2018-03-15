/* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package twitter;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

public class ExtractTest {

	/*
	 * Testing Strategy
	 * 
	 * Partitioning for getTimespan:
	 * 
	 * 1. tweets.lenght():
	 * 		(1) 0, (2) 1, (3) 2, (4) tweets.length() > 2;
	 * 
	 * 2. instant of time of a tweet in tweets:
	 * 
	 * 		(1) all same epoch-seconds and all same nanoseconds-of-second,
	 * 		(2) all same epoch-seconds and not all same nanoseconds-of-second,
	 * 		(3) not all same epoch-seconds
	 * 
	 * 3. position of earliest and latest tweets:
	 * 		(1) earliest at the start and latest at the end,
	 * 		(2) earliest at the start and latest not at the end,
	 * 		(3) earliest not at the start and latest at the end,
	 * 		(4) earliest not at the start and latest not at the end; 
	 * 
	 * 4. length of  the time-span:
	 * 		(1) max length,
	 * 		(2) 0,
	 * 		(3) greater than 0 less than max;
	 * 
	 * 5. instant of tweets relative to epoch:
	 * 		(1) some before and some at and some after the epoch,
	 * 		(2) not (1)
	 * 
	 * Partitioning for get mentioned users:
	 * 
	 * contains no tweets; (17)
	 * -------------------
	 * contains exactly one tweet:
	 * 	contains a user-name mention:
	 * 		contains one user-name mention:
	 * 			the user-name is preceded by hashtag, (1)
	 * 			the user-name is preceded by valid user-name character, (2)
	 * 			the user-name is preceded by invalid user-name character, (3)
	 * 			the user-name is preceded by nothing; (4)
	 * 		contains more than one user-name mention:
	 * 			contains a user-name-mention is preceded by a user-name mention,(5)
	 * 			does not contains a user-name mention preceded by another user-name mention; (6)
	 * 	contains no user-name mention; (7)
	 * 	------------
	 * 	contains "@" followed by invalid user-name, (8)
	 * 	else; (9)
	 * 
	 * contains more than one tweet:
	 * 	contains pair of tweets with user-mentions:
	 * 		contains user-name mentions in common, (10)
	 * 		contains no user-name mentions in common; (11) 	
	 * 	contains at most one tweet with user-name mention; (12)
	 * --------------
	 * contains pair of user-name mentions:
	 * 	contains pair of user-name mentions with same user-names:
	 * 		differing in case, (13)
	 * 		not differing in case; (14)
	 * 	all user-name mentions different; (15)
	 * contains at most one user-name mention; (16)
	 * 
	 * 
	 * 
	 * contains no tweets, (1.1)
	 * contains exactly one tweet, (1.2)
	 * contains more than one tweet; (1.3)
	 * 
	 * has mentions (2.1):
	 * 
	 * 	has a mention preceded by a mention, (1.1)
	 * 	else; (1.2)
	 * 
	 * 	has a mention preceded by hashtag, (2.1)
	 * 	else; (2.2)
	 * 
	 * 	has a mention preceded by neither mention nor hashtag but a valid user-name char, (3.1)
	 * 	else; (3.2)
	 * 
	 * 	has a mention preceded by invalid user-name char, (4.1)
	 * 	else; (4.2)
	 * 	
	 *  has a mention preceded by nothing, (5.1)
	 *  else; (5.2)
	 *  
	 *  has mentions over multiple lines, (6.1)
	 *  else; (6.2)
	 *  
	 *  has a pair of mentions with distinct user-names, (7.1)
	 *  else; (7.2)
	 *  
	 *  has a pair of mentions with same user-names differing in case, (8.1)
	 *  else; (8.2)
	 *   
	 *  has a pair of mentions with same user-names not differing in case, (9.1)
	 *  else; (9.2)
	 * has no mentions; (2.2)
	 * 	 	
	 * 
	 * has "@" (3.1):
	 * 	has "@" followed by valid user-name, (1.1)
	 * 	else; (1.2)
	 * 
	 * 	has "@" followed by invalid user-name, (2.1)
	 * 	else; (2.2)
	 * 	
	 * 	has "@" followed by nothing, (3.1)
	 * 	else; (3.2)
	 * has no "@"; (3.2)
	 * 
	 * has pair of tweets with mentions having user-names in common, (4.1)
	 * else; (4.2)
	 * 
	 * has pair of tweets with no mentions having user-names in common
	 * 	one of them having no mention, (5.1)
	 * else; (5.2)
	 * 
	 * has pair of tweets with no mentions having user-names in common
	 * 	both having mentions; (6.1)
	 * else; (6.2)
	 * 
	 * has empty tweet, (7.1)
	 * all tweets none empty; (7.2)
	 * 
	 * 
	 */
    
    private static final Instant d1 = Instant.parse("2016-02-17T10:00:00Z");
    private static final Instant d2 = Instant.parse("2016-02-17T11:00:00Z");
    private static final Instant d3 = Instant.parse("1939-09-01T12:00:00Z");
    private static final Instant d4 = Instant.parse("1945-09-01T12:00:00Z");
    private static final Instant d5 = Instant.parse("1776-07-01T12:00:00Z");
    
    

    
    private static final Tweet tweet1 = new Tweet(1, "alyssa", "is it reasonable to talk about rivest so much?", d1);
    private static final Tweet tweet2 = new Tweet(2, "bbitdiddle", "rivest talk in 30 minutes #hype", d2);
    
    private static final Tweet tweet3 = new Tweet(3, "me", "@A_b4@4AB_#4ab@_A4b", d1.plusMillis(5));
    private static final Tweet tweet4 = new Tweet(4, "me", " @A.B4@A_B4@@B_A4 ", d1.plusMillis(10));
    private static final Tweet tweet5 = new Tweet(5, "me", "C:@A_B4%n@A_B4%n@a_b4@.A4B.", d1.plusMillis(15));
    private static final Tweet tweet6 = new Tweet(6, "me", "no user mentions", d1.plusMillis(20));
    private static final Tweet tweet7 = new Tweet(7, "me", "", d3);
    
    private static final Tweet tweet8 = new Tweet(8, "me", "hello world!", d4);
    private static final Tweet tweet9 = new Tweet(9, "me", "hello world!", d5); 
    private static final Tweet tweet10 = new Tweet(10, "me", "hello world!", Instant.MIN);
    private static final Tweet tweet11 = new Tweet(11, "me", "hello world!", Instant.MAX);
    private static final Tweet tweet12 = new Tweet(12, "me", "hello world!", Instant.EPOCH);
    
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
    @Test
    public void testGetTimespanTwoTweets() {
        Timespan timespan = Extract.getTimespan(Arrays.asList(tweet1, tweet2));
        
        assertEquals("expected start", d1, timespan.getStart());
        assertEquals("expected end", d2, timespan.getEnd());
    }
      
    //covers getTimespan: 1.4, 2.3, 3.2, 4.3, 5.1 
    @Test
    public void testGetTimespanAcrossEpoch(){
    	Timespan timespan = Extract.getTimespan(Arrays.asList(tweet7, tweet12, tweet2, tweet8));
    	
        assertEquals("expected start", d3, timespan.getStart());
        assertEquals("expected end", d2, timespan.getEnd());	
    }
    
    //covers getTimespan: 3.3, 4.1, 5.2
    @Test
    public void testGetTimespanMaxTimespanDuration(){
    	Timespan timespan = Extract.getTimespan(Arrays.asList(tweet1, tweet7, tweet10, tweet11));
    	
        assertEquals("expected start", Instant.MIN, timespan.getStart());
        assertEquals("expected end", Instant.MAX, timespan.getEnd());
    }
    
    //covers getTimespan: 3.1
    @Test
    public void testGetTimespanMaxBeforeEpoch(){
    	Timespan timespan = Extract.getTimespan(Arrays.asList(tweet7, tweet8, tweet9));
    	
        assertEquals("expected start", d3, timespan.getStart());
        assertEquals("expected end", d5, timespan.getEnd());
    }
    
    //covers getTimespan: 2.2, 3.4
    @Test
    public void testGetTimespanFractionalDifference(){
    	Timespan timespan = Extract.getTimespan(Arrays.asList(tweet4,tweet6,tweet3,tweet5));
    	
        assertEquals("expected start", d1.plusMillis(5), timespan.getStart());
        assertEquals("expected end", d1.plusMillis(20), timespan.getEnd());
    }
    
    //covers getTimespan: 1.2, 2.1, 4.2
    @Test
    public void testGetTimespanLenghtOneList(){
    	Timespan timespan = Extract.getTimespan(Arrays.asList(tweet12));
    	
        assertEquals("expected start", Instant.EPOCH, timespan.getStart());
        assertEquals("expected end", Instant.EPOCH, timespan.getEnd());
    }
    
    // covers getMentionedUsers:
    @Test
    public void testGetMentionedUsersNoMention() {

        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet1));
        
        assertTrue("expected empty set", mentionedUsers.isEmpty());
    }
    
    // covers getMentionedUsers:
    @Test
    public void testGetMentionedUsersNoTweets() {

        Set<String> mentionedUsers = Extract.getMentionedUsers(Collections.emptyList());
        
        assertTrue("expected empty set", mentionedUsers.isEmpty());
    }
    
    // covers getMentionedUsers:
    @Test
    public void testGetMentionedUsersSingleTweet() {

        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet3));
        mentionedUsers = toLower(mentionedUsers);
        Set<String> expectedSet = new HashSet<String>(Arrays.asList("a_b4","4ab","_a4b"));
        
        assertTrue("expected mentionedUsers = {a_b4,4ab,_a4b}", mentionedUsers.equals(expectedSet));
    }
    
    // covers getMentionedUsers:
    @Test
    public void testGetMentionedMentionsInCommon() {

        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet3,tweet5));
        mentionedUsers = toLower(mentionedUsers);
        Set<String> expectedSet = new HashSet<String>(Arrays.asList("a_b4","4ab","_a4b"));
        
        assertTrue("expected mentionedUsers = {a_b4,4aB,_a4b}", mentionedUsers.equals(expectedSet));
    }
    
    // covers getMentionedUsers:
    @Test
    public void testGetMentionedNoMentionsInCommon() {

        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet6,tweet4));
        mentionedUsers = toLower(mentionedUsers);
        Set<String> expectedSet = new HashSet<String>(Arrays.asList("a","a_b4"));
        assertTrue("expected mentionedUsers = {a,b_a4}", mentionedUsers.equals(expectedSet));
    }
    
    // covers getMentionedUsers: 
    @Test
    public void testGetMentionedOverMultipleLines() {

        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet4,tweet5));
        mentionedUsers = toLower(mentionedUsers);
        Set<String> expectedSet = new HashSet<String>(Arrays.asList("a","b_a4","a_b4"));
        assertTrue("expected mentionedUsers = {a,b_a4,a_b4}", mentionedUsers.equals(expectedSet));
    }
    
    // covers getMentionedUsers: 
    @Test
    public void testGetMentionedEmptyTweet() {

        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet7));

        assertTrue("expected empty set", mentionedUsers.isEmpty());
    }
    
    
    private Set<String> toLower(Set<String> mentions){
    	Set<String> s = new HashSet<String>();
    	for(String mention: mentions){
    		s.add(mention.toLowerCase());
    	}
    	return s;
    }
    
    /*
     * Warning: all the tests you write here must be runnable against any
     * Extract class that follows the spec. It will be run against several staff
     * implementations of Extract, which will be done by overwriting
     * (temporarily) your version of Extract with the staff's version.
     * DO NOT strengthen the spec of Extract or its methods.
     * 
     * In particular, your test cases must not call helper methods of your own
     * that you have put in Extract, because that means you're testing a
     * stronger spec than Extract says. If you need such helper methods, define
     * them in a different class. If you only need them in this test class, then
     * keep them in this test class.
     */

}
