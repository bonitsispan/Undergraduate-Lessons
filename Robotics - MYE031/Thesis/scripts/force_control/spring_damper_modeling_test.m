%% --- Force Control Simulation on the Robotic System (Improved Version with Axis-Specific PID Gains) ---

clear; clc; close all;

% Simulation time
T = 1;
dt = 0.001;
t = 0:dt:T;

% Spring parameter
k_e = [100; 100];        % Spring constant [N/m]

l1 = 0.08; l2 = 0.12; l3 = 0.12; l4 = 0.08; l0 = 0.08;

% Initial conditions
q = zeros(4, length(t));
dq = zeros(4, length(t));
int_e_f = zeros(2, length(t));
d_e_f = zeros(2, length(t));

q(:,1) = [2.53011829042451; 0.620103; 2.521490; 0.611474363165284];
dq(:,1) = [0; 0; 0; 0];

% Data storage arrays
xe_arr = zeros(1, length(t));
ye_arr = zeros(1, length(t));
v_e_arr = zeros(2, length(t));
f_e_arr = zeros(2, length(t));
F_control_arr = zeros(2, length(t));
tau1_arr = zeros(1, length(t));
tau4_arr = zeros(1, length(t));

x_ref = 0.04;
y_ref = 0.1031;

for k = 2:length(t)

    q1 = q(1,k-1); q2 = q(2,k-1); q3 = q(3,k-1); q4 = q(4,k-1);

    % Inertia matrix
    M = [
        0.000853, 0.00024*cos(q1 - q2), 0, 0;
        0.00024*cos(q1 - q2), 0.00048, 0, 0;
        0, 0, 0.00048, 0.00024*cos(q3 - q4);
        0, 0, 0.00024*cos(q3 - q4), 0.000853
    ];

    % Coriolis matrix
    dq1 = dq(1,k-1); dq2 = dq(2,k-1); dq3 = dq(3,k-1); dq4 = dq(4,k-1);
    C = [
        0.00024*dq2*sin(q1 - q2), (-0.00024*dq1 + 0.00024*dq2)*sin(q1 - q2), 0, 0;
        (-0.00024*dq1 + 0.00024*dq2)*sin(q1 - q2), -0.00024*dq1*sin(q1 - q2), 0, 0;
        0, 0, -0.00024*dq4*sin(q3 - q4), (0.00024*dq4 - 0.00024*dq3)*sin(q3 - q4);
        0, 0, (0.00024*dq4 - 0.00024*dq3)*sin(q3 - q4), 0.00024*dq3*sin(q3 - q4)
    ];

    % Kinematics
    [xe, ye, jacobian] = fivebar_kinematics(q1, q4, l0, l1, l2, l3, l4);
    xe_arr(:,k) = xe;
    ye_arr(:,k) = ye;

    % Measured spring force
    f_e = k_e .* ([xe; ye] - [x_ref; y_ref]);
    f_e_arr(:,k) = f_e;

    % Add torque due to spring force reaction (force from spring on end-effector)
    tau_spring = jacobian' * (-f_e);
    tau_spring_full = [tau_spring(1); 0; 0; tau_spring(2)];

    % End-effector velocity
    v_e_now = jacobian * [dq1; dq4];
    v_e_arr(:,k) = v_e_now;

    % End Effector Point Force
    F_control = [0; 1.5];
    F_control_arr(:,k) = F_control;

    % Joint torques via Jacobian transpose
    tau = jacobian' * F_control;
    tau_full = [tau(1); 0; 0; tau(2)];

    % Torque saturation
    tau_full = max(min(tau_full, 0.12), -0.12);

    % Store applied torques
    tau1_arr(k) = tau_full(1);
    tau4_arr(k) = tau_full(4);

    % Operator force
    f_ox = 0; f_oy = 0;

    T_h = [
        -f_ox * 0.08 * sin(q1) + f_oy * 0.08 * cos(q1);
        -f_ox * 0.12 * sin(q2) + f_oy * 0.12 * cos(q2);
        -f_ox * 0.12 * sin(q3) + f_oy * 0.12 * cos(q3);
        -f_ox * 0.08 * sin(q4) + f_oy * 0.08 * cos(q4)
    ];

    % Cartesian damping force on end-effector
    B_cart = diag([5, 5]);
    F_damping = -B_cart * v_e_now;
    tau_damping = jacobian' * F_damping;
    tau_damping_full = [tau_damping(1); 0; 0; tau_damping(2)];
    q_ddot = M \ (tau_full + tau_spring_full + tau_damping_full + T_h - C * dq(:,k-1));

    % State integration
    dq(:,k) = dq(:,k-1) + q_ddot * dt;
    q(:,k) = q(:,k-1) + dq(:,k) * dt;

end

%% --- Figure 1: Applied Force by End-Effector in Y Axis (F_control) ---
figure;
plot(t, F_control_arr(2,:), 'b', 'LineWidth', 2); hold on;
xlabel('Time (s)');
ylabel('Force (N)');
title('Applied Force by End-Effector (Y Axis)');
grid on;


%% --- Figure 2: Spring Reaction Force in Y Axis (f_e) ---
figure;
plot(t, f_e_arr(2,:), 'r', 'LineWidth', 2); hold on;
xlabel('Time (s)');
ylabel('Force (N)');
title('Spring Force on End-Effector (Y Axis)');
grid on;


%% --- Figure 3: End-Effector Position (X and Y) in cm ---
figure;
plot(t, xe_arr * 100, 'b', 'LineWidth', 2); hold on;
plot(t, ye_arr * 100, 'r', 'LineWidth', 2);
xlabel('Time (s)');
ylabel('Position (cm)');
legend('x_e', 'y_e', 'Location', 'northeast');
title('End-Effector Position (in cm)');
grid on;


%% --- Figure 4: End-Effector Velocity (X and Y) in m/s ---
figure;
plot(t, v_e_arr(1,:) * 100, 'b', 'LineWidth', 2); hold on;
plot(t, v_e_arr(2,:) * 100, 'r', 'LineWidth', 2);
xlabel('Time (s)');
ylabel('Velocity (cm/s)');
legend('v_{e,x}', 'v_{e,y}', 'Location', 'northeast');
title('End-Effector Velocity (X and Y)');
grid on;


%% --- Figure 5: Instantaneous Position Change in cm ---
delta_xe_inst = [0, diff(xe_arr)];
delta_ye_inst = [0, diff(ye_arr)];

figure;
plot(t, delta_xe_inst * 100, 'b', 'LineWidth', 2); hold on;
plot(t, delta_ye_inst * 100, 'r', 'LineWidth', 2);
xlabel('Time (s)');
ylabel('Δ Position per time step (cm)');
legend('\Delta x_e(t)', '\Delta y_e(t)', 'Location', 'best');
title('Instantaneous Change in End-Effector Position (in cm)');

grid on;
