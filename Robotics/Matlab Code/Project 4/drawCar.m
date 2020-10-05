function drawCar(state, P)

    % process inputs to function
    x          = state(1);       % inertial x position (cm)
    y          = state(2);       % inertial y position (cm)
    theta      = state(3);       % heading angle (rad)
    vel        = state(4);       % forward velocity (cm/sec)
    theta_dot  = state(5);       % turn rate (rad/sec)
    t          = state(6);       % time (s)
    
    % define persistent variables 
    persistent car_handle;  % figure handle for car
    persistent lidar_handle; %figure for P.lidar
    persistent Vertices
    persistent Faces
    persistent facecolors

 
    % first time function is called, initialize plot and persistent vars
    if t==0
        figure(1), clf
        [Vertices,Faces,facecolors] = defineCarBody(P);
        car_handle = drawBody(Vertices,Faces,facecolors,...
                                   x, y, theta,...
                                   []);     
        title('Rover Course')
        xlabel('x (cm)')
        ylabel('y (cm)')
        
        axis([x-300,x+300,y-300,y+300]);
        grid on
        
    % at every other time step, redraw quadrotor and target
    else 
        drawBody(Vertices,Faces,facecolors,...
                     x, y, theta,...
                     car_handle);

        % move axes with car
        set(car_handle.Parent, 'XLim',[x-300,x+300])
        set(car_handle.Parent, 'YLim',[y-300,y+300])
    end
end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
function handle = drawBody(V,F,colors,...
                               x, y, theta,...
                               handle)
  V = rotate(V, theta);  % rotate rigid body  
  V = translate(V, x, y);  % translate after rotation

  if isempty(handle)
    handle = patch('Vertices', V', 'Faces', F,...
                 'FaceVertexCData',colors,...
                 'FaceColor','flat');
    %hold on;  
    %P.lidar_handle = rectangle('Position', P.lidar, 'Curvature', [1 1]); 
  else
    set(handle,'Vertices',V','Faces',F);
    %set(P.lidar_handle, 'Position', P.lidar);
    drawnow
  end
  
end 

%%%%%%%%%%%%%%%%%%%%%%%
function pts=rotate(pts,theta)

  % define rotation matrix (right handed)
  R = [...
          cos(theta), -sin(theta);...
          sin(theta), cos(theta)];
  % rotate vertices
  pts = R*pts;
  
end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% translate vertices by x, y
function pts = translate(pts,x, y)

  pts = pts + repmat([x;y],1,size(pts,2));
  
end

% end translate


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% define aircraft vertices and faces
function [V,F,colors] = defineCarBody(P)

% parameters for drawing aircraft
  % scale = 20  %only for scale drawing purposes.
  scale = 1;
%   P.chasis_width = 18;
%   P.chasis_forward = 20;
%   P.chasis_backward = -2;
%   P.wheel_base = 21;
%   P.wheel_width = 2.5;
%   P.wheel_radius = 4.5;
%   P.lidar_forward = 16*scale;
%   P.lidar_radius = 3.5*scale;
  
  %define points
  b_lf = [P.chasis_forward P.chasis_width/2]';
  b_rf = [P.chasis_forward -P.chasis_width/2]';
  b_lr = [P.chasis_backward P.chasis_width/2]';
  b_rr = [P.chasis_backward -P.chasis_width/2]';
  lw_lf = [P.wheel_radius P.wheel_width/2+P.wheel_base/2]';
  lw_rf = [P.wheel_radius -P.wheel_width/2+P.wheel_base/2]';
  lw_rr = [-P.wheel_radius -P.wheel_width/2+P.wheel_base/2]';
  lw_lr = [-P.wheel_radius P.wheel_width/2+P.wheel_base/2]';
  rw_lf = [P.wheel_radius P.wheel_width/2-P.wheel_base/2]';
  rw_rf = [P.wheel_radius -P.wheel_width/2-P.wheel_base/2]';
  rw_rr = [-P.wheel_radius -P.wheel_width/2-P.wheel_base/2]';
  rw_lr = [-P.wheel_radius P.wheel_width/2-P.wheel_base/2]';
  
  li_lf = [P.lidar_forward+P.lidar_radius P.lidar_radius]';
  li_rf = [P.lidar_forward+P.lidar_radius -P.lidar_radius]';
  li_rr = [P.lidar_forward-P.lidar_radius -P.lidar_radius]';
  li_lr = [P.lidar_forward-P.lidar_radius P.lidar_radius]';

  %define faces
  body = [b_lf, b_rf, b_rr, b_lr];
  left_wheel = [lw_lf, lw_rf, lw_rr, lw_lr];
  right_wheel = [rw_lf, rw_rf, rw_rr, rw_lr];
  lidar = [li_lf, li_rf, li_rr, li_lr];  %square here can make a circle
  
  
  % colors
  red     = [1, 0, 0];
  green   = [0, 1, 0];
  blue    = [0, 0, 1];
  yellow  = [1,1,0];
  magenta = [0, 1, 1];
  black   = [0, 0, 0];
  
V = [body, left_wheel, right_wheel, lidar];
  
  F = [...
      1, 2, 3, 4;... %body
      5, 6, 7, 8;... %left P.wheel
      9, 10, 11, 12;... %right P.wheel
      13, 14, 15, 16;... %P.lidar square
];      
colors = [...
        blue;... % body
        black;... % left P.wheel
        black;... % right P.wheel
        black;... %P.lidar
  ];

  V = scale*V;   % rescale vertices
  end
  