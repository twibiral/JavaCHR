package wibiral.tim.newjavachr.rules;

/**
 * These types are used to store how the head of a rule was defined
 * SIZE_SPECIFIED:      Just the number of head constraints was defined. Matching happens in {@link Guard}
 * TYPES_SPECIFIED:     The exact type was defined for every constraint in the head.
 * COMPLEX_DEFINITION:  Used to show, that the head was defined by using the {@link Head} class.
 */
public enum HEAD_DEFINITION_TYPE {
    SIZE_SPECIFIED, TYPES_SPECIFIED, COMPLEX_DEFINITION
}
