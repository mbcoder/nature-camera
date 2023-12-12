from gpiozero import MotionSensor
from datetime import datetime
from picamera2 import Picamera2
#import time

#start up the camera
picam2 = Picamera2()
capture_config = picam2.create_still_configuration()
picam2.start(show_preview=True)

#reading PIR on GPIO 17
pir = MotionSensor(17)
while True:
	pir.wait_for_motion()
	now = datetime.now()
	#unique filename based on datetime
	dt_string = now.strftime("images/%d-%m-%Y-%H-%M-%S.jpg")

	#capture image to file
	picam2.switch_mode_and_capture_file(capture_config, dt_string)
	pir.wait_for_no_motion()
