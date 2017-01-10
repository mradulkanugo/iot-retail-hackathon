#include <ArduinoJson.h>
#include<SoftwareSerial.h>
#include <Servo.h>

SoftwareSerial softwareSerial = SoftwareSerial(2, 3);
Servo servoMotorOne, servoMotorTwo;
StaticJsonBuffer<200> jsonBuffer;

char json[] = "{\"motorId\":0,\"degree\":90}";

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
  servoMotorOne.attach(9);
  servoMotorTwo.attach(10);
}

void loop() {
  JSONParser jsonParser (json);
  Serial.println(jsonParser.getMotorId());
  Serial.println(jsonParser.getDegreeOfRotation());
  delay(1000);

}
