package com.students.studentsproject.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimeUtil {

    private static final Logger logger = LoggerFactory.getLogger(TimeUtil.class);

    public static void logExecutionTime(long startMillis, long endMillis, String taskName, int studentCount) {
        long durationMillis = endMillis - startMillis;

        // Convert to seconds
        double seconds = durationMillis / 1000.0;

        // Convert to rounded minutes
        long minutesRounded = Math.round(seconds / 60.0);

        logger.info("Task: {}", taskName);
        logger.info("Number of students processed: {}", studentCount);
        logger.info("Duration: {} seconds", seconds);
        logger.info("Duration (rounded): {} minutes (for report)", minutesRounded);
    }
}
