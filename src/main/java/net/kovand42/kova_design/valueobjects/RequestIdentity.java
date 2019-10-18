package net.kovand42.kova_design.valueobjects;

import net.kovand42.kova_design.entities.Project;
import net.kovand42.kova_design.entities.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class RequestIdentity implements Serializable {
    private static final long SerialVersionUID = 1L;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "projectId")
    private Project project;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User user;

    public RequestIdentity() {}

    public RequestIdentity(Project project, User user) {
        this.project = project;
        this.user = user;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RequestIdentity)) return false;
        RequestIdentity that = (RequestIdentity) o;
        return getProject().equals(that.getProject()) &&
                getUser().equals(that.getUser());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getProject(), getUser());
    }

    @Override
    public String toString() {
        return "RequestIdentity{" +
                "project=" + project +
                ", user=" + user +
                '}';
    }
}
