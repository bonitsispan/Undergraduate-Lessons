// LIBRARIES
#include <GL/glut.h>
#include <iostream>
#include <thread>
#include <cstring>
#include <regex>

// EXTERNAL FILES
#include "Server.h"
#include "Client.h"

// STRUCTS & VARIABLES
Server mechanismServer; // Receives force data from simulation
Client mechanismClient; // Sends position (x, z) data to the simulation
int serverPort = 8080;
int clientPort = 9090;
std::string simulationServerIP = "127.0.0.1";//"172.23.55.111";

int rcvdMsgs = 0, sentMsgs = 0;
float receivedForceX = 0.0f;
float receivedForceZ = 0.0f;
float xCoord = 0.0f; // X-coordinate to send to simulation
float zCoord = 0.0f; // Z-coordinate to send to simulation
bool forceDataUpdated = false;
bool isKeyActive = false;

// ===========================================================================================================================================================
// SERVER & CLIENT SETUP
// Server listening thread
void serverThread() {
    if (mechanismServer.startServer(serverPort)) {
        std::cout << "Mechanism server started. Listening on port 8080..." << std::endl;
        while (true) {
            if (mechanismServer.acceptClient()) {
                while (true) { // Continuous loop to handle multiple messages
                    std::string receivedData = mechanismServer.receiveData();
                    if (!receivedData.empty()) {
                        // Use regex to parse the received data
                        std::regex regexPattern(R"(\{Fx:\s*(-?\d*\.?\d+),\s*Fz:\s*(-?\d*\.?\d+)\})");
                        std::smatch match;

                        if (std::regex_search(receivedData, match, regexPattern)) {
                            receivedForceX = std::stof(match[1].str()); // Extract Fx
                            receivedForceZ = std::stof(match[2].str()); // Extract Fz
                            forceDataUpdated = true; // Mark data as updated
                            rcvdMsgs++;
                        } else {
                            std::cerr << "Failed to parse data: " << receivedData << std::endl;
                        }
                    } else {
                        // Handle client disconnection
                        std::cout << "Client disconnected." << std::endl;
                        break;
                    }
                }
            }
        }
    } else {
        std::cerr << "Failed to start the server." << std::endl;
    }
}

// Client position sending thread
void clientThread() {
    bool isConnected = false;
    int retryCount = 0;
    const int maxRetries = 10; // Limit retries to avoid infinite loops
    const int retryDelay = 3000; // Wait 3 secondS between retries

    // Retry loop for connecting to the simulation server
    while (!isConnected && retryCount < maxRetries) {
        if (mechanismClient.connectToServer(simulationServerIP, clientPort)) {
            std::cout << "Connected to the simulation server. Sending position data..." << std::endl;
            isConnected = true;
        } else {
            std::cerr << "Failed to connect to the simulation server. Retrying in " << retryDelay / 1000 << " seconds..." << std::endl;
            retryCount++;
            std::this_thread::sleep_for(std::chrono::milliseconds(retryDelay));
        }
    }

    if (!isConnected) {
        std::cerr << "Unable to connect to the simulation server after " << maxRetries << " attempts. Exiting client thread." << std::endl;
        return; // Exit thread if connection fails
    }

    // Communication loop after successful connection
    while (true) {
        if (!isKeyActive) {
            // Send zero values when no arrow key is pressed
            xCoord = 0.0f;
            zCoord = 0.0f;
        }
        // Generate position data and send it to the simulation
        std::string positionData = "{X:" + std::to_string(xCoord) + ", Z:" + std::to_string(zCoord) + "}";
        mechanismClient.sendData(positionData);
        sentMsgs++;
        std::this_thread::sleep_for(std::chrono::milliseconds(5)); // Send data at ~200 Hz
    }
}

// ===========================================================================================================================================================
// CONTROLS
// Move on X & Z axis
void moveLancetSpecialKeys(int key, int x, int y)
{
    xCoord = 0.0f;
    zCoord = 0.0f;
    isKeyActive = true; // Assume a key is pressed by default

    switch (key)
    {
    case GLUT_KEY_UP:
        zCoord = -0.001f;  // Move forward
        xCoord = 0.0f;
        break;
    case GLUT_KEY_DOWN:
        zCoord = 0.001f;   // Move backward
        xCoord = 0.0f;
        break;
    case GLUT_KEY_LEFT:
        zCoord = 0.0f;      // Move left
        xCoord = -0.001f;
        break;
    case GLUT_KEY_RIGHT:
        zCoord = 0.0f;      // Move right
        xCoord = 0.001f;
        break;
    default:
        isKeyActive = false; // No valid key pressed
        break;
    }
}

// When no keyboard button is pressed
void keyRelease(int key, int x, int y)
{
    isKeyActive = false; // No key is active
}

// ===========================================================================================================================================================
// DISPLAY
// Main display function
void display() {
    glClear(GL_COLOR_BUFFER_BIT);

    // Display the received forces as text
    glColor3f(1.0f, 1.0f, 1.0f); // White text
    glRasterPos2f(-0.5f, 0.5f);

    std::string displayText = "DATA:";
    for (char c : displayText) {
        glutBitmapCharacter(GLUT_BITMAP_TIMES_ROMAN_24, c);
    }

    // Display Fx
    glRasterPos2f(-0.5f, 0.3f);
    std::string forceXText = "Rcvd Fx: " + std::to_string(receivedForceX) + " N";
    for (char c : forceXText) {
        glutBitmapCharacter(GLUT_BITMAP_HELVETICA_18, c);
    }

    // Display Fz
    glRasterPos2f(-0.5f, 0.1f);
    std::string forceZText = "Rcvd Fz: " + std::to_string(receivedForceZ) + " N";
    for (char c : forceZText) {
        glutBitmapCharacter(GLUT_BITMAP_HELVETICA_18, c);
    }

    // Display Received msgs
    glRasterPos2f(-0.5f, -0.1f);
    std::string rcvdMsgsText = "Rcvd Msg Count: " + std::to_string(rcvdMsgs);
    for (char c : rcvdMsgsText) {
        glutBitmapCharacter(GLUT_BITMAP_HELVETICA_18, c);
    }

    // Display Sent msgs
    glRasterPos2f(-0.5f, -0.3f);
    std::string sentMsgsText = "Sent Msg Count: " + std::to_string(sentMsgs);
    for (char c : sentMsgsText) {
        glutBitmapCharacter(GLUT_BITMAP_HELVETICA_18, c);
    }

    glutSwapBuffers();
}

// Update the screen every 16ms (60 FPS)
void timer(int value) {
    if (forceDataUpdated) {
        glutPostRedisplay(); // Redraw the window if data has changed
        forceDataUpdated = false; // Reset the flag
    }
    glutTimerFunc(16, timer, 0); // Schedule the next update
}

// ===========================================================================================================================================================
// MAIN
int main(int argc, char **argv) {
    
    std::thread server(serverThread);   // Start the mechanism server in a separate thread for force listening
    std::thread client(clientThread);   // Start the mechanism client in a separate thread for position sending

    // Initialize GLUT
    glutInit(&argc, argv);
    glutInitDisplayMode(GLUT_DOUBLE | GLUT_RGB);
    glutInitWindowSize(600, 400);
    glutCreateWindow("Mechanism Forces Display");

    glClearColor(0.0f, 0.0f, 0.0f, 1.0f); // Set background color to black

    // Register GLUT callbacks
    glutDisplayFunc(display);
    glutSpecialFunc(moveLancetSpecialKeys); // Register for arrow keys
    glutSpecialUpFunc(keyRelease);

    // Start the timer
    glutTimerFunc(0, timer, 0);

    // Enter the GLUT main loop
    glutMainLoop();

    // Wait for threads to finish (this won't happen until program exit)
    server.join();
    client.join();

    return 0;
}