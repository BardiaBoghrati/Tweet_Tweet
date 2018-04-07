package twitter;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TwitterUtility {
    
    /**
     * @param socialNetwork
     *                  a map associating a string representing valid twitter username with
     *                  a set of strings. Results of a retrieval with equivalent usernames is the same.
     *                  Key set contains at most one represention of any username.
     * @param userName
     *                  valid twitter username.
     * @return The set associated with username if username present in the map, else, returns empty set.
     */
    public static Set<String> get(Map<String,Set<String>> socialNetwork, String userName){
        for(String key : socialNetwork.keySet()){
            if(equalsUsername(key,userName)){
               return socialNetwork.get(key); 
            }
        }
        
        return Collections.emptySet();
    }
    
    /**
     * containment relation in context of usernames
     * 
     * @param usernames1 collection of distinct usernames; 'distinct' as defined by username equality
     * @param usernames2 collection of distinct usernames;
     * @return result is true iff. for each x, containsUsername(username1, x) implies containsUsername(username1, x)
     */
    public static boolean containsAllUsernames(Set<String> usernames1 , Set<String> usernames2){
        return toLower(usernames1).containsAll(toLower(usernames2));
    }
    /**
     * membership relation in context of usernames
     * 
     * @param usernames
     *               represents collection of  distinct usernames. That is, there are no strings s1 and s2 in usernames such that 
     *               equalsUsername(s1, s2 ). 
     * @param username
     *              string representing a valid username defined as in Tweet class
     * @return result is true iff. there is x in usernames such that equalsUsername(x, username)
     */
    public static boolean containsUsername(Set<String> usernames, String username){
        return toLower(usernames).contains(username.toLowerCase());
    }
    
    /**
     * Equality of two usernames.
     * 
     * @param username1 string representing a valid username defined as in Tweet class
     * @param username2 string representing a valid username defined as in Tweet class
     * @return result is true iff. username1.toLowerCase().equals(username2.toLowerCase()) 
     */
    public static boolean equalsUsername(String username1, String username2){
        return username1.toLowerCase().equals(username2.toLowerCase()) ;
    }
    
    public static Set<String> toLower(Set<String> s){
        Set<String> result = new HashSet<String>();
        
        for(String x : s){
            result.add(x.toLowerCase());
        }
        
        return result;
    }
}
