%We will use this script to set the rover parameters in one place to be
%used by all the functions and scripts.

P.chasis_width = 18;
P.chasis_forward = 20;
P.chasis_backward = -2;
P.wheel_base = 21;
P.half_wheel_base = P.wheel_base/2;
P.wheel_width = 2.5;
P.wheel_radius = 4.5;
P.lidar_forward = 16;
P.lidar_radius = 3.5;

setupSimParams();