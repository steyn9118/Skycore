package lampteam.skycore.models;

public enum LinearDirection {

    FORWARDS (1),
    BACKWARDS (-1);

    private int value;

    LinearDirection(int value){
        this.value = value;
    }

    public int getValue(){
        return value;
    }
}
