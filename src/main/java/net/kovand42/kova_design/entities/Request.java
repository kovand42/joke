package net.kovand42.kova_design.entities;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "requests")
@NamedEntityGraph(name = Request.WITH_USER_And_PROJECT,
        attributeNodes = {@NamedAttributeNode("project"),
                @NamedAttributeNode("user")})
public class Request implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final String WITH_USER_And_PROJECT = "Request.withProjectAndUser";
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long requestId;
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "projectId")
    private Project project;
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User user;
    private boolean invitation;
    @Version
    private long version;

    public Request() {}

    public Request(Project project, User user, boolean invitation) {
        this.project = project;
        this.user = user;
        this.invitation = invitation;
    }

    public long getRequestId() {
        return requestId;
    }

    public Project getProject() {
        return project;
    }

    public User getUser() {
        return user;
    }

    public boolean isInvitation() {
        return invitation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Request)) return false;
        Request request = (Request) o;
        return getProject().equals(request.getProject()) &&
                getUser().equals(request.getUser());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getProject(), getUser());
    }

    @Override
    public String toString() {
        return "Request{" +
                "project=" + project +
                ", user=" + user +
                '}';
    }
}
