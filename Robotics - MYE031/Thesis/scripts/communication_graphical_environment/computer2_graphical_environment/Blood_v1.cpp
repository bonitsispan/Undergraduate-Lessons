// LIBRARIES
#include <GL/glut.h>
#include <SOIL/SOIL.h>
#include <ostream>
#include <iostream>
#include <stdexcept>
#include <fstream>
#include <sstream>

// EXTERNAL FILES
#include "Structs.h"

// CONSTANTS
const int WINDOW_WIDTH = 1000;
const int WINDOW_HEIGHT = 800;

Mesh bloodMesh;
Camera mainCamera;
GLuint bloodTexture;

// ================================================================================================================================================================================
// MESH
// Load mesh from obj file (useful for lancet)
void loadFromOBJ(const std::string &filePath, Mesh &mesh)
{
    std::ifstream file(filePath);
    if (!file.is_open())
    {
        throw std::runtime_error("Failed to open OBJ file: " + filePath);
    }

    std::string line;
    while (std::getline(file, line))
    {
        // Ignore comments and trim whitespace
        if (line.empty() || line[0] == '#')
        {
            continue;
        }

        std::istringstream ss(line);
        std::string prefix;
        ss >> prefix;

        if (prefix == "v")
        { // Vertex position
            glm::vec3 vertex;
            ss >> vertex.x >> vertex.y >> vertex.z;
            if (ss.fail())
            {
                std::cerr << "Warning: Invalid vertex data in line: " << line << std::endl;
                continue;
            }
            mesh.vertices.push_back(vertex);
        }
        else if (prefix == "vt")
        { // Texture coordinate
            glm::vec2 uv;
            ss >> uv.x >> uv.y;
            if (ss.fail())
            {
                std::cerr << "Warning: Invalid UV data in line: " << line << std::endl;
                continue;
            }
            mesh.uvs.push_back(uv);
        }
        else if (prefix == "vn")
        { // Vertex normal
            glm::vec3 normal;
            ss >> normal.x >> normal.y >> normal.z;
            if (ss.fail())
            {
                std::cerr << "Warning: Invalid normal data in line: " << line << std::endl;
                continue;
            }
            mesh.normals.push_back(normal);
        }
        else if (prefix == "f")
        { // Face data
            unsigned int vertexIndex[3], uvIndex[3], normalIndex[3];
            for (int i = 0; i < 3; ++i)
            {
                char slash1, slash2;
                ss >> vertexIndex[i] >> slash1 >> uvIndex[i] >> slash2 >> normalIndex[i];

                // Check if parsing failed
                if (ss.fail() || slash1 != '/' || slash2 != '/')
                {
                    std::cerr << "Warning: Invalid face data in line: " << line << std::endl;
                    continue;
                }

                mesh.indices.push_back(vertexIndex[i] - 1);
            }
        }
    }

    file.close();
    std::cout << "OBJ file loaded successfully: " << filePath << std::endl;
}

// Calculate the center of the mesh
glm::vec3 calculateCenter(const Mesh &mesh)
{
    if (mesh.vertices.empty())
        return glm::vec3(0.0f);

    glm::vec3 min = mesh.vertices[0];
    glm::vec3 max = mesh.vertices[0];
    for (const auto &vertex : mesh.vertices)
    {
        min = glm::min(min, vertex);
        max = glm::max(max, vertex);
    }
    return (min + max) / 2.0f;
}


// ================================================================================================================================================================================
// TEXTURE RENDERING
void setImageParameters(int textureID)
{
    glBindTexture(GL_TEXTURE_2D, textureID);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
}

// Load the texture with debug messages
void loadTexture(const char *filename, GLuint &textureID)
{
    textureID = SOIL_load_OGL_texture(filename, SOIL_LOAD_AUTO, SOIL_CREATE_NEW_ID, SOIL_FLAG_INVERT_Y);
    if (textureID == 0)
    {
        std::cerr << "Error loading texture: " << SOIL_last_result() << std::endl;
    }
    else
    {
        std::cout << "Texture loaded successfully: " << filename << ", Texture ID: " << textureID << std::endl;
    }
    setImageParameters(textureID);
}

// Render a textured triangle using the given vertices (useful for the rest meshes)
void renderTriangle(const Vertex &v1, const Vertex &v2, const Vertex &v3)
{
    glBegin(GL_TRIANGLES);
    glTexCoord2f(v1.u, v1.v);
    glVertex3f(v1.x, v1.y, v1.z);
    glTexCoord2f(v2.u, v2.v);
    glVertex3f(v2.x, v2.y, v2.z);
    glTexCoord2f(v3.u, v3.v);
    glVertex3f(v3.x, v3.y, v3.z);
    glEnd();
}

// Render mesh with texture (useful for lancet)
void renderTexturedObjMesh(Mesh &mesh, GLuint &texture)
{
    if (texture == 0)
    {
        std::cerr << "Warning: Texture ID is 0. Skipping texture rendering." << std::endl;
        return;
    }

    glEnable(GL_TEXTURE_2D);
    glBindTexture(GL_TEXTURE_2D, texture);

    for (size_t i = 0; i < mesh.indices.size(); i += 3)
    {
        // Retrieve indices for the triangle vertices
        unsigned int idx1 = mesh.indices[i];
        unsigned int idx2 = mesh.indices[i + 1];
        unsigned int idx3 = mesh.indices[i + 2];

        // Ensure indices are within bounds for vertices and UVs
        if (idx1 < mesh.vertices.size() && idx2 < mesh.vertices.size() && idx3 < mesh.vertices.size())
        {
            Vertex v1 = {mesh.vertices[idx1].x, mesh.vertices[idx1].y, mesh.vertices[idx1].z,
                         idx1 < mesh.uvs.size() ? mesh.uvs[idx1].x : 0.0f,
                         idx1 < mesh.uvs.size() ? mesh.uvs[idx1].y : 0.0f};

            Vertex v2 = {mesh.vertices[idx2].x, mesh.vertices[idx2].y, mesh.vertices[idx2].z,
                         idx2 < mesh.uvs.size() ? mesh.uvs[idx2].x : 0.0f,
                         idx2 < mesh.uvs.size() ? mesh.uvs[idx2].y : 0.0f};

            Vertex v3 = {mesh.vertices[idx3].x, mesh.vertices[idx3].y, mesh.vertices[idx3].z,
                         idx3 < mesh.uvs.size() ? mesh.uvs[idx3].x : 0.0f,
                         idx3 < mesh.uvs.size() ? mesh.uvs[idx3].y : 0.0f};

            // Render the triangle using the helper function
            renderTriangle(v1, v2, v3);
        }
        else
        {
            std::cerr << "Warning: Vertex index out of bounds in mesh." << std::endl;
        }
    }

    glDisable(GL_TEXTURE_2D);
}

// Function to set initial camera position and retain target and up vector
void setCameraViewAtMeshCenter(Camera &camera, const Mesh &mesh, int win_width, int win_height)
{
    glm::vec3 center = calculateCenter(mesh); // Center of the mesh
    camera.target = center;
    camera.radius = glm::length(camera.position - center); // Calculate distance for orbiting

    // Calculate initial angle based on current position relative to target
    float dx = camera.position.x - center.x;
    float dz = camera.position.z - center.z;
    camera.horizontalAngle = atan2(dz, dx); // Set angle based on initial position

    // Set initial camera position and view
    camera.updatePositionAroundTarget();
    camera.setView(win_width, win_height);
}




// Display all the content
void display()
{
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

    // Enable depth test for 3D rendering
    glEnable(GL_DEPTH_TEST);

    // Rendering code
    renderTexturedObjMesh(bloodMesh, bloodTexture);

    glutSwapBuffers();
}



// ================================================================================================================================================================================
// MAIN
// Main function
int main(int argc, char **argv)
{
    glutInit(&argc, argv);
    glutInitDisplayMode(GLUT_DOUBLE | GLUT_RGB | GLUT_DEPTH);
    glutInitWindowSize(WINDOW_WIDTH, WINDOW_HEIGHT);
    glutCreateWindow("3D Blood Simulation");

    glEnable(GL_DEPTH_TEST);
    glEnable(GL_TEXTURE_2D);

    glutDisplayFunc(display);               // Display

    // Set camera view at epidermis mesh center
    setCameraViewAtMeshCenter(mainCamera, bloodMesh, WINDOW_WIDTH, WINDOW_HEIGHT);

    // Load mesh
    loadFromOBJ("Objects/Blood_2.obj", bloodMesh);
    // Load Texture
    loadTexture("Textures/Tex_Blood_3.jpg", bloodTexture);

    // Enter main loop
    glutMainLoop();

    return 0;
}