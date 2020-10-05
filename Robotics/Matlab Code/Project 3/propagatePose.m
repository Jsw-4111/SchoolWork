function [ pose ] = propagatePose( pose, v, w, delta_t )
%create a new pose based on the kinematics of the body of the rover

%unpack pose
x = pose(1);
y = pose(2);
theta = pose(3);

%TODO - propagate the pose of the vehicle by doing the translation first 
%then the rotation, this is a fair assumption if the timestep is small enough

x = v*delta_t*cos(theta) + x;
y = v*delta_t*sin(theta) + y;
theta = (theta + w*delta_t);

%return new pose
pose = [x; y; theta];
end

