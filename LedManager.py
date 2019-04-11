import RPi.GPIO as GPIO

class LedManager:

	
	
	def __init__(self):
		GPIO.setmode(GPIO.BCM)

		self.greenLed = 26
		self.yellowLed = 19
		self.redLed = 23
		
		GPIO.setup(self.greenLed, GPIO.OUT)
		GPIO.output(self.greenLed, False)
		
		GPIO.setup(self.yellowLed, GPIO.OUT)
		GPIO.output(self.yellowLed, False)
		
		GPIO.setup(self.redLed, GPIO.OUT)
		GPIO.output(self.redLed, False)


	def setYellowLight(self, state):
		GPIO.output(self.yellowLed, state)
		GPIO.output(self.greenLed, False)
		GPIO.output(self.redLed, False)
	
	def setRedLight(self, state):
		GPIO.output(self.redLed, state)
		GPIO.output(self.greenLed, False)
		GPIO.output(self.yellowLed, False)
	
	def setGreenLight(self, state):
		GPIO.output(self.greenLed, state)
		GPIO.output(self.redLed, False)
		GPIO.output(self.yellowLed, False)
