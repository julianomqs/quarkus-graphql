package br.com.ibasi;

import org.jooq.codegen.GenerationTool;
import org.jooq.meta.jaxb.Database;
import org.jooq.meta.jaxb.Generate;
import org.jooq.meta.jaxb.Generator;
import org.jooq.meta.jaxb.Jdbc;
import org.jooq.meta.jaxb.Target;

public class JooqTablesGenerator {

  public static void main(String[] args) {
    var generate = new Generate()
        .withJavaTimeTypes(true);

    var config = new org.jooq.meta.jaxb.Configuration()
        .withJdbc(new Jdbc()
            .withDriver("com.mysql.jdbc.Driver")
            .withUrl("jdbc:mysql://localhost:3306/quarkus-graphql")
            .withUser("root")
            .withPassword("ibasi.ts"))
        .withGenerator(new Generator()
            .withDatabase(new Database()
                .withName("org.jooq.meta.mysql.MySQLDatabase")
                .withIncludes(".*")
                .withInputSchema("quarkus-graphql")
                .withOutputSchemaToDefault(true)
                .withUnsignedTypes(false))
            .withGenerate(generate)
            .withTarget(new Target()
                .withPackageName("br.com.ibasi.jooq")
                .withDirectory("src/main/java")));

    try {
      GenerationTool.generate(config);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}