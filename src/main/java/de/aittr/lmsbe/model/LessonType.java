package de.aittr.lmsbe.model;

import lombok.Getter;

/**
 * @author Andrej Reutow
 * created on 17.11.2023
 */
@Getter
public enum LessonType {

    LECTURE("lecture"),
    CONSULTATION("consultation"),
    ELECTIVE("elective"),
    PROJECT("project"),
    QUIZ("quiz"),
    UNDEFINED("undefined");

    private final String lessonTypeName;

    LessonType(String lessonTypeName) {
        this.lessonTypeName = lessonTypeName;
    }

    public static LessonType getByName(String lessonTypeName) {
        if (lessonTypeName != null) {
            for (LessonType lessonType : LessonType.values()) {
                if (lessonType.getLessonTypeName().equalsIgnoreCase(lessonTypeName)) {
                    return lessonType;
                }
            }
        }
        return UNDEFINED;
    }
}
