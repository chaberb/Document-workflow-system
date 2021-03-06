package pl.edu.pw.ee.pyskp.documentworkflow.controller;

import org.apache.log4j.Logger;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.edu.pw.ee.pyskp.documentworkflow.dto.NewTaskForm;
import pl.edu.pw.ee.pyskp.documentworkflow.exception.ProjectNotFoundException;
import pl.edu.pw.ee.pyskp.documentworkflow.exception.TaskNotFoundException;
import pl.edu.pw.ee.pyskp.documentworkflow.exception.UserNotFoundException;
import pl.edu.pw.ee.pyskp.documentworkflow.service.ProjectService;
import pl.edu.pw.ee.pyskp.documentworkflow.service.TaskService;
import pl.edu.pw.ee.pyskp.documentworkflow.service.UserService;

import javax.validation.Valid;

/**
 * Created by p.pysk on 02.01.2017.
 */
@Controller
@RequestMapping("/projects/{projectId}/tasks")
public class TasksController {
    private static final Logger logger = Logger.getLogger(TasksController.class);

    private final TaskService taskService;
    private final UserService userService;
    private final ProjectService projectService;

    public TasksController(TaskService taskService,
                           UserService userService,
                           ProjectService projectService) {
        this.taskService = taskService;
        this.userService = userService;
        this.projectService = projectService;
    }

    @GetMapping
    public String redirectToProject(@PathVariable Long projectId) {
        return String.format("redirect:/projects/%d", projectId);
    }

    @GetMapping("/{taskId}")
    @PreAuthorize("@securityService.isTaskParticipant(#taskId)")
    public String getTaskInfo(@PathVariable long taskId,
                              @PathVariable long projectId,
                              Model model) {
        addTaskToModel(taskId, model);
        addCurrentUserToModel(model);
        addProjectToModel(projectId, model);
        return "task";
    }

    private void addTaskToModel(@PathVariable long taskId, Model model) {
        model.addAttribute("task", taskService.getTaskById(taskId)
                .map(TaskService::mapToTaskInfoDto)
                .orElseThrow(() -> new TaskNotFoundException(taskId)));
    }

    @DeleteMapping("/{taskId}")
    @PreAuthorize("@securityService.canDeleteTask(#taskId)")
    public String deleteTask(@PathVariable Long taskId, @PathVariable Long projectId) {
        logger.debug("Received HTTP DELETE request for deletion task of id=" + taskId);
        taskService.deleteTask(taskId);
        return String.format("redirect:/projects/%d?deleted", projectId);
    }

    @GetMapping("/{taskId}/addParticipant")
    public String redirectToTask(@PathVariable long projectId, @PathVariable long taskId) {
        return String.format("redirect:/projects/%d/tasks/%d", projectId, taskId);
    }

    @PostMapping("/{taskId}/addParticipant")
    @PreAuthorize("@securityService.canAddParticipantToTask(#taskId)")
    public String processAddParticipant(@PathVariable long taskId, @PathVariable long projectId,
                                        @RequestParam(name = "participantEmail") String userEmail,
                                        Model model) {
        addCurrentUserToModel(model);
        try {
            taskService.addParticipantToTask(userEmail, taskId);
        } catch (UserNotFoundException ex) {
            model.addAttribute("addParticipantErrorMessage", "Nie istnieje użytkownik z podanym adresem e-mail");
        }
        addProjectToModel(projectId, model);
        addTaskToModel(taskId, model);
        return "task";
    }

    @GetMapping("/add")
    @PreAuthorize("@securityService.canAddTask(#projectId)")
    public String getNewTaskForm(@ModelAttribute NewTaskForm newTask,
                                 @PathVariable long projectId,
                                 Model model) {
        addCurrentUserToModel(model);
        addProjectToModel(projectId, model);
        return "addTask";
    }

    private void addProjectToModel(long projectId, Model model) {
        model.addAttribute("project",
                projectService.getOneById(projectId)
                        .map(ProjectService::mapToProjectInfoDTO)
                        .orElseThrow(() -> new ProjectNotFoundException(projectId)));
    }

    @PostMapping("/add")
    @PreAuthorize("@securityService.canAddTask(#projectId)")
    public String processNewTaskForm(@PathVariable long projectId,
                                     @ModelAttribute @Valid NewTaskForm newTask,
                                     BindingResult bindingResult,
                                     Model model) {
        if (bindingResult.hasErrors()) {
            addCurrentUserToModel(model);
            addProjectToModel(projectId, model);
            return "addTask";
        }
        long taskId = taskService.createTaskFromForm(newTask, projectId).getId();
        return String.format("redirect:/projects/%d/tasks/%d", projectId, taskId);
    }

    private void addCurrentUserToModel(Model model) {
        model.addAttribute("currentUser", UserService.mapToUserInfoDTO(userService.getCurrentUser()));
    }

}
