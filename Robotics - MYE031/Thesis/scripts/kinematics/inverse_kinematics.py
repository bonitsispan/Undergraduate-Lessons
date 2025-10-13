import sympy as sym 
from sympy import sin, cos, sqrt, acos, pi
import math

l0 = sym.Symbol('l0')
l1 = sym.Symbol('l1')
l2 = sym.Symbol('l2')
l3 = sym.Symbol('l3')
l4 = sym.Symbol('l4')

theta1 = sym.Symbol('theta1')
theta2 = sym.Symbol('theta2')

xe = sym.Symbol('xe')
ye = sym.Symbol('ye')

a = acos( (xe) / (sqrt(xe**2 + ye**2)) )
b = acos( (l1**2 - l2**2 + xe**2 + ye**2) / (2*l1*sqrt(xe**2 + ye**2)) )
c = acos( (l0 - xe) / (sqrt((xe - l0)**2 + ye**2)) )
d = acos( (xe**2 + ye**2 + l0**2 + l4**2 - l3**2 - 2*xe*l0) / (2*sqrt((xe - l0)**2 + ye**2)*l4) )

theta1 = a + b
theta2 = pi - (c + d)

subs = {
    l0: 0.08,
    l1: 0.08,
    l2: 0.12,
    l3: 0.12,
    l4: 0.08,
    xe: 0.04,
    ye: 0.1031
}

print(f"\ntheta1 = {theta1}")
print(f"\ntheta2 = {theta2}\n")

theta1_value_rad = theta1.subs(subs).evalf()
theta2_value_rad = theta2.subs(subs).evalf()
theta1_value_deg = math.degrees(theta1_value_rad)
theta2_value_deg = math.degrees(theta2_value_rad)

print(f"theta1: {theta1_value_rad} rad = {theta1_value_deg} deg")
print(f"theta2: {theta2_value_rad} rad = {theta2_value_deg} deg\n")

