import firebase_admin
from firebase_admin import credentials
from firebase_admin import firestore
from stmpy import Machine, Driver

import nxppy
import time
import pygame

class inScanner:
	brukere={}
	def isAlowedToEnter(self, uid):
		for doc in self.brukere:
			if uid == doc:
				a = self.brukere[uid]
				if (a['isValid'] == True and a['isIntheGym'] == False):
					print("user can enter")
					return True
				elif a['isValid'] == False:
					print("invalid card")
				elif a['isIntheGym'] == True:
					print ("user already in gym")
				
				return False
		print("Unkown card")
		return False
	
	def g(self, doc_snapshot):
		for doc in doc_snapshot:
			self.brukere[doc.id] = doc.to_dict()
	
	
	def playSound(self, soundName):
		pygame.mixer.init()
		pygame.mixer.music.load(soundName)
		pygame.mixer.music.play()
		while pygame.mixer.music.get_busy() == True:
			continue
	#set up scanner
	print("Ready to scan")
	
	#wait for card to be scanned 
	def start_scanner(self):
		print("ready")
		hasScanned = False
		bb = nxppy.Mifare()
		while not hasScanned:
			try:	
				
				uid = bb.select()
				if uid:
					print("id: "+str(uid))
					hasScanned = True
					if self.isAlowedToEnter(uid):
						print("valid")
						self.stm.send('card_valid')
						#send entry to firebase
						doc_ref = db.collection(u'users').document(uid)
						doc_ref.set({u'isIntheGym':True, u'scanTime':firestore.SERVER_TIMESTAMP}, merge=True)
					else:
						print("invalid")
						self.stm.send('card_invalid')
					return
			except nxppy.SelectError:
				pass
			time.sleep(1)
	def approved(self):
		print("lys grønnt")
		self.playSound("sounds/enter.wav")
	def denied(self):
		print("lys rødt")
		self.playSound("sounds/accessDenied.mp3")
	def turn_off_leds(self):
		print("klar")



in_scanner = inScanner()

#connecting to the database
cred = credentials.Certificate('glive-29f4a-7c62255de9d1.json')
firebase_admin.initialize_app(cred)
	
db = firestore.client()
# Create a callback on_snapshot function to capture changes
def on_snapshot(doc_snapshot, changes, read_time):
	for change in changes:
		if change.type.name == 'MODIFIED':
			print(change.document.id)
	in_scanner.g(doc_snapshot)
	
doc_ref = db.collection(u'users')
	
# Watch the document
doc_watch = doc_ref.on_snapshot(on_snapshot)

#state machine


#the transistions
t0 = {'source':'initial',
	'target':'idle',
	'effect':'start_scanner'}
t1 = {'trigger':'card_valid',
	'source':'idle',
	'target':'green',
	'effect':'start_timer("t1",3000)'}
t2 = {'trigger':'card_invalid',
	'source':'idle',
	'target':'red',
	'effect':'start_timer("t2",1500)'}
t3 = {'trigger':'t1',
	'source':'green',
	'target':'idle',
	'effect':'start_scanner'}
t4 = {'trigger':'t2',
	'source':'red',
	'target':'idle',
	'effect':'start_scanner'}
t5 = {'trigger':'user_entered',
	'source':'green',
	'target':'idle',
	'effect':'start_scanner'}
#the states
idle = {'name':'idle',
	'entry':'turn_off_leds'}
green = {'name':'green',
	'entry':'approved'}
red = {'name':'red',
	'entry':'denied'}

machine = Machine(name='in_scanner',transitions=[t0, t1, t2, t3, t4, t5], obj = in_scanner,
			states = [idle, green, red])
in_scanner.stm = machine

driver = Driver()
driver.add_machine(machine)
driver.start()