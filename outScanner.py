import firebase_admin
from firebase_admin import credentials
from firebase_admin import firestore
from LedManager import *
import nxppy
import time

#connecting to the database
cred = credentials.Certificate('glive-29f4a-7c62255de9d1.json')
firebase_admin.initialize_app(cred)

db = firestore.client()

#getting the users
users_ref = db.collection(u'users')
docs = users_ref.get()
brukere = {}
for doc in docs:
		brukere[doc.id] = doc.to_dict()

def isAlowedToLeave(uid):
	for doc in brukere:
		if uid == doc:
			a = brukere[uid]
			if (a['isValid'] == True):
				print("user can leave")
				a['isIntheGym'] = False
				
				
				return True
			elif a['isValid'] == False:
				print("invalid card")
			return False
	print("Unkown card")
	return False
#set up scanner 
mifare = nxppy.Mifare()
print("Ready to scan")

#wait for card to be scanned 
while True:
	try:
		uid = mifare.select()
		if uid:
			print(uid)
			if isAlowedToLeave(uid):
				#set user left in firestore database 
				doc_ref = db.collection(u'users').document(uid)
				doc_ref.set({u'isIntheGym':False}, merge=True)
	except nxppy.SelectError:
        	pass
	time.sleep(1)

