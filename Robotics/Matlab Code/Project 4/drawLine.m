function [  ] = drawLine( line )
%draw a Line on Figure 1.
figure(1);
hold on;

%draw 10000 cm
x1 = line(1);
y1 = line(2);
x2 = line(1) + 10000 * cos(line(3));
y2 = line(2) + 10000 * sin(line(3));

plot([x1 x2], [y1 y2], 'k');

end

