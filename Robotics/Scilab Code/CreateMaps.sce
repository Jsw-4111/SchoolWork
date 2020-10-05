//This is just for the instructor
//Create occupancy maps
clear all
OccupancyMap = zeros(100, 100);
//Walls
OccupancyMap(1,:) = 1;
OccupancyMap(end,:) = 1;
OccupancyMap(:,1) = 1;
OccupancyMap(:,end) = 1;
//Obstacles v1
// OccupancyMap(30, 1:70) = 1;
// OccupancyMap(70, 1:35) = 1;
// OccupancyMap(70, 65:end) = 1;
OccupancyMap(45:60, 40) = 1;
OccupancyMap(45, 40:60) = 1;
OccupancyMap(45:60, 60) = 1;

iter = 0;
for i = 45:-1:1
    OccupancyMap(i, 60+iter)=1;
    OccupancyMap(i, 61+iter)=1;
    iter = iter+1;
    if(60+iter >= 99)
        break;
    end
end

OccupancyMap (10, 10:20) = 1;
OccupancyMap (20, 10:20) = 1;
OccupancyMap (10:20, 10) = 1;
OccupancyMap (10:20, 20) = 1;

OccupancyMap (40, 1:30) = 1;
OccupancyMap (45, 1:30) = 1;
OccupancyMap (40:45, 30) = 1;

iter = 0;
for i = 95:-1:1
    OccupancyMap(i, 40+iter)=1;
    OccupancyMap(i, 41+iter)=1;
    iter = iter+1;
    if(40+iter >= 90)
        break;
    end
end
PlotOccupancyMap(OccupancyMap, [1,1]);  //Print the occupancy map with a stock goal
//Obstacles v2

