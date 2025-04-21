import sympy as sym
from sympy import sin, cos, sqrt

l0 = 0.08
l1 = 0.08
l2 = 0.12
l3 = 0.12
l4 = 0.08

def forward_kinematics(theta1, theta2):

    a = ( ( sin(theta1)*l1 - sin(theta2)*l4 ) / ( l0 + cos(theta2)*l4 - cos(theta1)*l1 ) )
    b = ( ( (sin(theta2))**2 * l4**2 + (l0 + cos(theta2)*l4)**2 - l1**2 + l2**2 - l3**2 ) / ( 2*(l0 + cos(theta2)*l4 - cos(theta1)*l1) ) )
    c = ( (a**2) + 1 )
    d = ( (2*a*b) - (2*a*cos(theta1)*l1) - (2*sin(theta1)*l1) )
    e = ( b**2 - 2*b*cos(theta1)*l1 + l1**2 - l2**2 )

    ye = ( ( -d + sqrt(d**2 - 4*c*e) ) / ( 2*c ) )
    xe = ( (a*ye) + b )

    return xe, ye

"""
if __name__ == "__main__":
    from math import radians

    theta1 = radians(90)
    theta2 = radians(90)

    xe, ye = forward_kinematics(theta1, theta2)
    print(f"End-Effector Position: x = {xe:.4f}, y = {ye:.4f}")
"""