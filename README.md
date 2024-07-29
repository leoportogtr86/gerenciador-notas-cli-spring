### Passo 1: Configuração Inicial do Projeto

1. **Crie um novo projeto Spring Boot:**
    - Use o Spring Initializr (https://start.spring.io/) para gerar um projeto Spring Boot com as seguintes
      dependências:
        - Spring Web
        - Spring Data JPA
        - H2 Database (ou outro banco de dados de sua preferência)

2. **Importe o projeto para sua IDE:**
    - Baixe o projeto gerado e importe-o para sua IDE (IntelliJ, Eclipse, etc.).

### Passo 2: Configuração do Banco de Dados

1. **Configure o banco de dados no `application.properties`:**
   ```properties
   spring.datasource.url=jdbc:h2:mem:testdb
   spring.datasource.driverClassName=org.h2.Driver
   spring.datasource.username=sa
   spring.datasource.password=password
   spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
   spring.h2.console.enabled=true
   spring.jpa.hibernate.ddl-auto=update
   ```

### Passo 3: Criar a Entidade `Note`

1. **Crie a classe `Note`:**
   ```java
   package com.example.notemanager.model;

   import javax.persistence.Entity;
   import javax.persistence.GeneratedValue;
   import javax.persistence.GenerationType;
   import javax.persistence.Id;

   @Entity
   public class Note {
       @Id
       @GeneratedValue(strategy = GenerationType.IDENTITY)
       private Long id;

       private String title;
       private String content;

       // Getters and setters
       public Long getId() {
           return id;
       }

       public void setId(Long id) {
           this.id = id;
       }

       public String getTitle() {
           return title;
       }

       public void setTitle(String title) {
           this.title = title;
       }

       public String getContent() {
           return content;
       }

       public void setContent(String content) {
           this.content = content;
       }
   }
   ```

### Passo 4: Criar o Repositório `NoteRepository`

1. **Crie a interface `NoteRepository`:**
   ```java
   package com.example.notemanager.repository;

   import com.example.notemanager.model.Note;
   import org.springframework.data.jpa.repository.JpaRepository;
   import org.springframework.stereotype.Repository;

   @Repository
   public interface NoteRepository extends JpaRepository<Note, Long> {
   }
   ```

### Passo 5: Criar o Serviço `NoteService`

1. **Crie a classe `NoteService`:**
   ```java
   package com.example.notemanager.service;

   import com.example.notemanager.model.Note;
   import com.example.notemanager.repository.NoteRepository;
   import org.springframework.beans.factory.annotation.Autowired;
   import org.springframework.stereotype.Service;

   import java.util.List;
   import java.util.Optional;

   @Service
   public class NoteService {
       @Autowired
       private NoteRepository noteRepository;

       public List<Note> getAllNotes() {
           return noteRepository.findAll();
       }

       public Optional<Note> getNoteById(Long id) {
           return noteRepository.findById(id);
       }

       public Note createNote(Note note) {
           return noteRepository.save(note);
       }

       public Note updateNote(Long id, Note noteDetails) {
           Note note = noteRepository.findById(id)
               .orElseThrow(() -> new RuntimeException("Note not found"));
           note.setTitle(noteDetails.getTitle());
           note.setContent(noteDetails.getContent());
           return noteRepository.save(note);
       }

       public void deleteNote(Long id) {
           Note note = noteRepository.findById(id)
               .orElseThrow(() -> new RuntimeException("Note not found"));
           noteRepository.delete(note);
       }
   }
   ```

### Passo 6: Criar o Controlador `NoteController`

1. **Crie a classe `NoteController`:**
   ```java
   package com.example.notemanager.controller;

   import com.example.notemanager.model.Note;
   import com.example.notemanager.service.NoteService;
   import org.springframework.beans.factory.annotation.Autowired;
   import org.springframework.web.bind.annotation.*;

   import java.util.List;

   @RestController
   @RequestMapping("/api/notes")
   public class NoteController {
       @Autowired
       private NoteService noteService;

       @GetMapping
       public List<Note> getAllNotes() {
           return noteService.getAllNotes();
       }

       @GetMapping("/{id}")
       public Note getNoteById(@PathVariable Long id) {
           return noteService.getNoteById(id)
               .orElseThrow(() -> new RuntimeException("Note not found"));
       }

       @PostMapping
       public Note createNote(@RequestBody Note note) {
           return noteService.createNote(note);
       }

       @PutMapping("/{id}")
       public Note updateNote(@PathVariable Long id, @RequestBody Note noteDetails) {
           return noteService.updateNote(id, noteDetails);
       }

       @DeleteMapping("/{id}")
       public void deleteNote(@PathVariable Long id) {
           noteService.deleteNote(id);
       }
   }
   ```

### Passo 7: Testar a Aplicação

1. **Execute a aplicação:**
    - Na sua IDE, execute a classe principal `SpringBootApplication`.

2. **Use ferramentas como `curl` ou `Postman` para testar os endpoints:**
    - **Para listar todas as notas:**
      ```bash
      curl -X GET http://localhost:8080/api/notes
      ```
    - **Para criar uma nova nota:**
      ```bash
      curl -X POST http://localhost:8080/api/notes -H "Content-Type: application/json" -d '{"title": "Nova Nota", "content": "Conteúdo da nota"}'
      ```
    - **Para atualizar uma nota:**
      ```bash
      curl -X PUT http://localhost:8080/api/notes/1 -H "Content-Type: application/json" -d '{"title": "Nota Atualizada", "content": "Conteúdo atualizado"}'
      ```
    - **Para deletar uma nota:**
      ```bash
      curl -X DELETE http://localhost:8080/api/notes/1
      ```

### Passo 8: Implementar a CLI

1. **Adicione uma classe para a CLI:**
   ```java
   package com.example.notemanager.cli;

   import com.example.notemanager.model.Note;
   import com.example.notemanager.service.NoteService;
   import org.springframework.beans.factory.annotation.Autowired;
   import org.springframework.boot.CommandLineRunner;
   import org.springframework.stereotype.Component;

   import java.util.Scanner;

   @Component
   public class NoteManagerCLI implements CommandLineRunner {

       @Autowired
       private NoteService noteService;

       @Override
       public void run(String... args) throws Exception {
           Scanner scanner = new Scanner(System.in);
           while (true) {
               System.out.println("1. Listar notas");
               System.out.println("2. Adicionar nota");
               System.out.println("3. Atualizar nota");
               System.out.println("4. Deletar nota");
               System.out.println("5. Sair");
               System.out.print("Escolha uma opção: ");
               int choice = scanner.nextInt();
               scanner.nextLine();  // Consumir a nova linha

               switch (choice) {
                   case 1:
                       listNotes();
                       break;
                   case 2:
                       addNote(scanner);
                       break;
                   case 3:
                       updateNote(scanner);
                       break;
                   case 4:
                       deleteNote(scanner);
                       break;
                   case 5:
                       System.exit(0);
                   default:
                       System.out.println("Opção inválida.");
               }
           }
       }

       private void listNotes() {
           noteService.getAllNotes().forEach(note ->
               System.out.println(note.getId() + ": " + note.getTitle() + " - " + note.getContent()));
       }

       private void addNote(Scanner scanner) {
           System.out.print("Título da nota: ");
           String title = scanner.nextLine();
           System.out.print("Conteúdo da nota: ");
           String content = scanner.nextLine();
           Note note = new Note();
           note.setTitle(title);
           note.setContent(content);
           noteService.createNote(note);
       }

       private void updateNote(Scanner scanner) {
           System.out.print("ID da nota: ");
           Long id = scanner.nextLong();
           scanner.nextLine();
           System.out.print("Novo título da nota: ");
           String title = scanner.nextLine();
           System.out.print("Novo conteúdo da nota: ");
           String content = scanner.nextLine();

           Note noteDetails = new Note();
           noteDetails.setTitle(title);
           noteDetails.setContent(content);
           noteService.updateNote(id, noteDetails);
       }

       private void deleteNote(Scanner scanner) {
           System.out.print("ID da nota: ");
           Long id = scanner.nextLong();
           scanner.nextLine();
           noteService.deleteNote(id);
       }
   }
   ```

### Passo 9: Executar a CLI

1. **Execute novamente a aplicação Spring Boot:**
    - Na sua IDE, execute a classe principal `SpringBootApplication`.

2. **Interaja com a CLI via terminal:**
    - Ao iniciar, a CLI apresentará um menu com opções para listar, adicionar, atualizar e deletar notas.

### Conclusão

Seguindo esses passos, você criou um Gerenciador de Notas com uma interface de linha de comando usando Java e Spring
Boot. Esta aplicação permite a gestão básica de notas diretamente pelo terminal, utilizando uma estrutura de banco de
dados para armazenamento persistente.