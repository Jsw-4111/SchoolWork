function drawRR(config, P)

    pose = forwardKinematics(config, P);
    c_rad = deg2rad(config);
    % process inputs to function
    x          = pose(1);       % inertial x position (cm)
    y          = pose(2);       % inertial y position (cm)
    theta      = pose(3);       % heading angle (rad)
        
    % define persistent variables 
    persistent RR_handle;  % figure handle for RR robot
    persistent Vertices
    persistent Faces
    persistent facecolors
    persistent arc_handle

 
    % first time function is called, initialize plot and persistent vars
    if isempty(RR_handle)
        figure(1), clf
        [Vertices,Faces,facecolors] = defineRRBody(P);
        RR_handle = drawLinks(Vertices,Faces,facecolors,...
                                   c_rad, P,...
                                   []);     
        title('RR robot')
        xlabel('x (cm)')
        ylabel('y (cm)')
        
        axis([-5,25,-5,25]);
        grid on
        arc_handle = plot(x,y,'r');

    % at every other time step, redraw quadrotor and target
    else 
        drawLinks(Vertices,Faces,facecolors,...
                     c_rad, P,...
                     RR_handle);

        % move axes with car
        %set(RR_handle.Parent, 'XLim',[x-60,x+60])
        %set(RR_handle.Parent, 'YLim',[y-60,y+60])
        set(arc_handle,'Xdata',[get(arc_handle,'Xdata'),x]);
        set(arc_handle,'Ydata',[get(arc_handle,'Ydata'),y]);
    end
end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
function handle = drawLinks(V,F,colors,...
                               config, P,...
                               handle)
                           
  persistent EE_handle;  % figure handle for EndEffector Position
  persistent j2_handle;
  L1 = V(:, 1:6);
  L1 = rotate(L1, config(1));
  L2Pose = [0 0 config(1)];
  L2Pose = transformPose([P.l1, 0, config(2)]', L2Pose);
  L2 = V(:, 7:12);
  L2 = rotate(L2, L2Pose(3));
  L2 = translate(L2, L2Pose(1), L2Pose(2));
  EE(1) = P.l1 * cos(config(1)) + P.l2*cos(config(1)+config(2));
  EE(2) = P.l1 * sin(config(1)) + P.l2*sin(config(1)+config(2));
  J2(1) = P.l1 * cos(config(1));
  J2(2) = P.l1 * sin(config(1));
  
  %L2 = translate(L2, P.l1, 0);
  %L2 = rotate(L2, config(2));
  V = [L1 L2];
%   V = rotate(V, theta);  % rotate rigid body  
%   V = translate(V, x, y);  % translate after rotation

  if isempty(handle)
    handle = patch('Vertices', V', 'Faces', F,...
                 'FaceVertexCData',colors,...
                 'FaceColor','flat');
    hold on;
    EE_handle = plot(EE(1), EE(2), 'ro', 'MarkerSize', 7, 'MarkerFaceColor',[1,0,0]);
    j2_handle = plot(J2(1), J2(2), 'ko', 'MarkerSize', 4, 'MarkerFaceColor',[0,0,0]);
    plot(0, 0, 'ko', 'MarkerSize', 4, 'MarkerFaceColor',[0,0,0]);
    %hold on;  
    %P.lidar_handle = rectangle('Position', P.lidar, 'Curvature', [1 1]); 
  else
    set(handle,'Vertices',V','Faces',F);
    set(EE_handle, 'XData', EE(1), 'YData', EE(2));
    set(j2_handle, 'XData', J2(1), 'YData', J2(2));
    %set(P.lidar_handle, 'Position', P.lidar);
    drawnow
  end
  
end


%%%%%%%%%%%%%%%%%%%%%%%
function finalpose=transformPose(Tpose, origpose)

  T = [cos(Tpose(3)), -sin(Tpose(3)), Tpose(1);...
        sin(Tpose(3)), cos(Tpose(3)), Tpose(2);...
        0 0 1];
  O = [cos(origpose(3)), -sin(origpose(3)), origpose(1);...
        sin(origpose(3)), cos(origpose(3)), origpose(2);...
        0 0 1];      
  N = O * T;
  
  finalpose(1) = N(1,3);
  finalpose(2) = N(2,3);
  finalpose(3) = atan2(N(2,1), N(1,1));
 
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
function [V,F,colors] = defineRRBody(P)

% parameters for drawing RR robot

  scale = 1;
  P.arm_width = 2;
  P.halfwidth = 1;
  P.inset = P.halfwidth/2;
  
  %define arm points
  l1_rf = [P.l1+P.halfwidth -P.halfwidth]';
  l1_lf = [P.l1+P.halfwidth P.halfwidth]';
  l1_rr = [-P.halfwidth -P.halfwidth]';
  l1_lr = [-P.halfwidth P.halfwidth]';
  l1_lf_i = [P.l1+P.halfwidth+P.inset P.halfwidth-P.inset]';
  l1_rf_i = [P.l1+P.halfwidth+P.inset -P.halfwidth+P.inset]';
  
  l2_rf = [P.l2+P.halfwidth -P.halfwidth]';
  l2_lf = [P.l2+P.halfwidth P.halfwidth]';
  l2_rr = [-P.halfwidth -P.halfwidth]';
  l2_lr = [-P.halfwidth P.halfwidth]';
  l2_lf_i = [P.l2+P.halfwidth+P.inset P.halfwidth-P.inset]';
  l2_rf_i = [P.l2+P.halfwidth+P.inset -P.halfwidth+P.inset]';
  
  %define faces
  Link1 = [l1_lf, l1_lf_i, l1_rf_i, l1_rf, l1_rr, l1_lr];
  Link2 = [l2_lf,l2_lf_i, l2_rf_i, l2_rf, l2_rr, l2_lr];
    
  % colors
  red     = [1, 0, 0];
  green   = [0, 1, 0];
  blue    = [0, 0, 1];
  yellow  = [1,1,0];
  magenta = [0, 1, 1];
  black   = [0, 0, 0];
  grey    = [.5, .5, .5];
  
V = [Link1, Link2];
  
  F = [...
      1, 2, 3, 4, 5, 6;... %link1
      7, 8, 9, 10, 11, 12;... %link2
      ];      
colors = [...
        grey;... % link1
        grey;... % link2
        ];

  V = scale*V;   % rescale vertices
  end
  