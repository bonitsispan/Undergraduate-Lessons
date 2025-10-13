import socket
server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
server.bind(('192.168.2.4', 8080))  # Replace with your Windows IP
server.listen(1)
print("Listening on 192.168.2.4:8080...")
conn, addr = server.accept()
print(f"Connection from {addr}")
print(conn.recv(1024).decode())
conn.close()