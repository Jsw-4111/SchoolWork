// Here I've defined a function that takes in 3 inputs: a, x, and d.
// a refers to a vector, holding the coefficients for a polynomial
// x refers to a point at which we are evaluating or a deriving at
// and finally d is a toggle between whether we are to evaluate at point x (0) or derive (1)

a = [50 0 -2 80 1000]
x = 42
d = 1
function y = horner_Wu(a, x, d)
    size = size(a, 2)
    if (d == 0) then
        y = a(1);
        for i = 2:size
            y = y * x + a(i);
        end
    elseif (d == 1) then
        y = 0;
        j = 1;
        for i = size-1:-1:1
            y = a(i) * j*x^(j-1) + y;
            j = j + 1;
        end
    end
endfunction
y = horner_Wu(a, x, d)
disp(y)
