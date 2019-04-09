import firebase_admin
from firebase_admin import credentials
from firebase_admin import firestore

import nxppy
import time

#connecting to the database
cred = credentials.Certificate('glive-29f4a-7c62255de9d1.json')
firebase_admin.initialize_app(cred)

db = firestore.client()

def isAlowedToEnter(uid):
	for doc in brukere:
		if uid == doc:
			a = brukere[uid]
			if (a['isValid'] == True and a['isIntheGym'] == False):
				print("user can enter")
				a['isIntheGym'] = True
				playSound("sounds/enter.wav")
				return True
			elif a['isValid'] == False:
				print("invalid card")
			elif a['isIntheGym'] == True:
				print ("user already in gym")
			playSound("sounds/accessDenied.mp3")
			return False
	print("Unkown card")
	return False


brukere ={}
# Create a callback on_snapshot function to capture changes
def on_snapshot(doc_snapshot, changes, read_time):
	for doc in doc_snapshot:
		brukere[doc.id] = doc.to_dict()

doc_ref = db.collection(u'users')

# Watch the document
doc_watch = doc_ref.on_snapshot(on_snapshot)

import pygame
def playSound(soundName):
	pygame.mixer.init()
	pygame.mixer.music.load(soundName)
	pygame.mixer.music.play()
	while pygame.mixer.music.get_busy() == True:
		continue

#set up scanner 
mifare = nxppy.Mifare()
print("Ready to scan")

#wait for card to be scanned 
while True:
	try:
		uid = mifare.select()
		if uid:
			print("id: "+str(uid))
			if isAlowedToEnter(uid):
				#send entry to firebase
				doc_ref = db.collection(u'users').document(uid)
				doc_ref.set({u'isIntheGym':True}, merge=True)
	except nxppy.SelectError:
        	pass
	time.sleep(1)

