// This file is automatically generated by the Open Roberta Lab.

#define _ARDUINO_STL_NOT_NEEDED
#include <Arduino.h>

#include <Arduino_APDS9960.h>
#include <Adafruit_SSD1306.h>
#include <Arduino_LSM9DS1.h>
#include <Arduino_HTS221.h>
#include <Arduino_LPS22HB.h>
#include <NEPODefs.h>


int _output_Aa = 3;
int _output_Ad = 4;
int _led_L = LED_BUILTIN;
int rAsInt, gAsInt, bAsInt;
#define SCREEN_ADDRESS 0x3D
#define OLED_RESET 4
#define SCREEN_WIDTH 128
#define SCREEN_HEIGHT 64
Adafruit_SSD1306 _lcd_O(SCREEN_WIDTH, SCREEN_HEIGHT, &Wire, OLED_RESET);
int _led_red_R = 5;
int _led_green_R = 1;
int _led_blue_R = 0;
int _v_colour_temp;
float xAsFloat, yAsFloat, zAsFloat;
int _buzzer_S = 7;
int _relay_rel = 6;
void setup()
{
    Serial.begin(9600);
    pinMode(_output_Aa, OUTPUT);
    pinMode(_output_Ad, OUTPUT);
    pinMode(_led_L, OUTPUT);
    APDS.begin();
    _lcd_O.begin(SSD1306_SWITCHCAPVCC, SCREEN_ADDRESS);
    _lcd_O.clearDisplay();
    _lcd_O.setTextColor(SSD1306_WHITE);
    pinMode(_led_red_R, OUTPUT);
    pinMode(_led_green_R, OUTPUT);
    pinMode(_led_blue_R, OUTPUT);
    IMU.begin();
    pinMode(_relay_rel, OUTPUT);
    HTS.begin();
    BARO.begin();
}

void loop()
{
    tone(_buzzer_S, 300, 100);
    delay(100);
    digitalWrite(_led_L, HIGH);
    analogWrite(_led_red_R, 204);
    analogWrite(_led_green_R, 0);
    analogWrite(_led_blue_R, 0);
    
    analogWrite(_led_red_R, 0);
    analogWrite(_led_green_R, 0);
    analogWrite(_led_blue_R, 0);
    
    _lcd_O.setCursor(0,1);
    _lcd_O.print("Hallo");
    _lcd_O.display();
    _lcd_O.clearDisplay();
    _lcd_O.display();
    analogWrite(_output_Aa, (int)1);
    digitalWrite(_output_Ad, (int)1);
    digitalWrite(_relay_rel, LOW);
    Serial.println("Hallo");
}