import socket
import threading
import time
import re

# Configure global variables
SERVER_IP = "172.23.55.111"  # WSL's eth0 IP
SERVER_PORT = 9090           # Port to connect as client
RECEIVE_PORT = 8080          # Port to open as server

# Global variables to store received data
received_force_x = 0.0
received_force_z = 0.0
message_count = 0
force_data_updated = False

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
    global received_force_x, received_force_z, message_count, force_data_updated
    conn, addr = receive_socket.accept()
    print(f"Connection established with {addr}")

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
                print(f"Parsed -> Fx: {received_force_x}, Fz: {received_force_z}")
            else:
                print(f"Failed to parse data: {data}")

        except Exception as e:
            print(f"Error receiving forces: {e}")
            break

    conn.close()
    print("Connection closed.")

# Main function to manage client and server operations
def main():
    try:
        # Open client connection to send data
        client_socket = open_client_connection(SERVER_IP, SERVER_PORT)

        # Open server connection to receive data
        receive_socket = open_server_connection(RECEIVE_PORT)

        # Start the server thread for receiving forces
        receive_thread = threading.Thread(target=receive_forces, args=(receive_socket,))
        receive_thread.daemon = True
        receive_thread.start()

        while True:
            # Example data to send
            xe, ye = 1.234, 5.678
            message = f"{{X: {xe:.3f}, Z: {ye:.3f}}}"

            try:
                client_socket.sendall(message.encode("utf-8"))
                print(f"Sent: {message}")
            except (ConnectionAbortedError, ConnectionResetError, BrokenPipeError):
                print("Connection to the server was lost. Stopping message sending.")
                break  # Exit the loop when the connection is lost

            time.sleep(1)  # Send every 1 second for this example

    except KeyboardInterrupt:
        print("Stopped sending.")

    finally:
        # Ensure sockets are closed properly
        try:
            client_socket.close()
        except Exception as e:
            print(f"Error closing client socket: {e}")

        try:
            receive_socket.close()
        except Exception as e:
            print(f"Error closing server socket: {e}")


if __name__ == "__main__":
    main()
