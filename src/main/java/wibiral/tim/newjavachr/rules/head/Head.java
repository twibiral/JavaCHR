package wibiral.tim.newjavachr.rules.head;

/**
 * Head is used for complex head definitions. This way a header can be defined
 */
public class Head {
    /**
     * Called in the head definition to create a container that accepts any constraints.
     * @return Head object that is a container any constraints.
     */
    public static Head ANY(){
        return new Head();
    }

    /**
     * Called in the head definition to create a container that just accepts the given type.
     * @return Head object that is a container for the given type.
     */
    public static Head OF_TYPE(Class<?> type){
        return new Head(type);
    }

    /**
     * Called in the head definition to create a container that just accepts the given value.
     * @return Head object that is a container for the given value.
     */
    public static Head OF_VALUE(Object value){
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
    VAR var = VAR.NONE;

    private final HEAD_CONTAINS containerType;

    private Head(){
        containerType = HEAD_CONTAINS.ANY;
    }

    private Head(Class<?> type){
        containerType = HEAD_CONTAINS.TYPE;
        this.type = type;
    }

    private Head(Object value){
        containerType = HEAD_CONTAINS.VALUE;
        this.value = value;
    }

    public Head bindTo(VAR variable){
        var = variable;
        return this;
    }

    public VAR isBoundTo(){
        return var;
    }

    public Class<?> getType() {
        return type;
    }

    public Object getValue() {
        return value;
    }

    public HEAD_CONTAINS getContainerType() {
        return containerType;
    }
}