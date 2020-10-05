function [ state ] = FollowCircle( state, circleToFollow, lambda, time, P )
%FollowCircle inputs:
% state of the vehicle at the beginning
% circle you wish the vehicle to follow
% the direction (lambda) = -1 for clockwise, 1 for clockwise
% the time to follow the circle
% the parameters set P
%outputs are:  the state after the time has elapsed.
    

    %set parameters
    v = P.v_const*.8;
    
    %unpack pose
    pose = state(1:3);
    
    for t = 0:P.delta_t:time
        %TODO - define a function which sets the angular velocity 'w' based
        %on the position of the vehicle in relation to the circle.
        kd = .5;
        d = sqrt((pose(1)-circleToFollow(1))^2+(pose(2)-circleToFollow(2))^2)-circleToFollow(3);
        thetaCB = atan2(pose(2) - circleToFollow(2), pose(1) - circleToFollow(1));
        theta_c = thetaCB + lambda * (pi/2+atan(kd*d));
        w = wrapAnglePi(theta_c - pose(3))*2;  %Replace this line

        pose = propagatePose(pose, v, w, P.delta_t);
        state = [pose; v; w; state(6)+P.delta_t];
        drawCar(state, P);
    end

end

