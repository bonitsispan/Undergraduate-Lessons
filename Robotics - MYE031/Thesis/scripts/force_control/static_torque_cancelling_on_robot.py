import time
import re
import nidaqmx
from nidaqmx.constants import EncoderType, AngleUnits
from nidaqmx.constants import LineGrouping
import math
from forward_kinematics import forward_kinematics
from generate_torques import generate_torques

# Device and channel configuration
device_name = "Dev2"                        # Device name (NI 62210 USB)
channel_name0 = f"{device_name}/ctr0"       # Counter 0 for the first encoder
channel_name1 = f"{device_name}/ctr1"       # Counter 1 for the second encoder
initial_angle1 = 119.0
initial_angle2 = 65.0
pulses_per_rev = 9750
a0_input_term = "PFI0"
b0_input_term = "PFI1"
a1_input_term = "PFI2"
b1_input_term = "PFI3"
task0 = nidaqmx.Task()
task1 = nidaqmx.Task()

# Device and channel configuration
device_name_1 = "Dev1"
analog_channel_1 = f"{device_name_1}/ao1"  # Analog Output channel AO1
analog_channel_2 = f"{device_name_1}/ao0"  # Analog Output channel AO0
digital_channel_name = f"{device_name_1}/port0/line0"  # Digital Output channel (P0.0)
analog_task_a01 = nidaqmx.Task()
analog_task_a00 = nidaqmx.Task()
digital_task = nidaqmx.Task()


xy_time_start = 0.0
# Voltages to output
output_voltage_a00 = 0
output_voltage_a01 = 0
digital_enable = True


def configure_encoder(task, counter, a_input_term, b_input_term, pulses_per_rev, initial_angle):
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

class LowPassFilter:
    def __init__(self, alpha):
        self.alpha = alpha
        self.value = 0.0

    def update(self, value):
        self.value = self.alpha * value + (1 - self.alpha) * self.value
        return self.value

# Main function
def main():

    global force_x, force_z, digital_enable

    try:

        output_volt_file = open("output_volt.txt", "w")
        output_volt_file.write("V0, V1\n")  # Add header to the file

        configure_encoder(task0, channel_name0, a0_input_term, b0_input_term, pulses_per_rev, initial_angle1)
        configure_encoder(task1, channel_name1, a1_input_term, b1_input_term, pulses_per_rev, initial_angle2)

        task0.start()
        task1.start()

        # Add an analog output voltage channel for AO1
        analog_task_a01.ao_channels.add_ao_voltage_chan(
            physical_channel=analog_channel_1,
            min_val= -9.5,
            max_val= 9.5
        )

        # Add an analog output voltage channel for AO0
        analog_task_a00.ao_channels.add_ao_voltage_chan(
            physical_channel=analog_channel_2,
            min_val= -9.5,
            max_val= 9.5
        )

        # Add a digital output channel
        digital_task.do_channels.add_do_chan(
            lines=digital_channel_name,
            line_grouping=LineGrouping.CHAN_PER_LINE
        )

        digital_task.write(digital_enable, auto_start=True)

        # Initialize previous end-effector position
        xe_prev, ye_prev = None, None
        epsilon = 1e-4  # Movement threshold
        #epsilon = 0.0005  # Movement threshold

        while True:

            # Read the angular position of encoder 1 (theta1) in rad
            theta1 = math.radians(task0.read())

            # Read the angular position of encoder 2 (theta2) in rad
            theta2 = math.radians(task1.read())

            # Compute end-effector position
            xe, ye = forward_kinematics(theta1, theta2)

            # Check if the end-effector has moved
            if xe_prev is not None and ye_prev is not None:
                dx = xe - xe_prev
                dy = ye - ye_prev
                moved = abs(dx) > epsilon or abs(dy) > epsilon
            else:
                moved = True
                dx, dy = 0.0, 0.0

            force_x_lp = LowPassFilter(alpha=0.3)
            force_y_lp = LowPassFilter(alpha=0.3)

            if moved:

                if dx > epsilon:
                    force_x = 0.42 # 70 %
                elif dx < -epsilon:
                    force_x = -0.42 # 70 %
                else:
                    force_x = 0.0

                if dy > epsilon:
                    force_y = 0.42 # 70 %
                elif dy < -epsilon:
                    force_y = -0.42 # 70 %
                else:
                    force_y = 0.0

                force_x = force_x_lp.update(force_x)
                force_y = force_y_lp.update(force_y)


                T1, T2 = generate_torques(theta1, theta2, force_x, force_y)

                #output_voltage_a00 = 91.35*T1
                #output_voltage_a01 = 91.35*T2
                output_voltage_a00 = 79.16*T1
                output_voltage_a01 = 79.16*T2

                if output_voltage_a00 > 9.5:
                    output_voltage_a00 = 9.5
                elif output_voltage_a00 < -9.5:
                    output_voltage_a00 = -9.5

                if output_voltage_a01 > 9.5:
                    output_voltage_a01 = 9.5
                elif output_voltage_a01 < -9.5:
                    output_voltage_a01 = -9.5

                output_volt_file.write(f"{float(output_voltage_a00):.4f}, {float(output_voltage_a01):.4f}\n")

                analog_task_a00.write(output_voltage_a00, auto_start=True)
                analog_task_a01.write(output_voltage_a01, auto_start=True)

            # Update previous position
            xe_prev, ye_prev = xe, ye


    except KeyboardInterrupt:
        print("Stopped sending.")
        output_voltage_a00 = 0
        output_voltage_a01 = 0
        analog_task_a01.write(output_voltage_a01, auto_start=True)
        analog_task_a00.write(output_voltage_a00, auto_start=True)
        digital_enable = False
        digital_task.write(digital_enable, auto_start=True)

    except Exception as e:
        print(f"An error occurred: {e}")

    finally:
        # Stop and close the tasks
        analog_task_a01.stop()
        analog_task_a01.close()
        analog_task_a00.stop()
        analog_task_a00.close()
        digital_task.stop()
        digital_task.close()
        print("Tasks stopped and closed.")


if __name__ == "__main__":
    main()
