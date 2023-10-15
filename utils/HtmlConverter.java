package htmlConverter;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class HtmlConverter {
    public void Convert(String source, String destination) throws Exception {
        try (var fileReader = new BufferedReader(new FileReader(source))) {
            var head = readHeader(fileReader);
            var pages = readPages(fileReader);
            var footer = readFooter(fileReader);
            var htmlPages = buildPages(head, pages, footer, destination);
            writePages(htmlPages, destination);

            System.out.println(head.length());
            System.out.println(pages.length);
            System.out.println(footer.length());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void writePages(String[] htmlPages, String destination) {
        for (var i = 0; i < htmlPages.length; i++) {
            var fileName = String.format("%s_%s.html", destination, i);
            try (var fileWriter = new FileWriter(fileName)) {
                fileWriter.write(htmlPages[i]);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String[] buildPages(String head, String[] pages, String footer, String destination) {
        var htmlPages = new ArrayList<String>();
        var index = 0;
        final int count = 10;
        var name = destination.substring(destination.lastIndexOf("/") + 1);

        while (true) {
            var nextPages = Arrays
                    .stream(pages)
                    .skip(index * count)
                    .limit(count)
                    .toList();

            var page = new StringBuilder();

            page.append(head);
            page.append(System.lineSeparator());

            nextPages.forEach(x -> {
                page.append(x);
                page.append(System.lineSeparator());
            });

            page.append(String.format("<h2>" +
                            "<a href=\"%s_%s.html\">Назад (%s)</a>" +
                            " - " +
                            "<a href=\"%s_%s.html\">Вперед (%s)</a>" +
                            "</h2>"
                    , name, index - 1, index - 1
                    , name, index + 1, index + 1));

            page.append(footer);

            htmlPages.add(page.toString());

            index++;

            if (nextPages.stream().count() != count) {
                return htmlPages.toArray(new String[0]);
            }
        }
    }

    private String readHeader(BufferedReader fileReader) throws Exception {
        var head = new StringBuilder();

        while (true) {
            var line = fileReader.readLine();
            if (line == null) {
                throw new Exception("Не удалось прочитать заголовок");
            }

            head.append(line);
            head.append(System.lineSeparator());

            if (line.trim().equalsIgnoreCase("<body>")) {
                return head.toString();
            }
        }
    }

    private String[] readPages(BufferedReader fileReader) throws Exception {
        var page = new StringBuilder();
        var pages = new ArrayList<String>();

        while (true) {
            var line = fileReader.readLine();
            if (line == null) {
                throw new Exception("Не удалось прочитать заголовок");
            }

            if (line.trim().equalsIgnoreCase("</body>")) {
                break;
            }

            if (line.trim().equalsIgnoreCase("<div class=\"stl_02\">")
                    || line.trim().equalsIgnoreCase("<div class=\"stl_14\">")) {
                if (!page.isEmpty()) {
                    pages.add(page.toString());
                }

                page = new StringBuilder();
            }

            page.append(line);
            page.append(System.lineSeparator());
        }

        return pages.toArray(new String[0]);
    }

    private String readFooter(BufferedReader fileReader) throws Exception {
        var footer = new StringBuilder();

        footer.append("</body>");
        footer.append(System.lineSeparator());

        while (true) {
            var line = fileReader.readLine();
            if (line == null) {
                return footer.toString();
            }

            footer.append(line);
            footer.append(System.lineSeparator());
        }
    }
}
