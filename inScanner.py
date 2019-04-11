import firebase_admin
from firebase_admin import credentials
from firebase_admin import firestore
from stmpy import Machine, Driver
import RPi.GPIO as GPIO

import nxppy
import time
import pygame

class inScanner:
	brukere={}
	currUID = 0
	def isAlowedToEnter(self, uid):
		for doc in self.brukere:
			if uid == doc:
				a = self.brukere[uid]
				if (a['isValid'] == True and a['isIntheGym'] == False):
					print("valid, user can enter")
					return True
				elif a['isValid'] == False:
					print("invalid card")
				elif a['isIntheGym'] == True:
					print ("invalid, user already in gym")
				
				return False
		print("Unkown card")
		return False
	
	def g(self, doc_snapshot):
		for doc in doc_snapshot:
			self.brukere[doc.id] = doc.to_dict()
	
	
	#def playSound(self, soundName):
	#	pygame.mixer.init()
	#	pygame.mixer.music.load(soundName)
	#	pygame.mixer.music.play()
	#	while pygame.mixer.music.get_busy() == True:
	#		continue
	
	
	#wait for card to be scanned 
	def start_scanner(self):
		print("Ready to scan")
		hasScanned = False
		bb = nxppy.Mifare()
		while not hasScanned:
			try:	
				
				uid = bb.select()
				if uid:
					print("id: "+str(uid))
					hasScanned = True
					if self.isAlowedToEnter(uid):
						self.currUID = uid
						self.stm.send('card_valid')
						
					else:
						self.stm.send('card_invalid')
					return
			except nxppy.SelectError:
				pass
			time.sleep(1)
	def approved(self):
		GPIO.output(13, True)
		self.waiting_for_entry()
		#self.playSound("sounds/enter.wav")
	def denied(self):
		GPIO.output(26, True)
		#self.playSound("sounds/accessDenied.mp3")
	def turn_off_leds(self):
		GPIO.output(13, False)
		GPIO.output(26, False)
	def waiting_for_entry(self):
		print("please enter")
		try:
			# Loop until users quits with CTRL-C
			timeout=time.time() + 5
			while True and time.time() < timeout:
		   
				if GPIO.input(21)==1:
					print("user entered")
					self.stm.send('user_entered')
					doc_ref = db.collection(u'users').document(self.currUID)
					doc_ref.set({u'isIntheGym':True, u'scanTime':firestore.SERVER_TIMESTAMP}, merge=True)
					time.sleep(0.5)
					return
		except KeyboardInterrupt:
		  # Reset GPIO settings
		  GPIO.cleanup()
		

in_scanner = inScanner()

#connecting to the database
cred = credentials.Certificate('glive-29f4a-7c62255de9d1.json')
firebase_admin.initialize_app(cred)
	
db = firestore.client()
# Create a callback on_snapshot function to capture changes
def on_snapshot(doc_snapshot, changes, read_time):
	in_scanner.g(doc_snapshot)
	
doc_ref = db.collection(u'users')
	
# Watch the document
doc_watch = doc_ref.on_snapshot(on_snapshot)

# Tell GPIO library to use GPIO references
GPIO.setmode(GPIO.BCM)

# Set Switch GPIO as input
GPIO.setup(21 , GPIO.IN)
GPIO.setup(6, GPIO.OUT)
GPIO.output(6, True)  
# List of LED GPIO numbers
LedSeq = [26,19,13]

# Set up the GPIO pins as outputs and set False

for x in range(3):
    GPIO.setup(LedSeq[x], GPIO.OUT)
    GPIO.output(LedSeq[x], False)


    
#state machine


#the transistions
t0 = {'source':'initial',
	'target':'idle',
	'effect':'turn_off_leds;start_scanner'}
t1 = {'trigger':'card_valid',
	'source':'idle',
	'target':'green',
	'effect':'start_timer("t1",5000);approved'}
t2 = {'trigger':'card_invalid',
	'source':'idle',
	'target':'red',
	'effect':'start_timer("t2",1500)'}
t3 = {'trigger':'t1',
	'source':'green',
	'target':'idle',
	'effect':'turn_off_leds;start_scanner'}
t4 = {'trigger':'t2',
	'source':'red',
	'target':'idle',
	'effect':'turn_off_leds;start_scanner'}
t5 = {'trigger':'user_entered',
	'source':'green',
	'target':'idle',
	'effect':'turn_off_leds;start_scanner'}
#the states


red = {'name':'red',
	'entry':'denied'}

machine = Machine(name='in_scanner',transitions=[t0, t1, t2, t3, t4, t5], obj = in_scanner,
			states = [ red])
in_scanner.stm = machine

driver = Driver()
driver.add_machine(machine)
driver.start()
