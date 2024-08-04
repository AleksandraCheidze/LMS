package de.aittr.lmsbe.model;


import lombok.Getter;

/**
 * @author Andrej Reutow
 * created on 17.11.2023
 */
@Getter
public enum LessonModul {

    BASIC_PROGRAMMING("basic_programming"),
    GIT_AND_LINUX("linux_git"),
    BACK_END("back_end"),
    FRONT_END("front_end"),
    DB("db"),
    DEPLOYMENT("deployment"),
    JOB_SEARCH("job_search"),
    QA("qa"),
    UNDEFINED("undefined");

    private final String lessonModulName;

    LessonModul(String lessonModulName) {
        this.lessonModulName = lessonModulName;
    }

    public static LessonModul getByName(String lessonModulName) {
        if (lessonModulName != null) {
            for (LessonModul modul : LessonModul.values()) {
                if (modul.lessonModulName.equalsIgnoreCase(lessonModulName)) {
                    return modul;
                }
            }
        }
        return UNDEFINED;
    }
}
