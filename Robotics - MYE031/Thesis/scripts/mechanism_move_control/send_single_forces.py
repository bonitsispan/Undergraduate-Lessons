import nidaqmx
from nidaqmx.constants import LineGrouping
import time

# Device and channel configuration
device_name = "Dev1"
analog_channel_1 = f"{device_name}/ao1"  # Analog Output channel AO1
analog_channel_2 = f"{device_name}/ao0"  # Analog Output channel AO0
digital_channel_name = f"{device_name}/port0/line0"  # Digital Output channel (P0.0)

# Voltages to output
output_voltage_a00 = 8.8
output_voltage_a01 = 0
digital_enable = True

# Create separate tasks for each analog output channel
analog_task_a01 = nidaqmx.Task()
analog_task_a00 = nidaqmx.Task()
digital_task = nidaqmx.Task()

try:
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

    time.sleep(9)

    # Write voltage to AO1
    print(f"Outputting {output_voltage_a01}V to {analog_channel_1}...")
    analog_task_a01.write(output_voltage_a01, auto_start=True)

    # Write voltage to AO0
    print(f"Outputting {output_voltage_a00}V to {analog_channel_2}...")
    analog_task_a00.write(output_voltage_a00, auto_start=True)

    # Set digital output
    print(f"Setting {digital_channel_name} to {'High (5V)' if digital_enable else 'Low (0V)'}...")
    digital_task.write(digital_enable, auto_start=True)

    # Wait for some time
    time.sleep(30)

    output_voltage_a00 = 0
    output_voltage_a01 = 0
    analog_task_a01.write(output_voltage_a01, auto_start=True)
    analog_task_a00.write(output_voltage_a00, auto_start=True)
    # Reset digital output
    digital_enable = False
    print(f"Setting {digital_channel_name} to {'High (5V)' if digital_enable else 'Low (0V)'}...")
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
