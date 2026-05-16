package ru.student.videogames.parser;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import ru.student.videogames.model.RawGameRecord;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class GameCsvParserTest {
    @TempDir
    Path tempDir;

    @Test
    void parseHandlesDecimalYearMissingYearAndUnknownPublisher() throws Exception {
        Path csv = tempDir.resolve("games.csv");
        Files.writeString(csv, """
                Rank,Name,Platform,Year,Genre,Publisher,NA_Sales,EU_Sales,JP_Sales,Other_Sales,Global_Sales
                1,Wii Sports,Wii,2006.0,Sports,Nintendo,41.49,29.02,3.77,8.46,82.74
                2,Unknown Publisher Game,PC,,Action,,1.00,2.00,0.00,0.50,3.50
                3,N/A Publisher Game,PS2,N/A,Sports,N/A,0.10,0.20,0.30,0.40,1.00
                """);

        List<RawGameRecord> records = new GameCsvParser().parse(csv);

        assertEquals(3, records.size());

        RawGameRecord first = records.get(0);
        assertEquals(2006, first.getYear());
        assertEquals("Nintendo", first.getPublisher());
        assertEquals(82.74, first.getGlobalSales());

        RawGameRecord second = records.get(1);
        assertNull(second.getYear());
        assertEquals("Unknown", second.getPublisher());
        assertEquals(2.00, second.getEuSales());

        RawGameRecord third = records.get(2);
        assertNull(third.getYear());
        assertEquals("Unknown", third.getPublisher());
    }
}
