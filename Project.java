public class Project {
    private String neighbourhood;
    private String flatType;
    

    public Project(String neighbourhood, String flatType){
        this.neighbourhood = neighbourhood;
        this.flatType = flatType;
    }

    public String getNeighbourhood(){
        return this.neighbourhood;
    }

    public String getFlatType(){
        return this.flatType;
    }
}
