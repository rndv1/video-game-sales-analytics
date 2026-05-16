package ru.student.videogames.chart;

import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;
import org.knowm.xchart.CategorySeries;
import org.knowm.xchart.style.Styler;
import ru.student.videogames.dto.PlatformAverageSalesDto;

import java.awt.Color;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class ChartGenerator {
    public void saveAverageGlobalSalesByPlatformChart(
            List<PlatformAverageSalesDto> averages,
            Path outputPath,
            int topPlatforms
    ) throws IOException {
        if (averages.isEmpty()) {
            throw new IllegalArgumentException("Cannot build chart: analytics result is empty");
        }

        List<PlatformAverageSalesDto> chartData = averages.stream()
                .limit(topPlatforms)
                .toList();

        List<String> platforms = chartData.stream()
                .map(PlatformAverageSalesDto::getPlatform)
                .toList();
        List<Double> values = chartData.stream()
                .map(PlatformAverageSalesDto::getAvgGlobalSales)
                .toList();

        CategoryChart chart = new CategoryChartBuilder()
                .width(1200)
                .height(700)
                .title("Average Global Sales by Platform")
                .xAxisTitle("Platform")
                .yAxisTitle("Average Global Sales, million copies")
                .build();

        chart.addSeries("Average global sales", platforms, values);
        chart.getStyler().setLegendVisible(false);
        chart.getStyler().setXAxisLabelRotation(45);
        chart.getStyler().setChartBackgroundColor(Color.WHITE);
        chart.getStyler().setPlotBackgroundColor(new Color(250, 250, 250));
        chart.getStyler().setPlotGridLinesColor(new Color(220, 220, 220));
        chart.getStyler().setSeriesColors(new Color[]{new Color(52, 115, 210)});
        chart.getStyler().setDefaultSeriesRenderStyle(CategorySeries.CategorySeriesRenderStyle.Bar);
        chart.getStyler().setAvailableSpaceFill(0.85);
        chart.getStyler().setYAxisDecimalPattern("#0.00");
        chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNE);

        Path parent = outputPath.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        BitmapEncoder.saveBitmap(chart, outputPath.toString(), BitmapEncoder.BitmapFormat.PNG);
    }
}
