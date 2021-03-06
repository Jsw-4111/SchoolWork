function [ Jac ] = calculateJacobian( config, P )
%calculateJacobian takes as inputs the configuration and parameters P and
%produces the jacobian for the RR robot.

Jac = [-P.l1*sind(config(1))-P.l2*sind(config(1)+config(2)), -P.l2*sind(config(1)+config(2));...
    P.l1*cosd(config(1))+P.l2*cosd(config(1)+config(2)), P.l2*cosd(config(1)+config(2));...
    1 1];

end

