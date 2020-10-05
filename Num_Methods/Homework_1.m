% This is the function, I think, takes in a, x, and d; and outputs y
% a in this case is a vector containing the coefficients for each power
% x is the point at which the poolynomial is to be evaluated
% d is a toggle for whether you evaluate at the point or you derive at the point
% y is the output that you get from running the horner's algorithm on the polynomial given

vector = [54 -60 -123 0 51243];
point = 5;
derive = 0;

y = horner_wu(vector, point, derive)
function y = horner_wu(a,x,d)
    b = size(a, 2); % Here I take the size of the vector so I know how many coefficients I'm dealing with
    % For the evaluation method I work from the inside out
    % Again, working inside out, I know that Horner's algorithm has a lone number in the 
    % very middle (the leading coefficient), so I take advantage of this.
    % Due to my approach, I utilize order of operations and evaluate the inner parenthesis then multiply by x
    % after each run and finally add the next number in the sequence.
    
    if (d == 0) 
        y = a(1); % Lone number at a(1)
        for (i = 2:b)
            y = y * x + a(i); % Iteratively multiply by x then add a(i) per run-through
        end
    % In the derivative I do the exact opposite of my previous approach. I do this because I observed that
    % I cannot derive, multiply, divide again... That's just not how math works. So instead, I start with
    % the polynomial's constant (derived to 0) and set it to y. I then add the next coefficient after distributing
    % the previous x value and deriving it. In effect, the code does something like this: for (3x^3 - 7x + 10)dx
    %     (10 + x(-7 + x(0 + x(3))))dx -> (-7x+x^2(0+x(3)))dx -> -7 + 3x^3dx -> -7 + 9x^2
    elseif (d == 1)
        y = 0; % Constant derived
        j = 1; % Counter for the current power of x
        for (i = (b-1):-1:1)
            y = y + a(i)*j*x^(j-1); % Here I add to y the value of the next derived number
            j = j + 1;
        end
    end 
end  
