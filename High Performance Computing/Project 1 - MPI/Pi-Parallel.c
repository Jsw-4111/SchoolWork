#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <sys/time.h>
#include <mpi.h>


double distToOrigin(double x, double y, double circRad){
    return sqrt(pow(x, 2) + pow(y, 2));
}

int main() {
    struct timeval start, finish;

    int comm_sz; // Variable to hold the amount of processors being used
    int my_rank; // Variable to identify which processor we are
    double circRad = 1;
    double dartRange = 1 - (-1);
    double maxdarts = 10000000000;
    double numdarts = trunc(maxdarts * (rand() / (double) RAND_MAX)); // Arbitrarily assigned number of darts used
    long long int dartsInCircle = 0;

    MPI_Init(NULL, NULL);
    MPI_Comm_size(MPI_COMM_WORLD, &comm_sz);
    MPI_Comm_rank(MPI_COMM_WORLD, &my_rank);

    
    MPI_Bcast(&numdarts, 1, MPI_DOUBLE, 0, MPI_COMM_WORLD);
    srand(256 + my_rank);
    if(my_rank == 0) {
        gettimeofday(&start, NULL);
    }
    int extraDarts = fmod(numdarts, comm_sz); // This is the number of darts we still need to distribute
    double procElements = trunc(numdarts / comm_sz);
    printf("%f procelements", procElements);
    
    long long int sum = 0;
    for(int i = 0; i < procElements; i++){ // Process your darts!
        double x = -1 + rand() / (RAND_MAX / dartRange);
        double y = -1 + rand() / (RAND_MAX / dartRange);
        if(distToOrigin(x, y, circRad) <= circRad){
            sum++;
        }
    }
    if(my_rank < extraDarts){ // Threads 0 to extraDarts - 1 will process one extra dart
        double x = -1 + rand() / (RAND_MAX / dartRange);
        double y = -1 + rand() / (RAND_MAX / dartRange);
        if(distToOrigin(x, y, circRad) <= circRad){
            sum++;
        }
    }
    if(my_rank != 0){
        printf("Core with rank %d landed %lld darts!\n", my_rank, sum);
        MPI_Reduce(&sum, &dartsInCircle, 1, MPI_LONG_LONG_INT, MPI_SUM, 0, MPI_COMM_WORLD);
    } else {
        printf("Core with rank %d landed %lld darts!\n", my_rank, sum);
        MPI_Reduce(&sum, &dartsInCircle, 1, MPI_LONG_LONG_INT, MPI_SUM, 0, MPI_COMM_WORLD);
        printf("The total darts landed from a pool of %d cores is: %lld\n", comm_sz, dartsInCircle);
        double pi_estimate = 4*dartsInCircle/((double)numdarts);
        printf("%.15f\n", pi_estimate);
    }
    MPI_Barrier(MPI_COMM_WORLD);
    if(my_rank == 0) {
        gettimeofday(&finish, NULL);
        printf("Program complete. Time elapsed: %ld seconds.\n", finish.tv_sec - start.tv_sec);
    }
    MPI_Finalize();
}
