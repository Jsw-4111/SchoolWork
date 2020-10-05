function [ ] = drawCircle( circle, color )
%%draw a Circle on Figure 1.
figure(1)
hold on;
divisions = 30;
x = zeros(1,divisions+1);
y = zeros(1,divisions+1);
angle_div = 2*pi/divisions;

for i=1:divisions
    angle = (i-1)*angle_div;
    x(i) = circle(1) + circle(3)*cos(angle);
    y(i) = circle(2) + circle(3)*sin(angle);
end
angle = 2 * pi;
x(divisions+1) = circle(1) + circle(3)*cos(angle);
y(divisions+1) = circle(2) + circle(3)*sin(angle);

plot(x, y, color);

