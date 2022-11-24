
public class App {
    public static void main(String[] args) throws Exception {
        DB db = new DB();
        Menu menu = new Menu();

        while (!db.conn.isClosed()) {
            menu.login();
        }
    }
}
