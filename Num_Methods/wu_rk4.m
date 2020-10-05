function y = wu_rk4(fname, a, b, s, n)
    y(1:n) = s; % Here I create an array whose size varies based on my steps
    h = (b-a)/n; % Here I calculate my step size
    yi = s; % Here I store my initial value in a temporary variable.
    for i=1:n % This is a for loop that goes through the intended number of iterations
        k1 = fname(a+(i-1)*h, yi); % These four lines calculate the k values that are used in our final
        k2 = fname(a+(i-1)*h+.5*h, yi+.5*k1*h); % formula, as shown in the RK4 method
        k3 = fname(a+(i-1)*h+.5*h, yi+.5*k2*h);
        k4 = fname(a+(i-1)*h+h, yi+k3*h);
        y(i+1) = yi + 1/6*(k1+2*k2+2*k3+k4)*h; % Here I append my new value to its respective location
        yi = y(i+1); % Here I replace my previous temporary variable with my new value
                   % I will be using this value for the next iteration and
                   % changing it afterwards again as well.
    end
end