import javax.swing.*;
import java.awt.*;
import java.sql.Time;
import java.util.*;
import java.util.List;
import java.util.Timer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Main {

    /*Settings start*/

    //The IntelliJ IDEA uses a kind of log, not the console/terminal, so it can't clear the console. If DISABLED does not print a strange character at the start of a line.
    static boolean isIntelliJ = true;

    /*Settings end*/

    public static void main(String[] args) {
        try {
            start();
        }catch (InputMismatchException e) {
            System.out.println("Wrong Input!\nPlease input a number between 0-8!");
        }
    }

    public static void start() throws IllegalArgumentException, InputMismatchException {

        boolean beforeFirstPlay = true;
        result result = Main.result.NONE;
        char[] table = {'-', '-', '-',
                '-', '-', '-'
                , '-', '-', '-'};

        while (result == Main.result.NONE) {
            int tempInt = 0;
            for (char c : table) {
                tempInt++;
                if (tempInt == 3) {
                    tempInt = 0;
                    System.out.print(c + " \n");
                    continue;
                }
                System.out.print(c + " ");
            }
            System.out.print('\n');

            Scanner sc = new Scanner(System.in);
            int pos = sc.nextInt();

            //pos alr taken
            if (table[pos] != '-') {
                System.out.print("Position already taken!\n");
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                clear();
                continue;
            }

            table[pos] = 'x';

            result = check(table);

            int AiChose = AiPlay(table);
            //check if draw with custom exit code
            if (AiChose == 99) {
                if (check(table) != Main.result.NONE) {
                    break;
                }
                System.out.print("DRAW!");
                return;
            }
            table[AiChose] = 'o';

            result = check(table);

            //set firstPlay to false
            beforeFirstPlay = false;

            clear();
        }

        if (result == Main.result.LOOSE) {
            System.out.print("You lost :(\n");
            int tempInt = 0;
            for (char c : table) {
                tempInt++;
                if (tempInt == 3) {
                    tempInt = 0;
                    System.out.print(c + " \n");
                    continue;
                }
                System.out.print(c + " ");
            }
        } else {
            System.out.print("You won :)\n");
            int tempInt = 0;
            for (char c : table) {
                tempInt++;
                if (tempInt == 3) {
                    tempInt = 0;
                    System.out.print(c + " \n");
                    continue;
                }
                System.out.print(c + " ");
            }
        }


    }

    public static int AiPlay(char[] table) {
        StringBuilder tableStringBuilder = new StringBuilder();
        List<Integer> available = new ArrayList<>();
        int slot = 0;
        for (char c : table) {
            //add char to tableString
            tableStringBuilder.append(c);

            if (c == '-') {
                available.add(slot);
            }
            slot++;
        }
        //Draw: return custom error/draw exit code
        if (available.size() == 0) return 99;

        String tableS = tableStringBuilder.toString();


        //Execute AI
        return executeAi(table, available);
    }

    //the actual AI
    public static int executeAi(char[] table, List<Integer> available) {
        List<Character> tableList = new ArrayList<>();
        for (char c : table) {
            tableList.add(c);
        }

        StringBuilder row1B = new StringBuilder(); row1B.append(table[0]); row1B.append(table[1]); row1B.append(table[2]);
        StringBuilder row2B = new StringBuilder(); row2B.append(table[3]); row2B.append(table[4]); row2B.append(table[5]);
        StringBuilder row3B = new StringBuilder(); row3B.append(table[6]); row3B.append(table[7]); row3B.append(table[8]);
        String row1 = row1B.toString();
        String row2 = row2B.toString();
        String row3 = row3B.toString();
        String[] rows = {row1, row2, row3};

        StringBuilder diagonal1B = new StringBuilder(); diagonal1B.append(table[0]); diagonal1B.append(table[4]); diagonal1B.append(table[8]);
        StringBuilder diagonal2B = new StringBuilder(); diagonal2B.append(table[6]); diagonal2B.append(table[4]); diagonal2B.append(table[2]);
        String diagonal1 = diagonal1B.toString();
        String diagonal2 = diagonal2B.toString();

        StringBuilder column1B = new StringBuilder(); column1B.append(table[0]); column1B.append(table[3]); column1B.append(table[6]);
        StringBuilder column2B = new StringBuilder(); column2B.append(table[1]); column2B.append(table[4]); column2B.append(table[7]);
        StringBuilder column3B = new StringBuilder(); column3B.append(table[2]); column3B.append(table[5]); column3B.append(table[8]);
        String column1 = column1B.toString();
        String column2 = column2B.toString();
        String column3 = column3B.toString();
        String[] columns = {column1, column2, column3};

        /*
        Attack!
         */
        //check if row contains 2 o's and inserts an o if true
        {
            if ((row1.contains("oo") || row1.contains("o-o")) && row1.contains("-")) {
                for (int i = 0; i < row1.length(); i++) {
                    if (row1.charAt(i) == '-') {
                        return i;
                    }
                }
            }
            if ((row2.contains("oo") || row2.contains("o-o")) && row2.contains("-")) {
                for (int i = 0; i < row2.length(); i++) {
                    if (row2.charAt(i) == '-') {
                        return i + 3;
                    }
                }
            }
            if ((row3.contains("oo") || row3.contains("o-o")) && row3.contains("-")) {
                for (int i = 0; i < row3.length(); i++) {
                    if (row3.charAt(i) == '-') {
                        return i + 6;
                    }
                }
            }
        }

        //check if column contains 2 o's and inserts an o if true
        {
            if ((column1.contains("oo") || column1.contains("o-o")) && column1.contains("-")) {
                for (int i = 0; i < column1.length(); i++) {
                    if (column1.charAt(i) == '-') {
                        if (i == 0) {
                            return 0;
                        }else if (i == 1) {
                            return 3;
                        }else return 6;

                    }
                }
            }

            if ((column2.contains("oo") || column2.contains("o-o")) && column2.contains("-")) {
                for (int i = 0; i < column2.length(); i++) {
                    if (column2.charAt(i) == '-') {
                        if (i == 0) {
                            return 1;
                        }else if (i == 1) {
                            return 4;
                        }else return 7;

                    }
                }
            }

            if ((column3.contains("oo") || column3.contains("o-o")) && column3.contains("-")) {
                for (int i = 0; i < column3.length(); i++) {
                    if (column3.charAt(i) == '-') {
                        if (i == 0) {
                            return 2;
                        }else if (i == 1) {
                            return 5;
                        }else return 8;

                    }
                }
            }
        }

        //check if diagonal row contains 2 o's and inserts an o if true
        {
            if ((diagonal1.contains("oo") || diagonal1.contains("o-o")) && diagonal1.contains("-")) {
                for (int i = 0; i < diagonal1.length(); i++) {
                    if (diagonal1.charAt(i) == '-') {
                        if (i == 0) {
                            return 0;
                        }else if (i == 1) {
                            return 4;
                        }else return 8;
                    }
                }
            }

            if ((diagonal2.contains("oo") || diagonal2.contains("o-o")) && diagonal2.contains("-")) {
                for (int i = 0; i < diagonal2.length(); i++) {
                    if (diagonal2.charAt(i) == '-') {
                        if (i == 0) {
                            return 6;
                        }else if (i == 1) {
                            return 4;
                        }else return 2;
                    }
                }
            }
        }

        /*
        Defend!
         */
        //check if row contains 2 x's but not an o and blocks the path with an o
        {
            if ((row1.contains("xx") || row1.contains("x-x")) && row1.contains("-")) {
                for (int i = 0; i < row1.length(); i++) {
                    if (row1.charAt(i) == '-') {
                        return i;
                    }
                }
            }
            if ((row2.contains("xx") || row2.contains("x-x")) && row2.contains("-")) {
                for (int i = 0; i < row2.length(); i++) {
                    if (row2.charAt(i) == '-') {
                        return i + 3;
                    }
                }
            }
            if ((row3.contains("xx") || row3.contains("x-x")) && row3.contains("-")) {
                for (int i = 0; i < row3.length(); i++) {
                    if (row3.charAt(i) == '-') {
                        return i + 6;
                    }
                }
            }
        }

        //check if column contains 2 x's but not an o and blocks the path with an o
        {
            if ((column1.contains("xx") || column1.contains("x-x")) && column1.contains("-")) {
                for (int i = 0; i < column1.length(); i++) {
                    if (column1.charAt(i) == '-') {
                        if (i == 0) {
                            return 0;
                        }else if (i == 1) {
                            return 3;
                        }else return 6;

                    }
                }
            }

            if ((column2.contains("xx") || column2.contains("x-x")) && column2.contains("-")) {
                for (int i = 0; i < column2.length(); i++) {
                    if (column2.charAt(i) == '-') {
                        if (i == 0) {
                            return 1;
                        }else if (i == 1) {
                            return 4;
                        }else return 7;

                    }
                }
            }

            if ((column3.contains("xx") || column3.contains("x-x")) && column3.contains("-")) {
                for (int i = 0; i < column3.length(); i++) {
                    if (column3.charAt(i) == '-') {
                        if (i == 0) {
                            return 2;
                        }else if (i == 1) {
                            return 5;
                        }else return 8;

                    }
                }
            }
        }

        //check if diagonal row contains 2 x's but not an o and blocks the path with an o
        {
            if ((diagonal1.contains("xx") || diagonal1.contains("x-x")) && diagonal1.contains("-")) {
                for (int i = 0; i < diagonal1.length(); i++) {
                    if (diagonal1.charAt(i) == '-') {
                        if (i == 0) {
                            return 0;
                        }else if (i == 1) {
                            return 4;
                        }else return 8;
                    }
                }
            }

            if ((diagonal2.contains("xx") || diagonal2.contains("x-x")) && diagonal2.contains("-")) {
                for (int i = 0; i < diagonal2.length(); i++) {
                    if (diagonal2.charAt(i) == '-') {
                        if (i == 0) {
                            return 6;
                        }else if (i == 1) {
                            return 4;
                        }else return 2;
                    }
                }
            }
        }

        //else if field in the centre is free, place o on it
        if (table[4] == '-') {
            return 4;
        }

        //else
        Random random = new Random();
        int returnInt = random.nextInt(8);
        while (!available.contains(returnInt)) {
            returnInt = random.nextInt(8);
        }
        return returnInt;
    }


    public static result check(char[] table) {

        Pattern pattern = Pattern.compile("([XO])(?:(?:\\1\\1)|(?:(?:[\\sXO-]{2}\\1){2})|(?:[\\sXO-]{3}\\1){2}|(?:[\\sXO-]{4}\\1){2})", Pattern.CASE_INSENSITIVE);
        StringBuilder b = new StringBuilder();
        int is3 = 1;
        for (char c : table) {
            if (is3 == 3) {
                b.append(c).append("\n");
                is3 = 1;
                continue;
            }
            b.append(c);
            is3++;
        }
        Matcher matcher = pattern.matcher(b.toString());
        boolean matchFound = matcher.find();
        if (matchFound) {
            if (matcher.group(1).contains("x")) {

                return result.WIN;
            } else return result.LOOSE;
        }

        return result.NONE;
    }

    static enum result {
        WIN,
        LOOSE,
        NONE
    }

    public static void clear() {

        if (isIntelliJ) {
            return;
        }

        try{
            String operatingSystem = System.getProperty("os.name"); //Check the current operating system

            if(operatingSystem.contains("Windows")){
                ProcessBuilder pb = new ProcessBuilder("cmd", "/c", "cls");
                Process startProcess = pb.inheritIO().start();
                startProcess.waitFor();
            } else {
                ProcessBuilder pb = new ProcessBuilder("clear");
                Process startProcess = pb.inheritIO().start();

                startProcess.waitFor();
            }
        }catch(Exception e){
            e.printStackTrace();
        }

    }
}
