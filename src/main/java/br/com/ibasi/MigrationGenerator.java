package br.com.ibasi;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

/**
 * Gerador de migrações Flyway
 *
 * @author Juliano Marques
 */
public class MigrationGenerator {

  public static void main(String[] args) throws IOException {
    var scanner = new Scanner(System.in);
    String fileName = null;

    System.out.println("Bem-vindo a criação de arquivos de migrações SQL.");
    System.out.println("\nRegras:");
    System.out.println("(1): Espaços no nome do arquivo serão substituídos por _ (underline).");
    System.out.println("(2): Não é necessário informar a extensão no nome do arquivo.\n");

    if (args.length == 0 || args[0].isBlank()) {
      while (fileName == null || fileName.isBlank()) {
        System.out.print("Informe o nome da migração: ");
        fileName = scanner.nextLine();
      }
    } else {
      fileName = args[0];
    }

    fileName = fileName.replaceAll("\\s+", "_");

    var filename = "V" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")) + "__" + fileName
        + ".sql";
    Files.createFile(Paths.get("src", "main", "resources", "db", "migration", filename));

    scanner.close();

    System.out.println("\nMigração gerada com sucesso. Atualize (F5/Refresh) a pasta de atualizações (db/migration).");
  }
}
