#include <stdio.h>
#include <sys/time.h>
#include <time.h>
#include <math.h>

int main(int argc, char **argv)
{   // For our algorithm, we will define a circle of diameter 2
    // Still need to track time
    struct timeval start, elapsed;
    gettimeofday(&start, NULL);
    elapsed = start;
    double d = 2;
    double currentSide = 1;
    int inscribed = 1; // 0 for false, 1 for true
    double a, b;

    if (inscribed == 1)
    {
        currentSide = (d/2)/atan(30); // Finds side length by using the known length of the bisected point and the halfangle of a hexagon
    }
    for(int n = 6; n <= 100; n *= 2) // We will begin our algorithm with a polygon of 6 sides
    {
        double perimeter = n * currentSide;
        printf("perimeter = %f \n", perimeter);
        double approxPi = perimeter/d;
        printf("Finished calculation of pi for polygon of %d sides in: %lf milliseconds\n", n, (elapsed.tv_usec - start.tv_usec)/1000.0);
        gettimeofday(&elapsed, NULL);
        printf("Current Pi estimate: %f\n", approxPi);
        if (inscribed)
        {
            a = sqrt(pow(a, 2)-pow(currentSide/2.0, 2));
            b = a - d/2.0;
        }
        else
        {
            a = sqrt(pow(d/2.0, 2)-pow(currentSide/2.0, 2));
            b = d/2.0 - a;
        }
        double nextSide = sqrt(pow(b, 2) + pow(currentSide/2, 2));
        currentSide += 2.0;
        currentSide = nextSide;
    }
    printf("Finished program, total time elapsed: %lf milliseconds\n", (elapsed.tv_usec - start.tv_usec)/1000.0);
}