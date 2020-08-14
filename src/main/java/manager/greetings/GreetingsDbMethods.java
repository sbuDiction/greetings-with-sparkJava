package manager.greetings;

import com.google.gson.JsonObject;
import jsonserialization.Serializer;
import manager.languages.Language;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GreetingsDbMethods implements GreetingsInterface {
    private Connection connection;

    public GreetingsDbMethods(Connection connection) {
        this.connection = connection;
    }

    public void checkNameDuplicate(String name) {
        try {
            PreparedStatement find_user = connection.prepareStatement(String.valueOf(SqlQueries.FIND_NAME_SQL.getQuery()));
            find_user.setString(1, name);
            ResultSet resultSet = find_user.executeQuery();
            if (resultSet.next()) {
                PreparedStatement update_user_counter = connection.prepareStatement(String.valueOf(SqlQueries.UPDATE_USER_COUNTER.getQuery()));
                update_user_counter.setInt(1, resultSet.getInt("count_time") + 1);
                update_user_counter.setString(2, name);
                update_user_counter.execute();
            } else {
                PreparedStatement add_user = connection.prepareStatement(String.valueOf(SqlQueries.INSERT_NAME_SQL.getQuery()));
                add_user.setString(1, name);
                add_user.setInt(2, 1);
                add_user.execute();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public List<String> getNames(int offset) throws SQLException {
        List<String> greetedList = new ArrayList<>();
        PreparedStatement get_all_the_content = connection.prepareStatement(SqlQueries.GET_NAMES.getQuery());
        get_all_the_content.setInt(1, offset);
        ResultSet resultSet = get_all_the_content.executeQuery();


        while (resultSet.next()) {
            greetedList.add(new Serializer()
                    .fromObjectToJson(new People(resultSet.getInt("id"),
                            resultSet.getString("name"),
                            resultSet.getInt("count_time"),
                            getCount()
                    )));
        }
        System.out.println(greetedList);
        return greetedList;
    }


    public Integer getCount() {
        try {
            PreparedStatement statement = connection.prepareStatement("select count(*) as count from greetings");
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return rs.getInt("count");
            } else {
                return 0;
            }
        } catch (SQLException se) {
            se.printStackTrace();
            return 0;
        }
    }

    @Override
    public String greet(String name, Language language) {
        return name;
    }

    @Override
    public String addName(String name) {
        return name;
    }
}
