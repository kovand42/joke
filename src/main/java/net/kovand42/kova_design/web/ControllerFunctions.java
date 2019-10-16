package net.kovand42.kova_design.web;

import net.kovand42.kova_design.entities.*;
import net.kovand42.kova_design.services.*;
import net.kovand42.kova_design.sessions.ProjectSkills;
import net.kovand42.kova_design.sessions.SkillsForNewProject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class ControllerFunctions {
    @Autowired
    ProjectService projectService;
    @Autowired
    UserSkillService userSkillService;
    @Autowired
    SkillService skillService;
    @Autowired
    UserService userService;
    @Autowired
    SkillsForNewProject skillsForNewProject;
    @Autowired
    ProjectSkills projectSkills;
    @Autowired
    RepositoryService repositoryService;
    public List<Skill> makeSkillListFromUserForProfile(User user){
        List<UserSkill> userSkills = userSkillService.findByUser(user);
        List<Skill> skills = new LinkedList<>();
        userSkills.forEach(userSkill -> {
            skills.add(userSkill.getSkill());
        });
        return skills;
    }
    public List<Project> makeProjectListFromUser(User user){
        List<UserSkill> userSkills = userSkillService.findByUser(user);
        List<Project> projects = new LinkedList<>();
        userSkills.forEach(userSkill -> {
            userSkill.getProjects().stream().forEach(project -> {
                if(!projects.contains(project)) {
                    projects.add(project);
                }
            });
        });
        return projects;
    }
    public List<Project> makeNewProjectListForUser(User user){
        List<Project> userProjects = makeProjectListFromUser(user);
        List<Project> projects = projectService.findAll();
        List<Project> newProjectList = new LinkedList<>();
        List<Skill> userSkills = makeSkillListFromUserForProfile(user);
        projects.removeAll(userProjects);
        projects.forEach(application -> {
            application.getUserSkills().stream().forEach(userSkill -> {
                if(userSkills.contains(userSkill.getSkill())
                        &&!newProjectList.contains(application)
                        &&!userProjects.contains(application)){
                    newProjectList.add(application);
                }
            });
        });
        return newProjectList;
    }
    public List<Skill> makeSkillListFromUserForProject(User user){
        List<UserSkill> userSkills = userSkillService.findByUser(user);
        List<Skill> skills = new LinkedList<>();
        userSkills.forEach(userSkill -> {
            if(!skillsForNewProject.contains(userSkill.getSkill().getSkillId())){
                skills.add(userSkill.getSkill());
            }
        });
        return skills;
    }
    public void makeProjectFromUrlAndProjectName(String url, String projectName,
                                                  Principal principal){
        Repository repository = repositoryService.findByUrl(url).get();
        Project project = new Project(projectName, repository);
        User user = userService.findByUsername(principal.getName()).get();
        skillsForNewProject.getNewSkills().stream().forEach(id -> {
            userSkillService.findBySkill(skillService.findById(id).get()).forEach(userSkill -> {
                if(userSkill.getUser().equals(user)){
                    project.add(userSkill);
                }
            });
        });
        projectService.create(project);
        skillsForNewProject.setClear();
    }
    public List<Skill> lackingSkills(User user){
        List<Skill> userSkills = makeSkillListFromUserForProject(user);
        List<Skill> masterSkills = makeMasterSkills();
        masterSkills.removeAll(userSkills);
        return masterSkills;
    }
    public List<Skill> lackingProjectSkills(Project project){
        List<Skill> masterSkills = makeMasterSkills();
        List<Skill> projectSkills = makeProjectSkills(project);
        masterSkills.removeAll(projectSkills);
        return masterSkills;
    }
    public List<Skill> lackingUserSkills(Project project){
        List<Skill> masterSkills = makeProjectSkills(project);
        List<User> users = makeProjectUserList(project);
        users.forEach(user -> {
            if(!(user.getUsername().equals("master"))){
                masterSkills.removeAll(makeSkillListFromUserForProject(user));
            }
        });
        return masterSkills;
    }
    public List<Skill> makeMasterSkills(){
        User master = userService.findById(1).get();
        List<Skill> masterSkills = makeSkillListFromUserForProject(master);
        return masterSkills;
    }
    public List<Skill> makeProjectSkills(Project project){
        List<Skill> projectSkillsLocale = new LinkedList<>();
        project.getUserSkills().stream().forEach(userSkill -> {
            if(!projectSkillsLocale.contains(userSkill.getSkill())){
                projectSkillsLocale.add(userSkill.getSkill());
            }
        });
        projectSkillsLocale.forEach(skill -> {
            projectSkills.add(skill.getSkillId());
        });
        return projectSkillsLocale;
    }
    public void removeSkillFromProject(Project project, long skillId){
        List<User> users = makeProjectUserList(project);
        projectSkills.remove(skillId);
        users.forEach(user -> {
            userSkillService.findByUser(user).forEach(userSkill -> {
                if (userSkill.getSkill().getSkillId() == skillId){
                    project.remove(userSkill);
                }
            });
        });
        projectService.update(project);
    }
    public void addSkillToProject(Project project, long id){
        List<User> users = makeProjectUserList(project);
        projectSkills.add(id);
        users.forEach(user -> {
            userSkillService.findByUser(user).forEach(userSkill -> {
                if(userSkill.getSkill().getSkillId() == id){
                    project.add(userSkill);
                }
            });
        });
        projectService.update(project);
    }
    public List<User> makeProjectUserList(Project project){
        List<User> users = new LinkedList<>();
        project.getUserSkills()
                .stream().forEach(userSkill -> {
            if(!users.contains(userSkill.getUser())){
                users.add(userSkill.getUser());
            }
        });
        return users;
    }
    public List<User> makeLackingSkillsUserList(Project project){
        List<User> users = userService.findAll();
        List<User> lackingUsers = new LinkedList<>();
        users.remove(userService.findByUsername("master").get());
        List<Skill> lackingSkills = lackingUserSkills(project);
        users.forEach(user -> {
            List<Skill> userSkills = makeSkillListFromUserForProject(user);
            AtomicBoolean need = new AtomicBoolean(false);
            userSkills.forEach(userSkill -> {
                if(lackingSkills.contains(userSkill)){
                    need.set(true);
                }
            });
            if((need.get())&&!(makeProjectUserList(project).contains(user))){
                lackingUsers.add(user);
            }
        });
        return lackingUsers;
    }
}
