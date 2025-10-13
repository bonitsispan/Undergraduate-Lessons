#ifndef AUXILIARY_FUNCTIONS_H
#define AUXILIARY_FUNCTIONS_H

// LIBRARIES
#include <glm/glm.hpp>
#include <string>

#include "Structs.h"

//===================================================================================================================================================
// Print enum string value
std::string directionToString(LancetDirection dir) {
    switch (dir) {
        case FORWARD:       return "FORWARD";
        case BACKWARD:      return "BACKWARD";
        case LEFT:          return "LEFT";
        case RIGHT:         return "RIGHT";
        case NONE:          return "NONE";
        default:            return "UNKNOWN";
    }
}


//===================================================================================================================================================
// RENDERING
// Render multiple vertices in red and size 4
void renderMultipleVertices(std::vector<glm::vec3> vertices, glm::vec3 color)
{
    glPointSize(4.0f);                    // Set a larger point size for visibility
    glColor3f(color.r, color.g, color.b); // Set color from the argument
    glBegin(GL_POINTS);
    for (const auto &vertex : vertices)
    {
        glVertex3f(vertex.x, vertex.y, vertex.z);
    }
    glEnd();
}

// Render a single vertex in yellow and size 10
void renderSingleVertex(glm::vec3 vertex, glm::vec3 color)
{
    glPointSize(20.0f);                   // Set a larger point size for visibility
    glColor3f(color.r, color.g, color.b); // Set color from the argument
    glBegin(GL_POINTS);
    glVertex3f(vertex.x, vertex.y, vertex.z);
    glEnd();
}

// Render only the vertices of the layered mesh (useful in debbuging)
void renderLayeredMeshVertices(std::vector<Cube> &mesh)
{
    glPointSize(5.0f); // Set point size for better visibility

    glBegin(GL_POINTS);
    int cubeIndex = 0;
    for (const Cube &cube : mesh)
    {
        // Generate a unique color for each cube based on its index
        float r = (cubeIndex % 3 == 0) ? 1.0f : 0.5f; // Alternate red
        float g = (cubeIndex % 3 == 1) ? 1.0f : 0.5f; // Alternate green
        float b = (cubeIndex % 3 == 2) ? 1.0f : 0.5f; // Alternate blue

        glColor3f(r, g, b); // Set the color for the current cube

        // Access each corner of the cube
        glVertex3f(cube.frontBottomLeft.x, cube.frontBottomLeft.y, cube.frontBottomLeft.z);
        glVertex3f(cube.frontBottomRight.x, cube.frontBottomRight.y, cube.frontBottomRight.z);
        glVertex3f(cube.frontTopLeft.x, cube.frontTopLeft.y, cube.frontTopLeft.z);
        glVertex3f(cube.frontTopRight.x, cube.frontTopRight.y, cube.frontTopRight.z);
        glVertex3f(cube.backBottomLeft.x, cube.backBottomLeft.y, cube.backBottomLeft.z);
        glVertex3f(cube.backBottomRight.x, cube.backBottomRight.y, cube.backBottomRight.z);
        glVertex3f(cube.backTopLeft.x, cube.backTopLeft.y, cube.backTopLeft.z);
        glVertex3f(cube.backTopRight.x, cube.backTopRight.y, cube.backTopRight.z);

        ++cubeIndex; // Move to the next cube
    }
    glEnd();
}

//===================================================================================================================================================
// CALCULATIONS
// Filter and smoothen lancet position
float smoothPosition(float currentPos, float previousPos, float alpha = 0.055f)
{
    return alpha * currentPos + (1 - alpha) * previousPos;
}

#endif