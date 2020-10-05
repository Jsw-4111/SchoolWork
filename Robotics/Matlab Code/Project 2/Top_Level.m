x = 2;
y = 3;
theta = 90*pi/180;
V = 5;
theta_dot = 90*pi/180;
t = 0;

state = [x, y, theta, V, theta_dot, t];
drawCar(state);

%do some rotation of the vehilce
for i = 1:100
    theta = theta+theta_dot;
    state = [x, y, theta, V, theta_dot, t];
    drawCar(state);
    pause(.05);
end

%do some translation of the vehicle
for i = 1:100
    x = x + cos(theta) * V;
    y = y + sin(theta) * V;
    state = [x, y, theta, V, theta_dot, t];
    drawCar(state);
    pause(.05);
end
