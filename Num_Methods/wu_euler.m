function y = wu_euler(fname, a, b, s, n)
y(1:n) = s; % Here I create an array whose size varies based on my steps
h = (b-a)/n; % Here I calculate my step size
    for i=1:n % Here I have a for loop that goes through n steps, starting at 2 to not overwrite my y(1)
        temp = y(i) + fname(a+(i-1)*h, y(i))*h; % Here I evaluate my ith iteration of the Taylor Series method, multiplying i by h to get my x value for that step
        y(i+1) = temp; % Here I add the value I've calculated into the y-array
    end
end