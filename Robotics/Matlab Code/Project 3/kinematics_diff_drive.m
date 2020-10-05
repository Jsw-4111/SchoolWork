function [ v, w ] = kinematics_diff_drive( control_vector, P )
%derive the linear velocity and rotational velocity based on the wheel
%speed of the vehicle.

vl = control_vector(1);
vr = control_vector(2);

%TODO fill in the equations for the kinematics to give v and w

v = vl/2 + vr/2;
w = vr/21.5 - vl/21.5;

end

