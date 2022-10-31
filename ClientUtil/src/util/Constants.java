package util;

import com.google.gson.Gson;

public class Constants {

    // global constants
    public final static String LINE_SEPARATOR = System.getProperty("line.separator");
    public final static int REFRESH_RATE = 2000;
    public final static String CHAT_LINE_FORMATTING = "%tH:%tM:%tS | %.10s: %s%n";
    public final static String U_BOAT = "u-boat";
    public final static String ALLY = "ally";
    public final static String AGENT = "agent";
    public final static String THREAD_COUNT = "threadCount";
    public final static String MISSION_COUNT = "missionCount";
    public final static String MISSION_SIZE = "missionSize";
    public final static String IN_QUEUE = "inQueue";
    public final static String FROM_SERVER = "fromServer";
    public final static String FINISHED = "finished";
    public final static String CANDIDATES = "candidates";

    // fxml locations
    public final static String LOGIN_PAGE_FXML_RESOURCE_LOCATION = "/login/login.fxml";
    public final static String CANDIDATES_TABLE_FXML_RESOURCE_LOCATION = "/candidatesTable/candidatesTable.fxml";
    public final static String UBOAT_MAIN_FXML_RESOURCE_LOCATION = "/uBoat/client/component/main/uboat-main.fxml";
    public final static String FILE_UPLOAD_FXML_RESOURCE_LOCATION = "/uBoat/client/component/main/subComp/fileUpload.fxml";
    public final static String DICTIONARY_FXML_RESOURCE_LOCATION = "/uBoat/client/component/main/tabs/contestTab/subComp/dictionary.fxml";

    public final static String ALLIES_MAIN_FXML_RESOURCE_LOCATION = "/client/component/main/allies-main.fxml";
    public final static String AGENTS_TABLE_FXML_RESOURCE_LOCATION = "/client/component/tabs/dashboardTab/subComp/agentsTable/agentsTable.fxml";
    public final static String SINGLE_CONTEST_FXML_RESOURCE_LOCATION = "/client/component/tabs/dashboardTab/subComp/singleContest/singleContest.fxml";

    public final static String AGENT_MAIN_FXML_RESOURCE_LOCATION = "/client/component/main/agent-main.fxml";
    public final static String PREP_PANEL_FXML_RESOURCE_LOCATION = "/client/component/prepPanel/prepPanel.fxml";
    public final static String DETAILS_PANEL_FXML_RESOURCE_LOCATION = "/client/component/detailsPanel/detailsPanel.fxml";
    //maybe

    // Server resources locations
    public final static String BASE_DOMAIN = "localhost";
    private final static String BASE_URL = "http://" + BASE_DOMAIN + ":8080";
    private final static String CONTEXT_PATH = "/enigmaApp";
    private final static String FULL_SERVER_PATH = BASE_URL + CONTEXT_PATH;

    public final static String UPLOAD_FILE = FULL_SERVER_PATH + "/uploadFile";
    public final static String LOGIN_PAGE = FULL_SERVER_PATH + "/login";
    public final static String CONTEST_LIST_PAGE = FULL_SERVER_PATH + "/contestList";
    public final static String USER_LIST_PAGE = FULL_SERVER_PATH + "/userList";
    public final static String JOIN_PAGE = FULL_SERVER_PATH + "/join";
    public final static String AGENT_LIST_PAGE = FULL_SERVER_PATH + "/agentList";
    public final static String MY_ALLIES_LIST_PAGE = FULL_SERVER_PATH + "/myAlliesList";
    public final static String READY_CHECK_PAGE = FULL_SERVER_PATH + "/readyCheck";
    public final static String GET_CANDIDATES_PAGE = FULL_SERVER_PATH + "/getCandidates";
    public final static String ADD_CANDIDATES_PAGE = FULL_SERVER_PATH + "/addCandidates";
    public final static String START_CONTEST_PAGE = FULL_SERVER_PATH +"/startContest";
    public final static String GET_TASKS_PAGE = FULL_SERVER_PATH +"/getTasks";
    public final static String UPDATE_AGENT_PAGE = FULL_SERVER_PATH +"/updateAgent";
    public final static String AGENT_PROGRESS_PAGE = FULL_SERVER_PATH + "/agentProgress";
    public final static String TOTAL_TASKS_PAGE = FULL_SERVER_PATH + "/totalTasks";
    public final static String KILL_CONTEST_PAGE = FULL_SERVER_PATH + "/kill";
    public final static String LOGOUT_PAGE = FULL_SERVER_PATH + "/logout";


    public final static String WINNER_TEMPLATE = "Ally Team %s won the Contest!";




    // GSON instance
    public final static Gson GSON_INSTANCE = new Gson();

    public static void sleepForAWhile(long sleepTime) {
        if (sleepTime != 0) {
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException ignored) {

            }
        }
    }
}
