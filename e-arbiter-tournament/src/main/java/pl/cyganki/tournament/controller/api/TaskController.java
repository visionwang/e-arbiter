package pl.cyganki.tournament.controller.api;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.cyganki.tournament.model.CodeSubmitForm;
import pl.cyganki.tournament.model.QuizSubmission;
import pl.cyganki.tournament.model.Task;
import pl.cyganki.tournament.service.TaskService;
import pl.cyganki.utils.model.executor.ExecutionResult;
import pl.cyganki.utils.security.dto.User;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/task")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/{tournamentId}/{taskId}")
    public Task getTask(User user, @PathVariable("tournamentId") String tournamentId,
                        @PathVariable("taskId") String taskId) {
        return taskService.getTaskById(user.getId(), tournamentId, taskId);
    }

    @PostMapping("/submit")
    public ExecutionResult submitCode(User user, @RequestBody @Valid CodeSubmitForm codeSubmitForm) {
        return taskService.submitCode(user.getId(), codeSubmitForm);
    }

    @PostMapping("/submit/quiz")
    public void submitQuiz(User user, @RequestBody @Valid QuizSubmission quizSubmission) {
        taskService.submitQuiz(user.getId(), quizSubmission);
    }
}
