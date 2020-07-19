package com.kakaobank.menu.route;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.opencsv.CSVWriter;

/**
 * Top3 menu route finder class
 */
public class Top3Finder {

    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost:3306/kakaobank?serverTimezone=UTC";
    static final String USERNAME = "root";
    static final String PASSWORD = "easy1234";
    static final String SQL = "SELECT MENU_NM, LOG_TKTM FROM KAKAOBANK.MENU_LOG ORDER BY USR_NO, LOG_TKTM";
    static final String FILENAME = "result.csv";
    static final String PREFIX_MENU_ROUTE = "로그인";
    static final String SUFFIX_MENU_ROUTE = "로그아웃";

    /**
     * Main method.
     * 
     * @param args - 1. CSV result file path
     */
    public static void main(String[] args) {

        if (args == null || args.length < 1) {
            System.out.println("Please input result_file_path. \nCSV file path param is mandatory.");
            return;
        }

        // Initializing variables
        String filePath = args[0];
        long startTime = System.currentTimeMillis();

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        String loginTime = "";
        String logoutTime = "";
        long sessionTime = 0;

        Map<String, Integer> visitCountMap = new HashMap<String, Integer>();
        Map<String, Long> maxDurationMap = new HashMap<String, Long>();
        Map<String, Long> minDurationMap = new HashMap<String, Long>();

        String route = PREFIX_MENU_ROUTE;

        try {
            // Connecting DB
            Class.forName(JDBC_DRIVER);
            connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            statement = connection.createStatement();

            // Getting data from DB
            resultSet = statement.executeQuery(SQL);

            // For all result sets
            while (resultSet.next()) {

                // Get values
                String menuName = resultSet.getString("MENU_NM");
                String loggingTime = resultSet.getString("LOG_TKTM");

                // IF: Entry point for each session (=login)
                if (menuName.equals("login")) {
                    // Initializing route
                    route = PREFIX_MENU_ROUTE;

                    // Getting login time
                    loginTime = loggingTime;

                // ELIF: Exit point for each session (=logout)
                } else if (menuName.equals("logout")) {
                    // Ending menu route with suffix
                    route = String.join("-", route, SUFFIX_MENU_ROUTE);

                    // Getting logout time
                    logoutTime = loggingTime;

                    // Calculating session duration by seconds
                    sessionTime = getDuration(loginTime, logoutTime);

                    // IF: containing the current menu route as key in hashmap
                    if (visitCountMap.containsKey(route)) {
                        // Setting visit count + 1 into Hashmap
                        int currCount = visitCountMap.get(route);
                        currCount++;
                        visitCountMap.put(route, currCount);

                        // Setting the maximum session duration into Hashmap
                        long currMaxSessionTime = maxDurationMap.get(route);
                        if (sessionTime > currMaxSessionTime) {
                            maxDurationMap.put(route, sessionTime);
                        }

                        // Setting the minimum session duration into Hashmap
                        long currMinSessionTime = minDurationMap.get(route);
                        if (sessionTime < currMinSessionTime) {
                            minDurationMap.put(route, sessionTime);
                        }
                    // ELSE: NOT Containing
                    } else {
                        // Initializing key-value into eash hashmap
                        visitCountMap.put(route, 1);
                        maxDurationMap.put(route, sessionTime);
                        minDurationMap.put(route, sessionTime);
                    }
                // ELSE: On session
                } else {
                    // Recording menu route
                    route = String.join("-", route, menuName);
                }
            }
        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                resultSet.close();
                statement.close();
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        // Sorting Key by visit count and session time
        Iterator<String> iterator = sortHashMapByValue(visitCountMap, maxDurationMap).iterator();

        // Write CSV file
        writeCsv(filePath + "/" + FILENAME, iterator, visitCountMap, maxDurationMap, minDurationMap);

        long endTime = System.currentTimeMillis();

        // Logging processing time
        System.out.println(String.format("Processing Time = %.3f sec", (endTime - startTime)/1000.000));
        return;
    }

    /**
     * Sorting hashmap keys.
     * 
     * @param visitCountMap
     * @param durationMap
     * @return Sorted key list
     */
    private static List<String> sortHashMapByValue(final Map<String, Integer> visitCountMap, 
                                                    final Map<String, Long> durationMap) {
        List<String> list = new ArrayList<String>();
        list.addAll(visitCountMap.keySet());

        Collections.sort(list, new Comparator<String>() {
            public int compare(String obj1, String obj2) {
                Integer countVal1 = visitCountMap.get(obj1);
                Integer countVal2 = visitCountMap.get(obj2);

                // IF: both visit count have the same value
                if (countVal1 == countVal2) {
                    // Compare to the maximum session duration of two objects
                    Long durationVal1 = durationMap.get(obj1);
                    Long durationVal2 = durationMap.get(obj2);

                    // Return as descending order for the maximum session duration
                    return ((Comparable<Long>)durationVal2).compareTo(durationVal1);
                } else {
                    // Return as descending order for visit count
                    return ((Comparable<Integer>)countVal2).compareTo(countVal1);
                }
            }
        });

        return list;
    }

    /**
     * Return diff seconds between startTime and endTime.
     * 
     * @param startTime
     * @param endTime
     * @return Duration (second)
     * @throws ParseException
     */
    private static long getDuration(String startTime, String endTime) throws ParseException {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

        Date start = dateFormat.parse(startTime);
        Date end = dateFormat.parse(endTime);

        long diff = end.getTime() - start.getTime();
        return (diff / 1000);
    }


    /**
     * Writing csv file for top 3 menu routes.
     * 
     * @param filePath
     * @param iterator
     * @param visitCountMap
     * @param maxDurationMap
     * @param minDurationMap
     */
    private static void writeCsv(String filePath, 
                                Iterator<String> iterator, 
                                Map<String, Integer> visitCountMap, 
                                Map<String, Long> maxDurationMap,
                                Map<String, Long> minDurationMap) {
        CSVWriter writer = null;

        try {
            writer = new CSVWriter(new FileWriter(filePath), 
                                    CSVWriter.DEFAULT_SEPARATOR, 
                                    CSVWriter.NO_QUOTE_CHARACTER, 
                                    CSVWriter.DEFAULT_ESCAPE_CHARACTER, 
                                    CSVWriter.DEFAULT_LINE_END);
            int rank = 1;

            while (iterator.hasNext() && rank <= 3) {
                String key = (String)iterator.next();
                String maxSessionDuration = String.valueOf(maxDurationMap.get(key));
                String minSessionDuration = String.valueOf(minDurationMap.get(key));

                String[] line = {key, maxSessionDuration, minSessionDuration};
                writer.writeNext(line);

                rank++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}