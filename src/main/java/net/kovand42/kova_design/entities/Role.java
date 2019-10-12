package net.kovand42.kova_design.entities;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "roles")
@NamedEntityGraph(name = Role.WITH_USERS,
        attributeNodes = {@NamedAttributeNode("users")})
public class Role implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final String WITH_USERS="Role.withUsers";
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long roleId;
    @NotBlank
    @JoinColumn(name = "roleName", unique = true)
    private String roleName;
    @ManyToMany
    @JoinTable(
            name = "userroles",
            joinColumns = @JoinColumn(name = "roleId"),
            inverseJoinColumns = @JoinColumn(name = "userId"))
    private Set<User> users = new LinkedHashSet<>();
    @Version
    private long version;

    public long getRoleId() {
        return roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public boolean add(User user){
        boolean added = users.add(user);
        if(!user.getRoles().contains(this)){
            user.add(this);
        }
        return added;
    }

    public boolean remove(User user){
        boolean removed = users.remove(user);
        if(user.getRoles().contains(this)){
            user.remove(this);
        }
        return removed;
    }

    public Set<User> getUsers(){
        return Collections.unmodifiableSet(users);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Role)) return false;
        Role role = (Role) o;
        return getRoleName().equalsIgnoreCase(role.getRoleName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRoleName());
    }

    @Override
    public String toString() {
        return roleName;
    }
}
