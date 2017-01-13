#include <ArduinoJson.h>
#include <Servo.h>

Servo servoMotors[2];

const int pinForServoMotorOne = 9;
const int pinForServoMotorTwo = 10;
const int pinForConveyerBeltMotor = 5;

class DCMotor {
    int motorPin;
  public:
    DCMotor(int pin)
    {
      motorPin = pin;
    }

    void startMotor()
    {
      digitalWrite(motorPin, HIGH);
      return;
    }

    void stopMotor()
    {
      digitalWrite(motorPin, LOW);
      return;
    }
};

class JSONParser {
    StaticJsonBuffer<1000> jsonBuffer;
    const char* commandType;
    int motorId;
    int degreeOfRotation;
    JsonObject& root;

  public:
    JSONParser(const char json[]): root(jsonBuffer.parseObject(json)) {
      if (!root.success())
      {
        Serial.println("parseObject() failed");
      }
      commandType = root["commandType"];
      motorId = root["motorId"];
      degreeOfRotation = root["data"];
    }

    String getCommandType() {
      Serial.print("Command ");
      Serial.println(commandType);
      return commandType;
    }

    int getMotorId() {
      return motorId;
    }
    int getDegreeOfRotation() {
      return degreeOfRotation;
    }
};

String receiveDataFromAndroid() {
  String dataReceived;
  while (Serial1.available() == 0) { }
  char c = Serial1.read();
  while (c != '\n')
  {
    while (Serial1.available() == 0) { }
    c = Serial1.read();
    if (c != NULL)
    {
//      Serial1.write(c);
//      Serial1.write("\n");
//      Serial.println(c);
      dataReceived += c;
    }
  }
  return dataReceived;
}

void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
  Serial1.begin(9600);
  servoMotors[0].attach(pinForServoMotorOne);
  servoMotors[1].attach(pinForServoMotorTwo);
}

void loop() {
  String receivedJsonString = receiveDataFromAndroid();
  Serial.println(receivedJsonString);
  Serial1.println("Hello!!");
  Serial1.println(receivedJsonString);
  JSONParser jsonParser(receivedJsonString.c_str());
  DCMotor conveyerBeltMotor = DCMotor(pinForConveyerBeltMotor);
  String receivedCommand = jsonParser.getCommandType();
  if (receivedCommand == "start")
  {
    conveyerBeltMotor.startMotor();
  }
  else if (receivedCommand == "stop")
  {
    conveyerBeltMotor.stopMotor();
  }
  else {
    servoMotors[jsonParser.getMotorId()].write(jsonParser.getDegreeOfRotation());
  }
}
