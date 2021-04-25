#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#include <sys/time.h>
#include <math.h>

int thread_count;

struct arg {
    double* sum;
    double local_a;
    double local_b;
    double local_n;
    double h;
    pthread_mutex_t* mut;
    long thread;
};

void* mutTrap(void* args);
double f(double x);

int main(int argc, char* argv[]) {
    struct timespec start, end;
    clock_gettime(CLOCK_MONOTONIC, &start);
    long thread;
    pthread_t* threadhandles;

    int thread_count = strtol(argv[1], NULL, 10);
    int n = strtol(argv[2], NULL, 10);
    double a = 0.0, b = 3.0, h; // Defining an interval from a to b.
    h = (b-a)/n; // The base width of each trapezoid
    threadhandles = malloc(thread_count * sizeof(pthread_t)); // Thread handler
    double sum = 0;
    struct arg *args;
    args = malloc(sizeof(struct arg) * thread_count);
    pthread_mutex_t mut;
    pthread_mutex_init(&mut, NULL);

    for(thread = 0; thread < thread_count; thread++) {
        double local_n = n/thread_count; // The amount of trapezoids given to this thread
        double local_a = a + thread*local_n*h; // The starting element for the thread
        double local_b = local_a + local_n*h; // The ending element for the thread
        if(thread < n%thread_count) { // If this thread is one of the ones we will be giving an extra trapezoid
            local_n += 1; // Add an extra trapezoid to the count
        } else {
            local_b += n%thread_count*h; // We have passed the extra allocations, make sure to add them.
        }
        args[thread].sum = &sum;
        args[thread].local_a = local_a + 0; // Add 0 to make a unique copy
        args[thread].local_b = local_b + 0; // Add 0 to make a unique copy
        args[thread].local_n = local_n + 0; // Add 0 to make a unique copy
        args[thread].h = h + 0; // Add 0 to make a unique copy
        args[thread].mut = &mut;
        args[thread].thread = thread + 0; // Add 0 to make a unique copy

        pthread_create(&threadhandles[thread], NULL, mutTrap, (void*) &args[thread]);
    }
    for(thread = 0; thread < thread_count; thread++) {
        pthread_join(threadhandles[thread], NULL);
    }
    free(args);
    clock_gettime(CLOCK_MONOTONIC, &end);
    printf("Result has values of: %f\n", sum);
    printf("The computation took %ld milliseconds\n", (end.tv_sec - start.tv_sec) * 1000 + (end.tv_nsec - start.tv_nsec)/1000000);
    free(threadhandles);
}

void* mutTrap(void* args) {
    struct arg myArgs = *((struct arg*) args);
    double local_a = myArgs.local_a;
    double local_b = myArgs.local_b;
    double local_n = myArgs.local_n;
    double h = myArgs.h;
    long thread = myArgs.thread + 0;
    double temp = 0;
    for(int i = 0; i < local_n; i++) {
        temp += (f(local_a + i*h) + f(local_a + (i+1) * h))*h/2.0;
    }
    pthread_mutex_lock(myArgs.mut);
    *myArgs.sum += temp;
    pthread_mutex_unlock(myArgs.mut);
    return NULL;
}

double f(double x) {
    // Function is -x^3 + 9x; Full interval should be around 20.25
    return 9.0*x - pow(x, 3.0);
}