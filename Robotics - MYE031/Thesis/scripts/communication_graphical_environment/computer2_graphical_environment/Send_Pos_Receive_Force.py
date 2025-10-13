import nidaqmx
from nidaqmx.constants import EncoderType, AngleUnits
import socket
import threading
import time
import re
import math
from forward_kinematics import forward_kinematics

# Global variables to store received force data
received_force_x = 0.0
received_force_z = 0.0
message_count = 0
force_data_updated = False

# Configure socket for sending data
SERVER_IP = "172.23.55.111"  # WSL's eth0 IP
SERVER_PORT = 9090            # Server Port

# Wait for server to be ready
while True:
    try:
        client_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        client_socket.connect((SERVER_IP, SERVER_PORT))
        print(f"Connected to server at {SERVER_IP}:{SERVER_PORT}")
        break
    except ConnectionRefusedError:
        print(f"Server at {SERVER_IP}:{SERVER_PORT} not ready. Retrying in 1 second...")
        time.sleep(1)  # Wait for 1 second before retrying

# Configure socket for receiving data
RECEIVE_PORT = 8080  # Port for receiving fx and fy
receive_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
receive_socket.bind(("0.0.0.0", RECEIVE_PORT))
receive_socket.listen(1)

# Function to handle receiving data
def receive_forces():
    global received_force_x, received_force_z, message_count, force_data_updated
    print("Waiting for a connection on port 8080...")
    conn, addr = receive_socket.accept()
    print(f"Connection established with {addr}")

    while True:
        try:
            data = conn.recv(1024).decode("utf-8")
            if not data:
                print("No data received. Closing connection.")
                break

            # Parse the received data using regex
            regex_pattern = r"\{Fx:\s*(-?\d*\.?\d+),\s*Fz:\s*(-?\d*\.?\d+)\}"
            match = re.search(regex_pattern, data)

            if match:
                received_force_x = float(match.group(1))
                received_force_z = float(match.group(2))
                message_count += 1
                force_data_updated = True
                print(f"Parsed -> Fx: {received_force_x}, Fz: {received_force_z}")
            else:
                print(f"Failed to parse data: {data}")

        except Exception as e:
            print(f"Error receiving forces: {e}")
            break

    conn.close()
    print("Connection closed.")

# Start the receiving forces thread
receive_thread = threading.Thread(target=receive_forces)
receive_thread.daemon = True
receive_thread.start()

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

configure_encoder(task0, channel_name0, a0_input_term, b0_input_term, pulses_per_rev, initial_angle)
configure_encoder(task1, channel_name1, a1_input_term, b1_input_term, pulses_per_rev, initial_angle)

task0.start()
task1.start()

sampling_period = 1 / 200   # Define the sampling period: 25 Hz (40 ms per sample)


try:
    while True:
        start_time = time.time()

        # Read the angular position of encoder 1 (theta1) in rad
        theta1 = math.radians(task0.read())

        # Read the angular position of encoder 2 (theta2) in rad
        theta2 = math.radians(task1.read())

        # Calculate current end-effector point position
        xe, ye = forward_kinematics(theta1, theta2)

        # Create a message and send it via socket
        message = f"{{X: {xe:.3f}, Z: {ye:.3f}}}"
        client_socket.sendall(message.encode("utf-8"))

        elapsed_time = time.time() - start_time
        sleep_time = sampling_period - elapsed_time
        if sleep_time > 0:
            time.sleep(sleep_time)  # Wait for the remaining time to ensure constant frequency

except KeyboardInterrupt:
    print("Stopped sending.")

finally:
    task0.stop()
    task0.close()
    task1.stop()
    task1.close()
    client_socket.close()
    receive_socket.close()
