import sympy as sym
from sympy import diff, sin, cos, sqrt

theta1 = sym.Symbol('theta1')
theta2 = sym.Symbol('theta2')
theta1_dot = sym.Symbol('theta1_dot')
theta2_dot = sym.Symbol('theta2_dot')

l0 = sym.Symbol('l0')
l1 = sym.Symbol('l1')
l2 = sym.Symbol('l2')
l3 = sym.Symbol('l3')
l4 = sym.Symbol('l4')

xe = sym.Symbol('xe')
ye = sym.Symbol('ye')
xe_dot = sym.Symbol('xe_dot')
ye_dot = sym.Symbol('ye_dot')


a = ( ( sin(theta1)*l1 - sin(theta2)*l4 ) / ( l0 + cos(theta2)*l4 - cos(theta1)*l1 ) )

b = ( ( (sin(theta2))**2 * l4**2 + (l0 + cos(theta2)*l4)**2 - l1**2 + l2**2 - l3**2 ) / ( 2*(l0 + cos(theta2)*l4 - cos(theta1)*l1) ) )

c = ( (a**2) + 1 )

d = ( (2*a*b) - (2*a*cos(theta1)*l1) - (2*sin(theta1)*l1) )

e = ( b**2 - 2*b*cos(theta1)*l1 + l1**2 - l2**2 )

ye = ( ( -d + sqrt(d**2 - 4*c*e) ) / ( 2*c ) )

xe = ( (a*ye) + b )

f00 = diff(xe,theta1)
f10 = diff(ye,theta1)

f01 = diff(xe,theta2)
f11 = diff(ye,theta2)

xe_dot = f00*theta1_dot + f01*theta2_dot
ye_dot = f10*theta1_dot + f11*theta2_dot

print("\n ---- Jacobian Matrix (Arithmetic) ---- \n\n")
print(" ---- Element 00 ---- \n\n")
print(f00)
print("\n\n ---- Element 01 ---- \n\n")
print(f01)
print("\n\n ---- Element 10 ---- \n\n")
print(f10)
print("\n\n ---- Element 11 ---- \n\n")
print(f11)
print("\n\n")

print(f"xe_dot =\n\n {xe_dot}\n")
print(f"ye_dot =\n\n {ye_dot}\n")

subs = {
    l0: 0.08,
    l1: 0.08,
    l2: 0.12,
    l3: 0.12,
    l4: 0.08,
    theta1: 2.3561944902, 
    theta2: 0.7853981634,  
    theta1_dot: -0.6981317008,
    theta2_dot: 0.6981317008
}

xe_dot_value = xe_dot.subs(subs)
ye_dot_value = ye_dot.subs(subs)

print(f"xe_dot: {xe_dot_value}")
print(f"ye_dot: {ye_dot_value}\n")
