function [ pose ] = propagatePose( pose, v, w, delta_t )
%create a new pose based on the kinematics of the body of the rover
x = pose(1);
y = pose(2);
theta = pose(3);

%do the translation first then the rotation, this is a fair assumption if
%the timestep is small enough

x = x + v * delta_t * cos(theta);
y = y + v * delta_t * sin(theta);
theta = theta + w * delta_t;
theta = wrapAnglePi(theta);

pose = [x; y; theta];
end

