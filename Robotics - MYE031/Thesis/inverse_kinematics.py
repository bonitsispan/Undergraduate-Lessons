import sympy as sym 
from sympy import sin, cos, sqrt, acos, pi
import math

l0 = 0.08
l1 = 0.08
l2 = 0.12
l3 = 0.12
l4 = 0.08

def inverse_kinematics(xe, ye):
    
    a = acos( (xe) / (sqrt(xe**2 + ye**2)) )
    b = acos( (l1**2 - l2**2 + xe**2 + ye**2) / (2*l1*sqrt(xe**2 + ye**2)) )
    c = acos( (l0 - xe) / (sqrt((xe - l0)**2 + ye**2)) )
    d = acos( (xe**2 + ye**2 + l0**2 + l4**2 - l3**2 - 2*xe*l0) / (2*sqrt((xe - l0)**2 + ye**2)*l4) )

    theta1 = a + b
    theta2 = pi - (c + d)

    return theta1, theta2

if __name__ == "__main__":
    from math import radians

    xe = 0.0800 #0.0400
    ye = 0.0731

    theta1, theta2 = inverse_kinematics(xe, ye)
    theta1_deg = math.degrees(theta1)
    theta2_deg = math.degrees(theta2)
    print(f"Motor angles: theta1 = {theta1_deg:.4f}, theta2 = {theta2_deg:.4f}")
    print(f"Motor angles: theta1 = {theta1:.4f}, theta2 = {theta2:.4f}")