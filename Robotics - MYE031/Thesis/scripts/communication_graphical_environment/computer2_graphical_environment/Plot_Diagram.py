import pandas as pd
import matplotlib.pyplot as plt

# Read data from the force log file
force_data = pd.read_csv(r"C:\Users\KonKargas\Desktop\DIPLOMA\Diploma_4\force_log.txt", sep=",", names=["Time", "Fx", "Fz"])

# Clean and convert force data
force_data["Time"] = force_data["Time"].str.extract(r"(-?[0-9.]+)").astype(float)
force_data["Fx"] = force_data["Fx"].str.extract(r"(-?[0-9.]+)").astype(float)
force_data["Fz"] = force_data["Fz"].str.extract(r"(-?[0-9.]+)").astype(float)

# Read data from the velocity log file
velocity_data = pd.read_csv(r"C:\Users\KonKargas\Desktop\DIPLOMA\Diploma_4\velocity_log.txt", sep=",", names=["Time", "deltaX", "deltaZ"])

# Clean and convert velocity data
velocity_data["Time"] = velocity_data["Time"].str.extract(r"(-?[0-9.]+)").astype(float)
velocity_data["deltaX"] = velocity_data["deltaX"].str.extract(r"(-?[0-9.]+)").astype(float)
velocity_data["deltaZ"] = velocity_data["deltaZ"].str.extract(r"(-?[0-9.]+)").astype(float)

# Read data from the position log file
position_data = pd.read_csv(r"C:\Users\KonKargas\Desktop\DIPLOMA\Diploma_4\position_log.txt", sep=",", names=["Time", "PosX", "PosZ"])

# Clean and convert position data
position_data["Time"] = position_data["Time"].str.extract(r"(-?[0-9.]+)").astype(float)
position_data["PosX"] = position_data["PosX"].str.extract(r"(-?[0-9.]+)").astype(float)
position_data["PosZ"] = position_data["PosZ"].str.extract(r"(-?[0-9.]+)").astype(float)

# Convert Series to numpy arrays
force_time = force_data["Time"].to_numpy()
fx = force_data["Fx"].to_numpy()
fz = force_data["Fz"].to_numpy()

velocity_time = velocity_data["Time"].to_numpy()
delta_x = velocity_data["deltaX"].to_numpy()
delta_z = velocity_data["deltaZ"].to_numpy()

position_time = position_data["Time"].to_numpy()
pos_x = position_data["PosX"].to_numpy()
pos_z = position_data["PosZ"].to_numpy()

# Plot the data in 3 subplots
plt.figure(figsize=(12, 10))

# Subplot 1: Forces over time
plt.subplot(3, 1, 1)
plt.plot(force_time, fx, label="Fx (N)", color="b")
plt.plot(force_time, fz, label="Fz (N)", color="r")
plt.xlabel("Time (s)")
plt.ylabel("Force (N)")
plt.title("Forces Over Time")
plt.legend()
plt.grid(True)

# Subplot 2: Velocities over time
plt.subplot(3, 1, 2)
plt.plot(velocity_time, delta_x, label="deltaX (m/s)", color="g")
plt.plot(velocity_time, delta_z, label="deltaZ (m/s)", color="orange")
plt.xlabel("Time (s)")
plt.ylabel("Velocity (m/s)")
plt.title("Velocities Over Time")
plt.legend()
plt.grid(True)

# Subplot 3: Positions over time
plt.subplot(3, 1, 3)
plt.plot(position_time, pos_x, label="PosX (m)", color="purple")
plt.plot(position_time, pos_z, label="PosZ (m)", color="brown")
plt.xlabel("Time (s)")
plt.ylabel("Position (m)")
plt.title("Positions Over Time")
plt.legend()
plt.grid(True)

# Adjust layout and show the plot
plt.tight_layout()
plt.show()