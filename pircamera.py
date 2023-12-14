
# Copyright 2024 Esri.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

from gpiozero import MotionSensor
from datetime import datetime
from picamera2 import Picamera2
import shutil
import os

#create directories if they don't exist
if not os.path.exists("create"):
	os.makedirs("create")
if not os.path.exists("images"):
	os.makedirs("images")

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
	dt_file = now.strftime("%d-%m-%Y-%H-%M-%S.jpg")
	dt_create = "create/" + dt_file
	dt_process = "images/" + dt_file

	#capture image to file
	picam2.switch_mode_and_capture_file(capture_config, dt_create)

	#copy complete file into processing directory.  Prevents accidental reading before it is complete
	shutil.move(dt_create, dt_process)

	pir.wait_for_no_motion()
