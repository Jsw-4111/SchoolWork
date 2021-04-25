#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>

int* cities[1000];

int main() {
    struct timespec start, end;
    clock_gettime(CLOCK_MONOTONIC, &start);
    
    FILE* inputs = fopen("DistanceMatrix1000.csv", "r");
    char row[3000];
    int rowIterator = 0;
    while(fgets(row, 3000, inputs) != NULL) {
        char* tempString;
        int* tempInt = malloc(sizeof(int)*1000);
        int i = 0;
        for(tempString = strtok(row, ","); tempString && *tempString; tempString = strtok(NULL, ",")) {
            if(i == 999) {
                char* src, *dst;
                for (src = dst = tempString; *src != '\0'; src++) {
                    *dst = *src;
                    if(*dst != ';') {
                        dst++;
                    }
                }
                *dst = '\0';
            }
            tempInt[i] = strtol(tempString, NULL, 10);
            i++;
        }
        cities[rowIterator] = tempInt;
        rowIterator++;
    }
    clock_gettime(CLOCK_MONOTONIC, &end);
    printf("Time elapsed is %ld milliseconds\n", (end.tv_sec - start.tv_sec) * 1000 + (end.tv_nsec - start.tv_nsec)/1000000);
    // for(int i = 0; i < 1000; i++) {
    //     for (int j = 0; j < 1000; j++) {
    //         printf("%d ", *(*(cities + i) + j));
    //     }
    //     printf("\n");
    // }
}