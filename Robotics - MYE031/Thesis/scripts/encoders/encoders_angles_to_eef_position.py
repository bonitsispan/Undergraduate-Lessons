import nidaqmx
from nidaqmx.constants import EncoderType, AngleUnits

from forward_kinematics import forward_kinematics

import sympy as sym 
from sympy import sin, cos, sqrt

import math
import time


def configure_encoder(task, counter, a_input_term, b_input_term, pulses_per_rev, initial_angle):

    # Add the angular encoder channel to the task
    task.ci_channels.add_ci_ang_encoder_chan(
        counter=counter,
        decoding_type=EncoderType.X_4,                                  # Use X4 decoding for higher resolution
        zidx_enable=False,                                              # Disable Z Index
        zidx_val=0.0,                                                   # Set Z Index reset value to 0
        zidx_phase=nidaqmx.constants.EncoderZIndexPhase.AHIGH_BHIGH,    # Define Z Index phase (not used here)
        units=AngleUnits.DEGREES,                                       # Set angle measurement in degrees
        pulses_per_rev=pulses_per_rev,                                  # Set pulses per revolution for the encoder
        initial_angle=initial_angle,                                    # Set initial angle for the encoder
    )
    # Configure the physical input terminals for channels A and B
    task.ci_channels.all.ci_encoder_a_input_term = a_input_term
    task.ci_channels.all.ci_encoder_b_input_term = b_input_term

# Device and channel configuration
device_name = "Dev2"                        # Device name for the encoders
channel_name0 = f"{device_name}/ctr0"       # Counter 0 for the first encoder
channel_name1 = f"{device_name}/ctr1"       # Counter 1 for the second encoder
initial_angle = 90.0
#initial_angle1 = 119.0
#initial_angle2 = 65.0
pulses_per_rev = 9750
a0_input_term = "PFI0"
b0_input_term = "PFI1"
a1_input_term = "PFI2"
b1_input_term = "PFI3"
task0 = nidaqmx.Task()
task1 = nidaqmx.Task()

configure_encoder(task0, channel_name0, a0_input_term, b0_input_term, pulses_per_rev, initial_angle)
configure_encoder(task1, channel_name1, a1_input_term, b1_input_term, pulses_per_rev, initial_angle)

task0.start()
task1.start()

print("Reading encoder positions...")

# Open a file for writing the data
output_file = open("eef_position_xy.txt", "w")
output_file.write("X (m), Y (m)\n")  # Add header to the file

sampling_period = 1 / 200 # Define the sampling period: 200 Hz (5 ms per sample)

try:
    while True:
        
        start_time = time.time()

        # Read the angular position of encoder 1 (theta1) in rad
        theta1 = math.radians(task0.read())

        # Read the angular position of encoder 2 (theta2) in rad
        theta2 = math.radians(task1.read())

        xe, ye = forward_kinematics(theta1, theta2)

        print(f"X: {xe:.4f} m, Y: {ye:.4f} m")

        # Write the positions to the file
        output_file.write(f"{xe:.4f}, {ye:.4f}\n")

        elapsed_time = time.time() - start_time
        sleep_time = sampling_period - elapsed_time
        if sleep_time > 0:
            time.sleep(sleep_time)  # Wait for the remaining time to ensure constant frequency

except KeyboardInterrupt:
    print("Stopped reading.")   # Handle program termination with Ctrl+C

finally:
    # Stop the tasks
    task0.stop()
    task0.close()
    task1.stop()
    task1.close()
    output_file.close()


