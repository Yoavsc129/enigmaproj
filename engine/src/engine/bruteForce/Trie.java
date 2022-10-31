package engine.bruteForce;

import java.util.*;

public class Trie {
    public class TrieNode{
        private Map<Character, TrieNode> children;
        private char c;
        private boolean isWord;

        public TrieNode(){
            children = new HashMap<>();
        }

        public TrieNode(char c){
            this.c = c;
            children = new HashMap<>();
        }
        public void insert(String word){
            if(word == null || word.isEmpty())
                return;
            char firstChar = word.charAt(0);
            TrieNode child = children.get(firstChar);
            if(child == null){
                child = new TrieNode(firstChar);
                children.put(firstChar, child);
            }
            if(word.length() > 1)
                child.insert(word.substring(1));
            else
                child.isWord = true;
        }
    }

    private TrieNode root;

    public Trie(Collection<String> words) {
        root = new TrieNode();
        for (String word : words)
            root.insert(word);

    }
    public boolean find(String prefix, boolean exact) {
        TrieNode lastNode = root;
        for (char c : prefix.toCharArray()) {
            lastNode = lastNode.children.get(c);
            if (lastNode == null)
                return false;
        }
        return !exact || lastNode.isWord;
    }

    public boolean find(String prefix) {
        return find(prefix, false);
    }

    public void suggestHelper(TrieNode root, List<String> list, StringBuilder curr) {
        if (root.isWord) {
            list.add(curr.toString());
        }

        if (root.children == null || root.children.isEmpty())
            return;

        for (TrieNode child : root.children.values()) {
            suggestHelper(child, list, curr.append(child.c));
            curr.setLength(curr.length() - 1);
        }
    }

    public List<String> suggest(String prefix) {
        List<String> list = new ArrayList<>();
        TrieNode lastNode = root;
        StringBuilder curr = new StringBuilder();
        for (char c : prefix.toCharArray()) {
            lastNode = lastNode.children.get(c);
            if (lastNode == null)
                return list;
            curr.append(c);
        }
        suggestHelper(lastNode, list, curr);
        return list;
    }
}
