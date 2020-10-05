function y = wu_simp(fname, a, b, n)
    if mod(n, 2) == 1
        error('You have an odd number of segments and an even number of points!');
    end
    h = (b-a)/n; % establish step size for the function
    y = h/3*(fname(a) + fname(b)); % Start with the beginning and ending values of the function
    j = 1; % A counter
    for i=h:h:n*h-h % A for loop that goes from the first iteration of the step to the next to last step
       odds = ((-1)^(j+1)*.5*fname(a+i) + .5*fname(a+i)); % Rather than making nested for loops to evaluate the summations,
       evens = ((-1)^(j)*.5*fname(a+i) + .5*fname(a+i)); % I used -1 to the power of whatever step I'm on to choose between
                                                        % even and odd steps
       y = y +  h/3*(4*odds + 2*evens); % Add them to the running sum, multiplying by h/3 each time as outlined in the
                                        % formula
       j = j + 1; % increment our counter
    end
end