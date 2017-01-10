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
    JSONParser(const char json[]): root(jsonBuffer.parseObject(json)) {
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

String readStringFromSerial() {
  String readSerialString;
  while (softwareSerial.available() == 0) { }
  while (softwareSerial.available())
  {
    char c = softwareSerial.read();
    readSerialString += c;
  }
  return readSerialString;
}

void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
  softwareSerial.begin(9600);
  servoMotors[0].attach(pinForServoMotorOne);
  servoMotors[1].attach(pinForServoMotorTwo);
}

void loop() {
  String receivedJsonString = readStringFromSerial();
  JSONParser jsonParser(receivedJsonString.c_str());
  servoMotors[jsonParser.getMotorId()].write(jsonParser.getDegreeOfRotation());
  delay(2000);
}
