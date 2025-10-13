// LIBRARIES
#include <GL/glut.h>
#include <SOIL/SOIL.h>
#include <glm/glm.hpp>
#include <glm/gtc/matrix_transform.hpp>
#include <vector>
#include <algorithm>
#include <iostream>
#include <ostream>
#include <fstream>
#include <sstream>
#include <stdexcept>
#include <optional>
#include <iomanip>
#include <regex>

#include <chrono>        // For frequency testing in updateSimulationScene()
#include <omp.h>         // For parallel computations
#include <atomic>        // Same
#include <unordered_map> // For grid-based spatial partitioning

// EXTERNAL FILES
#include "Structs.h"
#include "Auxiliary_Functions.h"
#include "Client.h"
#include "Server.h"

// ================================================================================================================================================================================
// CONSTANTS
const int WINDOW_WIDTH = 1000;
const int WINDOW_HEIGHT = 800;
const int POINT_MASSES = 20;
const int TIME_STEP = 5;               // 5 ms time step for force calculations and vertex updates -> 200Hz for smooth and precise mechanism movement
const float SPLIT_THRESHOLD = -0.1f; // Z-Axis threshold for deformation until cutting occurs

// ================================================================================================================================================================================
// STRUCTS & VARIABLES
// Structs
std::vector<Cube> epidermisMesh, dermisMesh, dermisMesh2, hypodermisMesh;                                                                  // Skin layer mesh data
std::vector<VertexQuad> quadMesh;                                                                                                          // Quad vertices for the split effect
Mesh lancetMesh, bloodMesh;                                                                                                                // Lancet mesh data
Camera mainCamera;                                                                                                                         // Main camera component
LancetDirection direction = NONE;                                                                                                          // Lancet direction
std::ofstream logFile("force_log.txt");                                                                                                    // Global log file and start time
Client simulationClient;                                                                                                                   // Client sending force data to the mechanism's server
Server simulationServer;                                                                                                                   // Server receives position(x,z) data from the mechanism's client
std::vector<std::string> toolbarValues = {"Value 0: 0.0", "Value 1: 1.0", "Value 2: 2.5", "Value 3: 3.0", "Value 4: 4.5", "Value 5: 5.5"}; // Example initial values
glm::vec3 *cachedLancetTip = nullptr;                                                                                                      // Cached lancet tip vertex pointer
std::vector<glm::vec3 *> cachedBladeVertices;                                                                                              // Cached lancet blade vertex pointers
std::unordered_map<int, std::vector<VertexQuad>> quadGridMap;                                                                              // Quad grid
std::vector<VertexQuad> filteredQuadMesh;                                                                                                  // Filtered quads to feed the function which finds the closest to the blade
std::vector<BloodInstance> bloodInstances;                                                                                                 // Blood particles

// Buttons
std::vector<Button> buttons =
    {
        {10, 500, 110, 70, "LANCET", false},
        {10, 420, 110, 70, "CAMERA", false},
        {10, 340, 110, 70, "HELP", false},
        {10, 260, 110, 70, "CUT BOX", false}};

// Variables
GLuint epidermisMelanomaTexture, epidermisSideTexture, lancetTexture, dermisTexture, dermis2Texture, hypodermisTexture, bloodTexture; // Textures
float lancetPosX = -0.04f, lancetPosY = 0.0f, lancetPosZ = 0.19f;                                         // Initial lancet position
float previousLancetPosX = 0.0f, previousLancetPosZ = 0.0f;                                                                           // Variables to track lancet's previous position
float movementSpeed = 0.025f;                                                                                                         // Movement speed of lancet
float rotationSpeed = 0.1f;                                                                                                           // Rotation speed of the main camera around the scene
float Fx = 0.0f, Fz = 0.0f;                                                                                                           // Forces felt by the user
float simulation_time = 0.0f;                                                                                                         // Time passed since the application launch
bool isLancetActive = false;
bool showHelpBox = false;
bool isCameraLocked = true;
bool showCutBox = false;
bool serverConnection;
int messagesSent = 0, messagesReceived = 0;

int serverPort = 9090;
int clientPort = 8080;
std::string mechanismServerIP = "172.23.55.111";// /*"192.168.168.185";*/ "192.168.170.123";

auto lastFrameTime = std::chrono::high_resolution_clock::now(); // Time of the last frame
float fps = 0.0f;                                               // Calculated FPS

// ================================================================================================================================================================================
// MESH INITIALIZATION & SETTINGS
// Initialize vertex
void initializeVertex(Vertex &vertex, float x, float y, float z, float u, float v)
{
    vertex.x = x;
    vertex.y = y;
    vertex.z = z;
    vertex.u = u;
    vertex.v = v;
    vertex.originalZ = z;
    vertex.originalU = u;
    vertex.originalV = v;
}

// Initialize epidermis/dermis/hypodermis cube mesh on the x,y,z plane
void initializeLayeredMesh(std::vector<Cube> &skinMesh, int size = POINT_MASSES, float totalSize = 3.0f, float zSize = 0.1f, float zOffset = 0.0f)
{
    float spacing = totalSize / size; // Adjust spacing based on grid size to fit within totalSize

    // Define a gap between cubes
    float gap = 0.0f; // Adjust as needed to control the distance between cubes

    // Generate cubes for the skin mesh
    for (int x = 0; x < size; ++x)
    {
        for (int y = 0; y < size; ++y)
        {
            Cube cube;
            float offsetX = x * (spacing + gap) - (totalSize / 2.0f) + (spacing / 2.0f);
            float offsetY = y * (spacing + gap) - (totalSize / 2.0f) + (spacing / 2.0f);

            // Define front and back face vertices
            initializeVertex(cube.frontBottomLeft, (offsetX - spacing / 2.0f), (offsetY - spacing / 2.0f), (zOffset + zSize / 2.0f), 0.0f, 0.0f);
            initializeVertex(cube.frontBottomRight, (offsetX + spacing / 2.0f), (offsetY - spacing / 2.0f), (zOffset + zSize / 2.0f), 1.0f, 0.0f);
            initializeVertex(cube.frontTopRight, (offsetX + spacing / 2.0f), (offsetY + spacing / 2.0f), (zOffset + zSize / 2.0f), 1.0f, 1.0f);
            initializeVertex(cube.frontTopLeft, (offsetX - spacing / 2.0f), (offsetY + spacing / 2.0f), (zOffset + zSize / 2.0f), 0.0f, 1.0f);

            initializeVertex(cube.backBottomLeft, (offsetX - spacing / 2.0f), (offsetY - spacing / 2.0f), (zOffset - zSize / 2.0f), 0.0f, 0.0f);
            initializeVertex(cube.backBottomRight, (offsetX + spacing / 2.0f), (offsetY - spacing / 2.0f), (zOffset - zSize / 2.0f), 1.0f, 0.0f);
            initializeVertex(cube.backTopRight, (offsetX + spacing / 2.0f), (offsetY + spacing / 2.0f), (zOffset - zSize / 2.0f), 1.0f, 1.0f);
            initializeVertex(cube.backTopLeft, (offsetX - spacing / 2.0f), (offsetY + spacing / 2.0f), (zOffset - zSize / 2.0f), 0.0f, 1.0f);

            skinMesh.push_back(cube);
        }
    }
}

// Initialize epidermis inner quad vertices for mesh splitting effect
void initializeVertexQuads(std::vector<Cube> &skinMesh)
{
    int size = POINT_MASSES;
    // Build vertex quads using adjacent cubes' front vertices
    for (int x = 0; x < size - 1; ++x)
    {
        for (int y = 0; y < size - 1; ++y)
        {
            int currentIndex = x * size + y;

            VertexQuad frontQuad, backQuad;

            // Assign vertex pointers for front faces
            frontQuad.bottomRight = &skinMesh[currentIndex].frontTopRight;
            frontQuad.bottomLeft = &skinMesh[currentIndex + size].frontTopLeft;
            frontQuad.topRight = &skinMesh[currentIndex + size + 1].frontBottomLeft;
            frontQuad.topLeft = &skinMesh[currentIndex + 1].frontBottomRight;

            // Assign vertex pointers for front faces
            backQuad.bottomRight = &skinMesh[currentIndex].backTopRight;
            backQuad.bottomLeft = &skinMesh[currentIndex + size].backTopLeft;
            backQuad.topRight = &skinMesh[currentIndex + size + 1].backBottomLeft;
            backQuad.topLeft = &skinMesh[currentIndex + 1].backBottomRight;

            quadMesh.push_back(frontQuad);  // Add quads to the collection
            quadMesh.push_back(backQuad);
        }
    }
}

// Helper: Compute a spatial grid cell index
int computeCellIndex(const glm::vec3 &position, float gridSize)
{
    int xIndex = static_cast<int>(std::floor(position.x / gridSize));
    int yIndex = static_cast<int>(std::floor(position.y / gridSize));
    return xIndex + yIndex * 100; // Combine x, y into a unique key
}

// Partition quads into grid
void partitionQuadsIntoGrid(const std::vector<VertexQuad> &quads, std::unordered_map<int, std::vector<VertexQuad>> &gridMap, float gridSize)
{
    for (const auto &quad : quads)
    {
        glm::vec3 center(
            (quad.bottomLeft->x + quad.bottomRight->x + quad.topLeft->x + quad.topRight->x) / 4.0f,
            (quad.bottomLeft->y + quad.bottomRight->y + quad.topLeft->y + quad.topRight->y) / 4.0f,
            0.0f // Assuming quads are flat on z=0
        );
        int cellIndex = computeCellIndex(center, gridSize);
        gridMap[cellIndex].push_back(quad);
    }
}

// Filter by proximity in grid
void filterQuadsByGrid(const glm::vec3 &lancetTip, std::vector<VertexQuad> &filteredQuads, const std::unordered_map<int, std::vector<VertexQuad>> &gridMap, float gridSize)
{
    filteredQuads.clear();
    int cellIndex = computeCellIndex(lancetTip, gridSize);

    // Check neighboring cells
    for (int dx = -1; dx <= 1; ++dx)
    {
        for (int dy = -1; dy <= 1; ++dy)
        {
            int neighborIndex = cellIndex + dx + dy * 100;
            if (gridMap.find(neighborIndex) != gridMap.end())
            {
                filteredQuads.insert(filteredQuads.end(), gridMap.at(neighborIndex).begin(), gridMap.at(neighborIndex).end());
            }
        }
    }
}

// Initialize quad grid
void initializeQuadGrid()
{
    float gridSize = 0.5f; // Adjust grid size based on your mesh density
    partitionQuadsIntoGrid(quadMesh, quadGridMap, gridSize);
    std::cout << "Partitioned quads into grid with cell size " << gridSize << ".\n";
}

// Calculate epidermis center
glm::vec3 calculateEpidermisCenter(const std::vector<Cube> &skinMesh)
{
    if (skinMesh.empty())
    {
        return glm::vec3(0.0f, 0.0f, 0.0f); // Return origin if the mesh is empty
    }

    // Initialize min and max extents
    float minX = std::numeric_limits<float>::max();
    float maxX = std::numeric_limits<float>::lowest();
    float minY = std::numeric_limits<float>::max();
    float maxY = std::numeric_limits<float>::lowest();

    for (const auto &cube : skinMesh)
    {
        // Check each vertex in the cube to find the min and max extents
        std::vector<glm::vec3> vertices = {
            {cube.frontBottomLeft.x, cube.frontBottomLeft.y, cube.frontBottomLeft.z},
            {cube.frontBottomRight.x, cube.frontBottomRight.y, cube.frontBottomRight.z},
            {cube.frontTopLeft.x, cube.frontTopLeft.y, cube.frontTopLeft.z},
            {cube.frontTopRight.x, cube.frontTopRight.y, cube.frontTopRight.z},
            {cube.backBottomLeft.x, cube.backBottomLeft.y, cube.backBottomLeft.z},
            {cube.backBottomRight.x, cube.backBottomRight.y, cube.backBottomRight.z},
            {cube.backTopLeft.x, cube.backTopLeft.y, cube.backTopLeft.z},
            {cube.backTopRight.x, cube.backTopRight.y, cube.backTopRight.z}};

        for (const auto &vertex : vertices)
        {
            minX = std::min(minX, vertex.x);
            maxX = std::max(maxX, vertex.x);
            minY = std::min(minY, vertex.y);
            maxY = std::max(maxY, vertex.y);
        }
    }

    // Calculate the center as the midpoint of the min and max extents
    float centerX = (minX + maxX) / 2.0f;
    float centerY = (minY + maxY) / 2.0f;

    // Since this is on the x-y plane, we set z to 0
    return glm::vec3(centerX, centerY, 0.0f);
}

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

// Initialize lancet mesh at a given position and angle
void initializeLancetMesh(Mesh &lancetMesh, const glm::vec3 &initialPosition, float initialAngleX, float initialAngleY, float initialAngleZ)
{
    // Calculate the center of the lancet mesh for rotation purposes
    glm::vec3 center = calculateCenter(lancetMesh);

    // Create a transformation matrix
    glm::mat4 transformation = glm::mat4(1.0f);

    // Step 1: Translate to the origin (center) for rotation
    transformation = glm::translate(transformation, -center);

    // Step 2: Apply rotation around X, Y and Z axes
    transformation = glm::rotate(transformation, glm::radians(initialAngleX), glm::vec3(0.0f, 1.0f, 0.0f)); // Rotate around Y-axis
    transformation = glm::rotate(transformation, glm::radians(initialAngleY), glm::vec3(0.0f, 1.0f, 0.0f)); // Rotate around Y-axis
    transformation = glm::rotate(transformation, glm::radians(initialAngleZ), glm::vec3(0.0f, 0.0f, 1.0f)); // Rotate around Z-axis

    // Step 3: Translate back to the original center position
    transformation = glm::translate(transformation, center);

    // Step 4: Translate to the specified initial position
    transformation = glm::translate(transformation, initialPosition - center);

    // Apply the transformation to all vertices of the lancet mesh
    for (auto &vertex : lancetMesh.vertices)
    {
        glm::vec4 transformedVertex = transformation * glm::vec4(vertex, 1.0f);
        vertex = glm::vec3(transformedVertex);
    }
}

// ================================================================================================================================================================================
// VERTEX RENDERING
// Render only the vertices of the layered mesh (useful in debbuging)
/*void renderLayeredMeshVertices(std::vector<Cube> &mesh)
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
}*/


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

// Render uv coords
void renderUVCoordinates(const Vertex &v)
{
    glTexCoord2f(v.u, v.v);
    glVertex3f(v.x, v.y, v.z);
}

// Render a textured quad using the given vertices (useful for the epidermis mesh)
void renderQuad(const Vertex &v1, const Vertex &v2, const Vertex &v3, const Vertex &v4)
{
    glBegin(GL_QUADS);
    renderUVCoordinates(v1);
    renderUVCoordinates(v2);
    renderUVCoordinates(v3);
    renderUVCoordinates(v4);
    glEnd();
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

// Render front face
void renderTexturedFrontFace(std::vector<Cube> skinMesh, GLuint &texture)
{
    glEnable(GL_TEXTURE_2D);

    // Bind the primary texture
    glBindTexture(GL_TEXTURE_2D, texture);

    int gridSize = std::sqrt(skinMesh.size());
    float textureStep = 1.0f / gridSize;

    for (int x = 0; x < gridSize; ++x)
    {
        for (int y = 0; y < gridSize; ++y)
        {
            int index = x * gridSize + y;
            Cube &cube = skinMesh[index];

            float uStart = x * textureStep;
            float uEnd = uStart + textureStep;
            float vStart = y * textureStep;
            float vEnd = vStart + textureStep;

            // Set texture coordinates for each corner of the top face
            cube.frontTopLeft.u = uStart;
            cube.frontTopLeft.v = vEnd;
            cube.frontTopRight.u = uEnd;
            cube.frontTopRight.v = vEnd;
            cube.frontBottomRight.u = uEnd;
            cube.frontBottomRight.v = vStart;
            cube.frontBottomLeft.u = uStart;
            cube.frontBottomLeft.v = vStart;

            // Render the top face using renderQuad
            renderQuad(cube.frontTopLeft, cube.frontTopRight, cube.frontBottomRight, cube.frontBottomLeft);
        }
    }

    glDisable(GL_TEXTURE_2D);
}

// Render back face
void renderTexturedBackFace(std::vector<Cube> skinMesh, GLuint &texture)
{
    glEnable(GL_TEXTURE_2D);

    // Bind the primary texture
    glBindTexture(GL_TEXTURE_2D, texture);

    int gridSize = std::sqrt(skinMesh.size());
    float textureStep = 1.0f / gridSize;

    for (int x = 0; x < gridSize; ++x)
    {
        for (int y = 0; y < gridSize; ++y)
        {
            int index = x * gridSize + y;
            Cube &cube = skinMesh[index];

            float uStart = x * textureStep;
            float uEnd = uStart + textureStep;
            float vStart = y * textureStep;
            float vEnd = vStart + textureStep;

            // Set texture coordinates for each corner of the top face
            cube.backTopLeft.u = uStart;
            cube.backTopLeft.v = vEnd;
            cube.backTopRight.u = uEnd;
            cube.backTopRight.v = vEnd;
            cube.backBottomRight.u = uEnd;
            cube.backBottomRight.v = vStart;
            cube.backBottomLeft.u = uStart;
            cube.backBottomLeft.v = vStart;

            // Render the top face using renderQuad
            renderQuad(cube.backTopLeft, cube.backTopRight, cube.backBottomRight, cube.backBottomLeft);
        }
    }

    glDisable(GL_TEXTURE_2D);
}

// Render the side faces (outside and inside)
void renderTexturedSideFaces(std::vector<Cube> &skinMesh, GLuint &texture)
{
    glEnable(GL_TEXTURE_2D);

    // Bind the primary texture
    glBindTexture(GL_TEXTURE_2D, texture);

    int gridSize = std::sqrt(skinMesh.size());
    float textureStep = 1.0f / gridSize;

    for (int x = 0; x < gridSize; ++x)
    {
        for (int y = 0; y < gridSize; ++y)
        {
            int index = x * gridSize + y;
            Cube &cube = skinMesh[index];

            float uStart = x * textureStep;
            float uEnd = uStart + textureStep;
            float vStart = y * textureStep;
            float vEnd = vStart + textureStep;

            // Left face
            cube.frontBottomLeft.u = uStart;
            cube.frontBottomLeft.v = vStart;
            cube.backBottomLeft.u = uEnd;
            cube.backBottomLeft.v = vStart;
            cube.backTopLeft.u = uEnd;
            cube.backTopLeft.v = vEnd;
            cube.frontTopLeft.u = uStart;
            cube.frontTopLeft.v = vEnd;
            renderQuad(cube.frontBottomLeft, cube.backBottomLeft, cube.backTopLeft, cube.frontTopLeft);

            // Right face
            cube.frontBottomRight.u = uStart;
            cube.frontBottomRight.v = vStart;
            cube.backBottomRight.u = uEnd;
            cube.backBottomRight.v = vStart;
            cube.backTopRight.u = uEnd;
            cube.backTopRight.v = vEnd;
            cube.frontTopRight.u = uStart;
            cube.frontTopRight.v = vEnd;
            renderQuad(cube.frontBottomRight, cube.backBottomRight, cube.backTopRight, cube.frontTopRight);

            // Top face
            cube.frontTopLeft.u = uStart;
            cube.frontTopLeft.v = vEnd;
            cube.frontTopRight.u = uEnd;
            cube.frontTopRight.v = vEnd;
            cube.backTopRight.u = uEnd;
            cube.backTopRight.v = vStart;
            cube.backTopLeft.u = uStart;
            cube.backTopLeft.v = vStart;
            renderQuad(cube.frontTopLeft, cube.frontTopRight, cube.backTopRight, cube.backTopLeft);

            // Bottom face
            cube.frontBottomLeft.u = uStart;
            cube.frontBottomLeft.v = vStart;
            cube.frontBottomRight.u = uEnd;
            cube.frontBottomRight.v = vStart;
            cube.backBottomRight.u = uEnd;
            cube.backBottomRight.v = vEnd;
            cube.backBottomLeft.u = uStart;
            cube.backBottomLeft.v = vEnd;
            renderQuad(cube.frontBottomLeft, cube.frontBottomRight, cube.backBottomRight, cube.backBottomLeft);
        }
    }

    glDisable(GL_TEXTURE_2D);
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

// Crate new blood Particle
void createNewBloodInstance(VertexQuad &quad)
{
    if (quad.bloodSpawned) {
        return; // Skip if blood has already been spawned for this quad
    }

    // std::cout << "\nQuad state changed to SPLIT, creating blood instance.\n";

    glm::vec3 bloodCenter1 = glm::vec3(quad.topLeft->x, quad.topLeft->y - 0.07f, quad.topLeft->z);
    glm::vec3 bloodCenter2 = glm::vec3(quad.bottomLeft->x, quad.bottomLeft->y - 0.13f, quad.bottomLeft->z);

    BloodInstance newBlood1, newBlood2;
    newBlood1.position = bloodCenter1;
    newBlood2.position = bloodCenter2;

    newBlood1.scale = 0.1f;
    newBlood2.scale = 0.1f;

    // std::cout << "New blood instance 1 created at: (" << bloodCenter1.x << ", " << bloodCenter1.y << ", " << bloodCenter1.z << ")\n";
    // std::cout << "New blood instance 2 created at: (" << bloodCenter2.x << ", " << bloodCenter2.y << ", " << bloodCenter2.z << ")\n";

    bloodInstances.push_back(newBlood1);
    bloodInstances.push_back(newBlood2);

    // Mark this quad as having spawned blood
    quad.bloodSpawned = true;
    // std::cout << "Number of blood instances: " << bloodInstances.size() << std::endl;
}


// Render blood particles
void renderBlood()
{
    for (const auto &instance : bloodInstances)
    {
        glPushMatrix();
        glTranslatef(instance.position.x, instance.position.y, instance.position.z);
        // glRotatef(instance.rotation, 0.0f, 1.0f, 0.0f); // Rotate around Y-axis, for example
        glScalef(instance.scale, instance.scale, instance.scale);

        // Render the blood mesh
        renderTexturedObjMesh(bloodMesh, bloodTexture);

        glPopMatrix();
    }
}

// =============================================================================================================================================================================
// SIDE & TOP TOOLBAR MENU
// Function to render text (simple implementation)
void renderCenteredTextBold(float x, float y, const std::string &text, float buttonWidth, float buttonHeight)
{
    float textWidth = text.length() * 8;                   // Approximate width per character in GLUT_BITMAP_8_BY_13
    float textHeight = 13;                                 // Approximate height of GLUT_BITMAP_8_BY_13 font
    float centeredX = x + (buttonWidth - textWidth) / 2;   // Center horizontally
    float centeredY = y + (buttonHeight - textHeight) / 2; // Center vertically

    // Draw each character multiple times for a bold effect
    for (int i = 0; i < 3; i++)
    {
        glRasterPos2f(centeredX + i * 0.5f, centeredY + i * 0.5f); // Slight offset each layer
        for (char c : text)
        {
            glutBitmapCharacter(GLUT_BITMAP_8_BY_13, c);
        }
    }
}

// Render Button Menu
void renderSideToolbar()
{
    // Save OpenGL state settings to isolate the menu rendering
    glPushAttrib(GL_ENABLE_BIT | GL_COLOR_BUFFER_BIT | GL_CURRENT_BIT);

    // Set orthographic projection for fixed screen coordinates
    glMatrixMode(GL_PROJECTION);
    glLoadIdentity();
    gluOrtho2D(0, WINDOW_WIDTH, 0, WINDOW_HEIGHT);
    glMatrixMode(GL_MODELVIEW);
    glLoadIdentity();

    // Disable depth test so menu is drawn on top of everything
    glDisable(GL_DEPTH_TEST);

    for (const Button &button : buttons)
    {
        // Set button color based on hover state
        if (button.isHovered)
        {
            glColor3f(0.7f, 0.7f, 0.7f); // Light gray when hovered
        }
        else
        {
            glColor3f(0.5f, 0.5f, 0.5f); // Default color gray
        }

        // Draw button rectangle
        glBegin(GL_QUADS);
        glVertex2f(button.x, button.y);
        glVertex2f(button.x + button.width, button.y);
        glVertex2f(button.x + button.width, button.y + button.height);
        glVertex2f(button.x, button.y + button.height);
        glEnd();

        // Draw centered bold label text for each button
        glColor3f(0.0f, 0.0f, 0.0f); // Black color for text
        renderCenteredTextBold(button.x, button.y, button.label, button.width, button.height);
    }
    // Restore OpenGL states
    glPopAttrib();
}

// Render values on top
void renderTopToolbar()
{
    // Save OpenGL state
    glPushAttrib(GL_ENABLE_BIT | GL_COLOR_BUFFER_BIT | GL_CURRENT_BIT);

    // Set orthographic projection for 2D rendering
    glMatrixMode(GL_PROJECTION);
    glPushMatrix();
    glLoadIdentity();
    gluOrtho2D(0, WINDOW_WIDTH, 0, WINDOW_HEIGHT);

    glMatrixMode(GL_MODELVIEW);
    glPushMatrix();
    glLoadIdentity();

    // Disable depth testing
    glDisable(GL_DEPTH_TEST);

    float TOOLBAR_HEIGHT = 50.0f;  // Height of the toolbar
    float TOOLBAR_PADDING = 10.0f; // Padding for text

    // Render the toolbar background
    glColor3f(0.2f, 0.2f, 0.2f); // Dark gray
    glBegin(GL_QUADS);
    glVertex2f(0.0f, WINDOW_HEIGHT - TOOLBAR_HEIGHT);
    glVertex2f(WINDOW_WIDTH, WINDOW_HEIGHT - TOOLBAR_HEIGHT);
    glVertex2f(WINDOW_WIDTH, WINDOW_HEIGHT);
    glVertex2f(0.0f, WINDOW_HEIGHT);
    glEnd();

    // Render the toolbar text
    float textX = TOOLBAR_PADDING;
    float textY = WINDOW_HEIGHT - TOOLBAR_HEIGHT / 2.0f - 10.0f; // Vertically center the text

    glColor3f(1.0f, 1.0f, 1.0f); // White text
    int index = 0;
    for (const std::string &value : toolbarValues)
    {
        glRasterPos2f(textX, textY);
        for (char c : value)
        {
            glutBitmapCharacter(GLUT_BITMAP_HELVETICA_18, c);
        }
        float textLength = value.length() * 10.0f; // Approximate width of each character in GLUT_BITMAP_TIMES_ROMAN_24
        float spacing = textLength * 0.1f;         // Use 20% of the text length for spacing
        textX += textLength + spacing;             // Adjust horizontal spacing dynamically
        index++;
    }

    // Restore OpenGL state
    glMatrixMode(GL_PROJECTION);
    glPopMatrix();
    glMatrixMode(GL_MODELVIEW);
    glPopMatrix();
    glPopAttrib();
}

// Update the values displayed in the top toolbar
void updateTopToolbarValues()
{
    std::ostringstream fxStream, fzStream, timeStream, fpsStream;

    // Format each value with precision
    fxStream << std::fixed << std::setprecision(2) << Fx;
    fzStream << std::fixed << std::setprecision(2) << Fz;
    timeStream << std::fixed << std::setprecision(2) << simulation_time;
    fpsStream << std::fixed << std::setprecision(0) << fps; // FPS as an integer

    // Update toolbar values with formatted strings
    toolbarValues[0] = "Fx: " + fxStream.str() + " N";
    toolbarValues[1] = "Fz: " + fzStream.str() + " N";
    toolbarValues[2] = "Time Elapsed: " + timeStream.str() + " s";
    toolbarValues[3] = "Rcvd Pos Msgs: " + std::to_string(messagesReceived);
    toolbarValues[4] = "Sent Force Msgs: " + std::to_string(messagesSent);
    toolbarValues[5] = "FPS: " + fpsStream.str(); // Add FPS here
}

// Render help box when clicking help button
void renderHelpBox()
{
    // Set help box dimensions
    float boxWidth = 600.0f;
    float boxHeight = 175.0f;
    float boxX = (WINDOW_WIDTH - boxWidth) / 2.0f;
    float boxY = (WINDOW_HEIGHT - boxHeight) / 2.0f;

    // Save OpenGL state
    glPushAttrib(GL_ENABLE_BIT | GL_COLOR_BUFFER_BIT | GL_CURRENT_BIT);

    // Set orthographic projection for fixed 2D rendering
    glMatrixMode(GL_PROJECTION);
    glPushMatrix();
    glLoadIdentity();
    gluOrtho2D(0, WINDOW_WIDTH, 0, WINDOW_HEIGHT);

    glMatrixMode(GL_MODELVIEW);
    glPushMatrix();
    glLoadIdentity();

    // Disable depth testing and blending for consistent rendering
    glDisable(GL_DEPTH_TEST);
    glDisable(GL_TEXTURE_2D);

    // Render semi-transparent background
    glColor4f(0.0f, 0.0f, 0.0f, 0.8f); // Dark background with transparency
    glBegin(GL_QUADS);
    glVertex2f(boxX, boxY);
    glVertex2f(boxX + boxWidth, boxY);
    glVertex2f(boxX + boxWidth, boxY + boxHeight);
    glVertex2f(boxX, boxY + boxHeight);
    glEnd();

    // Render text lines inside the box
    std::vector<std::string> helpTextLines = {
        "Help Information:",
        "1. Click 'LANCET' to enable or disable the lancet tool.",
        "2. Click 'CAMERA' to enable or disable camera movement.",
        "3. Click 'HELP' to toggle this help box.",
        "4. Click 'CUT BOX' to toggle the cut box.",
        "5. Terminate the simulation by closing this window."};

    // Set text color
    glColor3f(1.0f, 1.0f, 1.0f); // White color for text

    // Calculate line spacing and starting position
    float lineSpacing = 20.0f;                   // Space between lines
    float textStartX = boxX + 20.0f;             // Left margin
    float textStartY = boxY + boxHeight - 30.0f; // Start near the top of the box

    for (size_t i = 0; i < helpTextLines.size(); ++i)
    {
        glRasterPos2f(textStartX, textStartY - i * lineSpacing); // Position for each line
        for (char c : helpTextLines[i])
        {
            glutBitmapCharacter(GLUT_BITMAP_8_BY_13, c);
        }
    }

    // Restore OpenGL state
    glMatrixMode(GL_PROJECTION);
    glPopMatrix();
    glMatrixMode(GL_MODELVIEW);
    glPopMatrix();
    glPopAttrib();
}

// Render the cut box with vertices and connecting lines
void renderCutBox()
{
    glm::vec3 cutBoxCenter = calculateEpidermisCenter(epidermisMesh);
    cutBoxCenter.x -= 0.1f;
    cutBoxCenter.y -= 0.15f;
    float scaleX = 0.65f, scaleY = 0.65f, scaleZ = 0.1f;

    std::vector<glm::vec3> vertices = {
        {cutBoxCenter.x - scaleX, cutBoxCenter.y - scaleY, cutBoxCenter.z + scaleZ}, // Bottom-left
        {cutBoxCenter.x + scaleX, cutBoxCenter.y - scaleY, cutBoxCenter.z + scaleZ}, // Bottom-right
        {cutBoxCenter.x - scaleX, cutBoxCenter.y + scaleY, cutBoxCenter.z + scaleZ}, // Top-left
        {cutBoxCenter.x + scaleX, cutBoxCenter.y + scaleY, cutBoxCenter.z + scaleZ}  // Top-right
    };

    // Render lines connecting the vertices
    glColor3f(1.0f, 1.0f, 1.0f); // Set color to white for lines
    glLineWidth(4.0f);           // Set line width

    glBegin(GL_LINES);
    // Connect bottom-left to bottom-right
    glVertex3f(vertices[0].x, vertices[0].y, vertices[0].z);
    glVertex3f(vertices[1].x, vertices[1].y, vertices[1].z);

    // Connect bottom-right to top-right
    glVertex3f(vertices[1].x, vertices[1].y, vertices[1].z);
    glVertex3f(vertices[3].x, vertices[3].y, vertices[3].z);

    // Connect top-right to top-left
    glVertex3f(vertices[3].x, vertices[3].y, vertices[3].z);
    glVertex3f(vertices[2].x, vertices[2].y, vertices[2].z);

    // Connect top-left to bottom-left
    glVertex3f(vertices[2].x, vertices[2].y, vertices[2].z);
    glVertex3f(vertices[0].x, vertices[0].y, vertices[0].z);
    glEnd();
}

// ================================================================================================================================================================================
// CONTROLS, TRANSFORMATIONS & DIRECTION
// Orbit camera around the scene 
void orbitCameraWithKeyboard(unsigned char key, int x, int y)
{
    // Handle camera rotation if unlocked
    if (isCameraLocked)
    {
        switch (key)
        {
        case 'a':
            mainCamera.orbitHorizontally(rotationSpeed);
            mainCamera.setView(WINDOW_WIDTH, WINDOW_HEIGHT);
            break;
        case 'd':
            mainCamera.orbitHorizontally(-rotationSpeed);
            mainCamera.setView(WINDOW_WIDTH, WINDOW_HEIGHT);
            break;
        case 'w':
            mainCamera.orbitVertically(rotationSpeed);
            mainCamera.setView(WINDOW_WIDTH, WINDOW_HEIGHT);
            break;
        case 's':
            mainCamera.orbitVertically(-rotationSpeed);
            mainCamera.setView(WINDOW_WIDTH, WINDOW_HEIGHT);
            break;
        }
    }
    glutPostRedisplay();
}

// Function to handle mouse motion (track position only)
void mouseMotion(int x, int y)
{
    // Get the window dimensions
    int windowWidth = glutGet(GLUT_WINDOW_WIDTH);
    int windowHeight = glutGet(GLUT_WINDOW_HEIGHT);

    // Map mouse position to OpenGL 2D space
    float adjustedX = static_cast<float>(x);
    float adjustedY = static_cast<float>(windowHeight - y); // Flip Y-axis

    for (Button &btn : buttons)
    {
        btn.isHovered = (x >= btn.x && x <= btn.x + btn.width && adjustedY >= btn.y && adjustedY <= btn.y + btn.height);
    }

    glutPostRedisplay();
}

// Function to handle mouse clicks (click buttons only)
void mouseClick(int button, int state, int x, int y)
{
    int adjustedY = glutGet(GLUT_WINDOW_HEIGHT) - y;

    // Always detect mouse motion for button hover updates
    mouseMotion(x, y);

    if (button == GLUT_LEFT_BUTTON && state == GLUT_DOWN)
    {
        bool actionHandled = false; // Track if a button click was handled

        for (Button &btn : buttons)
        {
            // Check if the mouse is within the button bounds
            if (x >= btn.x && x <= btn.x + btn.width && adjustedY >= btn.y && adjustedY <= btn.y + btn.height)
            {
                actionHandled = true;
                std::cout << "Button clicked: " << btn.label << std::endl;

                if (btn.label == "LANCET")
                {
                    isLancetActive = !isLancetActive;
                    glutSetCursor(isLancetActive ? GLUT_CURSOR_DESTROY : GLUT_CURSOR_INHERIT);
                    std::cout << (isLancetActive ? "Lancet tool activated!" : "Lancet tool deactivated!") << std::endl;
                }
                else if (btn.label == "CAMERA")
                {
                    isCameraLocked = !isCameraLocked;
                    std::cout << (isCameraLocked ? "Camera unlocked!" : "Camera locked!") << std::endl;
                }
                else if (btn.label == "HELP")
                {
                    showHelpBox = !showHelpBox;
                    glutSetCursor(showHelpBox ? GLUT_CURSOR_HELP : GLUT_CURSOR_INHERIT);
                    std::cout << (showHelpBox ? "Help box shown." : "Help box hidden.") << std::endl;
                }
                else if (btn.label == "CUT BOX")
                {
                    showCutBox = !showCutBox;
                    // glutSetCursor(showHelpBox ? GLUT_CURSOR_HELP : GLUT_CURSOR_INHERIT);
                    std::cout << (showCutBox ? "Cut box shown." : "Cut box hidden.") << std::endl;
                }
                else
                {
                    std::cerr << "Unhandled button label: " << btn.label << std::endl;
                }

                break; // No need to check other buttons if one was clicked
            }
        }

        // Handle clicks outside any button
        if (!actionHandled)
        {
            glutSetCursor(GLUT_CURSOR_INHERIT); // Reset cursor to default
        }
    }

    glutPostRedisplay();
}

// Get transformation matrix
glm::mat4 getLancetTransformationMatrix()
{
    // Step 1: Calculate the center of the lancet mesh (if not already calculated)
    glm::vec3 lancetCenter = calculateCenter(lancetMesh);
    // Step 2: Create the transformation matrix
    glm::mat4 transformation = glm::mat4(1.0f);
    // Step 3: Translate to the origin (center of the lancet), apply rotation, then translate back
    transformation = glm::translate(transformation, glm::vec3(lancetPosX, lancetPosY, lancetPosZ)); // Apply movement
    transformation = glm::translate(transformation, lancetCenter);                                  // Move to center
    transformation = glm::translate(transformation, -lancetCenter);                                 // Move back from center

    return transformation;
}

// Calculate lancet direction
void updateLancetDirection()
{
    // Calculate change on the 2 axis
    float deltaX = lancetPosX - previousLancetPosX; // Change of movement on X-axis
    float deltaZ = lancetPosZ - previousLancetPosZ; // Change of movement on Z-axis
    float epsilon = 0.0001f;
    float change_threshold = 0.015f;

    // std::cout << "deltaX: " << deltaX << "\n";
    // std::cout << "deltaZ: " << deltaZ << "\n";

    if (std::abs(deltaZ) > epsilon || std::abs(deltaX) > epsilon)   // Movement detected on Z-axis or X-axis
    {
        if (std::abs(deltaX) > std::abs(deltaZ))    // Greater change in X than in Z
        {
            if (std::abs(deltaZ) < epsilon && std::abs(deltaX)) // Z = 0 -> only right and left and only when the change is > change_threshold
            {
                direction = (deltaX > 0) ?  RIGHT : LEFT;
            }
            /*else
            {
                direction = (deltaX > 0) ?  FORWARD_RIGHT : FORWARD_LEFT;   // Change in X and a little bit on Z
            }// Greater change in X than in Z*/
        }
        else    // Greater change in Z than in X
        {
            if (std::abs(deltaX) < epsilon)
            {
                direction = (deltaZ > 0) ?  BACKWARD : FORWARD;
            }
            /*else
            {
                direction = (deltaX > 0) ?  FORWARD_RIGHT : FORWARD_LEFT;
            }*/
        }
    }

    // Update previous position
    previousLancetPosX = lancetPosX;
    previousLancetPosZ = lancetPosZ;
}

// ================================================================================================================================================================================
// MESSAGES AND LOGGING
// Function to log time and force values to the file
void logTimeAndForce(float time, float forceX, float forceZ)
{
    if (logFile.is_open())
    {
        logFile << std::fixed << std::setprecision(3)
                << "Time: " << time << " s, "
                << "ForceX: " << forceX << " N, "
                << "ForceZ: " << forceZ << " N\n";
    }
    else
    {
        std::cerr << "Log file is not open. Unable to log forces.\n";
    }
}

// Close log file on application termination
void closeLogFile()
{
    if (logFile.is_open())
    {
        logFile.close();
        std::cout << "Log file closed successfully.\n";
    }
}

// Function to print a startup message
void printStartupMessage()
{
    std::cout << "Starting 3D Simulation of Skin Melanoma Removal..." << std::endl;
    std::cout << "Initializing OpenGL settings and loading resources." << std::endl;
}

// Print message when simulation application is terminated
void onExit()
{
    std::cout << "Simulation Terminated!" << std::endl;
}

// ================================================================================================================================================================================
// SERVER & CLIENT SETUP
// Filter position
/*float smoothPosition(float currentPos, float previousPos, float alpha = 0.025f) {
    return alpha * currentPos + (1 - alpha) * previousPos;
}*/

// Setup simulation server to handle incoming messages from the mechanism
void simulationServerThread()
{
    if (simulationServer.startServer(serverPort))
    {
        std::cout << "**Simulation server started. Listening position data on port 9090..." << std::endl;

        while (true)
        {
            if (simulationServer.acceptClient())
            {
                while (true)
                { // Continuous loop to handle multiple messages
                    std::string receivedData = simulationServer.receiveData();
                    if (!receivedData.empty())
                    {
                        // Parse the received data using regex
                        std::regex regexPattern(R"(\{X:\s*(-?\d*\.?\d+),\s*Z:\s*(-?\d*\.?\d+)\})");
                        std::smatch match;
                        messagesReceived++;
                        if (std::regex_search(receivedData, match, regexPattern))
                        {
                            float receivedX = std::stof(match[1].str()); // Extract X
                            float receivedZ = std::stof(match[2].str()); // Extract Z

                            if (isLancetActive) // Perform the transformation only if the lancet is active
                            {
                                lancetPosX += receivedX;
                                lancetPosZ += receivedZ;
                                /*lancetPosX = smoothPosition(-10 * receivedX, lancetPosX);
                                lancetPosZ = smoothPosition(10 * receivedZ, lancetPosZ);*/
                            }
                        }
                        else
                        {
                            std::cerr << "Failed to parse data: " << receivedData << std::endl;
                        }
                    }
                    else
                    {
                        std::cout << "Mechanism client disconnected." << std::endl;
                        break;
                    }
                }
            }
        }
    }
    else
    {
        std::cerr << "Failed to start the simulation server!" << std::endl;
    }
}

// Setup simulation client to send force data to to mechanism
void simulationClientThread()
{
    serverConnection = simulationClient.connectToServer(mechanismServerIP, clientPort);
    if (!serverConnection)
    {
        std::cerr << "Failed to connect to the mechanism's server!" << std::endl;
    }
    else
    {
        std::cout << "**Mechanism connection established successfully! Sending force data..." << std::endl;
    }
}

// ================================================================================================================================================================================
// SIMULATION -> FORCE CALCULATION & TEXTURE DEFORMATION
// Function to find the lancet tip in object space (vertex with minimum z-coordinate)
glm::vec3 calculateLancetTipInObjectSpace()
{
    if (lancetMesh.vertices.empty())
    {
        return glm::vec3(0.0f, 0.0f, 0.0f); // Return a default value if there are no vertices
    }

    // Initialize the lancet tip with the first vertex
    glm::vec3 lancetTip = lancetMesh.vertices[0];
    for (const auto &vertex : lancetMesh.vertices)
    {
        // Check for the minimum z-coordinate
        if (vertex.z < lancetTip.z)
        {
            lancetTip = vertex;
        }
    }
    return lancetTip;
}

glm::vec3 transformCachedLancetTip()
{
    if (cachedLancetTip)
    {
        glm::mat4 transformationMatrix = getLancetTransformationMatrix();
        glm::vec4 transformedTip = transformationMatrix * glm::vec4(*cachedLancetTip, 1.0f);
        return glm::vec3(transformedTip);
    }
    return glm::vec3(0.0f, 0.0f, 0.0f); // Default value if the tip is not cached
}

// Cache blade vertices once and avoid recalculations
void cacheBladeVertices(std::vector<glm::vec3 *> &cachedBladeVertices, float radius = 0.45f)
{
    glm::vec3 lancetTipLocal = calculateLancetTipInObjectSpace(); // Tip in object space
    cachedLancetTip = nullptr;                                    // Reset cached tip

    // Iterate through lancetMesh vertices and cache the pointers of vertices close to the tip
    for (auto &vertex : lancetMesh.vertices)
    {
        if (vertex.z > lancetTipLocal.z &&
            std::abs(vertex.z - lancetTipLocal.z) <= radius)
        {
            cachedBladeVertices.push_back(&vertex);
        }

        // Cache the lancet tip if this is the vertex with the minimum z
        if (vertex.z == lancetTipLocal.z)
        {
            cachedLancetTip = &vertex;
            cachedBladeVertices.push_back(&vertex); // cache the lancet tip too
        }
    }
}

// Transform the cached blade verrtices to match the movement of the whole tool
std::vector<glm::vec3> transformCachedBladeVertices(const std::vector<glm::vec3 *> &cachedBladeVertices)
{
    std::vector<glm::vec3> transformedVertices;
    transformedVertices.reserve(cachedBladeVertices.size());

    glm::mat4 transformationMatrix = getLancetTransformationMatrix();

    for (const auto *vertex : cachedBladeVertices)
    {
        glm::vec4 transformedVertex = transformationMatrix * glm::vec4(*vertex, 1.0f);
        transformedVertices.push_back(glm::vec3(transformedVertex));
    }

    return transformedVertices;
}

// Search the grid partitions to find the closest quads and return pointers so the changes can be applied to the origianl not to temporary copies
std::vector<VertexQuad*> findAllClosestQuadsToBlade(const std::vector<glm::vec3> &bladeVertices,
                                                    std::vector<VertexQuad> &quads,
                                                    float threshold = 0.05f)
{
    std::vector<VertexQuad*> closeQuads;
    float thresholdSquared = threshold * threshold;

    for (auto &quad : quads)
    {
        glm::vec3 quadCenter(
            (quad.bottomLeft->x + quad.bottomRight->x + quad.topLeft->x + quad.topRight->x) / 4.0f,
            (quad.bottomLeft->y + quad.bottomRight->y + quad.topLeft->y + quad.topRight->y) / 4.0f,
            0.0f);

        for (const auto &bladeVertex : bladeVertices)
        {
            float distanceSquared = glm::dot(quadCenter - bladeVertex, quadCenter - bladeVertex);
            if (distanceSquared < thresholdSquared)
            {
                closeQuads.push_back(&quad);
                // Break to avoid adding the same quad multiple times.
                break;
            }
        }
    }

    return closeQuads;
}

// Force Calculation
float calculateSpringDamperForceForZDisplacement(
    float currentZ,           // Current z-coordinate
    float restZ,              // Original z-coordinate
    float springConstant,     // Spring constant (stiffness)
    float dampingCoefficient, // Damping coefficient
    float lancetVelocityZ     // Approximation of velocity in the z-axis
)
{
    // Hooke's law: F_spring = -k * displacement
    float displacement = currentZ - restZ;
    // std::cout << "Displacement: " << displacement << "\n";
    float springForce = -springConstant * displacement;

    // Damping force: F_damping = -c * velocity
    float dampingForce = -dampingCoefficient * lancetVelocityZ;

    // Total force
    float totalForce = springForce + dampingForce;

    return abs(totalForce);
}

// Break into mesh when lancet goes too deep
bool quadExceedsSplitThreshold(const VertexQuad &quad)
{
    return quad.bottomLeft->z < SPLIT_THRESHOLD;
}

void revertVertexZUV(Vertex *vertex)
{
    vertex->z = vertex->originalZ;
    vertex->u = vertex->originalU;
    vertex->v = vertex->originalV;
}

// Revert z-coords when threshold is reached
void revertSplitQuad(VertexQuad &quad)
{
    // Revert each vertex using the helper function
    revertVertexZUV(quad.bottomLeft);
    revertVertexZUV(quad.bottomRight);
    revertVertexZUV(quad.topLeft);
    revertVertexZUV(quad.topRight);
}

// Split a quad and reveal a wound effect
void splitVertexQuad(VertexQuad &quad)
{
    const float yoffset = 0.01; // Controls vertical spread
    const float uvoffset = yoffset;

    // Adjust y-coordinates
    quad.bottomLeft->y -= yoffset;
    quad.bottomRight->y -= yoffset;
    quad.topLeft->y += yoffset;
    quad.topRight->y += yoffset;

    // Adjust UV coordinates to simulate texture deformation
    quad.bottomLeft->v += uvoffset;
    quad.bottomRight->v += uvoffset;
    quad.topLeft->v -= uvoffset;
    quad.topRight->v -= uvoffset;
}

// Combine revert and split logic
void revertAndSplitQuad(VertexQuad &quad)
{
    revertSplitQuad(quad);
    splitVertexQuad(quad);
}

// Deform the mesh without cutting
void deformWithoutCutting(VertexQuad &quad)
{
    float deformationFactor = 0.0025;
    // std::cout << "Deformation factor: " << deformationFactor << "\n";

    // Parameters
    float springConstant = 10.0f;    // Stiffness
    float dampingCoefficient = 5.0f; // Damping factor

    // Calculate forces felt
    // Fx = 0.0f;
    Fz = calculateSpringDamperForceForZDisplacement(quad.bottomLeft->z, quad.bottomLeft->originalZ, springConstant, dampingCoefficient, deformationFactor);

    // Apply deformation to Z-coordinates
    quad.bottomLeft->z -= deformationFactor;
    quad.bottomRight->z -= deformationFactor;
    quad.topLeft->z -= deformationFactor;
    quad.topRight->z -= deformationFactor;

    // Apply UV deformation
    quad.bottomLeft->u -= deformationFactor;
    quad.bottomLeft->v -= deformationFactor;

    quad.bottomRight->u -= deformationFactor;
    quad.bottomRight->v -= deformationFactor;

    quad.topLeft->u -= deformationFactor;
    quad.topLeft->v -= deformationFactor;

    quad.topRight->u -= deformationFactor;
    quad.topRight->v -= deformationFactor;
}

// Main function to update our simulation scene
void updateSimulationScene(int value)
{
    auto start = std::chrono::high_resolution_clock::now();

    // Update Top Toolbar Values
    auto toolbarStart = std::chrono::high_resolution_clock::now();
    updateTopToolbarValues();
    auto toolbarEnd = std::chrono::high_resolution_clock::now();
    //std::cout << "updateTopToolbarValues() execution time: " << std::chrono::duration<double, std::milli>(toolbarEnd - toolbarStart).count() << " ms\n";

    // Calculate Blade Vertices
    auto bladeStart = std::chrono::high_resolution_clock::now();
    std::vector<glm::vec3> bladeVertices = transformCachedBladeVertices(cachedBladeVertices);
    auto bladeEnd = std::chrono::high_resolution_clock::now();
    //std::cout << "calculateBladeVerticesInWorldSpace() execution time: " << std::chrono::duration<double, std::milli>(bladeEnd - bladeStart).count() << " ms\n";

    // Update Closest Quad Logic
    auto quadStart = std::chrono::high_resolution_clock::now();

    if (previousLancetPosX != lancetPosX)
    {
        auto filterStart = std::chrono::high_resolution_clock::now();
        glm::vec3 lancetTip = transformCachedLancetTip();
        filterQuadsByGrid(lancetTip, filteredQuadMesh, quadGridMap, 0.75f);
        auto filterEnd = std::chrono::high_resolution_clock::now();
        //std::cout << "filterQuadsByXYZ() execution time: " << std::chrono::duration<double, std::milli>(filterEnd - filterStart).count() << " ms\n";
    }

    // Find the closest quad using the filtered subset
    std::vector<VertexQuad*> quadsNearBlade = findAllClosestQuadsToBlade(bladeVertices, filteredQuadMesh);
    auto quadEnd = std::chrono::high_resolution_clock::now();
    //std::cout << "findClosestQuadToBlade() execution time: " << std::chrono::duration<double, std::milli>(quadEnd - quadStart).count() << " ms\n";

    // Other logic for deforming or splitting
    auto logicStart = std::chrono::high_resolution_clock::now();
    // Cutting
    if (!quadsNearBlade.empty()) {
        // Update Lancet Direction
        auto directionStart = std::chrono::high_resolution_clock::now();
        updateLancetDirection();
        auto directionEnd = std::chrono::high_resolution_clock::now();
        // std::cout << "updateLancetDirection() execution time: " << std::chrono::duration<double, std::milli>(directionEnd - directionStart).count() << " ms\n";
        for(auto* quad : quadsNearBlade)
        {
            std::cout << "Current direction: " << directionToString(direction) << std::endl;
            switch (direction)  // Perform the deformation depending on how the lancet moves
            {
                case FORWARD:
                //case FORWARD_RIGHT:
                //case FORWARD_LEFT:
                    switch (quad->state)
                    {
                        case DEFAULT:
                            if (!quadExceedsSplitThreshold(*quad))  // Begin defroming the epidermis without cutting it
                            {
                                deformWithoutCutting(*quad);
                                quad->state = DEFORMED;
                            }
                            break;
                        case DEFORMED:
                            if (quadExceedsSplitThreshold(*quad))   // Revert and split quad if threshold is reached
                            {
                                revertAndSplitQuad(*quad);
                                quad->state = SPLIT;
                            }
                            else
                            {
                                deformWithoutCutting(*quad); // Continue deforming until threshold is reached
                            }
                            break;
                        case SPLIT:
                            if(!quad->bloodSpawned)
                            {
                                createNewBloodInstance(*quad);  // Apply blood effect on split quads
                                quad->bloodSpawned = true;
                                // Fx = 0.0f;
                                Fz = 0.12f;
                            }
                            break;
                    }
                    break;
                case LEFT:
                    Fx = -1.2f;
                    Fz = 0.12f;
                    if (quad->state == SPLIT && !quad->bloodSpawned) {
                        createNewBloodInstance(*quad);
                        quad->bloodSpawned = true;
                        //Fx = 0.12f;
                    }
                    switch (quad->state)
                    {
                        case DEFAULT:
                            splitVertexQuad(*quad);
                            quad->state = SPLIT;
                            break;
                        case DEFORMED:
                            revertAndSplitQuad(*quad);
                            quad->state = SPLIT;
                            break;
                    }
                    break;
                case RIGHT:
                    Fx = 1.2f;
                    Fz = 0.12f;
                    if (quad->state == SPLIT && !quad->bloodSpawned) {
                        createNewBloodInstance(*quad);
                        quad->bloodSpawned = true;
                        //Fx = 0.12f;
                    }
                    switch (quad->state)
                    {
                        case DEFAULT:
                            splitVertexQuad(*quad);
                            quad->state = SPLIT;
                            break;
                        case DEFORMED:
                            revertAndSplitQuad(*quad);
                            quad->state = SPLIT;
                            break;
                    }
                    break;
                case NONE:
                    switch (quad->state)
                    {
                        case DEFAULT:
                        case DEFORMED:  
                            //Fx = 0.5f;
                            Fz = 0.12f;
                            break;
                        case SPLIT:
                            //Fx = 0.12f;
                            Fz = 0.12f;
                            break;
                    }
                    break;
            }
            //revertSplitQuad(*quad);
        }
    }
    // Not cutting
    else 
    {
        Fx = 0.0f;
        Fz = 0.0f;
    }

    // Log Time and Forces + sending force message
    auto logStart = std::chrono::high_resolution_clock::now();

    // Capture the current values to avoid changes during thread execution
    float currentSimulationTime = simulation_time;
    float currentFx = Fx;
    float currentFz = Fz;

    // Use a lambda to offload logging and data sending to a separate thread
    std::thread logThread([currentSimulationTime, currentFx, currentFz]()
                          {
        logTimeAndForce(currentSimulationTime, currentFx, currentFz);
        simulationClient.sendData("{Fx: " + std::to_string(currentFx) + ", Fz: " + std::to_string(currentFz) + "}"); });

    // Detach the thread to allow it to execute independently
    logThread.detach();

    messagesSent++;
    auto logEnd = std::chrono::high_resolution_clock::now();
    //std::cout << "logTimeAndForce() execution time: " << std::chrono::duration<double, std::milli>(logEnd - logStart).count() << " ms\n";

    // Increment Simulation Time
    simulation_time += (float)TIME_STEP / 1000;

    // Set Timer for Next Update
    glutTimerFunc(TIME_STEP, updateSimulationScene, 0);

    auto end = std::chrono::high_resolution_clock::now();
    //std::cout << "Total updateSimulationScene() execution time: " << std::chrono::duration<double, std::milli>(end - start).count() << " ms\n\n";
}

// ================================================================================================================================================================================
// DISPLAY
// Function to set initial camera position and retain target and up vector
void setCameraViewAtMeshCenter(Camera &camera, const std::vector<Cube> &skinMesh, int win_width, int win_height)
{
    glm::vec3 center = calculateEpidermisCenter(skinMesh); // Center of the mesh
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

// Reshape view
void reshape(int width, int height)
{
    glViewport(0, 0, width, height);
    glMatrixMode(GL_PROJECTION);
    glLoadIdentity();
    gluPerspective(45.0, (GLfloat)width / (GLfloat)height, 1.0, 100.0);
    glMatrixMode(GL_MODELVIEW);
}

// Fps regulator with FPS calculation
void timer(int value)
{
    auto currentFrameTime = std::chrono::high_resolution_clock::now();
    float elapsedTime = std::chrono::duration<float>(currentFrameTime - lastFrameTime).count();
    lastFrameTime = currentFrameTime;

    // Calculate FPS
    if (elapsedTime > 0.0f)
    {
        fps = 1.0f / elapsedTime; // FPS = 1 / frame time
    }

    glutPostRedisplay();         // Request a redraw
    glutTimerFunc(32, timer, 0); // Set up next timer for 32ms (approx. 30 FPS)
}

// Display all the content
void display()
{
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

    // Enable depth test for 3D rendering
    glEnable(GL_DEPTH_TEST);

    // Update the camera view based on any rotations or position changes
    mainCamera.setView(WINDOW_WIDTH, WINDOW_HEIGHT);

    // Render epidermis mesh
    renderTexturedFrontFace(epidermisMesh, epidermisMelanomaTexture); // Front face (with melanoma)
    renderTexturedBackFace(epidermisMesh, epidermisSideTexture);      // Back face
    renderTexturedSideFaces(epidermisMesh, epidermisSideTexture);     // Side and interrio faces

    // Render dermis mesh
    renderTexturedFrontFace(dermisMesh, dermisTexture);
    renderTexturedBackFace(dermisMesh, dermisTexture);
    renderTexturedSideFaces(dermisMesh, dermisTexture);

    // Render dermis mesh 2nd layer
    renderTexturedFrontFace(dermisMesh2, dermis2Texture);
    renderTexturedBackFace(dermisMesh2, dermis2Texture);
    renderTexturedSideFaces(dermisMesh2, dermis2Texture);

    // Render hypodermis mesh
    renderTexturedFrontFace(hypodermisMesh, hypodermisTexture);
    renderTexturedBackFace(hypodermisMesh, hypodermisTexture);
    renderTexturedSideFaces(hypodermisMesh, hypodermisTexture);

    //renderBlood();

    // Render lancet with texture and transformations only if activated
    if (isLancetActive)
    {
        glPushMatrix();
        glm::mat4 lancetTransformation = getLancetTransformationMatrix();
        glMultMatrixf(&lancetTransformation[0][0]);
        renderTexturedObjMesh(lancetMesh, lancetTexture);
        glPopMatrix();
    }

    // Render Cut box, don't move from here otherwise it won't render
    if (showCutBox)
        renderCutBox();

    // Render helpbox
    if (showHelpBox)
        renderHelpBox();

    // Render side and top bar menu
    renderSideToolbar();
    //renderTopToolbar();

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
    glutCreateWindow("3D Simulation Of Skin Melanoma Removal");

    glEnable(GL_DEPTH_TEST);
    glEnable(GL_TEXTURE_2D);

    glutDisplayFunc(display);               // Display
    glutReshapeFunc(reshape);               // Reshape window view
    atexit(onExit);                         // Print message on application termination
    atexit(closeLogFile);                   // Close lof file on exit
    glutMouseFunc(mouseClick);              // Detect button click
    glutMotionFunc(mouseMotion);            // Detect mouse motion
    glutKeyboardFunc(orbitCameraWithKeyboard); // Register for normal keys '+' and '-'

    timer(0);                 // Start the frame-rate loop
    updateSimulationScene(0); // Start the simulation-rate loop

    // Start the simulation server and client in separate threads
    std::thread server(simulationServerThread);
    std::thread client(simulationClientThread);

    printStartupMessage(); // Print startup message

    // Load skin layered mesh
    initializeLayeredMesh(epidermisMesh, POINT_MASSES, 3.0f, 0.15f, -0.05f); // Initialize epidermis mesh
    initializeVertexQuads(epidermisMesh);                                    // Initilize vertex quads only for the epidermis
    initializeLayeredMesh(dermisMesh, POINT_MASSES, 3.0f, 0.25f, -0.25f);    // Initialize dermis mesh
    initializeLayeredMesh(dermisMesh2, POINT_MASSES, 3.0f, 0.35, -0.55f);
    initializeLayeredMesh(hypodermisMesh, POINT_MASSES, 3.0f, 0.25f, -0.85f); // Initialize dermis mesh
    initializeQuadGrid();
    glm::vec3 lancetTip = transformCachedLancetTip();
    filterQuadsByGrid(lancetTip, filteredQuadMesh, quadGridMap, 0.75f);

    // Set camera view at epidermis mesh center
    setCameraViewAtMeshCenter(mainCamera, epidermisMesh, WINDOW_WIDTH, WINDOW_HEIGHT);

    // Load lancet obj mesh
    loadFromOBJ("Objects/Lancet_v1.obj", lancetMesh);                                   // Load lancet from obj file
    initializeLancetMesh(lancetMesh, glm::vec3(-0.15f, -1.5f, 0.0f), 0.0f, 15.0f, 90.0f); // Set lancet initial position and orientation to match the haptic
    std::cout << "**Lancet initialized at position (" << lancetPosX << ", " << lancetPosY << ", " << lancetPosZ << ")" << std::endl;
    cacheBladeVertices(cachedBladeVertices); // Cache blade vertices and tip to avoid time-cost recalculations

    // Load Textures
    loadTexture("Textures/Tex_Epidermis_Melanoma.jpg", epidermisMelanomaTexture);
    loadTexture("Textures/Tex_Side_Epidermis.png", epidermisSideTexture);
    loadTexture("Textures/Tex_Metal.jpg", lancetTexture);
    loadTexture("Textures/Tex_Dermis.jpg", dermisTexture);
    loadTexture("Textures/Tex_Dermis_2.png", dermis2Texture);
    loadTexture("Textures/Tex_Hypodermis.jpg", hypodermisTexture);

    // Load mesh for blood
    loadFromOBJ("Objects/Blood_2.obj", bloodMesh);
    // Load blood Texture
    loadTexture("Textures/Tex_Blood_3.jpg", bloodTexture);

    // Enter main loop
    glutMainLoop();

    // Wait for the server thread to finish (this won't happen until program exit)
    server.join();

    return 0;
}