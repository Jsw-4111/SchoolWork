function [ pose ] = forwardKinematics( config, P )
%forwardKinematics takes in the configuration and parameters P and produces
%the pose of the end effector.

pose = [P.l1*cosd(config(1))+P.l2*cosd(config(1)+config(2));...
    P.l1*sind(config(1))+P.l2*sind(config(1)+config(2));...
    config(1)+config(2)];

end

