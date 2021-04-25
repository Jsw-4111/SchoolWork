#include <stdio.h>
#include <stdlib.h>
#include <math.h>

int *merge(int residentList[], int receivedList[], int resSize, int recSize){
    int size = resSize + recSize; // Gets the size of our merged list
    int *newList = malloc(size * sizeof(int));
    int i = 0; int j = 0; int k = 0;
    while(j < resSize && k < recSize){
        if(residentList[j] < receivedList[k]) { // If my value is less than theirs
            *(newList + i) = residentList[j]; // Add my value in first
            i++, j++;
        } else if (residentList[j] > receivedList[k]){ // If my value is greater than theirs
            *(newList + i) = receivedList[k]; // Add their value in first
            i++, k++;
        } else { // If they're equal
            *(newList + i) = residentList[j]; // Add my value first
            i++, j++;
            *(newList + i) = receivedList[k]; // Then add their value next
            i++, k++;
        }
    }
    if(j < resSize){ // If my list hasn't finished yet, add the rest of them in
        while(j < resSize){
            newList[i] = residentList[j];
            i++, j++;
        }
    } else if (k < recSize) { // Otherwise if their list hasn't finished yet, add the rest of those in
        while(k < recSize){
            newList[i] = receivedList[k];
            i++, k++;
        }
    }
    return newList;
}

int main(){
    int comm_sz = 1; // Variable to hold the amount of processors being used
    int my_rank = 0; // Variable to identify which processor we are
    double randMax = 128000000;
    double n = randMax*(rand()/(double) RAND_MAX);
    double extraElements = fmod(n, (double) comm_sz);
    double procElements = (int) n / comm_sz;

    double local_n = procElements;
    printf("Core %d has %f elements\n", my_rank, local_n);
    if(my_rank < extraElements) {
        local_n += 1;
    }
    srand(256 + my_rank);

    int val = 0 + rand();
    int tempList[] = {0 + val}; // Need to rethink tempList and localList
    int *localList = tempList;
    for(int i = 1; i < local_n; i++) { // Start at index 1 because we initialized the array with a random value already 
        val = 0 + rand();
        int temp[] = {0 + val};
        localList = merge(localList, temp, i, 1); // Need to sort this initial list generated by each core
    }
    // double runs = (int)(ceil(log10(comm_sz)/log10(2))); // Calculates the amount of levels our tree will be
    // for(int i = 0; i < runs; i++) {
    //     if(fmod((double) my_rank, pow(2, i)) == 0) { // If the rank is a multiple of 1, 2, 4, 8 ...
    //         if(fmod((double) my_rank, pow(2, i+1)) == 0 && my_rank + pow(2, i) < comm_sz) {  // If the rank is used in the next run, receive a list
    //             int srcCore = my_rank + pow(2, i);
    //             int recvElements = procElements;
    //             if(srcCore < extraElements)
    //                 recvElements += 1;
    //             int *temp = malloc(sizeof(int)*recvElements);
    //             // MPI_Recv(temp, recvElements, MPI_INT, srcCore, i, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
    //             printf("Core %d received message from core %d!\n", my_rank, srcCore);
    //             localList = merge(localList, temp, local_n, recvElements);
    //             free(temp);
    //         } else if (my_rank - pow(2, i) > 0) { // If the rank has been selected but won't be used in the future, send a list
    //             int destCore = my_rank - pow(2, i);
    //             // MPI_Send(localList, local_n, MPI_INT, destCore, i, MPI_COMM_WORLD);
    //         }
    //     }
    // }
    // MPI_Barrier(MPI_COMM_WORLD);
    if(my_rank == 0) {
        printf("Completed merge sort of array with %0f values using %d cores\n", n, comm_sz);
        printf("Memory size of the list is %ld\n", sizeof(localList));
        printf("List size is %ld\n", sizeof(*localList)/sizeof(*(localList) + 0));
        printf("int size is %ld\n", sizeof(int));
        for(int i = 0; i < n; i++) {
            printf("%d \n", *(localList + i));
        }
    }
    // MPI_Finalize();
    free(localList);
}