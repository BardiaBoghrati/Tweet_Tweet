/* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package twitter;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * SocialNetwork provides methods that operate on a social network.
 * 
 * A social network is represented by a Map<String, Set<String>> where map[A] is
 * the set of people that person A follows on Twitter, and all people are
 * represented by their Twitter usernames. Users can't follow themselves. If A
 * doesn't follow anybody, then map[A] may be the empty set, or A may not even exist
 * as a key in the map; this is true even if A is followed by other people in the network.
 * Twitter usernames are not case sensitive, so "ernie" is the same as "ERNie".
 * A username should appear at most once as a key in the map or in any given
 * map[A] set.
 * 
 * DO NOT change the method signatures and specifications of these methods, but
 * you should implement their method bodies, and you may add new public or
 * private methods or classes if you like.
 */
public class SocialNetwork {

    /**
     * Guess who might follow whom, from evidence found in tweets.
     * 
     * @param tweets
     *            a list of tweets providing the evidence, not modified by this
     *            method.
     * @return a social network (as defined above) in which Ernie follows Bert
     *         if and only if there is evidence for it in the given list of
     *         tweets.
     *         One kind of evidence that Ernie follows Bert is if Ernie
     *         @-mentions Bert in a tweet. This must be implemented. Other kinds
     *         of evidence may be used at the implementor's discretion.
     *         All the Twitter usernames in the returned social network must be
     *         either authors or @-mentions in the list of tweets.
     */
    public static Map<String, Set<String>> guessFollowsGraph(List<Tweet> tweets) {
        //a map with case insensitive keys
        final Map<String,Set<String>> followsGraph = new TreeMap<String,Set<String>>(String.CASE_INSENSITIVE_ORDER);
        
        for(Tweet tweet : tweets){
            final String author = tweet.getAuthor();
            if(!followsGraph.containsKey(author)){
                final Set<String> mentionedUsers = 
                        Extract.getMentionedUsers(Filter.writtenBy(tweets, author));
                
                //the set s stored in map for a given key must also have case insensitive property.
                //That is, s.contains(x) iff. s.contains(x.toLowerCase()) for any string x
                final Set<String> caseInsensitiveMentions = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
                caseInsensitiveMentions.addAll(mentionedUsers);
                caseInsensitiveMentions.remove(author);
                
                followsGraph.put(author, caseInsensitiveMentions);
            }
        }
        
        return followsGraph;
    }

    /**
     * Find the people in a social network who have the greatest influence, in
     * the sense that they have the most followers.
     * 
     * @param followsGraph
     *            a social network (as defined above)
     * @return a list of all distinct Twitter usernames in followsGraph, in
     *         descending order of follower count.
     */
    public static List<String> influencers(Map<String, Set<String>> followsGraph) {
        //having a case insensitive frequency histogram simplifies the problem
        Map<String,Integer> followerCount = new TreeMap<String,Integer>(String.CASE_INSENSITIVE_ORDER);
        
        //construct a mapping in followerCount; such, every username in followerCount key set is
        //associated with a number of times it appears in either the key set or the set associated
        //with a key in followsGraph
        for(String usr : followsGraph.keySet()){
            followerCount.putIfAbsent(usr, 0);
            for(String followedByUsr : followsGraph.get(usr)){
                
                followerCount.put(followedByUsr, 
                        followerCount.getOrDefault(followedByUsr, 0) + 1);
                
            }
        }
        
        String[] users = new String[followerCount.size()];
        followerCount.keySet().toArray(users);
        
        Arrays.sort(users, new Comparator<String>(){
            @Override
            public int compare(String o1, String o2) {
                
                final Integer val1 = followerCount.get(o1);
                final Integer val2 = followerCount.get(o2);
                
                return -Integer.compare(val1, val2);
            }      
        });
        
        return Arrays.asList(users);
    }
}
