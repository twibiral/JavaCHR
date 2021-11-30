package wibiral.tim.javachr.rules.head;

/**
 * Head is used for complex head definitions. This way a header can be defined
 */
public class Head {
    /**
     * Called in the head definition to create a container that accepts any constraints.
     * @return Head object that is a container any constraints.
     */
    public static Head any(){
        return new Head();
    }

    /**
     * Called in the head definition to create a container that just accepts the given type.
     * @return Head object that is a container for the given type.
     */
    public static Head ofType(Class<?> type){
        return new Head(type);
    }

    /**
     * Called in the head definition to create a container that just accepts the given value.
     * @return Head object that is a container for the given value.
     */
    public static Head ofValue(Object value){
        return new Head(value);
    }

    /**
     * If this container is of type CONTAINS.VALUE -> contains the value this head constraint wants.
     * Otherwise, null.
     */
    private Object value;

    /**
     * If this container is of type CONTAINS.TYPE -> contains the class this head constraint wants.
     * Otherwise, null.
     */
    private Class<?> type;

    /**
     * The variable this container is bound to. (Used to set the same value for two Constraints in the head).
     */
    private VAR variableThisIsBoundTo = VAR.NONE;

    private final HEAD_CONTAINS headContains;

    private Head(){
        headContains = HEAD_CONTAINS.ANY;
    }

    private Head(Class<?> type){
        headContains = HEAD_CONTAINS.TYPE;
        this.type = type;
    }

    private Head(Object value){
        headContains = HEAD_CONTAINS.VALUE;
        this.value = value;
    }

    public Head bindTo(VAR variable){
        variableThisIsBoundTo = variable;
        return this;
    }

    /**
     * @return The variable this head constraint is bound to.
     */
    public VAR isBoundTo(){
        return variableThisIsBoundTo;
    }

    /**
     * @return The type of which this head constraint must be.
     */
    public Class<?> getType() {
        return type;
    }

    /**
     * @return The value this head constraint must be equal to.
     */
    public Object getValue() {
        return value;
    }

    /**
     * @return How this head constraint is defined.
     */
    public HEAD_CONTAINS getHeadConstraintDefType() {
        return headContains;
    }
}