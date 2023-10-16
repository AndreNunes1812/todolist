package br.com.asn.todolist.task;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.asn.todolist.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/tasks")
public class TaskController {

  @Autowired
  ITaskRepository taskRepository;

  @PostMapping("/")
  public ResponseEntity create(@RequestBody TaskModel taskModel, HttpServletRequest request) {
    var idUser = request.getAttribute("idUser");

    var currentDate = LocalDateTime.now();

    if(currentDate.isAfter(taskModel.getStartAt()) ||currentDate.isAfter(taskModel.getEndAt())  ) {
      return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("A data de Início / data términio deve ser maior do que a data atual");      
    }

    if(taskModel.getStartAt().isAfter(taskModel.getEndAt()) ) {
      return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("A data de Início deve ser menor do que a data de términio");      
    }

    taskModel.setIdUser((UUID) idUser);
    var task = this.taskRepository.save(taskModel);
    return ResponseEntity.status(HttpStatus.OK).body(task);  
  }

  @GetMapping("/")
  public List<TaskModel> list( HttpServletRequest request) {
    var idUser = request.getAttribute("idUser");
    var tasks = this.taskRepository.findByIdUser((UUID) idUser);
    return tasks;   
  }

  @PutMapping("/{id}")
  public TaskModel update(@RequestBody TaskModel taskModel, HttpServletRequest request, @PathVariable UUID id ) {
    var task = this.taskRepository.findById(id).orElse(null);

    Utils.copyNonNullProperties(taskModel,task);
    return this.taskRepository.save(task);

  }

}
