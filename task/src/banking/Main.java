package banking;

import org.sqlite.SQLiteDataSource;

import java.sql.*;
import java.util.*;

public class Main {
    Scanner scanner = new Scanner(System.in);
    ArrayList<Card> accounts = new ArrayList<Card>();
    SQLiteDataSource dataSource;
    Connection con;

    private void welcomePrompt(){
        System.out.println("1. Create an account");
        System.out.println("2. Log into account");
        System.out.println("0. Exit");

        chooseAction(scanner.next());
    }

    private void chooseAction(String a){
        switch (a){
            case "1": createAcc(); break;
            case "2": logInAcc(); break;
            case "0": exit();
        }
    }

    private void createAcc(){
        Card newUser = new Card();

        created(newUser);
        accounts.add(newUser);
        // Statement execution
        addToDB(newUser);
        welcomePrompt();
    }

    private void addToDB(Card newC){

        try (PreparedStatement preparedst = con.prepareStatement("INSERT INTO card (number, pin) VALUES (?,?)")) {
            preparedst.setString(1, newC.getBin());
            preparedst.setString(2, newC.getPin());
            preparedst.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private void logInAcc(){
        System.out.println("Enter your card number:");
        String cNum = scanner.next();
        System.out.println("Enter your PIN:");
        String pNum = scanner.next();

        auth(cNum, pNum);
    }

    private void auth(String num, String p){
        String query = "SELECT * FROM card "
                + "WHERE number = ? AND pin = ?";

        try(PreparedStatement st = con.prepareStatement(query)){
            st.setString(1, num);
            st.setString(2, p);
            try(ResultSet currUser = st.executeQuery()){
                if(currUser.next()){
                    System.out.println("You have successfully logged in!");
                    loggedInterface(num);
                } else {
                    System.out.println("Wrong card number or PIN!");
                    welcomePrompt();
                }
            } catch(SQLException e){
                e.printStackTrace();
            }
        } catch(SQLException e){
            e.printStackTrace();
        }

    }

    private void loggedInterface(String n){
        prompt2();

        switch(scanner.next()){
            case "1": printIncome(n); loggedInterface(n); break;
            case "2": addIncome(n); break;
            case "3": transfer(n); break;
            case "4": deleteAccount(n); break;
            case "5": welcomePrompt(); break;
            case "0": exit();
        }
    }

    private void printIncome(String num) {
        String query = "SELECT * FROM card "
                + "WHERE number = ?";

        try(PreparedStatement st = con.prepareStatement(query)){
            st.setString(1, num);
            try(ResultSet currUser = st.executeQuery()){
                if(currUser.next()){
                    System.out.println("Balance: " + currUser.getInt("balance"));
                    loggedInterface(num);
                }
            } catch(SQLException e){
                e.printStackTrace();
            }
        } catch(SQLException e){
            e.printStackTrace();
        }

    }

    private void addIncome(String pnum){
        System.out.println("Enter income:");
        int inc = scanner.nextInt();

        String updateBalance =  "UPDATE card "
                + "SET balance = balance + ? "
                + "WHERE number = ?";


        try (PreparedStatement prep = con.prepareStatement(updateBalance)){
            prep.setInt(1, inc);
            prep.setString(2, pnum);
            prep.executeUpdate();
            System.out.println("Income was added!");
            loggedInterface(pnum);
        } catch(SQLException e){
            e.printStackTrace();
        }

    }

    private void decIncome(int tr, String pnum){
        //System.out.println("Enter income:");

        String updateBalance =  "UPDATE card "
                + "SET balance = balance - ? "
                + "WHERE number = ?";

        try (PreparedStatement prep = con.prepareStatement(updateBalance)){
            prep.setInt(1, tr);
            prep.setString(2, pnum);
            prep.executeUpdate();
        } catch(SQLException e){
            e.printStackTrace();
        }

    }

    private void transfer(String pnum) {
        System.out.println("Transfer");
        System.out.println("Enter card number:");
        String toCard = scanner.next();
        Luhn l = new Luhn();
        boolean adheres = l.adheresToLuhn(toCard);
        System.out.println(adheres);
        if (adheres == false){
            System.out.println("Probably you made a mistake in the card number. Please try again!");
            loggedInterface(pnum);
        }
        String query = "SELECT * FROM card "
                + "WHERE number = ?";
        int balanceOnOriginalCard = 0;
        try(PreparedStatement st = con.prepareStatement(query)){
            st.setString(1, pnum);
            try(ResultSet currUser = st.executeQuery()){
                if(currUser.next()){
                    balanceOnOriginalCard = currUser.getInt("balance");
                }
            } catch(SQLException e){
                e.printStackTrace();
            }
        } catch(SQLException e){
            e.printStackTrace();
        }


        try(PreparedStatement st = con.prepareStatement(query)){
            st.setString(1, toCard);
            try(ResultSet currUser = st.executeQuery()){
                if(currUser.next()){
                    System.out.println("Enter how much money you want to transfer:");
                    int toTranfer = scanner.nextInt();
                    if(toTranfer <= balanceOnOriginalCard){
                        System.out.println("Success!");
                        decIncome(toTranfer, pnum);
                        addTransferedMoney(toTranfer, toCard);
                        loggedInterface(pnum);
                    } else {
                        System.out.println("Not enough Money!");
                    }
                } else {
                    System.out.println("Such a card does not exist.");
                }
                loggedInterface(pnum);
            } catch(SQLException e){
                    e.printStackTrace();
                }
        } catch(SQLException e){
                e.printStackTrace();
            }

    }

    private void addTransferedMoney(int t, String c){
        String updateBalance =  "UPDATE card "
                + "SET balance = balance + ? "
                + "WHERE number = ?";


        try (PreparedStatement prep = con.prepareStatement(updateBalance)){
            prep.setInt(1, t);
            prep.setString(2, c);
            prep.executeUpdate();
        } catch(SQLException e){
                e.printStackTrace();
            }


    }

    private void deleteAccount(String num){
        String closeAcc = "DELETE FROM card "
                + "WHERE number = ?";

        try (PreparedStatement prep = con.prepareStatement(closeAcc)){
            prep.setString(1, num);
            prep.executeUpdate();
        } catch(SQLException e){
                e.printStackTrace();
            }

        System.out.println("The account has been closed!");
        welcomePrompt();
    }

    private void exit(){
        System.out.println("Bye!");
        System.exit(0);
    }

    static void prompt2(){
        System.out.println("1. Balance");
        System.out.println("2. Add income");
        System.out.println("3. Do transfer");
        System.out.println("4. Close account");
        System.out.println("5. Log out");
        System.out.println("0. Exit");
    }

    static void created(Card a){
        System.out.println("Your card has been created");
        System.out.println("Your card number:");
        System.out.println(a.getBin());
        System.out.println("Your card PIN:");
        System.out.println(a.getPin());
    }

    public static void main(String[] args) {
        new Main().go(args);
    }

    void go(String[] args){
        String url = "jdbc:sqlite:" + args[1];

        dataSource = new SQLiteDataSource();
        dataSource.setUrl(url);


        try {
            con = dataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try (Statement statement = con.createStatement()) {
            // Statement execution
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS card(" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "number TEXT NOT NULL," +
                    "pin TEXT NOT NULL," +
                    "balance INTEGER DEFAULT 0)");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        welcomePrompt();
    }
}