#include <ArduinoJson.h>
#include <Servo.h>

Servo servoMotorOne;
Servo servoMotorTwo;

const int pinForConveyerBeltMotor = 13;

class ServoMotorArm {
    Servo servoMotor;
    int servoPosition;
    int delayForStep = 50;
  public:
    ServoMotorArm(Servo motor) {
      servoMotor = motor;
      servoPosition = 90;
      servoMotor.write(servoPosition);
    }

    void setPosition(int destinationPosition) {
      while (servoPosition != destinationPosition) {
        if (destinationPosition > servoPosition) {
          servoPosition++;
        }
        else {
          servoPosition--;
        }

        servoMotor.write(servoPosition);
        Serial.println(servoPosition);
        delay(delayForStep);
      }
    }
};



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
      dataReceived += c;
    }
  }
  return dataReceived;
}

ServoMotorArm servoMotorArmOne(servoMotorOne) ;
ServoMotorArm servoMotorArmTwo(servoMotorTwo) ;
ServoMotorArm servoMotorArms[] = {servoMotorArmOne, servoMotorArmTwo};

void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
  Serial1.begin(9600);
  servoMotorOne.attach(10);
  servoMotorTwo.attach(5);
}

void loop() {
  String receivedJsonString = receiveDataFromAndroid();
  Serial.println(receivedJsonString);
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
    servoMotorArms[jsonParser.getMotorId()].setPosition(jsonParser.getDegreeOfRotation());
  }
}
