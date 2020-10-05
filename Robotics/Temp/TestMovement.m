function [ obstacleHit ] = TestMovement( x_move, y_move, RobotLocation, OccupancyMap )
%TestMovement tests a movement before it is actually made.  No movement of
%the robot is accomplished in this function
    newRobotLocation = RobotLocation + [x_move, y_move]';
    if(OccupancyMap(newRobotLocation(2), newRobotLocation(1)) == 1)
        obstacleHit = 1;
    else
        obstacleHit = 0;
    end

end

