package ru.student.videogames.util;

import ru.student.videogames.dto.GameSalesDto;
import ru.student.videogames.dto.PlatformAverageSalesDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ConsoleTablePrinter {
    public void printPlatformAverages(List<PlatformAverageSalesDto> rows) {
        formatPlatformAverages(rows).forEach(System.out::println);
    }

    public void printGameSales(GameSalesDto gameSales, String salesLabel) {
        formatGameSales(gameSales, salesLabel).forEach(System.out::println);
    }

    public List<String> formatPlatformAverages(List<PlatformAverageSalesDto> rows) {
        List<String[]> table = new ArrayList<>();
        table.add(new String[]{"Platform", "Avg Global Sales"});
        for (PlatformAverageSalesDto row : rows) {
            table.add(new String[]{
                    row.getPlatform(),
                    String.format(Locale.US, "%.2f", row.getAvgGlobalSales())
            });
        }

        return formatTable(table);
    }

    public List<String> formatGameSales(GameSalesDto gameSales, String salesLabel) {
        List<String> lines = new ArrayList<>();
        lines.add("Name: " + gameSales.getName());
        lines.add("Platform: " + gameSales.getPlatform());
        lines.add("Year: " + (gameSales.getReleaseYear() == null ? "Unknown" : gameSales.getReleaseYear()));
        lines.add("Genre: " + gameSales.getGenre());
        lines.add("Publisher: " + gameSales.getPublisher());
        lines.add(String.format(Locale.US, "%s: %.2f", salesLabel, gameSales.getSales()));
        return lines;
    }

    private List<String> formatTable(List<String[]> rows) {
        List<String> lines = new ArrayList<>();
        if (rows.isEmpty()) {
            return lines;
        }

        int columns = rows.get(0).length;
        int[] widths = new int[columns];

        for (String[] row : rows) {
            for (int column = 0; column < columns; column++) {
                widths[column] = Math.max(widths[column], row[column].length());
            }
        }

        lines.add(formatRow(rows.get(0), widths));
        lines.add(formatSeparator(widths));

        for (int index = 1; index < rows.size(); index++) {
            lines.add(formatRow(rows.get(index), widths));
        }

        lines.add(formatSeparator(widths));
        return lines;
    }

    private String formatRow(String[] row, int[] widths) {
        StringBuilder line = new StringBuilder("|");
        for (int column = 0; column < row.length; column++) {
            line.append(' ')
                    .append(padRight(row[column], widths[column]))
                    .append(" |");
        }
        return line.toString();
    }

    private String formatSeparator(int[] widths) {
        StringBuilder line = new StringBuilder("|");
        for (int width : widths) {
            line.append("-".repeat(width + 2)).append("|");
        }
        return line.toString();
    }

    private String padRight(String value, int width) {
        return value + " ".repeat(width - value.length());
    }
}
