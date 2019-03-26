from sense_hat import SenseHat

sense = SenseHat()

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
