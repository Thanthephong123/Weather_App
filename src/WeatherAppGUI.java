import org.json.simple.JSONObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class WeatherAppGUI extends JFrame {
    private JSONObject weatherData;

    public WeatherAppGUI() {
        //GUI setup
        super("Weather App");

        //End program once it has been closed
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        //set size of GUI
        setSize(450, 650);

        //Center GUI at the screen when loaded
        setLocationRelativeTo(null);

        setLayout(null);
        getContentPane().setBackground(Color.PINK);

        setResizable(false); //Prevent resizing

        addGuiComponents();
    }

    private void addGuiComponents() {
        JTextField searchTextField = new JTextField();
        searchTextField.setBounds(15, 15, 351, 45);
        searchTextField.setFont(new Font("Dialog", Font.PLAIN, 24));
        add(searchTextField);


        //Weather icons - images
        JLabel weatherConditionImage = new JLabel(loadImage("src/assets/cloudy.png"));
        weatherConditionImage.setBounds(0, 125, 450, 217);
        add(weatherConditionImage);

        //Text
        JLabel temperatureText = new JLabel("10 C");
        temperatureText.setBounds(0, 350, 450, 54);
        temperatureText.setFont(new Font("Dialog", Font.BOLD, 48));
        temperatureText.setHorizontalAlignment(SwingConstants.CENTER);
        add(temperatureText);

        //Weather description (complete later)
        JLabel weatherConditionDesc = new JLabel("Cloudy");
        weatherConditionDesc.setBounds(0,405,450,36);
        weatherConditionDesc.setFont(new Font("Dialog", Font.PLAIN, 32));
        weatherConditionDesc.setHorizontalAlignment(SwingConstants.CENTER);
        add(weatherConditionDesc);

        //Add humidity images
        JLabel humidityImage = new JLabel(loadImage("src/assets/humidity.png"));
        humidityImage.setBounds(15, 500, 74, 66);
        add(humidityImage);

        //Humidity text
        JLabel humidityText = new JLabel("<html><b>Humidity</b> 100% </html>");
        humidityText.setBounds(90,500,85,55);
        humidityText.setFont(new Font("Dialog", Font.PLAIN, 16));
        add(humidityText);

        //Add wind
        JLabel windspeedImage = new JLabel(loadImage("src/assets/windspeed.png"));
        windspeedImage.setBounds(220, 500, 74, 66);
        add(windspeedImage);

        JLabel windspeedText = new JLabel("<html><b>Humidity</b> 15km/h </html>");
        windspeedText.setBounds(310, 500, 85, 55);
        windspeedText.setFont(new Font("Dialog", Font.PLAIN, 16));
        add(windspeedText);

        //Search Button
        JButton searchButton = new JButton(loadImage ("src/assets/search.png"));

        //Event Listener, cursor adjust when hovering over button
        searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        searchButton.setBounds(375, 13, 47, 45);
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userInput = searchTextField.getText();
                if (userInput.replaceAll("\\s", "").length() <= 0) {
                    return;
                }
                // Retrieve weather data
                weatherData = WeatherApp.getWeatherData(userInput);
                System.out.println(weatherData);

                // Update image
                String weatherCondition = (String) weatherData.get("weather_condition");
                switch (weatherCondition) {
                    case "Clear":
                        weatherConditionImage.setIcon(loadImage("src/assets/clear.png"));
                        break;
                    case "Cloudy":
                        weatherConditionImage.setIcon(loadImage("src/assets/cloudy.png"));
                        break;
                    case "Rain":
                        weatherConditionImage.setIcon(loadImage("src/assets/rain.png"));
                        break;
                    case "Snow":
                        weatherConditionImage.setIcon(loadImage("src/assets/snow.png"));
                        break;
                }

                //Update temp
                double temperature = (double) weatherData.get("temperature");
                temperatureText.setText(temperature + " C");

                //Update weather condition text
                weatherConditionDesc.setText(weatherCondition);

                //Update humidity
                long humidity = (long) weatherData.get("humidity");
                humidityText.setText("<html><b>Humidity</b> " + humidity + "%</html?");

                // Update wind
                double windspeed = (double) weatherData.get("windspeed");
                windspeedText.setText("<html><b>Windspeed</b> " + windspeed + "km/h</html?");

            }
        });
        add(searchButton);

    }

    // Create images in GUI component (FIX ERROR)
    private ImageIcon loadImage(String resourcePath) {
        try{
            //read images
            BufferedImage image = ImageIO.read(new File(resourcePath));

            //Return image icon
            return new ImageIcon(image);
        }catch(IOException e){
            e.printStackTrace();
        }

        System.out.println("Could not find image");
        return null;
    }
}