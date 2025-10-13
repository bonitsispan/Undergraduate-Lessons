%% --- Force Control Simulation with Moving Hand and Dynamic Desired Force ---

clear; clc; close all;

% Simulation time
T = 5;
dt = 0.001;
t = 0:dt:T;

% Spring and damping parameters
k_e = [100; 100];  % Hand spring constants
b_e = [10; 10];    % Hand damping constants
k_des = [8; 8];    % Desired force spring
b_des = [5; 5];    % Desired force damping

% Geometry
l1 = 0.08; l2 = 0.12; l3 = 0.12; l4 = 0.08; l0 = 0.08;

% Initial joint conditions
q = zeros(4, length(t));
dq = zeros(4, length(t));

q(:,1) = [2.53011829042451; 0.620103; 2.521490; 0.611474363165284];
dq(:,1) = [0; 0; 0; 0];

% Hand initial state
x_ref = 0.04;
y_ref = 0.1031;
x_hand = x_ref;
y_hand = y_ref;
v_hand = [0; -0.01];

% Storage arrays
xe_arr = zeros(1, length(t));
ye_arr = zeros(1, length(t));
v_e_arr = zeros(2, length(t));
f_e_arr = zeros(2, length(t));
F_control_arr = zeros(2, length(t));
F_applied_arr = zeros(2, length(t));
tau1_arr = zeros(1, length(t));
tau4_arr = zeros(1, length(t));

int_e_force = zeros(2, length(t));
d_e_force = zeros(2, length(t));
I_term_arr = zeros(2, length(t));

x_hand_arr = zeros(1, length(t));
y_hand_arr = zeros(1, length(t));
delta_hand_arr = zeros(2, length(t));
delta_desired_arr = zeros(2, length(t));
f_h_arr = zeros(2, length(t));

for k = 2:length(t)
    q1 = q(1,k-1); q2 = q(2,k-1); q3 = q(3,k-1); q4 = q(4,k-1);

    % Update hand position
    x_hand = x_hand + v_hand(1)*dt;
    y_hand = y_hand + v_hand(2)*dt;

    x_hand_arr(k) = x_hand;
    y_hand_arr(k) = y_hand;

    % Inertia matrix
    M = [
        0.000853, 0.00024*cos(q1 - q2), 0, 0;
        0.00024*cos(q1 - q2), 0.00048, 0, 0;
        0, 0, 0.00048, 0.00024*cos(q3 - q4);
        0, 0, 0.00024*cos(q3 - q4), 0.000853];

    % Coriolis matrix
    dq1 = dq(1,k-1); dq2 = dq(2,k-1); dq3 = dq(3,k-1); dq4 = dq(4,k-1);
    C = [
        0.00024*dq2*sin(q1 - q2), (-0.00024*dq1 + 0.00024*dq2)*sin(q1 - q2), 0, 0;
        (-0.00024*dq1 + 0.00024*dq2)*sin(q1 - q2), -0.00024*dq1*sin(q1 - q2), 0, 0;
        0, 0, -0.00024*dq4*sin(q3 - q4), (0.00024*dq4 - 0.00024*dq3)*sin(q3 - q4);
        0, 0, (0.00024*dq4 - 0.00024*dq3)*sin(q3 - q4), 0.00024*dq3*sin(q3 - q4)];

    % Kinematics
    [xe, ye, jacobian] = fivebar_kinematics(q1, q4, l0, l1, l2, l3, l4);
    xe_arr(:,k) = xe;
    ye_arr(:,k) = ye;

    v_e_now = jacobian * [dq(1,k-1); dq(4,k-1)];
    v_e_arr(:,k) = v_e_now;

    % Relative extension and velocity
    delta_x = xe - x_hand;
    delta_y = ye - y_hand;
    delta_hand_arr(:,k) = [delta_x; delta_y];
    delta_vx = v_e_now(1) - v_hand(1);
    delta_vy = v_e_now(2) - v_hand(2);

    % Force on end-effector from spring
    f_e = k_e .* [delta_x; delta_y];
    f_e_arr(:,k) = f_e;

    % Desired force calculation
    F_control_desired = - k_des .* ([xe; ye] - [x_ref; y_ref]) - b_des .* v_e_now;

    %F_control_desired = [0; 1.5];

    %Fy_ramp = 0.05 * t(k);  % Adjust slope as needed
    %Fx_ramp = 0;
    #{
    if t(k) < 1
        Fy_ramp = 0;
    else
        Fy_ramp = 0.2 * (t(k) - 1);
    end
    #}
    %F_control_desired = [Fx_ramp; Fy_ramp];

    delta_desired_arr(:,k) = [xe - x_ref; ye - y_ref];
    F_control_arr(:,k) = F_control_desired;

    % PID Force Control
    error_force = F_control_desired - f_e;
    int_e_force(:,k) = int_e_force(:,k-1) + error_force * dt;
    d_e_force(:,k) = (error_force - (F_control_desired - f_e_arr(:,k-1))) / dt;

    Kp_force = diag([25; 25]);
    Ki_force = diag([1000; 1000]);
    Kd_force = diag([0.2; 0.2]);
    F_control_applied = Kp_force * error_force + Ki_force * int_e_force(:,k) + Kd_force * d_e_force(:,k);
    %F_control_applied = Kp_force * error_force + Ki_force * int_e_force(:,k) + Kd_force * d_e_force(:,k) + F_control_desired;

    I_term = Ki_force * int_e_force(:,k);
    I_term_arr(:,k) = I_term;


    % Disturbance
    alpha = 0.6;

    F_applied_arr(:,k) = F_control_applied;

    % Torque calculation
    tau = jacobian' * F_control_applied;
    tau_full = [tau(1); 0; 0; tau(2)];
    tau_full = alpha * tau_full;
    tau1_arr(k) = tau_full(1);
    tau4_arr(k) = tau_full(4);


    f_h = -k_e .* [delta_x; delta_y] - b_e .* [delta_vx; delta_vy];
    f_h_arr(:,k) = f_h;
    tau_h = jacobian' * f_h;
    T_h = [tau_h(1); 0; 0; tau_h(2)];

    #{
    T_h = [
        -f_h(1) * 0.08 * sin(q1) + f_h(2) * 0.08 * cos(q1);
        -f_h(1) * 0.12 * sin(q2) + f_h(2) * 0.12 * cos(q2);
        -f_h(1) * 0.12 * sin(q3) + f_h(2) * 0.12 * cos(q3);
        -f_h(1) * 0.08 * sin(q4) + f_h(2) * 0.08 * cos(q4)
    ];
    #}

    q_ddot = M \ (tau_full + T_h - C * dq(:,k-1));

    % State integration
    dq(:,k) = dq(:,k-1) + q_ddot * dt;
    q(:,k) = q(:,k-1) + dq(:,k) * dt;
end

figure;
plot(t, xe_arr * 100, 'b', 'LineWidth', 2); hold on;
plot(t, ye_arr * 100, 'r', 'LineWidth', 2);
xlabel('Time (s)');
ylabel('Position (cm)');
xlim([0 2]);
legend('x_e', 'y_e', 'Location', 'northeast');
title('End-Effector Position (in cm)');
grid on;


% 1. Hand Position (cm)
figure;
plot(t, x_hand_arr * 100, 'b', 'LineWidth', 2); hold on;
plot(t, y_hand_arr * 100, 'r', 'LineWidth', 2);
xlabel('Time (s)');
ylabel('Position (cm)');
xlim([0 2]);
legend('x_{hand}', 'y_{hand}');
title('Hand Position');
grid on;


figure;
plot(t, f_h_arr(1,:), 'b', 'LineWidth', 2); hold on;
plot(t, f_h_arr(2,:), 'r', 'LineWidth', 2);
xlabel('Time (s)');
ylabel('Force (N)');
legend('f_{hx}', 'f_{hy}');
title('Force by Hand on Mechanism');
grid on;


% 4. Relative Extension: Hand to End-Effector (cm)
figure;
plot(t, delta_hand_arr(1,:) * 100, 'b', 'LineWidth', 2); hold on;
plot(t, delta_hand_arr(2,:) * 100, 'r', 'LineWidth', 2);
xlabel('Time (s)');
ylabel('Extension (cm)');
legend('\Delta x_{hand}', '\Delta y_{hand}');
title('Spring Extension: Hand to End-Effector');
grid on;


% 5. Relative Extension: End-Effector to Reference (cm)
figure;
plot(t, delta_desired_arr(1,:) * 100, 'b', 'LineWidth', 2); hold on;
plot(t, delta_desired_arr(2,:) * 100, 'r', 'LineWidth', 2);
xlabel('Time (s)');
ylabel('Extension (cm)');
legend('\Delta x_{ref}', '\Delta y_{ref}');
title('Spring Extension: End-Effector to Reference');
grid on;



figure;
plot(t, f_e_arr(2,:), 'b--', 'LineWidth', 1.5); hold on;
plot(t, F_control_arr(2,:), 'r', 'LineWidth', 2);
xlabel('Time (s)');
ylabel('Force (N)');
xlim([0 2]);
ylim([0 0.3]);
legend('F_{D}(desired)', 'F_{h} (Spring Force)');
title('Desired vs Applied Spring Force (Y Axis)');
grid on;


error_force_y_arr = F_control_arr(2,:) - f_e_arr(2,:);
figure;
plot(t, error_force_y_arr, 'm', 'LineWidth', 2);
xlabel('Time (s)');
ylabel('Force Error (N)');
xlim([0 2]);
title('Force Error on Y Axis');
legend('e (force error)');
grid on;

figure;
plot(t, F_applied_arr(1,:), 'b', 'LineWidth', 1.5); hold on;
plot(t, F_applied_arr(2,:), 'r', 'LineWidth', 2); hold on;
xlabel('Time (s)');
ylabel('Force (N)');
legend('Fx', 'Fy');
title('Applied Force of End-Effector point');
grid on;


figure;
plot(t, I_term_arr(1,:), 'b', 'LineWidth', 2); hold on;
plot(t, I_term_arr(2,:), 'r', 'LineWidth', 2);
xlabel('Time (s)');
ylabel('Force Contribution (N)');
title('Integral Term of Force Controller');
legend('I_{force,x}', 'I_{force,y}');
grid on;
