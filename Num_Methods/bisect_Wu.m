% This code utilizes the Bisection method to find roots where fname = the name of the file that evaluates f(x)
% a and b are the interval for which we have detected a root, tol is the upper bound of length of the final interval,
% and finally nmax is the maximum number of iterations that the code is allowed to run through
% Finally, the 4 outputs stand for: n = total iterations executed, l and r are the final interval,
% c  is the approximation of the root.
function [n, l, r, c] = bisect_Wu(fname, a, b, tol, nmax)
    % Here we are to take the inputs of f at a and b, seeing if their product = a negative number
    % This would be a preliminary step to see if there is a root held in the interval
    if sign(fname(a))*sign(fname(b)) == sign(-1) % Check for change in sign
        prevC = 0; % Create variable to keep track of C
        c = a + (b - a)/2; % Find midpoint: Use this equation because it decreases loss of significant figures
        tempa = a;
        tempb = b;
        l = tempa;
        r = tempb;
        for i=1:nmax % Repeat until maximum iterations reached
            n = i; % Create some number n that returns the number of iterations needed
            c = (tempa + (tempb - tempa)/2); % Calculate midpoint iteratively
            if abs(c - prevC) >= tol % Check difference between current and previous midpoint.
                prevC = c; % Hold c's value for reference later
                if sign(fname(c))*sign(fname(tempa)) == sign(-1) % Check if a change in sign exists between midpoint and a
                    tempb = c; % Shrink the interval, replace b with c and start again
                elseif sign(fname(c))*sign(fname(tempb)) == sign(-1) % Check if change in sign exists between mid and b
                    tempa = c; % Shrink the interval, replace a with c and start again
                elseif fname(c) == 0 % If the value of the function at the midpoint is c, you got lucky!
                    break % Found a midpoint so you're done
                else % If all else fails
                    error('Root lost') % Just in case something really weird happens, stop everything
                end
            l = tempa; % Load new interval into output variables
            r = tempb;
            else % If the difference between midpoints is less than the tolerance, you should be done!
                break
            end
            if i == nmax; % Check to see how many times you've iterated
                error('Maximum number of iterations has been achieved') % You've done enough so just stop
            end
        end
    else 
        error('There was an issue with the bounds given. Does not satisfy bisection method conditions.')
    end
end

% TIPS FOR CODE
% For the report, you only need one, not one for each SEPARATE FILES THOUGH
% Great opportunity to compare the methods
    % Secant method is faster except at multiple roots
% Pick different ICs for secant
% Try to break it
% Have test files AND functions
