//--------------(PT) Add libraries ------------//
#include <ESP8266WiFi.h>
#include <Wire.h>
#include <BH1750.h>
#include "ThingSpeak.h"

//----------Wifi information--------//
char ssid[] = "Name of your wifi";
char pass[] = "Password of your wifi";

const int DELAY_DEMO = 5000; //5000ms
bool flag_send = true;
int count_send = 0;
const int EXCEED_THRESHOLD = 5;
int count_exceed_threshold = 0;

//--------------SMS message-------------//
const char* host = "maker.ifttt.com";
const int httpsPort = 443;

//----------ThingSpeak details---------//
long unsigned int Channel_ID = 1575517;
const char *myWriteAPI_Key = "OK0IGXDXYMD4298G";
WiFiClient client;

//----------Ngưỡng giá trị cho phép----------//
const int MAX_LIGHT_INTENSITY = 1000;
const int MIN_LIGHT_INTENSITY = 10;

/*
  BH1750 can be physically configured to use two I2C addresses:
    - 0x23 (most common) (if ADD pin had < 0.7VCC voltage)
    - 0x5C (if ADD pin had > 0.7VCC voltage)

  Library uses 0x23 address as default, but you can define any other address.
  If you had troubles with default value - try to change it to 0x5C.

*/
BH1750 lightMeter(0x23);

//----------Send Email & SMS------------//
void sendEmail_SMS(){
  WiFiClientSecure notifyclient;
  notifyclient.setInsecure();
  Serial.print("Connecting to ");
  Serial.println(host);
  if (!notifyclient.connect(host, httpsPort)) {
    Serial.println("Connection failed");
    return;
  }

  String url = "/trigger/ESP/with/key/jYlplx0oQXUB3bw_XQjQJUV_4S_02vJDZlooGzzzJhH";
  Serial.print("Requesting URL: ");
  Serial.println(url);

  notifyclient.print(String("GET ") + url + " HTTP/1.1\r\n" +
               "Host: " + host + "\r\n" +
               "User-Agent: BuildFailureDetectorESP8266\r\n" +
               "Connection: close\r\n\r\n");

  Serial.println("Request sent");
  while (notifyclient.connected()) {
    String line = notifyclient.readStringUntil('\n');
    if (line == "\r") {
      Serial.println("Headers received");
      break;
    }
  }
  String line = notifyclient.readStringUntil('\n');

  Serial.println("Reply was:");
  Serial.println("==========");
  Serial.println(line);
  Serial.println("==========");
  Serial.println("Closing connection");    
}
//get data light intensity from Sensor
float getDataSensor(){
  float lux = lightMeter.readLightLevel();
  Serial.print("Light: ");
  Serial.print(lux);
  Serial.println(" lx");
  return lux;
}

//Publish data to server
void publishData(float light_intensity_value){
  
  Serial.printf("Send %f to ThingSpeak server.\n", light_intensity_value);
  int status = ThingSpeak.writeField(Channel_ID, 1, light_intensity_value, myWriteAPI_Key);
  while(status != 200){
    Serial.printf("Resend %f to ThingSpeak server.\n", light_intensity_value);
    status = ThingSpeak.writeField(Channel_ID, 1, light_intensity_value, myWriteAPI_Key);
    delay(1500);
  }
  Serial.printf("Data has been sent by device\n");
}
void setup() {
  // put your setup code here, to run once:
  Wire.begin();
  Serial.begin(115200);
  WiFi.mode(WIFI_STA);
  ThingSpeak.begin(client);
  WiFi.begin(ssid, pass);
  Serial.print("Connecting to wifi ...");
  while(WiFi.status() != WL_CONNECTED){
    Serial.print("...");
    delay(1000);
  }
  // Initialize the I2C bus (BH1750 library doesn't do this automatically)
  Wire.begin(D2, D1);
  // On esp8266 you can select SCL and SDA pins using Wire.begin(D1, D2);
  // begin returns a boolean that can be used to detect setup problems.
  if (lightMeter.begin(BH1750::CONTINUOUS_HIGH_RES_MODE)) {
    Serial.println(F("BH1750 Advanced begin"));
  }
  else {
    Serial.println(F("Error initialising BH1750"));
  }
}

void loop() {
  if(flag_send){
    flag_send = false;
    float value = getDataSensor();
    publishData(value);
    if((value > MAX_LIGHT_INTENSITY) | (value < MIN_LIGHT_INTENSITY)){
      count_exceed_threshold++;
      if(count_exceed_threshold >= EXCEED_THRESHOLD){ //Bỏ qua 4 lần gửi sau đó nếu vẫn bị vượt ngưỡng thì gửi lại SMS
        sendEmail_SMS();
        count_exceed_threshold = 0;
      }
    }
    else{
      count_exceed_threshold = 0;
    }
  }
  count_send++;
  if(count_send == DELAY_DEMO){
    flag_send = true;
    count_send = 0;
  }
  delay(1);
}
