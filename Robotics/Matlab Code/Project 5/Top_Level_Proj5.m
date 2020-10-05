setupRoverParams();

%simulation initilization
pose = [0; 0; deg2rad(35)]; %intial pose vector [x, y, theta]; cm and radians are the units
state = [pose; 0; 0; 0];  %initialization state
drawCar(state, P);  %initialize drawing


%Starting at the initial pose follow the waypoints in W in order from 1 to
%4 in order given a constant velocity transitioning using the half plane method.
% Also determine when the path is done (w4) is achieved and end the simulation. 
W1 = [-400; 400];
W2 = [400; 400];
W3 = [-400; -400];
W4 = [400; -400];
W = [W1 W2 W3 W4];

%Draw Waypoints and connecting lines, also use this as an example for
%defining the lines for your line following algorithms.
lineToFollow = zeros(3,4);
lineToFollow(:,1) = [state(1:2);atan2(W(2,1)-state(2), W(1,1)-state(1))];
drawLine(lineToFollow(:,1));

for i = 2:size(W, 2)
    lineToFollow(:,i) = [W(1,i-1); W(2,i-1); atan2(W(2,i)-W(2,i-1), W(1,i)-W(1,i-1))];
    drawLine(lineToFollow(:,i));
end
plot(W(1,:), W(2,:), 'r*');

%This is a switch you will need to set manually for the different runs.
if(1)
    state = WaypointController_throughPoint(state, W, P);
else
    state = WaypointController_roundCorner(state, W, P);
end
