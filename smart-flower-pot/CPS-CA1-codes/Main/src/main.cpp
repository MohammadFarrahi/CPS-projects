#include <LiquidCrystal.h>
#include <SoftwareSerial.h>
#include <Wire.h>
#include <Arduino.h>


#define LCD_RS 12
#define LCD_E 13
#define LCD_D4 5
#define LCD_D5 4
#define LCD_D6 3
#define LCD_D7 2

#define VMTX 7
#define VMRX 6

#define SBTX 11
#define SBRX 10

#define Null -10000.0

struct SensorData {
  float humidity;
  float cTemp;
};

SoftwareSerial VMonitor(VMRX, VMTX); // RX | TX
SoftwareSerial senderBluetooth(SBRX,SBTX);
// Set up the LCD
LiquidCrystal lcd(LCD_RS, LCD_E, LCD_D4, LCD_D5, LCD_D6, LCD_D7); // RS, E, D4, D5, D6, D7


SensorData recieveFromSensor() {
    SensorData recievedData = {Null,Null};
    // Check if data is available from the Sensor Node via Bluetooth
    if (Serial.available()) {
      // Read the received data
      String data = Serial.readStringUntil('\n');
      // Parse the temperature and humidity values from the received data
      float humidity = data.substring(data.indexOf("H") + 1, data.indexOf("T")).toFloat();
      float temperature = data.substring(data.indexOf("T") + 1).toFloat();
      // Print the received data to the Serial Monitor
      VMonitor.print("Received data: ");
      VMonitor.println(data);
      recievedData.cTemp = temperature;
      recievedData.humidity = humidity;
    }
    return recievedData;
}

int getIrrigationDutyCycle(SensorData sensorData) {
  // Calculate the irrigation rate based on the specified conditions
  int irrigationDutyCycle = 0;
  if (sensorData.humidity >= 20 && sensorData.humidity <= 30) {
    if (sensorData.cTemp > 25) {
    // Irrigation rate of 10 cc/min with 10% duty cycle
      irrigationDutyCycle = 10;
    }
  } else if (sensorData.humidity >= 10 &&  sensorData.humidity < 20) {
  // Irrigation rate of 15 cc/min with 20% duty cycle
  irrigationDutyCycle = 20;
  } else if ( sensorData.humidity < 10) {
  // Irrigation rate of 20 cc/min with 25% duty cycle
    irrigationDutyCycle = 25;
  }

  VMonitor.print("IRR-DC:");
  VMonitor.print(irrigationDutyCycle);
  VMonitor.println("---");
  return irrigationDutyCycle;
}

void printOnLCD(SensorData sensorData, int irrigationDutyCycle) {
  lcd.setCursor(0, 0);
  lcd.print("Temp: ");
  lcd.print(sensorData.cTemp);
  lcd.print(" C");
  lcd.setCursor(0, 1);
  lcd.print("Humid: ");
  lcd.print(sensorData.humidity);
  lcd.print(" %");
  lcd.setCursor(0, 2);
  lcd.print("IRR-DC:");
  lcd.print(irrigationDutyCycle);
  lcd.print(" %");
}

void setup() {
  // Start the serial communication(attached to reciever bluetooth module)
  Serial.begin(9600);
    // Start the serial communication to sender bluetooth module
  senderBluetooth.begin(9600);
  // Start the Virtual Monitor communication
  VMonitor.begin(9600);
  // Set up the LCD
  lcd.begin(20, 4);
}

void loop() {
  SensorData recievedData = recieveFromSensor();
  // check if data recieved correctly
  if (recievedData.humidity != Null) {
    // get the duty cycle based on the specified conditions
    int irrigationDutyCycle = getIrrigationDutyCycle(recievedData);
    // Display the temperature and humidity values on the LCD
    printOnLCD(recievedData, irrigationDutyCycle);
    // Send the irrigation rate command to the Actuator Node via Bluetooth
    senderBluetooth.print("DC:");
    senderBluetooth.print(irrigationDutyCycle);
  }
}
