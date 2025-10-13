#ifndef STRUCTS_H
#define STRUCTS_H

#include <glm/glm.hpp>
#include <string>
#include <vector>

// Define vertex structure to hold position and UV coordinates
struct Vertex
{
    float x, y, z; // Position
    float u, v;    // Texture (UV) coordinates
    float originalY, originalZ;
    float originalU, originalV;
};

// Struct for a cube with 8 vertices each (4 front and 4 back)
struct Cube
{
    Vertex frontBottomLeft;
    Vertex frontBottomRight;
    Vertex frontTopRight;
    Vertex frontTopLeft;
    Vertex backBottomLeft;
    Vertex backBottomRight;
    Vertex backTopRight;
    Vertex backTopLeft;
};

// Button structure
struct Button
{
    float x, y;          // Position of the button
    float width, height; // Size of the button
    std::string label;   // Button label
    bool isHovered;      // Hover state
};

struct Mesh
{
    std::vector<glm::vec3> vertices;
    std::vector<glm::vec2> uvs;
    std::vector<glm::vec3> normals;
    std::vector<unsigned int> indices;
};

// Bounding boc for colision
struct BoundingBox {
    glm::vec3 minExtents;
    glm::vec3 maxExtents;
};

// Camera management for easy view adjustments
struct Camera {
    glm::vec3 position;
    glm::vec3 target;
    glm::vec3 up;
    float fov;
    float horizontalAngle;           // Horizontal rotation angle
    float verticalAngle;   // Vertical rotation angle
    float radius;          // Distance from the target

    Camera() 
        : position(glm::vec3(0.0f, 2.0f, 5.0f)), target(glm::vec3(0.0f, 0.0f, 0.0f)), 
          up(glm::vec3(0.0f, 1.0f, 0.0f)), fov(45.0f), horizontalAngle(0.0f), verticalAngle(glm::radians(15.0f)), radius(7.0f) {}

    void setView(int win_width, int win_height) {
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        gluPerspective(fov, (float)win_width / (float)win_height, 0.1f, 100.0f);
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        gluLookAt(position.x, position.y, position.z, target.x, target.y, target.z, up.x, up.y, up.z);
    }

    void updatePositionAroundTarget() {
        // Calculate position based on horizontal (angle) and vertical (verticalAngle) orbit
        position.x = target.x + radius * cos(verticalAngle) * cos(horizontalAngle);
        position.y = target.y + radius * sin(verticalAngle);
        position.z = target.z + radius * cos(verticalAngle) * sin(horizontalAngle);
    }

    void orbitHorizontally(float deltaAngle) {
        horizontalAngle += deltaAngle;
        updatePositionAroundTarget();
    }
    
    void orbitVertically(float deltaAngle) {
        verticalAngle += deltaAngle;
        verticalAngle = glm::clamp(verticalAngle, glm::radians(-85.0f), glm::radians(85.0f));  // Limit vertical angle to avoid flipping
        updatePositionAroundTarget();
    }
};

// States of a VertexQuad (enhance cutting logic)
enum QuadState { DEFAULT, DEFORMED, SPLIT };

// 4 corners of each front face that will separate on cut (useful for the cut/split effect)
struct VertexQuad {
    Vertex *bottomLeft;
    Vertex *bottomRight;
    Vertex *topLeft;
    Vertex *topRight;
    QuadState state = DEFAULT;
    bool bloodSpawned = false;
};

// Apply the desired cut effect depending on the lancet's direction
enum LancetDirection {
    FORWARD,
    BACKWARD,
    LEFT,
    RIGHT,
    NONE            // When no movement is detected
};


struct BloodInstance {
    glm::vec3 position;
    float scale;
    float rotation; // Optional, for a random orientation
};

#endif // STRUCTS_H