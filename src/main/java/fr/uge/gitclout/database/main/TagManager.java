package fr.uge.gitclout.database.main;

import fr.uge.gitclout.database.entities.Tag;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Objects;

public class TagManager {
    private final LinkedHashMap<Tag, ContributorManager> tagManager;

    public TagManager() {
        this.tagManager = new LinkedHashMap<>();
    }

    /**
     * Add ContributorManager as a value of the key tag
     * @param tag key
     * @param contributors value
     */
    public void add(Tag tag, ContributorManager contributors) {
        Objects.requireNonNull(tag);
        Objects.requireNonNull(contributors);

        tagManager.put(tag, contributors);
    }

    /**
     * Return HashMap
     * @return HashMap with tag as key and ContributorManager as value
     */
    public HashMap<Tag, ContributorManager> getTagManager() {
        return tagManager;
    }

    @Override
    public String toString() {
        return tagManager + " SIZE = " + tagManager.size();
    }
}
