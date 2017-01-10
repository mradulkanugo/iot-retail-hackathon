#include <ArduinoJson.h>
#include<SoftwareSerial.h>
#include <Servo.h>

SoftwareSerial softwareSerial = SoftwareSerial(2, 3);
Servo servoMotors[2];
StaticJsonBuffer<200> jsonBuffer;

char json[] = "{\"motorId\":0,\"degree\":90}";

const int pinForServoMotorOne = 9;
const int pinForServoMotorTwo = 10;

class JSONParser {
    int motorId;
    int degreeOfRotation;
    JsonObject& root;

  public:
    JSONParser(char json[]): root(jsonBuffer.parseObject(json)) {
      if (!root.success())
      {
        Serial.println("parseObject() failed");
      }
      motorId = root["motorId"];
      degreeOfRotation = root["degree"];
    }

    int getMotorId() {
      return motorId;
    }
    int getDegreeOfRotation() {
      return degreeOfRotation;
    }
};

void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
  softwareSerial.begin(9600);
  servoMotors[0].attach(pinForServoMotorOne);
  servoMotors[1].attach(pinForServoMotorTwo);
}

void loop() {
  JSONParser jsonParser(json);
  servoMotors[jsonParser.getMotorId()].write(jsonParser.getDegreeOfRotation());

  delay(1000);
}
