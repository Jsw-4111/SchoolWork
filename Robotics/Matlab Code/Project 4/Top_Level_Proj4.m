setupRoverParams();

%simulation initilization
pose = [100; 200; deg2rad(35)]; %intial pose vector [x, y, theta]; cm and radians are the units
state = [pose; 0; 0; 0];  %initialization state
drawCar(state, P);  %initialize drawing


%Follow the line given for 10 seconds
lineToFollow = [0; 0; deg2rad(120)]; %line vector with initial point (cm) and heading angle (radians) of the line frame
drawLine(lineToFollow);
state = FollowLine(state, lineToFollow, 10, P)

%then after that follow a circle circleToFollow in a counter clockwise direction for 20 seconds of
%simulations
%reset and draw the circle in the figure
circleToFollow = [state(1); state(2); 200]; %circle to follow with center point at current state and radius (all cm)
drawCar([state(1:5);0], P);
drawCircle(circleToFollow, 'r');
%perform the manuvuer
state = FollowCircle(state, circleToFollow, 1, 20, P)


%then turn around and follow the circle in a clockwise direction for 20 seconds of
%simulations
%reset the figure with a black circle ('k')
drawCircle(circleToFollow, 'k');
%perform the manuveur
state = FollowCircle(state, circleToFollow, -1, 20, P)