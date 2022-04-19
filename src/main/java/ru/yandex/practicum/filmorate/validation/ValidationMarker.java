package ru.yandex.practicum.filmorate.validation;

/**
 * Helper marking interface for fields validation grouping.
 */
public interface ValidationMarker {

    /**
     * Marks for validation during creation.
     */
    interface OnCreate {}

    /**
     * Marks for validation during update.
     */
    interface OnUpdate {}
}
