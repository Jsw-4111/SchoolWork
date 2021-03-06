#include <stdio.h>
#include <sys/time.h>
#include <time.h>
#include <math.h>

int main(int argc, char **argv)
{   
    struct timeval start, elapsed; // Here we create timeval structs to measure the time it takes for each operation
    gettimeofday(&start, NULL); // Here we start the timer for the first operation
    // First, we time the multiplication operation
    int mult;
    for(int i = 0; i < 50; i++) // Here we do multiple multiplication operations
    {
        mult = i*(i+1);
    }
    gettimeofday(&elapsed, NULL);
    float time = ((float)elapsed.tv_usec - (float)start.tv_usec)/1000.0; // Here we get the averaged time in milliseconds
    time = time/50.0;
    printf("Time for the multiplication to finish: %f\n", time);

    gettimeofday(&start, NULL);
    // Now we begin timing the division operation
    float div;
    for(int i = 0; i < 50; i++) // Here we do multiple division operations
    {
        div = i/(i+1.0);
    }
    gettimeofday(&elapsed, NULL);
    time = ((float)elapsed.tv_usec - (float)start.tv_usec)/1000.0;
    time = time/50.0;
    printf("Time for the division to finish: %f\n", time);

    gettimeofday(&start, NULL);
    // Now we begin timing the sqrt function
    float root;
    for(int i = 0; i < 50; i++) // Here we do multiple sqrt operations
    {
        root = sqrt(i+1);
    }
    gettimeofday(&elapsed, NULL);
    time = ((float)elapsed.tv_usec - (float)start.tv_usec)/1000.0;
    time = time/50.0;
    printf("Time for the sqrt to finish: %f\n", time);

    gettimeofday(&start, NULL);
    // Now we begin timing the sin function
    float sine;
    for(int i = 0; i < 50; i++) // Here we do multiple sine operations
    {
        sine = sin(i+1);
    }
    gettimeofday(&elapsed, NULL);
    time = ((float)elapsed.tv_usec - (float)start.tv_usec)/1000.0;
    time = time/50.0;
    printf("Time for the sine to finish: %lf\n", time);
}