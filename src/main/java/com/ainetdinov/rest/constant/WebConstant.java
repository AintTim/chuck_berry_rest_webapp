package com.ainetdinov.rest.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class WebConstant {
    public static final String STUDENT_SERVICE = "studentService";
    public static final String TEACHER_SERVICE = "teacherService";
    public static final String GROUP_SERVICE = "groupService";
    public static final String SCHEDULE_SERVICE = "scheduleService";
    public static final String HTTP_SERVICE = "httpService";
    public static final String PARSER_SERVICE = "parserService";
    public static final String PRECONDITION_SERVICE = "preconditionService";

    public static final String MAX_GROUP_SIZE = "max.group.size";
    public static final String MIN_GROUP_SIZE = "min.group.size";
    public static final String MAX_GROUP_SCHEDULE_LOAD = "max.group.schedule";

    public static final String NUMBER = "number";
    public static final String SURNAME = "surname";
    public static final String SUBJECTS = "subjects";
    public static final String GROUP_NUMBER = "groupNumber";
    public static final String STUDENT_NAME = "studentName";
    public static final String TEACHER_NAME = "teacherName";
    public static final String STUDENT_SURNAME = "studentSurname";
    public static final String SCHEDULE_DATE = "date";

    public static final String STUDENTS_PATH = "students.path";
    public static final String TEACHERS_PATH = "teachers.path";
    public static final String GROUPS_PATH = "groups.path";
    public static final String SCHEDULES_PATH = "schedules.path";

    public static final String DIGIT_REGEX = "\\d";
    public static final String CAPITAL_REGEX = "[A-Z]\\w+";
    public static final String BELARUS_PHONE_REGEX = "^(\\+375) \\d{2} \\d{3}-\\d{2}-\\d{2}";
    public static final String RUSSIAN_PHONE_REGEX = "^(\\+7) \\d{3} \\d{3}-\\d{2}-\\d{2}";
    public static final String UUID_REGEX = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";
}
