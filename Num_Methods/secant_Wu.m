% This is the start of a function that performs the secant method to find
% roots of a function. Here we have input values of fname - the name of the
% function file, x0 - the value of the first point, x1 - the value of the
% second point, tol - the tolerance that indicates when you're close enough
% to stop, and nmax - the maximum amount of iterations we should run.
% Our outputs are n - which returns the amount of iterations and r - the
% expected root.

function [n r] = secant_Wu(fname, x0, x1, tol, nmax)
    a = x0; % Simplify things by setting easily trackable variables.
    b = x1;
    for i = 1:nmax % Start at 1 and go to nmax, then stop
        n = i; % Hold the value of i for outputs
        r = b - fname(b)*(b - a)/(fname(b)-fname(a)); % Find the root by getting the slope, multiplying it by the value at b and subtracting that from b
        if tol >= abs(r - b) % See if the values are less than the toleration
            disp('Reached root within target tolerance') % Indicate to user that you've gotten close enough
            break % Exit the loop
        end
        a = b;
        b = r;
        if i == nmax % Check if the maximum iterations has been reached
            error('Maximum number of runs achieved') % Notify user that the maximum number of iterations has been reached.
        end
    end