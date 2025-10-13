#include <iostream>
#include <thread>
#include <cstring>
#include <arpa/inet.h> // For socket operations (Linux/Mac)
#include <unistd.h>    // For close()

struct Server {
    int serverSock, clientSock;         // Socket descriptors
    struct sockaddr_in serverAddr, clientAddr; // Address structures
    bool isRunning = false;            // Server status flag

    // Initialize and start the server
    bool startServer(int port) {
        serverSock = socket(AF_INET, SOCK_STREAM, 0);
        if (serverSock < 0) {
            std::cerr << "Error creating socket." << std::endl;
            return false;
        }

        serverAddr.sin_family = AF_INET;
        serverAddr.sin_addr.s_addr = INADDR_ANY;
        serverAddr.sin_port = htons(port);

        if (bind(serverSock, (struct sockaddr*)&serverAddr, sizeof(serverAddr)) < 0) {
            std::cerr << "Bind failed." << std::endl;
            return false;
        }

        if (listen(serverSock, 3) < 0) {
            std::cerr << "Listen failed." << std::endl;
            return false;
        }

        isRunning = true;
        // std::cout << "Server started and listening on port " << port << std::endl;
        return true;
    }

    // Accept a single client connection
    bool acceptClient() {
        socklen_t clientLen = sizeof(clientAddr);
        clientSock = accept(serverSock, (struct sockaddr*)&clientAddr, &clientLen);
        if (clientSock < 0) {
            std::cerr << "Failed to accept connection." << std::endl;
            return false;
        }

        // std::cout << "Client connected." << std::endl;
        return true;
    }

    // Receive data from the client
    std::string receiveData() {
        char buffer[1024] = {0};
        int bytesRead = recv(clientSock, buffer, 1024, 0);
        if (bytesRead > 0) {
            return std::string(buffer, bytesRead);
        } else if (bytesRead == 0) {
            std::cout << "Client disconnected." << std::endl;
            isRunning = false;
        } else {
            std::cerr << "Error receiving data." << std::endl;
        }
        return "";
    }

    // Stop the server and close connections
    void stopServer() {
        if (isRunning) {
            close(clientSock);
            close(serverSock);
            isRunning = false;
            std::cout << "Server stopped." << std::endl;
        }
    }
};