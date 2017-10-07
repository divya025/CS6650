package bsds;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class MyClient {

    public static void main(String[] args) {

        MyServer myServer = new MyServer();
        BufferedReader myBuffer = null;

        try {
            String line;
            myBuffer = new BufferedReader(new FileReader("BSDSAssignment2Day1.csv"));
            ArrayList<String> result = new ArrayList<String>();

            // How to read file in java line by line?
            while ((line = myBuffer.readLine()) != null) {
                result = myCSVtoArrayList(line);
                String postResult = myServer.postText(result.get(0),
                        result.get(1), result.get(2), result.get(3),
                        result.get(4));
                System.out.println(postResult);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (myBuffer != null) myBuffer.close();
            } catch (IOException myException) {
                myException.printStackTrace();
            }
        }
    }

    // Utility which converts CSV to ArrayList using Split Operation
    public static ArrayList<String> myCSVtoArrayList(String fileCSV) {
        ArrayList<String> result = new ArrayList<String>();

        if (fileCSV != null) {
            String[] splitData = fileCSV.split("\\s*,\\s*");
            for (int i = 0; i < splitData.length; i++) {
                if (!(splitData[i] == null) || !(splitData[i].length() == 0)) {
                    result.add(splitData[i].trim());
                }
            }
        }

        return result;
    }

}
