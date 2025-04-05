import java.util.*;

public class projectController {
    private List<Project> projectList;

    public projectController(){
        this.projectList = new ArrayList<>();
    }

    public boolean checkVisibility(User user, String flatType){
        int age = user.getAge();
        String maritalStatus = user.getMaritalStatus();

        // Case 1: 21 yrs & older + married can apply for all rooms.
        if (age >= 21 && maritalStatus.equalsIgnoreCase("Married")){
            return true;
        }

        // Case 2: 35 yrs & older & single can only apply for 2 room flat.
        if (age >= 35 && flatType.equals("2-Room")){
            return true;
        }

        // Default: all other cases are invalid.
        return false;
    }

    // get the list of eligible projects for a user based on the flatType
    public List<Project> filterProjects(User user, String flatType){
        List<Project> eligibleProjects = new ArrayList<>();

        for (Project project : projectList){
            if (checkVisibility(user, flatType)){
                eligibleProjects.add(project);
            }
        }
        return eligibleProjects;
    }

    public void addProject(Project project){
        this.projectList.add(project);
    }

    public List<Project> getProjectList(){
        return projectList;
    }
}
