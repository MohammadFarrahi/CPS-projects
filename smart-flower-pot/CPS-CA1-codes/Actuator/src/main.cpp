#include <SoftwareSerial.h>
#include <Wire.h>
#include <Arduino.h>

#define VMTX 3
#define VMRX 2
#define MOTOR_EN_PIN 9
#define MOTOR_IN1_PIN 8
#define MOTOR_IN2_PIN 7

SoftwareSerial VMonitor(VMRX, VMTX); // RX, TX pins for Vritual Monitor

int recieveFromMain() {
    int dutyCycle = -1;
    // Check if there is any data available on the Bluetooth module
    if (Serial.available()) {
      // Read the received data
      String data = Serial.readStringUntil('\n');
      // Parse the duty cycle value from the received data
      dutyCycle = data.substring(data.indexOf("DC:") + 3).toInt();
      // Print the received data to the Serial Monitor
      VMonitor.print("Received data: ");
      VMonitor.println(data);
    }
    return dutyCycle;
}

void setup() {
  // setup DC motor pins
  pinMode(MOTOR_EN_PIN, OUTPUT);
  pinMode(MOTOR_IN1_PIN, OUTPUT);
  pinMode(MOTOR_IN2_PIN, OUTPUT);
  digitalWrite(MOTOR_IN1_PIN, HIGH);
  digitalWrite(MOTOR_IN2_PIN, LOW);

  VMonitor.begin(9600); // Start serial communication with Vurtyak Monitor module
  Serial.begin(9600); // Start serial communication with Bluetooth module
}

void loop() {
  int dutyCycle = recieveFromMain();
  if (dutyCycle >= 0) {
      // Control the motor based on the received speed
      int pwmValue = map(dutyCycle, 0, 100, 0, 255);
      analogWrite(MOTOR_EN_PIN, pwmValue);
  }
}

