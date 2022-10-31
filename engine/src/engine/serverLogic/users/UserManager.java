package engine.serverLogic.users;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class UserManager {

    private final String AGENT = "agent";

    private final String ALLY = "ally";

    private final Set<User> usersSet;

    public UserManager() {
        usersSet = new HashSet<>();
    }

    public synchronized void addUser(String username, String type) {
        //maybe later?
        if(type.equals(AGENT))
            usersSet.add(new Agent(username, type));
        else if (type.equals(ALLY))
            usersSet.add(new Ally(username, type));
        else
            usersSet.add(new User(username, type));
    }

    public synchronized void removeUser(String username) {
        for(User user : usersSet)
            if(user.getName().equals(username))
                usersSet.remove(user);
    }

    public synchronized Set<User> getUsers() {
        return Collections.unmodifiableSet(usersSet);
    }

    public User getUser(String userName){
        User res = null;
        for(User u: usersSet)
            if(userName.equals(u.getName()))
                res = u;
        return res;
    }

    public boolean isUserExists(String username) {
        for(User user : usersSet)
            if(user.getName().equals(username))
                return true;
        return false;
    }

}
