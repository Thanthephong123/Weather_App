import javax.swing.*;

public class AppLauncher {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run(){
                //Displaying weather app GUI
                new WeatherAppGUI().setVisible(true);
            }
        });
    }
}

