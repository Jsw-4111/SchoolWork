%This is the top level script used to build the point robot navigation
%example for project 1.  Let's jump right in!

%Some matlab cleanup work here.
clear all
clc
scaling = 10;

%The first thing we want to do is to load the map.  It is called an
%"occupancy map" but don't worry about that yet.  Just know it is filled with
%1's and 0's (with 1 signifying an obstacle or wall and 0 signifing free
%space).  So, we can only move in free space, mkay.
load('OccupancyMap_v2.mat');

%The goal is where we are trying to get to.
GoalLocation = [5, 5]';

%The path length variable is where we will store the number of moves 
%needed to reach the path
pathLength = 0;

%This variable will store whether a obstacle is encountered on the
%attempted move.  0 for no obstacle 1 for obstacle
obstacleEncountered = 0;


%Begin the plotting routines by plotting the base map.
PlotOccupancyMap(OccupancyMap, GoalLocation);

%This is the RobotStartingLocation, I will provide this for the competition
RobotLocation = [98,98]';
%Draw the robot location on the map
PlotRobotLocation(RobotLocation);

%loop until we reach the goal location or some maximum number of moves have
%been tried or we are not moving and have no hope of movement
x_move = -1;
y_move = -1;
nextDest = zeros(2,10); %Create a 10 element array
nextDest(1,1) = GoalLocation(1,1);
nextDest(2,1) = GoalLocation(2,1);
dest = 1;
while(~isequal(RobotLocation, GoalLocation) && (pathLength < 2000))

    %[newRobotLocation, obstacleHitInMove] = moveRobot(x_move, y_move, CurrentRobotLocation, OccupancyMap)
    %x_move and y_move are either -1, 0, +1
    [RobotLocation, obstacleEncountered, pathLength] = moveRobot(x_move, y_move, RobotLocation, OccupancyMap);
    if(obstacleEncountered)
        obstacleLoc = RobotLocation + [x_move, y_move]';
        %I've collided with an obstacle, this is where I start to trace the
        %obstacle to find any sort of opening.
        direction = zeros(8,2); % 1:right, 2:up-right, 3:up, 4:up-left, 5:left, 6:down-left, 7:down, 8:down-right
        dirCount = 0;
        if(TestMovement(0, 1, obstacleLoc, OccupancyMap) == 1) %Wall goes down
            dirCount = dirCount + 1;
            direction(dirCount,1) = 0;
            direction(dirCount,2) = 1;
        end
        if(TestMovement(1, 1, obstacleLoc, OccupancyMap) == 1) %Wall goes down-right
            dirCount = dirCount + 1;
            direction(dirCount,1) = 1;
            direction(dirCount,2) = 1;
        end
        if(TestMovement(1, 0, obstacleLoc, OccupancyMap) == 1) %Wall goes right
            dirCount = dirCount + 1;
            direction(dirCount,1) = 1;
            direction(dirCount,2) = 0;
        end
        if(TestMovement(1, -1, obstacleLoc, OccupancyMap) == 1) %Wall goes up-right
            dirCount = dirCount + 1;
            direction(dirCount,1) = 1;
            direction(dirCount,2) = -1;
        end
        if(TestMovement(0, -1, obstacleLoc, OccupancyMap) == 1) %Wall goes up
            dirCount = dirCount + 1;
            direction(dirCount,1) = 0;
            direction(dirCount,2) = -1;
        end
        if(TestMovement(-1, -1, obstacleLoc, OccupancyMap) == 1) %Wall goes up-left
            dirCount = dirCount + 1;
            direction(dirCount,1) = -1;
            direction(dirCount,2) = -1;
        end
        if(TestMovement(-1, 0, obstacleLoc, OccupancyMap) == 1) %Wall goes left
            dirCount = dirCount + 1;
            direction(dirCount,1) = -1;
            direction(dirCount,2) = 0;
        end
        if(TestMovement(-1, 1, obstacleLoc, OccupancyMap) == 1) %Wall goes down-left
            dirCount = dirCount + 1;
            direction(dirCount,1) = -1;
            direction(dirCount,2) = 1;
        end
        for(i = dirCount:-1:1)
            if direction(dirCount,1) ~= x_move
                dirX = (direction(dirCount,1)-x_move)/abs(direction(dirCount,1)-x_move); % gets normal vector to robot
            else
                dirX = 0;
            end
            if direction(dirCount,2) ~= y_move
                dirY = (direction(dirCount,2)-y_move)/abs(direction(dirCount,2)-y_move); % gets normal vector to robot
            else
                dirY = 0;
            end
            containsX = 0; % boolean, used to see if there are openings that would get in the way of the robot
            containsY = 0; % from getting to the current opening
            for(j = dirCount:-1:1)
                if direction(j,1) == dirX && j ~= i
                    containsX =1;
                end
            end
            for(j = dirCount:-1:1)
                if direction(j, 2) == dirY && j ~= i
                    containsY = 1;
                end
            end
            if(containsX == 1 && containsY == 1)
                tempX = direction(i,1);
                tempY = direction(i,2);
                direction(i,1) = direction(dirCount,1); % Swap current opening with last opening
                direction(i,2) = direction(dirCount,2);
                direction(dirCount,1) = tempX;
                direction(dirCount,2) = tempY;
                dirCount = dirCount - 1; % Shorten the size of the count of directions. Essentially deleting the last opening
            end
        end
        for i = 1:dirCount % Catalog each direction the wall extends to
            a = obstacleLoc(1);
            b = obstacleLoc(2);
            stop = 0; % if = 1, stop the while loop
            while((a < 100 && a>1 && b < 100 && b>1) && stop ~= 1)
                if(TestMovement(direction(i,1), direction(i,2), [a, b]', OccupancyMap) == 0)
                    stop = 1; % We've reached an opening, no need to check further in this direction
                    dest = dest + 1;
                    nextDest(1, dest) = a + direction(i,1);
                    nextDest(2, dest) = b + direction(i,2);
                end
                a = a + direction(i,1);
                b = b + direction(i,2);
            end
        end
        % Where do we move when we're at an outer edge?
        % Current code doesn't take into account if we're at the start of a
        % wall/Collided but right next to an opening
        int_x = nextDest(1, dest) - RobotLocation(1);
        int_y = nextDest(2, dest) - RobotLocation(2);
        x_move = 0;
        y_move = 0;
        if(dirCount == 0) % If it's a pillar, just go to the side
            if(x_move == 0)
                x_move = (GoalLocation(1)-RobotLocation(1))/abs(GoalLocation(1)-RobotLocation(1));
            elseif(y_move == 0)
                y_move = (GoalLocation(2)-RobotLocation(2))/abs(GoalLocation(2)-RobotLocation(2));
            end
        end
        if(int_x ~= 0 && int_y ~= 0)
            if(TestMovement(int_x/abs(int_x), 0, RobotLocation, OccupancyMap) == 0)
                    x_move = int_x/abs(int_x);
            elseif(TestMovement(0, int_y/abs(int_y), RobotLocation, OccupancyMap) == 0)
                    y_move = int_y/abs(int_y);
            elseif(int_x/abs(int_x) == int_y/abs(int_y) && int_y/abs(int_y) == 1)
                % We're actually one move from the next destination, so move straight to it
                x_move = int_x/abs(int_x);
                y_move = int_y/abs(int_y);
                if(RobotLocation(1) == nextDest(1, dest) && RobotLocation(2) == nextDest(2, dest))
                    dest = 1;
                end
            end
        else
            if(int_x == 0)
                y_move = int_y/abs(int_y);
            else
                x_move = int_x/abs(int_x);
            end
        end
        if(TestMovement(x_move, y_move, RobotLocation, OccupancyMap) == 1)
            break
        end
        [RobotLocation, obstacleEncountered, pathLength] = moveRobot(x_move, y_move, RobotLocation, OccupancyMap);
    end
    if(RobotLocation(1) == nextDest(1, dest) && RobotLocation(2) == nextDest(2, dest))
        dest = 1;
    end
    if(dest > 0)
        int_x = nextDest(1, dest) - RobotLocation(1);
        int_y = nextDest(2, dest) - RobotLocation(2);
        if(abs(int_x) > 1 && abs(int_y) > 1)
            x_move = int_x/abs(int_x);
            y_move = int_y/abs(int_y);
        elseif(abs(int_x) >= 1 && abs(int_y) == 0)
            x_move = int_x/abs(int_x)
            y_move = 0;
        elseif(abs(int_y) >= 1 && abs(int_x) == 0)
            y_move = int_y/abs(int_y)
            x_move = 0;
        elseif(abs(int_y) == 1 && abs(int_x) == 1)
            y_move = int_y/abs(int_y);
            x_move = int_x/abs(int_x);
        end
    end
        
    %pause for dramatic effect
    pause(.01);
    
    %plot out the robot location
    PlotRobotLocation(RobotLocation);
end

%Just display some extremely relevant information
if(isequal(RobotLocation, GoalLocation))
    disp('Goal Reached!');
else
    disp('Goal Not Achieved... :(');
end

disp(strcat('Path Length =  ', num2str(pathLength)));