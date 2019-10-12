package net.kovand42.kova_design.sessions;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

@Component
@SessionScope
public class Open implements Serializable {
    private final long serialVersionUID = 1L;
    private Set<Long> open = new LinkedHashSet<>();
    private boolean notEmpty = false;

    public void add (long id) {
        open.add(id);
        this.notEmpty = true;
    }

    public boolean contains (long id) {
        return open.contains(id);
    }

    public boolean isEmpty () {
        return !notEmpty;
    }

    public void remove (long id) {
        if (open.size() == 1) {
            open.remove(id);
            this.notEmpty = false;
        } else {
            open.remove(id);
        }
    }

    public int getSize(){
        return open.size();
    }

    public Set<Long> getOpen () {
        return Collections.unmodifiableSet(open);
    }
}
