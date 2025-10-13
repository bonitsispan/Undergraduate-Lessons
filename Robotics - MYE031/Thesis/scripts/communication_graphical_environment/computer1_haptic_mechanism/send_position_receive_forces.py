import socket
import threading
import time
import re
import nidaqmx
from nidaqmx.constants import EncoderType, AngleUnits
from nidaqmx.constants import LineGrouping
import math
from forward_kinematics import forward_kinematics
from generate_torques import generate_torques

# Configure global variables
SERVER_IP = "10.7.3.29"
SERVER_PORT = 9090           # Port to connect as client
RECEIVE_PORT = 8080          # Port to open as server

# Device and channel configuration
device_name = "Dev2"                        # Device name (NI 62210 USB)
channel_name0 = f"{device_name}/ctr0"       # Counter 0 for the first encoder
channel_name1 = f"{device_name}/ctr1"       # Counter 1 for the second encoder
initial_angle = 90.0
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

# Global variables to store received data
received_force_x = 0.0
received_force_z = 0.0
message_count = 0
force_data_updated = False
sampling_period = 1 / 200   # Define the sampling period: 200 Hz (5 ms per sample)
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

# Function to configure the client socket
def open_client_connection(server_ip, server_port):
    while True:
        try:
            client_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            client_socket.connect((server_ip, server_port))
            print(f"Connected to server at {server_ip}:{server_port}")
            return client_socket
        except ConnectionRefusedError:
            print(f"Server at {server_ip}:{server_port} not ready. Retrying in 1 second...")
            time.sleep(1)

# Function to configure the server socket for receiving data
def open_server_connection(receive_port):
    receive_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    receive_socket.bind(("0.0.0.0", receive_port))
    receive_socket.listen(1)
    print(f"Server listening on port {receive_port}...")
    return receive_socket

# Function to handle receiving data on the server
def receive_forces(receive_socket):
    global received_force_x, received_force_z, message_count, force_data_updated, xy_time_start
    conn, addr = receive_socket.accept()
    #xy_time_start = time.time()
    print(f"Connection established with {addr}")

    output_forces_file = open("output_forces.txt", "w")
    output_forces_file.write("fx, fz\n")  # Add header to the file

    while True:
        try:
            data = conn.recv(1024).decode("utf-8")
            if not data:
                print("No data received. Closing connection.")
                break

            # Parse the received data
            regex_pattern = r"\{Fx:\s*(-?\d*\.?\d+),\s*Fz:\s*(-?\d*\.?\d+)\}"
            match = re.search(regex_pattern, data)
            if match:
                received_force_x = float(match.group(1))
                received_force_z = float(match.group(2))
                message_count += 1
                force_data_updated = True
                #print(f"Parsed -> Fx: {received_force_x}, Fz: {received_force_z}")
                output_forces_file.write(f"{float(received_force_x):.4f}, {float(received_force_z):.4f}\n")
            else:
                print(f"Failed to parse data: {data}")

        except Exception as e:
            print(f"Error receiving forces: {e}")
            break

    conn.close()
    print("Connection closed.")

# Main function to manage client and server operations
def main():

    global received_force_x, received_force_z, digital_enable, xy_time_start

    try:

        output_volt_file = open("output_volt_sockets.txt", "w")
        output_volt_file.write("V0, V1\n")  # Add header to the file

        send_xy_file = open("send_xy.txt", "w")
        send_xy_file.write("X, Y, Time\n")  # Add header to the file

        configure_encoder(task0, channel_name0, a0_input_term, b0_input_term, pulses_per_rev, initial_angle)
        configure_encoder(task1, channel_name1, a1_input_term, b1_input_term, pulses_per_rev, initial_angle)

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

        # Open client connection to send data
        client_socket = open_client_connection(SERVER_IP, SERVER_PORT)

        # Open server connection to receive data
        receive_socket = open_server_connection(RECEIVE_PORT)

        # Start the server thread for receiving forces
        receive_thread = threading.Thread(target=receive_forces, args=(receive_socket,))
        receive_thread.daemon = True
        receive_thread.start()

        xy_time_start = time.time()
        while True:
            start_time = time.time()

            # Read the angular position of encoder 1 (theta1) in rad
            theta1 = math.radians(task0.read())

            # Read the angular position of encoder 2 (theta2) in rad
            theta2 = math.radians(task1.read())

            print(f"Fx: {received_force_x:.4f} N, Fz: {received_force_z:.4f} N")

            T1, T2 = generate_torques(theta1, theta2, received_force_x, received_force_z)

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

            #freq_xy_time_start = time.time()
            xe, ye = forward_kinematics(theta1, theta2)
            xy_time = time.time() - xy_time_start
            send_xy_file.write(f"{float(xe):.4f}, {float(ye):.4f}, {float(xy_time):.4f}\n")
            message = f"{{X: {xe:.3f}, Z: {ye:.3f}}}"

            try:
                client_socket.sendall(message.encode("utf-8"))
                #print(f"Sent: {message}")
            except (ConnectionAbortedError, ConnectionResetError, BrokenPipeError):
                print("Connection to the server was lost. Stopping message sending.")
                break  # Exit the loop when the connection is lost

            #freq_xy_time_end = time.time() - freq_xy_time_start
            #print(f"XY time: {freq_xy_time_end}")

            elapsed_time = time.time() - start_time
            sleep_time = sampling_period - elapsed_time
            if sleep_time > 0:
                time.sleep(sleep_time)  # Wait for the remaining time to ensure constant frequency

    except KeyboardInterrupt:
        print("Stopped sending.")
        output_voltage_a00 = 0
        output_voltage_a01 = 0
        analog_task_a01.write(output_voltage_a01, auto_start=True)
        analog_task_a00.write(output_voltage_a00, auto_start=True)
        digital_enable = False
        digital_task.write(digital_enable, auto_start=True)

    finally:
        # Ensure sockets are closed properly
        try:
            analog_task_a01.stop()
            analog_task_a01.close()
            analog_task_a00.stop()
            analog_task_a00.close()
            digital_task.stop()
            digital_task.close()
            task0.stop()
            task0.close()
            task1.stop()
            task1.close()
            client_socket.close()
        except Exception as e:
            print(f"Error closing client socket: {e}")

        try:
            receive_socket.close()
        except Exception as e:
            print(f"Error closing server socket: {e}")


if __name__ == "__main__":
    main()
