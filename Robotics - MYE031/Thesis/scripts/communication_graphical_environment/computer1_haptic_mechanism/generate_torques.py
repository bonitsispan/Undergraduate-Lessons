import numpy as np
import sympy as sym
from sympy import diff, sin, cos, sqrt

def generate_Jacobian_Transpose(theta1_value, theta2_value):

    theta1, theta2 = sym.symbols('theta1 theta2')
    l0, l1, l2, l3, l4 = sym.symbols('l0 l1 l2 l3 l4')

    a = ((sin(theta1)*l1 - sin(theta2)*l4) / (l0 + cos(theta2)*l4 - cos(theta1)*l1))
    b = (((sin(theta2))**2 * l4**2 + (l0 + cos(theta2)*l4)**2 - l1**2 + l2**2 - l3**2) / (2 * (l0 + cos(theta2)*l4 - cos(theta1)*l1)))
    c = (a**2) + 1
    d = (2*a*b) - (2*a*cos(theta1)*l1) - (2*sin(theta1)*l1)
    e = (b**2 - 2*b*cos(theta1)*l1 + l1**2 - l2**2)

    ye = ((-d + sqrt(d**2 - 4*c*e)) / (2*c))
    xe = (a*ye) + b

    f00 = diff(xe, theta1)
    f01 = diff(xe, theta2)
    f10 = diff(ye, theta1)
    f11 = diff(ye, theta2)

    J = sym.Matrix([[f00, f01], [f10, f11]])

    subs = {
        l0: 0.08,
        l1: 0.08,
        l2: 0.12,
        l3: 0.12,
        l4: 0.08,
        theta1: theta1_value,  
        theta2: theta2_value   
    }

    J_numeric = J.subs(subs)  
    J_numpy = np.array(J_numeric).astype(np.float64)  

    return J_numpy.T  


def generate_torques(theta1_value, theta2_value, Fx, Fy):
    
    forces = np.array([[Fx], [Fy]])  

    Jacobian_T = generate_Jacobian_Transpose(theta1_value, theta2_value)  

    torques = np.dot(Jacobian_T, forces)  

    return torques


#ex_torques = generate_torques(2.3561944902, 0.7853981634, 1.2, 1.2)
#print("Torques:")
#print(ex_torques)
