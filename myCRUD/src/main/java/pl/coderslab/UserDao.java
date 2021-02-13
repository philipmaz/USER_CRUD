package pl.coderslab;

import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.Arrays;

public class UserDao {
    public static final String CREATE_USER_QUERY = "INSERT INTO users(id,username,email,password) VALUES(default,?,?,?);";
    private static final String READ_ID = "SELECT * FROM users WHERE id = ?;";
    private static final String UPDATE_QUERY = "UPDATE users SET username=?, email=?, password=? WHERE id=?;";
    private static final String DELETE_QUERY = "DELETE FROM users WHERE id=?;";
    private static final String SELECT_ALL = "SELECT * FROM users;";
    private static final String SELECT_LINE = "SELECT * FROM users WHERE id=?;";

    public static User create(User user) {

        try (Connection conn = DBUtil.connect("workshop2")) {
            PreparedStatement statement =
                    conn.prepareStatement(CREATE_USER_QUERY, Statement.RETURN_GENERATED_KEYS);

            statement.setString(1, user.getUserName());
            statement.setString(2, user.getEmail());
            statement.setString(3, hashPassword(user.getPassword()));

            statement.executeUpdate();
            //Pobieramy wstawiony do bazy identyfikator, a następnie ustawiamy id obiektu user.
            ResultSet resultSet = statement.getGeneratedKeys();

            if (resultSet.next()) {
                user.setId(resultSet.getInt(1));
            }
            return user;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

    }

    public static void read_line(int userId, String... columnNames) {
        try (Connection conn = DBUtil.connect("workshop2")) {
            PreparedStatement statement = conn.prepareStatement(READ_ID);
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                for (String param : columnNames) {
                    System.out.print(resultSet.getString(param) + " | ");
                }
                System.out.println();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static User read(int userId) {
        try (Connection conn = DBUtil.connect("workshop2")) {
            PreparedStatement statement = conn.prepareStatement(READ_ID);
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                User user = new User();
                user.setId(resultSet.getInt(1));
                user.setUserName(resultSet.getString(2));
                user.setEmail(resultSet.getString(3));
                user.setPassword(resultSet.getString(4));
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

//   zmiana atrybutow usera odbywa sie w OP za pomoca setterow nastepnie tak zmieniony user zostaje przepisany do bazy danych za pomoca tej metody update
    public static void update(User user) {
        try (Connection conn = DBUtil.connect("workshop2")) {
            PreparedStatement preparedStatement = conn.prepareStatement(UPDATE_QUERY);
            preparedStatement.setString(1, user.getUserName());
            preparedStatement.setString(2, user.getEmail());
            preparedStatement.setString(3, hashPassword(user.getPassword()));
            preparedStatement.setInt(4, user.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void delete(int userId) {
        try(Connection conn=DBUtil.connect("workshop2")){
            PreparedStatement preparedStatement = conn.prepareStatement(DELETE_QUERY);
            preparedStatement.setInt(1,userId);
            preparedStatement.executeUpdate();
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }

    public static User[] addToArray(User u, User[] users) {

        User[] tmpUsers = Arrays.copyOf(users, users.length + 1); // Tworzymy kopię tablicy powiększoną o 1.
        tmpUsers[users.length] = u; // Dodajemy obiekt na ostatniej pozycji.
        return tmpUsers; // Zwracamy nową tablicę.

    }

    public static User[] findAll(){
        try(Connection conn=DBUtil.connect("workshop2")){
            PreparedStatement preparedStatement = conn.prepareStatement(SELECT_ALL);
            ResultSet resultSet = preparedStatement.executeQuery();

            User[] users={};

            while(resultSet.next()){
                User user= new User();

                user.setId(resultSet.getInt(1));
                user.setUserName(resultSet.getString(2));
                user.setEmail(resultSet.getString(3));
                user.setPassword(resultSet.getString(4));

                users=addToArray(user, users);
            }
            return users;
        }
        catch (SQLException e){
            e.printStackTrace();
            return null;
        }

    }


    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }
}
