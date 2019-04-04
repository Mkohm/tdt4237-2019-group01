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
				return True
			elif a['isValid'] == False:
				print("invalid card")
			elif a['isIntheGym'] == True:
				print ("user already in gym")
			return False
	return False

brukere ={}
# Create a callback on_snapshot function to capture changes
def on_snapshot(doc_snapshot, changes, read_time):
	for doc in doc_snapshot:
		brukere[doc.id] = doc.to_dict()

doc_ref = db.collection(u'users')

# Watch the document
doc_watch = doc_ref.on_snapshot(on_snapshot)

#set up scanner 
mifare = nxppy.Mifare()
print("Ready to scan")

#wait for card to be scanned 
while True:
	try:
		uid = mifare.select()
		if uid:
			print(uid)
			if isAlowedToEnter(uid):
				#user can enter 
				doc_ref = db.collection(u'users').document(uid)
				doc_ref.set({u'isIntheGym':True}, merge=True)
	except nxppy.SelectError:
        	pass
	time.sleep(1)

