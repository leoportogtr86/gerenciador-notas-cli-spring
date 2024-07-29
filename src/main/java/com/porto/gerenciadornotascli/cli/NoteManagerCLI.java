package com.porto.gerenciadornotascli.cli;

import com.porto.gerenciadornotascli.model.Note;
import com.porto.gerenciadornotascli.service.NoteService;
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
