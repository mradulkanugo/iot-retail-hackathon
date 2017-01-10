#include <ArduinoJson.h>
#include<SoftwareSerial.h>
#include <Servo.h>

SoftwareSerial softwareSerial = SoftwareSerial(2, 3);
Servo servoMotorOne, servoMotorTwo;
StaticJsonBuffer<200> jsonBuffer;

char json[] = "{\"motorNumber\":0,\"degree\":90}";

void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
  softwareSerial.begin(9600);
  servoMotorOne.attach(9);
  servoMotorTwo.attach(10);
}

void loop() {
    JsonObject& root = jsonBuffer.parseObject(json);
    if (!root.success())
    {
      Serial.println("parseObject() failed");
      return;
    }
    int motorNumber = root["motorNumber"];
    int degreeOfRotation = root["degree"];

    Serial.println(motorNumber);
    Serial.println(degreeOfRotation);
    
    switch (motorNumber)
    {
      case 0:
        servoMotorOne.write(degreeOfRotation);
        break;
      case 1:
        servoMotorTwo.write(degreeOfRotation);
        break;
    }
    delay(1000);
    while(1){}
}
