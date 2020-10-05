function q = wu_trap(fname, a, b, n)
    h = (b-a)/n; % Find the step size for the function
    f1 = fname(a); % Take the first value on our interval
    fn = fname(b); % Take the last value on our interval
    q = h/2*(f1 + fn); % Add the two together and multiply them by h/2
    for i=h:h:n*h-h % Make a for loop for the summation, going between but not including a and b in steps of h
        q = q + h*fname(a+i); % Sum all intervals between a and b, then multiply by h
    end % end loop
end % end function