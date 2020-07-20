package com.kakaobank.menu.route;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

// import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

@RunWith(JUnitPlatform.class)
public class Top3FinderTest {

    Map<String, Integer> visitCountMap = new HashMap<String, Integer>();
    Map<String, Long> durationMap = new HashMap<String, Long>();
    List<String> correct = new ArrayList<String>();
    List<String[]> correctCsv = new ArrayList<String[]>();

    @BeforeEach
    public void initEach() {

        correct.add("LOGIN-MENU1-MENU2-MENU3-LOGOUT");
        correct.add("LOGIN-MENU1-MENU2-LOGOUT");
        correct.add("LOGIN-MENU1-LOGOUT");
        correct.add("LOGIN-MENU2-LOGOUT");
        correct.add("LOGIN-MENU1-MENU3-LOGOUT");
        correct.add("LOGIN-MENU1-MENU3-MENU2-LOGOUT");
        correct.add("LOGIN-MENU3-LOGOUT");
        correct.add("LOGIN-MENU1-MENU4-LOGOUT");
        correct.add("LOGIN-MENU2-MENU1-MENU3-LOGOUT");
        correct.add("LOGIN-MENU2-MENU1-LOGOUT");
        correct.add("LOGIN-MENU2-MENU3-MENU1-LOGOUT");

        correctCsv.add(new String[] { "LOGIN-MENU1-MENU2-MENU3-LOGOUT", "34", "34" });
        correctCsv.add(new String[] { "LOGIN-MENU1-MENU2-LOGOUT", "33", "33" });
        correctCsv.add(new String[] { "LOGIN-MENU1-LOGOUT", "30", "30" });
        correctCsv.add(new String[] { "LOGIN-MENU2-LOGOUT", "32", "32" });
        correctCsv.add(new String[] { "LOGIN-MENU1-MENU3-LOGOUT", "29", "29" });
        correctCsv.add(new String[] { "LOGIN-MENU1-MENU3-MENU2-LOGOUT", "20", "20" });
        correctCsv.add(new String[] { "LOGIN-MENU3-LOGOUT", "31", "31" });
        correctCsv.add(new String[] { "LOGIN-MENU1-MENU4-LOGOUT", "28", "28" });
        correctCsv.add(new String[] { "LOGIN-MENU2-MENU1-MENU3-LOGOUT", "19", "19" });
        correctCsv.add(new String[] { "LOGIN-MENU2-MENU1-LOGOUT", "27", "27" });
        correctCsv.add(new String[] { "LOGIN-MENU2-MENU3-MENU1-LOGOUT", "18", "18" });

        visitCountMap.put("LOGIN-MENU1-LOGOUT", 10);
        visitCountMap.put("LOGIN-MENU2-LOGOUT", 9);
        visitCountMap.put("LOGIN-MENU3-LOGOUT", 8);
        visitCountMap.put("LOGIN-MENU1-MENU2-LOGOUT", 10);
        visitCountMap.put("LOGIN-MENU1-MENU3-LOGOUT", 9);
        visitCountMap.put("LOGIN-MENU1-MENU4-LOGOUT", 8);
        visitCountMap.put("LOGIN-MENU2-MENU1-LOGOUT", 7);
        visitCountMap.put("LOGIN-MENU1-MENU2-MENU3-LOGOUT", 10);
        visitCountMap.put("LOGIN-MENU1-MENU3-MENU2-LOGOUT", 9);
        visitCountMap.put("LOGIN-MENU2-MENU1-MENU3-LOGOUT", 8);
        visitCountMap.put("LOGIN-MENU2-MENU3-MENU1-LOGOUT", 7);

        durationMap.put("LOGIN-MENU1-LOGOUT", 30L);
        durationMap.put("LOGIN-MENU2-LOGOUT", 32L);
        durationMap.put("LOGIN-MENU3-LOGOUT", 31L);
        durationMap.put("LOGIN-MENU1-MENU2-LOGOUT", 33L);
        durationMap.put("LOGIN-MENU1-MENU3-LOGOUT", 29L);
        durationMap.put("LOGIN-MENU1-MENU4-LOGOUT", 28L);
        durationMap.put("LOGIN-MENU2-MENU1-LOGOUT", 27L);
        durationMap.put("LOGIN-MENU1-MENU2-MENU3-LOGOUT", 34L);
        durationMap.put("LOGIN-MENU1-MENU3-MENU2-LOGOUT", 20L);
        durationMap.put("LOGIN-MENU2-MENU1-MENU3-LOGOUT", 19L);
        durationMap.put("LOGIN-MENU2-MENU3-MENU1-LOGOUT", 18L);
    }

    @DisplayName("Test Top3Finder.sortHashMapByValue")
    @Test
    public void testSortHashMapByValue() throws NoSuchMethodException, SecurityException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        final Method sortHashMapByValue = Top3Finder.class.getDeclaredMethod("sortHashMapByValue", Map.class,
                Map.class);
        sortHashMapByValue.setAccessible(true);

        @SuppressWarnings("unchecked")
        List<String> result = (ArrayList<String>) sortHashMapByValue.invoke(sortHashMapByValue, visitCountMap,
                durationMap);

        for (int i = 0; i < correct.size(); i++) {
            if (!correct.get(i).equals(result.get(i))) {
                assertEquals(result.get(i), correct.get(i), "Result is not correct.");
            }
        }

        assertTrue(true);
    }

    @DisplayName("Test Top3Finder.getDuration()")
    @Test
    public void testGetDuration() throws NoSuchMethodException, SecurityException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        final Method getDuration = Top3Finder.class.getDeclaredMethod("getDuration", String.class, String.class);
        getDuration.setAccessible(true);

        String startTime = "20200301235912";
        String endTime = "20200302000125";
        Long correct = 133L; // 133 seconds is correct
        Long result = (Long) getDuration.invoke(getDuration, startTime, endTime);

        assertEquals(correct, result);
    }

    @DisplayName("Test Top3Finder.writeCsv()")
    @Test
    public void testWriteCsv() throws CsvValidationException, IOException, NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        final Method sortHashMapByValue = Top3Finder.class.getDeclaredMethod("sortHashMapByValue", Map.class, Map.class);
        sortHashMapByValue.setAccessible(true);

        @SuppressWarnings("unchecked")
        List<String> result = (ArrayList<String>)sortHashMapByValue.invoke(sortHashMapByValue, visitCountMap, durationMap);

        final Method writeCsv = Top3Finder.class.getDeclaredMethod("writeCsv", String.class, Iterator.class, Map.class, Map.class, Map.class);
        writeCsv.setAccessible(true);

        String path = "./result.csv";
        writeCsv.invoke(writeCsv, path, result.iterator(), visitCountMap, durationMap, durationMap);

        File f = new File(path);
        if (!f.exists()) {
            assertTrue(false);
        }
        
        CSVReader reader = null;

        try {
            reader = new CSVReader(new FileReader(path));
            String[] line = null;
            int idx = 0;

            while ((line = reader.readNext()) != null) {
                if (!correct.get(idx).equals(line[0])) {
                    assertEquals(line[0], correct.get(idx), "Result is not correct.");
                }

                idx++;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            assertTrue(false);
        } finally {
            try {
                if (reader != null)
                    reader.close();
            } catch (IOException e) {
                e.printStackTrace();
                assertTrue(false);
            }
        }

        assertTrue(true);
    }
}