package dev.lilianagorga.mywebsite.controller;

import dev.lilianagorga.mywebsite.entity.Message;
import dev.lilianagorga.mywebsite.entity.Project;
import dev.lilianagorga.mywebsite.entity.User;
import dev.lilianagorga.mywebsite.repository.MessageRepository;
import dev.lilianagorga.mywebsite.repository.UserRepository;
import dev.lilianagorga.mywebsite.service.EmailSender;
import dev.lilianagorga.mywebsite.service.MessageService;
import dev.lilianagorga.mywebsite.service.ProjectService;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

  private final ProjectService projectService;
  private final MessageService messageService;
  private final MessageRepository messageRepository;
  private final UserRepository userRepository;
  private final EmailSender emailSender;
  private final PasswordEncoder passwordEncoder;

  public AdminController(ProjectService projectService,
                          MessageService messageService,
                          MessageRepository messageRepository,
                          UserRepository userRepository,
                          EmailSender emailSender,
                          PasswordEncoder passwordEncoder) {
    this.projectService = projectService;
    this.messageService = messageService;
    this.messageRepository = messageRepository;
    this.userRepository = userRepository;
    this.emailSender = emailSender;
    this.passwordEncoder = passwordEncoder;
  }

  @GetMapping("/login")
  public String loginPage(Model model) {
    model.addAttribute("adminExists", userRepository.existsByRolesContaining("ADMIN"));
    return "admin/login";
  }

  @GetMapping("/register")
  public String registerPage(Model model) {
    if (userRepository.existsByRolesContaining("ADMIN")) {
      return "redirect:/admin/login?adminExists=true";
    }
    return "admin/register";
  }

  @PostMapping("/register")
  public String registerAdmin(@RequestParam String username,
                               @RequestParam String email,
                               @RequestParam String password,
                               RedirectAttributes redirectAttributes) {
    if (userRepository.existsByRolesContaining("ADMIN")) {
      redirectAttributes.addFlashAttribute("error", "An admin account already exists.");
      return "redirect:/admin/login";
    }
    if (userRepository.findByEmail(email).isPresent()) {
      redirectAttributes.addFlashAttribute("error", "Email already in use.");
      return "redirect:/admin/register";
    }
    User admin = User.builder()
            .username(username)
            .email(email)
            .password(passwordEncoder.encode(password))
            .roles(Collections.singletonList("ADMIN"))
            .build();
    userRepository.save(admin);
    redirectAttributes.addFlashAttribute("registered", true);
    return "redirect:/admin/login";
  }

  @GetMapping("/dashboard")
  public String dashboard(Model model) {
    model.addAttribute("projectCount", projectService.getAllProjects().size());
    model.addAttribute("messageCount", messageService.getAllMessages().size());
    model.addAttribute("unreadCount", messageRepository.countByReadFalse());
    return "admin/dashboard";
  }

  // --- Projects ---

  @GetMapping("/projects")
  public String listProjects(Model model) {
    model.addAttribute("projects", projectService.getAllProjects());
    return "admin/projects";
  }

  @GetMapping("/projects/new")
  public String newProjectForm(Model model) {
    model.addAttribute("project", new Project());
    model.addAttribute("techStackInput", "");
    return "admin/project-form";
  }

  @PostMapping("/projects")
  public String createProject(@ModelAttribute Project project,
                               @RequestParam("techStackInput") String techStackInput,
                               RedirectAttributes redirectAttributes) {
    project.setTechStack(parseTechStack(techStackInput));
    projectService.createProject(project);
    redirectAttributes.addFlashAttribute("success", "Project created successfully");
    return "redirect:/admin/projects";
  }

  @GetMapping("/projects/{id}/edit")
  public String editProjectForm(@PathVariable String id, Model model) {
    Project project = projectService.getProjectById(id)
            .orElseThrow(() -> new RuntimeException("Project not found: " + id));
    model.addAttribute("project", project);
    model.addAttribute("techStackInput",
            project.getTechStack() != null ? String.join(", ", project.getTechStack()) : "");
    return "admin/project-form";
  }

  @PostMapping("/projects/{id}")
  public String updateProject(@PathVariable String id,
                               @ModelAttribute Project project,
                               @RequestParam("techStackInput") String techStackInput,
                               RedirectAttributes redirectAttributes) {
    project.setTechStack(parseTechStack(techStackInput));
    projectService.updateProject(id, project);
    redirectAttributes.addFlashAttribute("success", "Project updated successfully");
    return "redirect:/admin/projects";
  }

  @PostMapping("/projects/{id}/delete")
  public String deleteProject(@PathVariable String id, RedirectAttributes redirectAttributes) {
    projectService.deleteProject(id);
    redirectAttributes.addFlashAttribute("success", "Project deleted successfully");
    return "redirect:/admin/projects";
  }

  // --- Messages ---

  @GetMapping("/messages")
  public String listMessages(Model model) {
    List<Message> messages = messageRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
    model.addAttribute("messages", messages);
    model.addAttribute("unreadCount", messageRepository.countByReadFalse());
    return "admin/messages";
  }

  @GetMapping("/messages/{id}")
  public String viewMessage(@PathVariable String id, Model model) {
    Message message = messageService.getMessageById(id)
            .orElseThrow(() -> new RuntimeException("Message not found: " + id));
    if (!message.isRead()) {
      message.setRead(true);
      messageRepository.save(message);
    }
    model.addAttribute("message", message);
    return "admin/message-detail";
  }

  @PostMapping("/messages/{id}/reply")
  public String replyToMessage(@PathVariable String id,
                                @RequestParam("replyText") String replyText,
                                RedirectAttributes redirectAttributes) {
    Message message = messageService.getMessageById(id)
            .orElseThrow(() -> new RuntimeException("Message not found: " + id));
    String subject = "Re: Messaggio da my-website - Risposta";
    emailSender.sendEmail(message.getEmail(), subject, replyText, replyText);
    redirectAttributes.addFlashAttribute("success", "Reply sent to " + message.getEmail());
    return "redirect:/admin/messages/" + id;
  }

  @PostMapping("/messages/{id}/delete")
  public String deleteMessage(@PathVariable String id, RedirectAttributes redirectAttributes) {
    messageService.deleteMessage(id);
    redirectAttributes.addFlashAttribute("success", "Message deleted successfully");
    return "redirect:/admin/messages";
  }

  private List<String> parseTechStack(String input) {
    if (input == null || input.isBlank()) {
      return List.of();
    }
    return Arrays.stream(input.split(","))
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .toList();
  }
}
