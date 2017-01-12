#include <ArduinoJson.h>
#include<SoftwareSerial.h>
#include <Servo.h>

SoftwareSerial BlueToothSerial = SoftwareSerial(2, 3);
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

class BluetoothChannel {
  public:
    String receiveDataFromAndroid() {
      String dataReceived;
      while (BlueToothSerial.available() == 0) { }
      char c = BlueToothSerial.read();
      while (c != '\n')
      {
        while (BlueToothSerial.available() == 0) { }
        c = BlueToothSerial.read();
        if (c != NULL)
        {
          dataReceived += c;
        }
      }
      return dataReceived;
    }

    void sendDataToAndroid(const String& data) {
      while (!BlueToothSerial.available()) { }
      softwareSerial.println(data);
      return;
    }

};

/*String readStringFromSerial() {
  String readSerialString;
  while (softwareSerial.available() == 0) { }
  char c = softwareSerial.read();
  while (c != '\n')
  {
    while (softwareSerial.available() == 0) { }
    c = softwareSerial.read();
    if (c != NULL)
    {
      readSerialString += c;
    }
  }
  return readSerialString;
}*/

void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
  BlueToothSerial.begin(9600);
  servoMotors[0].attach(pinForServoMotorOne);
  servoMotors[1].attach(pinForServoMotorTwo);
}

void loop() {
  BluetoothChannel btChannel;
  //String receivedJsonString = readStringFromSerial();
  String receivedJsonString = btChannel.receiveDataFromAndroid();
  btChannel.sendDataToAndroid(receivedJsonString);
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
    servoMotors[jsonParser.getMotorId()].write(jsonParser.getDegreeOfRotation());
  }
}
