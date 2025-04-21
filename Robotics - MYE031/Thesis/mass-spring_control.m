% Simulation parameters
T = 5;             % total simulation time [s]
dt = 0.001;        % time step [s]
t = 0:dt:T;

% System parameters
m = 1.0;           % mass [kg]
k_e = 100;         % spring constant [N/m]

% PID controller gains
k_pf = 40;     % proportional gain
k_vf = 20;     % derivative gain
k_if = 5;     % integral gain

% Desired force input (step)
f_D = 5 * ones(size(t));

% External disturbance force (step at 2s)
f_d = zeros(size(t));
f_d(t >= 2) = 2;   % disturbance of 2 N

% Initialize variables
x = zeros(size(t));         % position
v = zeros(size(t));         % velocity
f = zeros(size(t));         % applied force
f_e = zeros(size(t));       % measured spring force
e_f = zeros(size(t));       % force error
int_e_f = zeros(size(t));   % integral of error

% Simulation loop
for k = 2:length(t)
    f_e(k-1) = k_e * x(k-1);                     % spring force
    e_f(k-1) = f_D(k-1) - f_e(k-1);              % force error

    % Integrate the error
    int_e_f(k) = int_e_f(k-1) + e_f(k-1) * dt;

    % PID control law
    f(k) = m * (k_pf * (1/k_e) * e_f(k-1) + k_if * int_e_f(k) - k_vf * v(k-1) ) + f_D(k-1);

    % System dynamics: f = m*a + k_e*x + f_d => a = (f - k_e*x - f_d) / m
    a = (f(k) - k_e * x(k-1) - f_d(k-1)) / m;

    % Euler integration for velocity and position
    v(k) = v(k-1) + a * dt;
    x(k) = x(k-1) + v(k) * dt;
end

% Final values
f_e(end) = k_e * x(end);
e_f(end) = f_D(end) - f_e(end);

% Plots
figure;
subplot(4,1,1);
plot(t, f_D, 'b', 'LineWidth', 2.5, t, f_e, 'g', 'LineWidth', 2.5);
legend('f_D (desired)', 'f_e (measured)');
ylabel('Force (N)');
title('PID Force Control in a Mass-Spring System');
grid on;

subplot(4,1,2);
plot(t, e_f, 'm', 'LineWidth', 2.5);
ylabel('Force Error (N)');
xlabel('Time (s)');
grid on;

subplot(4,1,3);
plot(t, x, 'r', 'LineWidth', 2.5);
ylabel('Position (m)');
grid on;

subplot(4,1,4);
plot(t, f, 'k', 'LineWidth', 2.5);
ylabel('Applied Force (N)');
grid on;

