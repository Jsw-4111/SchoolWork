#include <stdio.h>
#include <stdlib.h>
#include <math.h>


double distToOrigin(double x, double y, double circRad){
    return sqrt(pow(x, 2) + pow(y, 2));
}

int main() {
    double circRad = 1;
    double dartRange = 1 - (-1);
    double randMax = 10000000000;
    long long int numdarts = randMax/(double)(rand()/RAND_MAX);
    long long int dartsInCircle = 0;
    for(int i = 0; i < numdarts; i++){
        double x = -1 + rand() / (RAND_MAX / dartRange);
        double y = -1 + rand() / (RAND_MAX / dartRange);
        if(distToOrigin(x, y, circRad) <= circRad){
            dartsInCircle++;
        }
    }
    double pi_estimate = 4*dartsInCircle/((double)numdarts);
    printf("%.15f", pi_estimate);
}
