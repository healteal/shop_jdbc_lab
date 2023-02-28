import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;

public class General {
    public static void main(String[] args) {
        operation("jdbc:mysql://localhost/shop_jdbc", "root", "123qwe");
    }

    static void operation(String url, String username, String password) {
        try {
            System.out.println("Введите логин:");
            String login = input();
            String fio;
            Connection connection = DriverManager.getConnection(url, username, password);
            Statement statement = connection.createStatement();
            statement.execute("CREATE TABLE IF NOT EXISTS products" +
                    " (id_product int primary key auto_increment," +
                    "name_product nvarchar(50) not null," +
                    "country_of_produce nvarchar(50) not null," +
                    "count_product int not null," +
                    "price_product int not null );");
            ResultSet checkDataProducts = statement.executeQuery("SELECT * FROM products");
            if (!checkDataProducts.next()) {
                putDataAboutProducts(statement);
            }
            ResultSet checkUser = statement.executeQuery("SELECT * FROM clients WHERE" +
                    " (login_client = '" + login + "')");
            if (checkUser.next()) {
                int sum = checkUser.getInt("sum_client");
                fio = checkUser.getString("fio_client");
                ResultSet selectAllProducts = statement.executeQuery("SELECT * FROM products");
                while (selectAllProducts.next()) {
                    System.out.println(selectAllProducts.getString("name_product"));
                }
                System.out.println("Выберите продукт:");
                String product = input();
                ResultSet checkCorrectInput = statement.executeQuery("SELECT * FROM products");
                boolean isExists = false;
                int price = 0;
                while (checkCorrectInput.next()) {
                    if (checkCorrectInput.getString("name_product").equals(product)) {
                        price = checkCorrectInput.getInt("price_product");
                        isExists = true;
                        break;
                    }
                }
                if (isExists) {
                    if (sum - price > 0) {
                        statement.executeUpdate("UPDATE products SET " +
                                "count_product = count_product - 1" +
                                " WHERE (name_product = '" + product + "')");
                        statement.executeUpdate("UPDATE clients SET " +
                                "sum_client = sum_client -" + price +
                                " WHERE (login_client = '" + login + "');");
                        statement.executeUpdate("INSERT INTO history_operations " +
                                "(fio_client_history, sum_client_history)" +
                                " VALUES ('" + fio + "'," + (sum - price) + ");");
                    } else {
                        System.out.println("Недостаточно средств на балансе");
                        System.exit(1);
                    }
                } else {
                    System.out.println("Неверный ввод");
                    System.exit(1);
                }
            } else {
                System.out.println("Такого юзера нет");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void putDataAboutProducts(Statement statement) throws SQLException {
        statement.executeUpdate("INSERT INTO products" +
                "(name_product," +
                " country_of_produce, " +
                "count_product," +
                "price_product) VALUES(" +
                "'груша', 'Беларусь', 10, 20),");
        statement.executeUpdate("INSERT INTO products" +
                "(name_product," +
                " country_of_produce, " +
                "count_product," +
                "price_product) VALUES(" +
                "'яблоко', 'Беларусь', 8, 15)");
        statement.executeUpdate("INSERT INTO products" +
                "(name_product," +
                " country_of_produce, " +
                "count_product," +
                "price_product) VALUES(" +
                "'дыня', 'Беларусь', 11, 25)");
        statement.executeUpdate("INSERT INTO products" +
                "(name_product," +
                " country_of_produce, " +
                "count_product," +
                "price_product) VALUES(" +
                "'арбуз', 'Беларусь', 15, 23)");
        statement.executeUpdate("INSERT INTO products" +
                "(name_product," +
                " country_of_produce, " +
                "count_product," +
                "price_product) VALUES(" +
                "'вишня', 'Беларусь', 3, 30)");
    }

    static String input() {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        try {
            return bufferedReader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
