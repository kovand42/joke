package net.kovand42.kova_design.web;

import net.kovand42.kova_design.entities.*;
import net.kovand42.kova_design.services.*;
import net.kovand42.kova_design.sessions.ProjectSkills;
import net.kovand42.kova_design.sessions.SkillsForNewProject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

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
    @Autowired
    ProjectAuthorityService projectAuthorityService;
    @Autowired
    ProjectMessageService projectMessageService;
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
        AtomicReference<Project> atomicProject = new AtomicReference<>();
        projectService.create(project);
        projectService.findByProjectName(project.getProjectName()).forEach(project1 -> {
            if(project1.getRepository().getUrl().equals(url)){
                atomicProject.set(project1);
            }
        });
        ProjectAuthority projectAuthority = new ProjectAuthority("admin", atomicProject.get());
        projectAuthority.addUser(user);
        projectAuthorityService.create(projectAuthority);
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
    public void deleteProject(Project project){
        List<ProjectMessage> projectMessages = messages(project);
        projectMessages.forEach(projectMessage -> {
            projectMessageService.delete(projectMessage);
        });
        List<ProjectAuthority> projectAuthorities = new LinkedList<>();
        projectAuthorityService.findAllByProject(project).forEach(projectAuthority -> {
            projectAuthorities.add(projectAuthority);

        });
        projectAuthorities.forEach(projectAuthority -> {
            projectAuthorityService.delete(projectAuthority);
        });
        projectService.delete(project);
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
        AtomicInteger counter = new AtomicInteger();
        if(users.size()>0){
            users.forEach(user -> {
                userSkillService.findByUser(user).forEach(userSkill -> {
                    if(userSkill.getSkill().getSkillId() == id){
                        project.add(userSkill);
                        counter.getAndIncrement();
                    }
                    if(counter.get()==0){
                        putMasterUserSkillToSession(project, id);
                    }
                });
            });
        }else{
            putMasterUserSkillToSession(project, id);
        }
        projectService.update(project);
    }

    private void putMasterUserSkillToSession(Project project, long id){
        userSkillService
                .findByUser(userService.findById(1).get())
                .forEach(userSkill1 -> {
                    if(userSkill1.getSkill().getSkillId()==id){
                        project.add(userSkill1);
                    }
                });
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
    public String projectUserAuth(Project project, User user){
        StringBuilder projectUserStringAuth = new StringBuilder();
        List<ProjectAuthority> authList = projectAuthorityService.findAllByProject(project);
        authList.forEach(projectAuthority -> {
            if(projectAuthority.getUsersWithAuth().contains(user)){
                projectUserStringAuth.append(projectAuthority.getProjectAuthority());
            }
        });
        String auth = projectUserStringAuth.toString();
        return auth;
    }
    public Map<ProjectMessage, User> userMessages(Project project, User user){
        List<ProjectMessage> messages = projectMessageService.findByProject(project);
        Map<ProjectMessage, User> messagesMap = new LinkedHashMap<>();
        messages.forEach(message -> {
            messagesMap.put(message, message.getUser());
        });
        return messagesMap;
    }
    public List<ProjectMessage> messages(Project project){
        return projectMessageService.findByProject(project);
    }
    public String redirectToProjectWithId(long projectId){
        StringBuilder strB = new StringBuilder();
        strB.append("redirect:/projects/").append(projectId);
        String redirectURL = strB.toString();
        return redirectURL;
    }
    public String redirectToProfileAfterDeleteProject(long id){
        StringBuilder strB = new StringBuilder();
        strB.append("redirect:/profile/").append(id);
        String redirectURL = strB.toString();
        return redirectURL;
    }
}
