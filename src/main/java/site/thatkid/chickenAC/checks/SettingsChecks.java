package site.thatkid.chickenAC.checks;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE) // or ElementType.METHOD if you prefer
public @interface SettingsChecks {
    String name();
    String description();
    Category category();
}
