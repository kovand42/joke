package net.kovand42.kova_design.entities;

import net.kovand42.kova_design.valueobjects.RequestIdentity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "requests")
public class Request {
    @EmbeddedId
    private RequestIdentity requestIdentity;
    private boolean invitation;
    @Version
    private long version;

    public Request() {}

    public Request(RequestIdentity requestIdentity,
                   boolean invitation) {
        this.requestIdentity = requestIdentity;
        this.invitation = invitation;
    }

    public RequestIdentity getRequestIdentity() {
        return requestIdentity;
    }

    public boolean isInvitation() {
        return invitation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Request)) return false;
        Request request = (Request) o;
        return getRequestIdentity().equals(request.getRequestIdentity());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRequestIdentity());
    }

    @Override
    public String toString() {
        return "Request{" +
                "requestIdentity=" + requestIdentity +
                ", invitation=" + invitation +
                '}';
    }
}
