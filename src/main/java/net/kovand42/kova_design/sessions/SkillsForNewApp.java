package net.kovand42.kova_design.sessions;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

@Component
@SessionScope
public class SkillsForNewApp implements Serializable {
    private final long serialVersionUID = 1L;
    private Set<Long> newSkills = new LinkedHashSet<>();
    private boolean notEmpty = false;

    public void add (long id) {
        newSkills.add(id);
        this.notEmpty = true;
    }

    public boolean contains (long id) {
        return newSkills.contains(id);
    }

    public boolean isEmpty () {
        return !notEmpty;
    }

    public void remove (long id) {
        if (newSkills.size() == 1) {
            newSkills.remove(id);
            this.notEmpty = false;
        } else {
            newSkills.remove(id);
        }
    }

    public int getSize(){
        return newSkills.size();
    }

    public Set<Long> getNewSkills () {
        return Collections.unmodifiableSet(newSkills);
    }
}
