import java.io.BufferedReader;
import java.io.InputStreamReader;

class Main {
    public static void main(String[] args) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        // start coding here
        String str = "";
        int val = reader.read();
        while (val != -1) {
            str = String.format("%c%s", val, str);
            val = reader.read();
        }
        System.out.println(str);
        reader.close();
    }
}