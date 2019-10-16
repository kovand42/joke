package net.kovand42.kova_design.forms;

import net.kovand42.kova_design.entities.Project;
import net.kovand42.kova_design.entities.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class ProjectMessageForm {
    @NotNull
    private Project project;
    @NotNull
    private User user;
    @NotBlank
    private String message;

    public ProjectMessageForm(Project project, User user, String message) {
        this.project = project;
        this.user = user;
        this.message = message;
    }

    public Project getProject() {
        return project;
    }

    public User getUser() {
        return user;
    }

    public String getMessage() {
        return message;
    }
}
