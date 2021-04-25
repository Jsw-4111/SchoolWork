#include <stdio.h>
#include <stdlib.h>
#include <omp.h>
#include <time.h>
#include <semaphore.h>
#include <string.h>

/* 
    This is my OpenMP exercise program. It will be using a mix of producers and consumers
    entered at the command line with the following format: 
        <program exe> <# Producers> <# Consumers>
    and run at varying combinations of 1, 2, 5, and 8 producers and 1, 2, 3, and 8 consumers.
    The producers will have files that they will write into buffers, whereupon the consumers
    will read the buffers, parse them, then prints them to the terminal.
*/

//*** Need to make queues manually, no queue support in C ***//
void producer();
void consumer();
FILE** files;
char** bufQueue;
volatile int queuePos = 0; // Position of the first element in the queue
volatile int lastElPos = 0; // Position of the most recently pushed element
volatile int inQueue = 0; // Number of elements in the queue
volatile int finProd = 0; // Number of producer that have finished
int numProd;

void prodconController();
void producer();
void consumer();
char* pop();
char* peek();
void push(char* el);

int main(int argc, char* argv[]) { 
    struct timespec start, end;
    clock_gettime(CLOCK_MONOTONIC, &start);
    numProd = strtol(argv[1], NULL, 10);
    int numCon = strtol(argv[2], NULL, 10);
    files = malloc(sizeof(FILE*)*numProd);
    bufQueue = malloc(sizeof(char*)*numProd);
    for(int i = 0; i < numProd; i++) { 
        char* filename = malloc(sizeof(char) * 256);
        sprintf(filename, "thread%d.txt", i);
        files[i] = fopen(filename, "r");
        if(files[i] == NULL) {
            printf("Couldn't read file %d", i);
        }
    }
#   pragma omp parallel num_threads(numProd + numCon)
    prodconController();

#   pragma omp barrier
    clock_gettime(CLOCK_MONOTONIC, &end);
    printf("The program took %ld milliseconds\n", (end.tv_sec - start.tv_sec) * 1000 + (end.tv_nsec - start.tv_nsec)/1000000);
    free(files);
    free(bufQueue);
    return 0;
}

void prodconController() {
    int my_rank = omp_get_thread_num();
    if(my_rank < numProd) { // If you're a producer
        producer();
    } else { // Otherwise, you're a consumer
        consumer();
    }
}
/*
    Here we have our producer method. This method will take the files opened in the main method
    and will read from them, line by line. With each line, they will copy the line read into the
    buffer queue. When the queue is full and there are more inputs, they will sleep/wait.
*/
void producer() {
    int my_rank = omp_get_thread_num();
    // int my_rank = 0;
    char* str = malloc(sizeof(char) * 256);
    int pushed = 1; // 0 = false, start at 1 so fgets is evaluated
    while(pushed == 0 || fgets(str, 256, files[my_rank]) != NULL) { // Make sure we actually got something from fgets
    #   pragma omp critical
        if(inQueue < numProd) {
            {
            push(str);
            }
            pushed = 1;
        } else {
            pushed = 0;
        }
    }
    finProd++; // We've finished, so increment the number of finished producers
}
/**
 *  Here we have our consumer method. This method will take the lines read into the queue, pop
 *  them, and print them out. If the queue is empty and the producers have more lines to be read,
 *  they will sleep/wait.
 **/

// TO-DO: Consumer currently exits before all elements are pushed
void consumer() {
    while(finProd < numProd || inQueue > 0) { // While not all producers have finished
        // queuePos <= lastElPos checks to see if the current position in queue is greater than
        // the most recently placed element. If it is, then it means the queue is empty.
        char** str = calloc(1, sizeof(char*));
    #   pragma omp critical // Crit section
        if (inQueue > 0 && queuePos <= lastElPos) {
            {
                *str = pop();
                inQueue--;
                queuePos++;
                puts(*str);
                free(*str); // Free the malloc'd data that is being pointed at by str
            }
        }
        free(str);
    }
}

char* pop() { // Be sure to free the element returned by pop after calling and using it
    return bufQueue[queuePos%numProd];
}

char* peek() {
    return bufQueue[queuePos%numProd];
}

void push(char* el) {
    char* str = malloc(sizeof(char)*256);
    strcpy(str, el);
    bufQueue[lastElPos%numProd] = str;
    lastElPos++;
    inQueue++;
}