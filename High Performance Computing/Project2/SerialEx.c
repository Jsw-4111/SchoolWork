#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <math.h>

double serialTrap(int n, double a, double b, double h);
double f(double x);

int main(int argc, char* argv[]) {
    struct timespec start, end;
    clock_gettime(CLOCK_MONOTONIC, &start);

    int n = strtol(argv[1], NULL, 10);
    double a = 0.0, b = 3.0, h; // Defining an interval from a to b.
    h = (b-a)/n; // The base width of each trapezoid

    double sum = serialTrap(n, a, b, h);

    clock_gettime(CLOCK_MONOTONIC, &end);
    printf("Result has values of: %f\n", sum);
    printf("The computation took %ld milliseconds\n", (end.tv_sec - start.tv_sec) * 1000 + (end.tv_nsec - start.tv_nsec)/1000000);
}

double serialTrap(int n, double a, double b, double h) {
    double temp = 0;
    for(int i = 0; i < n; i++) {
        temp += (f(a + i*h) + f(a + (i+1) * h))*h/2.0;
    }
    return temp;
}

double f(double x) {
    // Function is -x^3 + 9x; Full interval should be around 20.25
    return 9.0*x - pow(x, 3.0);
}