#from sense_hat import SenseHat
from firebase import firebase
firebase = firebase.FirebaseApplication('https://glive-29f4a.firebaseio.com/', None)
result = firebase.get('/users', None)
print(result)


#sense = SenseHat()


print("hei")
while True:
    for event in sense.stick.get_events():
        if event.direction == "up" and event.action == "released":
            print("up")
        elif event.direction == "down" and event.action == "released":
            print("down")
        elif event.direction == "right" and event.action == "released":
            print("right")
        elif event.direction == "left" and event.action == "released":
            print("left")
