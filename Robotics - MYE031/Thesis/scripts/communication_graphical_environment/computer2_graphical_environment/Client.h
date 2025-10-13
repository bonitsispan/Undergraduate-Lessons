#include <iostream>
#include <thread>
#include <cstring>
#include <arpa/inet.h> // For socket operations (Linux/Mac)
#include <unistd.h>    // For close()

struct Client {
    int sock;                   // Socket file descriptor
    struct sockaddr_in server;  // Server address structure
    bool isConnected = false;   // Connection status flag

    // Initialize the client and connect to the server
    bool connectToServer(const std::string& ip, int port) {
        sock = socket(AF_INET, SOCK_STREAM, 0);
        if (sock < 0) {
            std::cerr << "Error creating socket." << std::endl;
            return false;
        }

        server.sin_family = AF_INET;
        server.sin_port = htons(port);
        if (inet_pton(AF_INET, ip.c_str(), &server.sin_addr) <= 0) {
            std::cerr << "Invalid address or address not supported." << std::endl;
            return false;
        }

        if (connect(sock, (struct sockaddr*)&server, sizeof(server)) < 0) {
            std::cerr << "Connection failed." << std::endl;
            return false;
        }

        isConnected = true;
        std::cout << "Connected to server at " << ip << ":" << port << std::endl;
        return true;
    }

    // Send data to the server
    void sendData(const std::string& data) {
        if (isConnected) {
            if (send(sock, data.c_str(), data.size(), 0) < 0) {
                std::cerr << "Error sending data." << std::endl;
            }
        }
    }

    // Close the connection
    void closeConnection() {
        if (isConnected) {
            close(sock);
            isConnected = false;
            std::cout << "Connection closed." << std::endl;
        }
    }
};
