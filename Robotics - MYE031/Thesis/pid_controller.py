import nidaqmx
from nidaqmx.constants import LineGrouping
from nidaqmx.constants import EncoderType, AngleUnits
import time
from configure_encoder import configure_encoder
import math
from inverse_kinematics import inverse_kinematics
from forward_kinematics import forward_kinematics

# Device and channel configuration
device_name_1 = "Dev1"
analog_channel_1 = f"{device_name_1}/ao1"  # Analog Output channel AO1
analog_channel_2 = f"{device_name_1}/ao0"  # Analog Output channel AO0
digital_channel_name = f"{device_name_1}/port0/line0"  # Digital Output channel (P0.0)

device_name_2 = "Dev2"                        # Device name for the encoders
channel_name0 = f"{device_name_2}/ctr0"       # Counter 0 for the first encoder
channel_name1 = f"{device_name_2}/ctr1"       # Counter 1 for the second encoder
initial_angle = 90.0
pulses_per_rev = 9750
a0_input_term = "PFI0"
b0_input_term = "PFI1"
a1_input_term = "PFI2"
b1_input_term = "PFI3"

reading_task_0 = nidaqmx.Task()
reading_task_1 = nidaqmx.Task()
analog_task_a01 = nidaqmx.Task()
analog_task_a00 = nidaqmx.Task()
digital_task = nidaqmx.Task()

configure_encoder(reading_task_0, channel_name0, a0_input_term, b0_input_term, pulses_per_rev, initial_angle)
configure_encoder(reading_task_1, channel_name1, a1_input_term, b1_input_term, pulses_per_rev, initial_angle)

sampling_period = 1 / 200   # Define the sampling period: 200 Hz (5 ms per sample)

# Voltages to output
output_voltage_a00 = 0
output_voltage_a01 = 0
digital_enable = True

x_d = 0.0400
y_d = 0.0731
theta1 = theta2 = math.radians(initial_angle)
theta1_d, theta2_d = inverse_kinematics(x_d, y_d)

error1 = theta1_d - theta1
error2 = theta2_d - theta2
#print(f"error1: {abs(error1):.5f} rad, error2: {abs(error2):.5f} rad\n")
error_x = 0.0400 - 0.0400
error_y = 0.1931 - 0.0731 

# PID controller gains
Kp = 0.06
Kd = 0.01
Ki = 0.04

# Initialize previous errors, integrals, and time for PID control
prev_error1 = error1
prev_error2 = error2
integral_error1 = 0
integral_error2 = 0
prev_time = time.time()

prev_theta1 = theta1
prev_theta2 = theta2

try:

    reading_task_0.start()
    reading_task_1.start()

    # Add an analog output voltage channel for AO1
    analog_task_a01.ao_channels.add_ao_voltage_chan(
        physical_channel=analog_channel_1,
        min_val=-9.5,
        max_val=9.5
    )

    # Add an analog output voltage channel for AO0
    analog_task_a00.ao_channels.add_ao_voltage_chan(
        physical_channel=analog_channel_2,
        min_val=-9.5,
        max_val=9.5
    )

    # Add a digital output channel
    digital_task.do_channels.add_do_chan(
        lines=digital_channel_name,
        line_grouping=LineGrouping.CHAN_PER_LINE
    )

    digital_task.write(digital_enable, auto_start=True)

    xe, ye = forward_kinematics(theta1, theta2)
    #print(f"X: {xe:.4f} m, Y: {ye:.4f} m")
    #print(f"error1: {abs(error1):.5f} m, error2: {abs(error1):.5f} m")

    output_volt_file = open("output_volt.txt", "w")
    output_volt_file.write("V0, V1\n")  # Add header to the file

    error_file = open("error.txt", "w")
    error_file.write("error1, error2, time\n")  # Add header to the file

    velocity_file = open("velocity.txt", "w")
    velocity_file.write("velocity1, velocity2, time\n") # Add header to the file

    position_error_file = open("position_error.txt", "w")
    position_error_file.write("x_error, y_error, time\n") # Add header to the file

    position_file = open("position.txt", "w")
    position_file.write("xe, ye, time\n") # Add header to the file

    position_deg_file = open("position_deg.txt", "w")
    position_deg_file.write("theta1, theta2, time\n") # Add header to the file

    torques_file = open("torques.txt", "w")
    torques_file.write("T1, T2, time\n") # Add header to the file


    error_time_start = time.time()

    #while abs(error1) > 0.005 or abs(error2) > 0.005:
    while abs(error_x) > 0.001 or abs(error_y) > 0.001:
    #while time.time() - error_time_start < 20:

        start_time = time.time()

        # Calculate time elapsed since last loop
        delta_time = start_time - prev_time
        #print(f"delta_time: {delta_time:.5f} sec")

        velocity1 = (theta1 - prev_theta1) / delta_time if delta_time > 0 else 0
        velocity2 = (theta2 - prev_theta2) / delta_time if delta_time > 0 else 0
        #print(f"velocity1: {velocity1:.5f} rad/sec, velocity2: {velocity2:.5f} rad/sec")
        #print(f"theta1 - prev_theta1: {theta1 - prev_theta1:.4f} rad, theta2 - prev_theta2: {theta2 - prev_theta2:.4f} rad")
        velocity_file.write(f"{velocity1:.5f}, {velocity2:.5f}, {time.time() - error_time_start:.5f}\n")

        # Integral term for errors
        integral_error1 += error1 * delta_time
        integral_error2 += error2 * delta_time

        # Derivative term for errors
        if delta_time > 0:
            d_error1 = (error1 - prev_error1) / delta_time
            d_error2 = (error2 - prev_error2) / delta_time
        else:
            d_error1 = 0
            d_error2 = 0

        # PID Control calculations
        T1 = Kp * error1 + Kd * d_error1 + Ki * integral_error1
        T2 = Kp * error2 + Kd * d_error2 + Ki * integral_error2

        torques_file.write(f"{T1:.5f}, {T2:.5f}, {time.time() - error_time_start:.5f}\n")

        # Convert torques to output voltages
        output_voltage_a00 = 91.35 * T1
        output_voltage_a01 = 91.35 * T2

        # Voltage saturation [-10V, 10V]
        output_voltage_a00 = max(min(output_voltage_a00, 9.5), -9.5)
        output_voltage_a01 = max(min(output_voltage_a01, 9.5), -9.5)

        # Write voltages to file
        output_volt_file.write(f"{output_voltage_a00:.4f}, {output_voltage_a01:.4f}\n")

        # Send voltages to the analog outputs
        analog_task_a00.write(output_voltage_a00, auto_start=True)
        analog_task_a01.write(output_voltage_a01, auto_start=True)

        prev_theta1 = theta1
        prev_theta2 = theta2

        # Read angular positions from encoders
        theta1 = math.radians(reading_task_0.read())
        theta2 = math.radians(reading_task_1.read())
        #print(f"Theta1: {theta1:.5f} rad, Theta2: {theta2:.5f} rad")
        theta1_deg = math.degrees(theta1)
        theta2_deg = math.degrees(theta2)
        position_deg_file.write(f"{theta1_deg:.5f}, {theta2_deg:.5f}, {time.time() - error_time_start:.5f}\n")

        prev_error1 = error1
        prev_error2 = error2

        # Update errors
        error1 = theta1_d - theta1
        error2 = theta2_d - theta2
        error1_deg = math.degrees(error1)
        error2_deg = math.degrees(error2)
        #print(f"error_theta1: {abs(error1_deg):.5f} deg, error_theta2: {abs(error2_deg):.5f} deg\n")
        error_time = time.time() - error_time_start 
        error_file.write(f"{abs(error1_deg):.5f}, {abs(error2_deg):.4f}, {error_time:.5f}\n")

        # Update position for logging
        xe, ye = forward_kinematics(theta1, theta2)
        position_file.write(f"{xe:.5f}, {ye:.5f}, {time.time() - error_time_start:.5f}\n")
        print(f"X: {xe:.4f} m, Y: {ye:.4f} m")
        print(f"Xd: {x_d:.4f} m, Yd: {y_d:.4f} m")
        error_x = x_d - xe
        error_y = y_d - ye
        #print(f"error_x: {abs(error_x):.5f} m, error_y: {abs(error_y):.5f} m\n")
        print(f"error_x: {abs(error_x)*100:.5f} cm, error_y: {abs(error_y)*100:.5f} cm\n")
        position_error_file.write(f"{abs(error_x):.5f}, {abs(error_y):.5f}, {time.time() - error_time_start:.5f}\n")

        # Update time for next iteration
        prev_time = start_time

        # Maintain constant loop frequency
        elapsed_time = time.time() - start_time
        sleep_time = sampling_period - elapsed_time
        if sleep_time > 0:
            time.sleep(sleep_time)

        print("---------------------------------")


    output_voltage_a00 = 0
    output_voltage_a01 = 0
    analog_task_a01.write(output_voltage_a01, auto_start=True)
    analog_task_a00.write(output_voltage_a00, auto_start=True)
    digital_enable = False
    digital_task.write(digital_enable, auto_start=True)

except KeyboardInterrupt:
    output_voltage_a00 = 0
    output_voltage_a01 = 0
    analog_task_a01.write(output_voltage_a01, auto_start=True)
    analog_task_a00.write(output_voltage_a00, auto_start=True)
    digital_enable = False
    digital_task.write(digital_enable, auto_start=True)
    print("Stopped.")  # Handle program termination with Ctrl+C

finally:
    # Stop and close the tasks
    analog_task_a01.stop()
    analog_task_a01.close()
    analog_task_a00.stop()
    analog_task_a00.close()
    digital_task.stop()
    digital_task.close()
    reading_task_0.stop()
    reading_task_0.close()
    reading_task_1.stop()
    reading_task_1.close()
    print("Tasks stopped and closed.")
