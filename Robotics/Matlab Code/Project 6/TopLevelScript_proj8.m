clear all;
clear drawRR;
clear drawLinks;

SetupRRParams();
delta_t = .01;
Goal_Loc = [10, 4]';

% guess for the configuration
config_deg = [45, -45]';  %beginning configuration - set to test
%% draw RR
%plot the robot arm and the goal location
drawRR(config_deg, P);
plot(Goal_Loc(1), Goal_Loc(2), 'r*', 'MarkerSize', 5);

omega = [.4 .4];  %sample omega control

for t = 0:delta_t:10 
    
    
    pose_endEffector = forwardKinematics(config_deg, P);
    J = calculateJacobian(config_deg, P);
    J_inv = pinv(J(1:2,:));
    delta_pos = Goal_Loc - pose_endEffector(1:2);
    disToGoal = norm(delta_pos);

    %TODO create a control strategy to move the robot end effector to the
    %goal location -- replace the following example code.
    if(config_deg(1) > 90)
        omega = [-.4 .4];
    elseif(config_deg(1) < 0)
        omega = [.4 .4];
    end
    
    %End TODO if you put your motor speeds in the omega row vector [omega(1), omega(2)]
    config_next = config_deg + rad2deg(delta_t*omega');
    config_deg = config_next;
    drawRR(config_deg, P);
    if(disToGoal < .1)
        break;
    end
    
    pause(delta_t);
end 