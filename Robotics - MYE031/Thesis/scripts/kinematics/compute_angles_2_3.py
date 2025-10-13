import math

# Given values
l1 = l5 = l4 = 0.08
l2 = l3 = 0.12
theta1 = theta4 = 1.5707963268  # in rad (90 degrees)
#theta1 = 2.53011829042451
#theta4 = 0.611474363165284

# Compute intermediate variables A, B, C
A = 2 * l3 * l4 * math.sin(theta4) - 2 * l1 * l3 * math.cos(theta1)
B = 2 * l3 * l5 - 2 * l1 * l3 * math.cos(theta1) + 2 * l4 * l3 * math.cos(theta4)

C = (l1**2 - l2**2 + l3**2 + l4**2 + l5**2
     - l1 * l4 * math.sin(theta1) * math.sin(theta4)
     - 2 * l1 * l5 * math.cos(theta1)
     - 2 * l4 * l5 * math.cos(theta4)
     - 2 * l4 * l1 * math.cos(theta4) * math.cos(theta1))

# Compute theta3 (using the positive root)
theta3_rad = 2 * math.atan((A + math.sqrt(A**2 + B**2 - C**2)) / (B - C))

# Compute theta2
numerator = (l3 * math.sin(theta3_rad) + l4 * math.sin(theta4) - l1 * math.sin(theta1))
theta2_rad = math.asin(numerator / l2)

# Convert to degrees
theta3_deg = math.degrees(theta3_rad)
theta2_deg = math.degrees(theta2_rad)

# Print results
print(f"theta3 = {theta3_rad:.6f} rad  |  {theta3_deg:.2f}°")
print(f"theta2 = {theta2_rad:.6f} rad  |  {theta2_deg:.2f}°")
