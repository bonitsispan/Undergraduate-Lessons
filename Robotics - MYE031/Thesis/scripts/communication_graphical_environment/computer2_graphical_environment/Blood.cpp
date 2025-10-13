#include <GL/glut.h>
#include <vector>
#include <cstdlib>
#include <ctime>
#include <cmath>
#include <ostream>
#include <iostream>
#include <algorithm> // Include for std::remove_if
#include <SOIL/SOIL.h> // For loading textures

// Structure to represent a blood particle
struct BloodParticle {
    float x, y, z;        // Position
    float vx, vy, vz;     // Velocity
    float lifetime;       // Time before the particle disappears
    float alpha;          // Opacity (fades out over time)
};

// Particle collection
std::vector<BloodParticle> bloodParticles;

GLuint bloodTexture; // Texture for blood particles

// Load the blood texture
void loadBloodTexture() {
    bloodTexture = SOIL_load_OGL_texture("Textures/Tex_Blood_2.jpg", SOIL_LOAD_AUTO, SOIL_CREATE_NEW_ID, SOIL_FLAG_INVERT_Y);
    if (!bloodTexture) {
        std::cerr << "Failed to load blood texture!" << std::endl;
        exit(1);
    }
}

// Spawn new blood particles at a given position
void spawnBlood(float startX, float startY, float startZ, int count = 20) {
    for (int i = 0; i < count; ++i) {
        BloodParticle particle;
        particle.x = startX;
        particle.y = startY;
        particle.z = startZ;

        // Compact cluster with slight random spread
        particle.vx = ((rand() % 10 - 5) / 100.0f);
        particle.vy = -0.01f; // Falling velocity
        particle.vz = ((rand() % 10 - 5) / 100.0f);

        particle.lifetime = 2.0f + (rand() % 100) / 100.0f; // Random lifetime
        particle.alpha = 1.0f; // Fully opaque at start
        bloodParticles.push_back(particle);
    }
}

// Update the blood particles
void updateBlood(float deltaTime) {
    for (auto &particle : bloodParticles) {
        particle.x += particle.vx * deltaTime;
        particle.y += particle.vy * deltaTime;
        particle.z += particle.vz * deltaTime;
        particle.vy -= 0.001f; // Simulate gravity
        particle.alpha -= 0.005f; // Gradually fade out
        particle.lifetime -= deltaTime;
    }

    // Remove particles that have expired
    bloodParticles.erase(
        std::remove_if(bloodParticles.begin(), bloodParticles.end(),
                       [](const BloodParticle &p) { return p.lifetime <= 0 || p.alpha <= 0; }),
        bloodParticles.end());
}

// Render the blood particles
void renderBlood() {
    glEnable(GL_TEXTURE_2D);
    glBindTexture(GL_TEXTURE_2D, bloodTexture);
    glEnable(GL_BLEND);
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

    glBegin(GL_QUADS);
    for (const auto &particle : bloodParticles) {
        glColor4f(1.0f, 0.0f, 0.0f, particle.alpha); // Red with alpha fade

        float size = 0.02f; // Size of the particle
        glTexCoord2f(0.0f, 0.0f);
        glVertex3f(particle.x - size, particle.y - size, particle.z);

        glTexCoord2f(1.0f, 0.0f);
        glVertex3f(particle.x + size, particle.y - size, particle.z);

        glTexCoord2f(1.0f, 1.0f);
        glVertex3f(particle.x + size, particle.y + size, particle.z);

        glTexCoord2f(0.0f, 1.0f);
        glVertex3f(particle.x - size, particle.y + size, particle.z);
    }
    glEnd();

    glDisable(GL_BLEND);
    glDisable(GL_TEXTURE_2D);
}

// Callback for display
void display() {
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    renderBlood();
    glutSwapBuffers();
}

// Callback for updating
void timer(int value) {
    updateBlood(0.016f); // Update with a fixed time step
    glutPostRedisplay();
    glutTimerFunc(16, timer, 0); // 60 FPS
}

// Callback for mouse input (spawn blood at mouse position)
void mouseFunc(int button, int state, int x, int y) {
    if (button == GLUT_LEFT_BUTTON && state == GLUT_DOWN) {
        float normalizedX = (x / 300.0f) - 1.0f;
        float normalizedY = 1.0f - (y / 300.0f);
        spawnBlood(normalizedX, normalizedY, 0.0f, 50);
    }
}

// Main function
int main(int argc, char **argv) {
    srand(static_cast<unsigned>(time(0))); // Seed for randomness

    glutInit(&argc, argv);
    glutInitDisplayMode(GLUT_DOUBLE | GLUT_RGB | GLUT_DEPTH);
    glutInitWindowSize(600, 600);
    glutCreateWindow("Blood Simulation");

    glEnable(GL_DEPTH_TEST);

    glutDisplayFunc(display);
    glutMouseFunc(mouseFunc);
    glutTimerFunc(16, timer, 0);

    glClearColor(0.0f, 0.0f, 0.0f, 1.0f); // Background color

    loadBloodTexture(); // Load blood texture

    glutMainLoop();
    return 0;
}
