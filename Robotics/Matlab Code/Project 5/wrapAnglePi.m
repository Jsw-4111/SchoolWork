function [ angle ] = wrapAnglePi( angle )
%UNTITLED2 Summary of this function goes here
%   Detailed explanation goes here
while(angle > pi)
    angle = angle - 2*pi;
end
while(angle < -pi)
    angle = angle + 2*pi;
end

end

