package src.runner;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Runner {

    public static void run(String outputFile, String lang) throws IOException {
        if ("java".equals(lang)) {
            runJava(outputFile);
        } else {
            runKotlin(outputFile);
        }
    }

    private static void runJava(String outputFile) throws IOException {
        Path generatedPath = Paths.get(outputFile).toAbsolutePath();

        System.out.println("=== EXECUTANDO O RESULTADO (Java) ===");
        runCommand(List.of("java", generatedPath.toString()), generatedPath.getParent());
    }

    private static void runKotlin(String outputFile) throws IOException {
        Path generatedPath = Paths.get(outputFile).toAbsolutePath();
        String baseName = generatedPath.getFileName().toString().replaceAll("\\.[^.]+$", "");
        Path outputJar = generatedPath.getParent().resolve(baseName + ".jar");

        System.out.println("=== COMPILANDO O RESULTADO (Kotlin) ===");
        runCommand(List.of("cmd", "/c", "kotlinc", generatedPath.toString(), "-include-runtime", "-d", outputJar.toString()), generatedPath.getParent());

        System.out.println("=== EXECUTANDO O RESULTADO (Kotlin) ===");
        runCommand(List.of("java", "-jar", outputJar.toString()), generatedPath.getParent());
    }

    private static void runCommand(List<String> command, Path workingDir) throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.directory(workingDir.toFile());
        processBuilder.inheritIO();

        try {
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new IOException("Comando falhou (código " + exitCode + "): " + String.join(" ", command));
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Execução interrompida: " + String.join(" ", command), e);
        }
    }
}

