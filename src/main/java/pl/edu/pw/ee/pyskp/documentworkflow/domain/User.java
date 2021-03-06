package pl.edu.pw.ee.pyskp.documentworkflow.domain;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by piotr on 11.12.16.
 */
@Entity
@Table(name = "Users", indexes = {
        @Index(columnList = "id", name = "user_id_hidx"),
        @Index(columnList = "login", name = "user_login_hidx")
})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true, nullable = false)
    private String login;

    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false)
    private String email;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, updatable = false)
    private Date creationDate;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private PersonalData personalData;

    @ManyToMany(mappedBy = "participants", cascade = CascadeType.ALL)
    private List<Task> taskList;

    @OneToMany(mappedBy = "administrator")
    private List<Task> administratedTasks;

    @OneToMany(mappedBy = "administrator")
    private List<Project> administratedProjects;

    @Transient
    public boolean hasAccessToProject(Project project) {
        return this.getAdministratedProjects().contains(project)
                || this.getParticipatedProjects().contains(project);
    }

    @Transient
    public boolean hasAccessToTask(Task task) {
        return getAdministratedTasks().contains(task)
                || getTaskList().contains(task)
                || equals(task.getProject().getAdministrator());
    }

    @Transient
    public Set<Project> getParticipatedProjects() {
        Set<Task> tasks = new HashSet<>(taskList);
        tasks.addAll(administratedTasks);
        return tasks.stream().map(Task::getProject).collect(Collectors.toSet());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return id == user.id
                && login.equals(user.login)
                && email.equals(user.email);
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + login.hashCode();
        result = 31 * result + email.hashCode();
        return result;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public PersonalData getPersonalData() {
        return personalData;
    }

    public void setPersonalData(PersonalData personalData) {
        this.personalData = personalData;
    }

    public List<Task> getTaskList() {
        return taskList;
    }

    public void setTaskList(List<Task> taskList) {
        this.taskList = taskList;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public List<Project> getAdministratedProjects() {
        return administratedProjects;
    }

    public void setAdministratedProjects(List<Project> administratedProjects) {
        this.administratedProjects = administratedProjects;
    }

    public List<Task> getAdministratedTasks() {
        return administratedTasks;
    }

    public void setAdministratedTasks(List<Task> administratedTasks) {
        this.administratedTasks = administratedTasks;
    }
}
