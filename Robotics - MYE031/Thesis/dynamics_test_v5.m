% Simulation of a complete robotic dynamic system (q1-q4)

T = 20;
dt = 0.001;
t = 0:dt:T;

% Termination options
use_position_condition = true;    % Set to false to stop after a specific time
stop_time = 1;                    % Time in seconds if use_position_condition == false

% Physical parameters
mA = 0.1; mB = 0.1; mC = 0.1;
l1 = 0.08; l2 = 0.12; l3 = 0.12; l4 = 0.08; l0 = 0.08;

% PID gains
Kp = 0.06; Kd = 0.01; Ki = 0.04;

% Option to disable Coriolis terms
ignore_coriolis = false;

% Optional printing
printing = true;

% External forces from the operator
f_ox = 0;  f_oy = 0;

% Initial Conditions
max_steps = length(t);
q = zeros(4, max_steps);
dq = zeros(4, max_steps);
int_err = zeros(2, max_steps);

q(:,1) = [1.5707963268; 0.294515; 2.847077; 1.5707963268];
dq(:,1) = [0; 0; 0; 0];

% Desired angles for q1 and q4
qd = [2.7202; 0.4214];
%qd = [2.3919; 0.0799];

% Desired end-effector position
x_d = 0.0400;
%x_d = 0.0600;
y_d = 0.0731;

% Initialize end-effector arrays
xe = zeros(1, max_steps);
ye = zeros(1, max_steps);
xe(1) = 0.0400;
ye(1) = 0.1931;
error_x = x_d - xe(1);
error_y = y_d - ye(1);

max_inertia_q1 = 0;
max_inertia_q4 = 0;
max_coriolis_q1 = 0;
max_coriolis_q4 = 0;

k = 2;
while true

    q1 = q(1,k-1); q2 = q(2,k-1); q3 = q(3,k-1); q4 = q(4,k-1);
    dq1 = dq(1,k-1); dq2 = dq(2,k-1); dq3 = dq(3,k-1); dq4 = dq(4,k-1);

    % Inertia matrix
    M = [
        0.000853, 0.00024*cos(q1 - q2), 0, 0;
        0.00024*cos(q1 - q2), 0.00048, 0, 0;
        0, 0, 0.00048, 0.00024*cos(q3 - q4);
        0, 0, 0.00024*cos(q3 - q4), 0.000853
    ];

    % Coriolis matrix
    C = [
        0.00024*dq2*sin(q1 - q2), (-0.00024*dq1 + 0.00024*dq2)*sin(q1 - q2), 0, 0;
        (-0.00024*dq1 + 0.00024*dq2)*sin(q1 - q2), -0.00024*dq1*sin(q1 - q2), 0, 0;
        0, 0, -0.00024*dq4*sin(q3 - q4), (0.00024*dq4 - 0.00024*dq3)*sin(q3 - q4);
        0, 0, (0.00024*dq4 - 0.00024*dq3)*sin(q3 - q4), 0.00024*dq3*sin(q3 - q4)
    ];

    if ignore_coriolis
        C = diag(diag(C));
    end

    % PID control (q1 and q4)
    err = [qd(1); qd(2)] - [q(1,k-1); q(4,k-1)];
    int_err(:,k) = int_err(:,k-1) + err * dt;

    if k == 2
        d_err = [0; 0];
    else
        prev_err = [qd(1); qd(2)] - [q(1,k-2); q(4,k-2)];
        d_err = (err - prev_err) / dt;
    end

    tau = [
        Kp*err(1) + Kd*d_err(1) + Ki*int_err(1,k);
        0;
        0;
        Kp*err(2) + Kd*d_err(2) + Ki*int_err(2,k)
    ];

    % Operator torque
    T_h = [
        -f_ox * l1 * sin(q1) + f_oy * l1 * cos(q1);
        -f_ox * l2 * sin(q2) + f_oy * l2 * cos(q2);
        -f_ox * l3 * sin(q3) + f_oy * l3 * cos(q3);
        -f_ox * l4 * sin(q4) + f_oy * l4 * cos(q4)
    ];

    % Static friction
    T_static_friction = -0.016 * sign(dq(:,k-1));

    % Compute accelerations
    q_ddot = M \ (tau + T_h + T_static_friction - C * dq(:,k-1));

    % Integration
    dq(:,k) = dq(:,k-1) + q_ddot * dt;
    q(:,k) = q(:,k-1) + dq(:,k) * dt;

    % Forward Kinematics
    a = ( sin(q(1,k)) * l1 - sin(q(4,k)) * l4 ) / ( l0 + cos(q(4,k)) * l4 - cos(q(1,k)) * l1 );
    b = ( sin(q(4,k))^2 * l4^2 + (l0 + cos(q(4,k)) * l4)^2 - l1^2 + l2^2 - l3^2 ) / ( 2*(l0 + cos(q(4,k)) * l4 - cos(q(1,k)) * l1) );
    c = a^2 + 1;
    d = 2*a*b - 2*a*cos(q(1,k))*l1 - 2*sin(q(1,k))*l1;
    e = b^2 - 2*b*cos(q(1,k))*l1 + l1^2 - l2^2;
    ye(k) = (-d + sqrt(d^2 - 4*c*e)) / (2*c);
    xe(k) = a * ye(k) + b;

    % Error update
    error_x = x_d - xe(k);
    error_y = y_d - ye(k);

    % Forces for analysis
    F_inertia = M * q_ddot;
    F_coriolis = C * dq(:,k-1);
    max_inertia_q1 = max(max_inertia_q1, abs(F_inertia(1)));
    max_inertia_q4 = max(max_inertia_q4, abs(F_inertia(4)));
    max_coriolis_q1 = max(max_coriolis_q1, abs(F_coriolis(1)));
    max_coriolis_q4 = max(max_coriolis_q4, abs(F_coriolis(4)));

    % Optional printing
    if printing

        %fprintf('t = %.3f s | x_e = %.4f m | y_e = %.4f m\n', (k-1)*dt, xe(k), ye(k));
        %fprintf('Inertia torque on q1: %.6f Nm | Coriolis torque on q1: %.6f Nm\n', F_inertia(1), F_coriolis(1));
        %fprintf('Inertia torque on q4: %.6f Nm | Coriolis torque on q4: %.6f Nm\n', F_inertia(4), F_coriolis(4));

        fprintf('t = %.3f s | q1 = %.5f rad | q2 = %.5f rad | q3 = %.5f rad | q4 = %.5f rad\n', t(k-1), q1, q2, q3, q4);
        fprintf('t = %.3f s | sin(q1 - q2) = %.6f | sin(q3 - q4) = %.6f \n', t(k-1), sin(q1 - q2), sin(q3 - q4));
        fprintf('t = %.3f s | dq1 = %.5f rad/s | dq2 = %.5f rad/s | dq3 = %.5f rad/s | dq4 = %.5f rad/s\n', t(k-1), dq1, dq2, dq3, dq4);
        fprintf('t = %.3f s | dq1 - dq2 = %.6f | dq3 - dq4 = %.6f \n', t(k-1), dq1 - dq2, dq3 - dq4);

        % --- Coriolis torque breakdown for q1 ---
        C11 = C(1,1); C12 = C(1,2);
        tau_coriolis_q1_term1 = C11 * dq1;
        tau_coriolis_q1_term2 = C12 * dq2;
        tau_coriolis_q1_total = tau_coriolis_q1_term1 + tau_coriolis_q1_term2;
        fprintf("t = %.3f s | Coriolis q1 breakdown:  Term1 = %.9f  | Term2 = %.9f  | Sum = %.9f Nm\n", t(k-1), tau_coriolis_q1_term1, tau_coriolis_q1_term2, tau_coriolis_q1_total);

        % --- Coriolis torque breakdown for q4 ---
        C43 = C(4,3); C44 = C(4,4);
        tau_coriolis_q4_term1 = C43 * dq3;
        tau_coriolis_q4_term2 = C44 * dq4;
        tau_coriolis_q4_total = tau_coriolis_q4_term1 + tau_coriolis_q4_term2;
        fprintf("t = %.3f s | Coriolis q4 breakdown:  Term1 = %.9f  | Term2 = %.9f  | Sum = %.9f Nm\n\n", t(k-1), tau_coriolis_q4_term1, tau_coriolis_q4_term2, tau_coriolis_q4_total);

        %fprintf("Applied torque q1: %.9f Nm\n", tau(1));
        %fprintf("Applied torque q4: %.9f Nm\n\n", tau(4));

    end

    % Termination condition
    if use_position_condition
        if abs(error_x) <= 0.001 && abs(error_y) <= 0.001
            break;
        end
    else
        if t(k) >= stop_time
            break;
        end
    end

    k = k + 1;
end

fprintf('Max |Inertia q1|: %.10f Nm | Max |Coriolis q1|: %.10f Nm\n', max_inertia_q1, max_coriolis_q1);
fprintf('Max |Inertia q4|: %.10f Nm | Max |Coriolis q4|: %.10f Nm\n\n', max_inertia_q4, max_coriolis_q4);

% Plot
figure;
%plot(t(1:k-1), xe(1:k-1), 'b-', 'LineWidth', 2);
plot(t(1:k-1), dq(1,1:k-1), 'b-', 'LineWidth', 2, 'DisplayName', 'dq1');

hold on;
%plot(t(1:k-1), ye(1:k-1), 'r-', 'LineWidth', 2);
plot(t(1:k-1), dq(4,1:k-1), 'r-', 'LineWidth', 2, 'DisplayName', 'dq2');

xlabel('Time (s)');

%ylabel('Position (m)');
ylabel('Velocity (rad/sec)');
%ylim([-4.5 4.5]);

%legend('x_e', 'y_e');
%legend('Velocity - Theta1', 'Velocity - Theta4');

%title('Simulation - End-Effector Trajectory');
title('Simulation - Angles Velocity Trajectory');

grid minor;

