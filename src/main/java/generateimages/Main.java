package generateimages;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    private static final String URL_REGEX = "(https?://[\\w-]+(\\.[\\w-]+)+(/[^\\s)]*)?)";
    private static final Pattern PATTERN = Pattern.compile(URL_REGEX);

    private static void fillFilesRecursively(Path directory, final List<File> resultFiles)
            throws IOException {
        Files.walkFileTree(
                directory,
                new java.nio.file.SimpleFileVisitor<>() {
                    @Override
                    public java.nio.file.FileVisitResult visitFile(
                            Path file, java.nio.file.attribute.BasicFileAttributes attrs) {
                        final String filePath = file.toString().toLowerCase();
                        if (filePath.endsWith("readme.md")) {
                            resultFiles.add(file.toFile());
                        }
                        return java.nio.file.FileVisitResult.CONTINUE;
                    }
                });
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        List<File> resultFiles = new ArrayList<>();
        fillFilesRecursively(Paths.get("."), resultFiles);
        int ind = 0;
        for (File file : resultFiles) {
            if (ind % 20 == 0) {
                System.out.println(file.getAbsolutePath());
            }
            ind++;
            String readmeMdText = Files.readString(file.toPath(), UTF_8);
            Matcher matcher = PATTERN.matcher(readmeMdText);

            StringBuilder builder = new StringBuilder();
            // Extract and print all matched URLs
            while (matcher.find()) {
                String group = matcher.group(3);
                int lasted = group == null ? -1 : group.lastIndexOf('/');
                if (lasted > 0 && !group.contains(":")) {
                    String fileName = group.substring(lasted + 1).toLowerCase();
                    if (fileName.endsWith(".png")
                            || fileName.endsWith(".jpg")
                            || fileName.endsWith(".gif")
                            || fileName.endsWith(".svg")
                            || matcher.group(1).contains("assets.")) {
                        if (!fileName.contains(".")) {
                            fileName = fileName + ".png";
                        }
                        if (!Files.exists(Path.of(file.getParent() + "/" + fileName))) {
                            ImageDownloader.downloadImage(
                                    matcher.group(1), file.getParent() + "/" + fileName);
                        }
                        matcher.appendReplacement(builder, fileName);
                    }
                }
            }
            matcher.appendTail(builder);
            Files.writeString(file.toPath(), builder.toString(), UTF_8);
        }
    }
}
