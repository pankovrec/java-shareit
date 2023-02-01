package ru.practicum.shareit.booking;

import java.lang.annotation.*;
import javax.validation.Constraint;
import javax.validation.Payload;

@Documented
@Constraint(validatedBy = BookingStartBeforeEndValidator.class)
@Target({ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
public @interface DateValidator {
    String message() default "Дата окончания должна быть после даты начала";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
