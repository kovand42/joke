package net.kovand42.kova_design.entities;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Entity
@Table(name = "projectMessages")
@NamedEntityGraph(name = ProjectMessage.WITH_USER_AND_PROJECT,
        attributeNodes = {@NamedAttributeNode("user"),
        @NamedAttributeNode("project")})
public class ProjectMessage implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final String WITH_USER_AND_PROJECT="ProjectMessage.withUser";
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long projectMessageId;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "projectId")
    @NotNull
    private Project project;
    @ManyToOne
    @JoinColumn(name = "userId")
    @NotNull
    private User user;
    @NotNull
    private LocalDateTime messageDateTime;
    @NotBlank
    private String message;
    @Version
    private long version;

    public ProjectMessage() {}

    public ProjectMessage(Project project, User user,
                          LocalDateTime messageDateTime, String message) {
        this.project = project;
        this.user = user;
        this.messageDateTime = messageDateTime;
        this.message = message;
    }

    public long getProjectMessageId() {
        return projectMessageId;
    }

    public Project getProject() {
        return project;
    }

    public User getUser() {
        return user;
    }

    public String getMessageDateTimeString() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String dateTime = messageDateTime.format(dateTimeFormatter);
        return dateTime;
    }

    public LocalDateTime getMessageDateTime() {
        return messageDateTime;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProjectMessage)) return false;
        ProjectMessage that = (ProjectMessage) o;
        return getUser().equals(that.getUser()) &&
                getMessageDateTime().equals(that.getMessageDateTime());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUser(), getMessageDateTime());
    }

    @Override
    public String toString() {
        return "ProjectMessage{" +
                "project=" + project +
                ", user=" + user +
                ", messageDateTime=" + messageDateTime +
                '}';
    }
}
