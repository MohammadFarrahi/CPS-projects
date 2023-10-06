// TODO : clean the code 

#include <Arduino.h>
#include <Wire.h>
#include <SoftwareSerial.h>
#include <stdlib.h>

// SHT25 I2C address is 0x40(64)
#define Addr 0x40

// TX|RX of Virtual Monitor
#define VMTX 3
#define VMRX 2

struct SensorData {
  float humidity;
  float cTemp;
};

SoftwareSerial VMonitor(VMRX, VMTX); // RX | TX

void setup() {
  // Start the I2C communication with the SHT25 sensor
  Wire.begin();
  // Initialise serial communication, set baud rate = 9600
  Serial.begin(9600);
  VMonitor.begin(9600);
}



struct SensorData PollFromSHT25() {
  struct SensorData result;
  unsigned int data[2];
  
  // Start I2C transmission
  Wire.beginTransmission(Addr);
  // Send humidity measurement command (Trigger RH measurement), NO HOLD master
  Wire.write(0xF5);
  // Stop I2C transmission
  Wire.endTransmission();
  
  delay(500);
  
  // Request 2 bytes of data
  Wire.requestFrom(Addr, 2);
  
  // Read 2 bytes of data in the following format:
  // [humidity msb, humidity lsb]
  if(Wire.available() == 2) {
    data[0] = Wire.read();
    data[1] = Wire.read();
    
    // Convert the data
    float humidity = (((data[0] * 256.0 + data[1]) * 125.0) / 65536.0) - 6;
    result.humidity = humidity;
    // Output data to Serial virtualMonitor
    VMonitor.print("Humidity :");
    VMonitor.print(humidity);
    VMonitor.println(" %RH");
  }

  // Start I2C transmission
  Wire.beginTransmission(Addr);
  // Send temperature measurement command (Trigger T measurement), NO HOLD master
  Wire.write(0xF3);
  // Stop I2C transmission
  Wire.endTransmission();
  delay(500);
  
  
  // Request 2 bytes of data  
  Wire.requestFrom(Addr, 2);
  
  // Read 2 bytes of data in the following format:
  // [temp msb, temp lsb]
  if(Wire.available() == 2) {
    data[0] = Wire.read();    
    data[1] = Wire.read();
    
    // Convert the data
    float cTemp = (((data[0] * 256.0 + data[1]) * 175.72) / 65536.0) - 46.85;
    result.cTemp = cTemp;
    
    // Output data to Serial virtualMonitor
    VMonitor.print("Temperature in Celsius :");
    VMonitor.print(cTemp);
    VMonitor.println(" C");
    VMonitor.println("---");
  }

  return result;
}

void sendToMainNode(struct SensorData* sensorData) {
  // Data will be sent in the following format:
  // "H<humidity>T<temperature>$"
  
  Serial.print("H");
  Serial.print(sensorData->humidity);
  Serial.print("T");
  Serial.print(sensorData->cTemp);
  Serial.print("$");
}

float latestSentHumdity = -100;

void loop() {
  struct SensorData sensorData = PollFromSHT25();
  if(abs(latestSentHumdity - sensorData.humidity) > 5) {
    sendToMainNode(&sensorData);
    latestSentHumdity = sensorData.humidity;
  }
  delay(1000);
}